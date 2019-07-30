import React, { Component, PureComponent } from 'react';
import { inject, observer } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Button, DataSet, Modal, Table, Tooltip } from 'choerodon-ui/pro';
import { $l, axiosPro as axios, ContentPro as Content } from '@choerodon/boot';
import ExecutionDataSet from './stores/ExecutionDataSet';
import TaskDetailDataSet from './stores/TaskDetailDataSet';
import TaskGroupDataSet from './stores/TaskGroupDataSet';
import ParameterDataSet from './stores/ParameterDataSet';
import TaskDetailModal from './view/TaskDetailModal';
import GroupModal from './view/GroupModal';

const { Column } = Table;
const modalKey = Modal.key();

function statusRender({ record, text }) {
  if (record.get('status') === 'FAILURE') {
    return <span style={{ color: '#DD0000' }}>{text}</span>;
  } else if (record.get('status') === 'SUCCESS') {
    return <span style={{ color: '#00AA00' }}>{text}</span>;
  } else if (record.get('status') === 'READY') {
    return <span style={{ color: '#EFA90D' }}>{text}</span>;
  } else if (record.get('status') === 'EXECUTING') {
    return <span style={{ color: '#006BB0' }}>{text}</span>;
  } else if (record.get('status') === 'UNEXECUTED') {
    return <span style={{ color: '#808080' }}>{text}</span>;
  } else if (record.get('status') === 'ROLLBACK') {
    return <span style={{ color: '#BA55D3' }}>{text}</span>;
  } else if (record.get('status') === 'CANCEL') {
    return <span style={{ color: '#A52A2A' }}>{text}</span>;
  } else if (record.get('status') === 'CANCELING') {
    return <span style={{ color: '#CD5C5C' }}>{text}</span>;
  }
}

export default class Index extends PureComponent {
  downLoad() {
    const executionId = this.executionDS.current.get('executionId');
  }

  constructor(props) {
    super(props);
    this.taskDetailDS = new DataSet(TaskDetailDataSet);
    this.taskGroupDS = new DataSet(TaskGroupDataSet);
    this.executionDS = new DataSet(ExecutionDataSet);
    this.parameterDS = new DataSet(ParameterDataSet);
  }

  setDataSetData(data) {
    const { parameterDS } = this;
    parameterDS.loadData(data);
  }


  cancleExecution = async () => {
    const executionId = this.executionDS.current.get('executionId');
    const status = this.executionDS.current.get('status');
    await axios.post('/sys/task/execution/cancleExecute', { executionId, status }).then((res) => {
      this.executionDS.current.set('status', 'CANCEL');
    });
  };

  closeModal = () => {
    this.modal.close();
  };


  openTaskEditModal = async () => {
    const executionId = this.executionDS.current.get('executionId');
    const { taskExecutionDetail } = (await axios.post('/sys/task/execution/detail', { executionId })).rows[0];
    const { executionLog } = taskExecutionDetail;
    const { stacktrace } = taskExecutionDetail;
    this.setDataSetData(JSON.parse(taskExecutionDetail.parameter));
    this.modal = Modal.open({
      key: modalKey,
      drawer: true,
      destroyOnClose: true,
      children: (
        <TaskDetailModal executionDS={this.executionDS} parameterDS={this.parameterDS} executionLog={executionLog} stacktrace={stacktrace} />
      ),
      footer: [
        <Button color="blue" icon="playlist_add" href={`/sys/task/execution/detailDownload?executionId=${executionId}`} download>
          {$l('task.execution.download')}
        </Button>,
        <Button style={{ marginLeft: 10 }} onClick={this.closeModal}>
          {$l('hap.close')}
        </Button>,
      ],
      style: {
        width: 850,
      },
    });
  };

  openGroupModal = async () => {
    const executionId = this.executionDS.current.get('executionId');
    this.taskGroupDS.queryParameter.executionId = executionId;
    this.taskGroupDS.query();
    Modal.open({
      key: modalKey,
      title: $l('task.execution.detail'),
      drawer: true,
      destroyOnClose: true,
      children: (
        <GroupModal executionDS={this.executionDS} taskGroupDS={this.taskGroupDS} parameterDS={this.parameterDS} />
      ),
      okCancel: false,
      okText: $l('hap.close'),
      style: {
        width: 850,
      },
    });
  };

  renderBtn = ({ record }) => {
    if (record.get('taskDetail').type === 'TASK') {
      return (
        <Tooltip title={$l('task.execution.detail')}>
          <Button
            funcType="flat"
            color="blue"
            icon="branding_watermark"
            onClick={() => this.openTaskEditModal(record)}
          />
        </Tooltip>
      );
    } else if (record.get('taskDetail').type === 'GROUP') {
      return (
        <Tooltip title={$l('task.execution.detail')}>
          <Button
            funcType="flat"
            color="blue"
            icon="branding_watermark"
            onClick={() => this.openGroupModal(record)}
          />
        </Tooltip>
      );
    }
  };

  renderResultBtn = ({ record }) => {
    const executionId = record.get('executionId');
    if (record.get('status') === 'SUCCESS' && record.get('taskDetail').type === 'TASK') {
      return (
        <Tooltip title={$l('task.execution.download')}>
          <Button
            funcType="flat"
            color="blue"
            icon="cloud_download"
            href={`/sys/task/execution/resultDownload?executionId=${executionId}`}
            download
          />
        </Tooltip>
      );
    }
  };

  renderCancelBtn = ({ record }) => {
    if (record.get('status') === 'EXECUTING' || record.get('status') === 'READY') {
      return (
        <Tooltip title="task.execution.cancel">
          <Button
            funcType="flat"
            color="blue"
            icon="cloud_download"
            onClick={() => this.cancleExecution(record)}
          />
        </Tooltip>
      );
    }
  };

  render() {
    return (
      <Content>
        <Table
          dataSet={this.executionDS}
          queryFieldsLimit={4}
        >
          <Column name="executionNumber" sortable />
          <Column name="code" />
          <Column name="name" />
          <Column name="type" />
          <Column name="status" renderer={statusRender} />
          <Column name="startTime" sortable />
          <Column name="endTime" sortable />
          <Column
            name="edit"
            header={$l('hap.action')}
            lock="right"
            width={100}
            renderer={this.renderBtn}
          />
          <Column
            header={$l('task.execution.result.download')}
            renderer={this.renderResultBtn}
          />
          <Column
            header={$l('task.execution.cancel')}
            width={100}
            renderer={this.renderCancelBtn}
          />
        </Table>
      </Content>
    );
  }
}
