import React, { PureComponent } from 'react';
import { $l, ContentPro as Content } from '@choerodon/boot';
import { Button, DataSet, Modal, Table, Tooltip } from 'choerodon-ui/pro';
import TaskExecuteDS from './stores/TaskExecuteDS';
import ParameterConfigsDS from './stores/ParameterConfigsDS';
import TaskExecuteDetailModal from './view/TaskExecuteDetailModal';
import './index.less';

const { Column } = Table;
const modalKey = Modal.key();

export default class Index extends PureComponent {
  constructor(props) {
    super(props);
    this.taskExecuteDS = new DataSet(TaskExecuteDS);
    this.taskExecuteDS.query();
  }

  async openTaskDetailModal(record) {
    const childrenTasks = new DataSet(TaskExecuteDS);
    const parameterConfigs = new DataSet(ParameterConfigsDS);

    const ds = new DataSet({
      ...TaskExecuteDS,
      children: {
        childrenTasks,
        parameterConfigs,
      },
    });
    ds.queryUrl = '/sys/task/detail/detail';
    ds.queryParameter.taskId = record.get('taskId');
    ds.queryParameter.ids = record.get('ids');
    await ds.query();
    if (ds.length !== 0) {
      Modal.open({
        key: modalKey,
        title: $l('task.execute.form'),
        drawer: true,
        children: (
          <TaskExecuteDetailModal dataset={ds} childrenTasks={childrenTasks} />
        ),
        style: { width: 600 },
        footer: null,
      });
    }
  }

  render() {
    return (
      <Content>
        <Table
          dataSet={this.taskExecuteDS}
          queryFieldsLimit={3}
        >
          <Column name="code" />
          <Column name="name" />
          <Column name="type" />
          <Column name="description" />
          <Column
            name="edit"
            header={$l('hap.action')}
            lock="right"
            width={100}
            renderer={({ record }) => (
              <Tooltip title={$l('parameterconfig.execute')}>
                <Button
                  funcType="flat"
                  icon="branding_watermark"
                  onClick={() => this.openTaskDetailModal(record)}
                />
              </Tooltip>
            )}
          />
        </Table>
      </Content>
    );
  }
}
