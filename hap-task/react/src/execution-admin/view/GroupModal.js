import React, { Component } from 'react';
import { Button, Form, Output, Table, Tooltip } from 'choerodon-ui/pro';
import { $l, axiosPro as axios } from '@choerodon/boot';
import { DataSet, Modal } from 'choerodon-ui/pro/lib/index';
import TaskGroupDetailModal from './TaskGroupDetailModal';
import ExecutionDataSet from '../stores/ExecutionDataSet';
import ParameterDataSet from '../stores/ParameterDataSet';


const { Column } = Table;
const modalKey = Modal.key();


export default (props) => {
  let modal;
  const executionDS1 = new DataSet(ExecutionDataSet);
  const parameterDS1 = new DataSet(ParameterDataSet);


  function setDataSetData(data) {
    parameterDS1.loadData(data);
  }

  function closeModal() {
    modal.close();
  }


  const openTaskEditModal = async (record) => {
    const executionId = record.get('executionId');
    const { taskExecutionDetail } = (await axios.post('/sys/task/execution/detail', { executionId })).rows[0];
    const { executionLog } = taskExecutionDetail;
    const { stacktrace } = taskExecutionDetail;
    setDataSetData(JSON.parse(taskExecutionDetail.parameter));
    props.taskGroupDS.current.set('executionNumber', props.executionDS.current.get('executionNumber'));
    modal = Modal.open({
      key: modalKey,
      drawer: true,
      destroyOnClose: true,
      children: (
        <TaskGroupDetailModal executionDS={props.taskGroupDS} parameterDS={parameterDS1} executionLog={executionLog} stacktrace={stacktrace} />
      ),
      footer: [
        <Button color="blue" icon="playlist_add" href={`/sys/task/execution/detailDownload?executionId=${executionId}`} download>
          {$l('task.execution.download')}
        </Button>,
        <Button style={{ marginLeft: 10 }} onClick={() => closeModal()}>
          {$l('hap.close')}
        </Button>,
      ],
      style: {
        width: 700,
      },
    });
  };

  let executionId;
  if (props.executionDS.current) {
    executionId = props.executionDS.current.get('executionId');
  }

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

  function renderBtn({ record }) {
    if (record.get('taskDetail').type === 'TASK') {
      return (
        <Tooltip title={$l('task.execution.detail')}>
          <Button
            funcType="flat"
            color="blue"
            icon="branding_watermark"
            onClick={() => openTaskEditModal(record)
            }
          />
        </Tooltip>
      );
    }
  }

  function renderResultBtn({ record }) {
    executionId = record.get('executionId');
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
  }

  const filterRow = record => props.taskGroupDS.indexOf(record) > 0;

  return (
    <div>
      <Form columns={2} labelWidth={100} style={{ marginRight: 80 }} dataSet={props.executionDS}>
        <Output name="executionNumber" />
        <Output name="status" />
        <Output name="code" />
        <Output name="name" />
        <Output name="startTime" />
        <Output name="endTime" />
        <Output name="lastExecuteDate" />
        <Output name="executionDescription" />
      </Form>
      <Table header={$l('task.group.subtask')} dataSet={props.taskGroupDS} filter={filterRow}>
        <Column name="code" />
        <Column name="name" />
        <Column name="status" renderer={statusRender} />
        <Column
          name="edit"
          header={$l('hap.action')}
          lock="right"
          renderer={renderBtn}
        />
        <Column
          header={$l('task.execution.result.download')}
          renderer={renderResultBtn}
        />
      </Table>
    </div>
  );
};
