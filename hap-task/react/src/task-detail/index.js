import React, { PureComponent } from 'react';
import { ContentPro as Content } from '@choerodon/boot';
import { DataSet } from 'choerodon-ui/pro';
import { RoleDataSet, TaskAssignDataSet, TaskDetailDataSet, ParameterDataSet, BaseTaskDetailDataSet } from './stores';
import TaskDetail from './view/TaskDetail';

export default class Index extends PureComponent {
  taskDetailDS = new DataSet(TaskDetailDataSet);

  childrenTaskDS = new DataSet(BaseTaskDetailDataSet);

  taskAssignDS = new DataSet(TaskAssignDataSet);

  roleDS = new DataSet(RoleDataSet);

  parameterDS = new DataSet(ParameterDataSet);

  unboundTaskDS = new DataSet(BaseTaskDetailDataSet);

  constructor(props) {
    super(props);
    this.taskDetailDS.query();
    this.childrenTaskDS.bind(this.taskDetailDS, 'childrenTasks');
    this.parameterDS.bind(this.taskDetailDS, 'parameterConfigs');
  }

  render() {
    return (
      <Content>
        <TaskDetail
          taskDetailDS={this.taskDetailDS}
          taskAssignDS={this.taskAssignDS}
          roleDS={this.roleDS}
          childrenTaskDS={this.childrenTaskDS}
          parameterDS={this.parameterDS}
          unboundTaskDS={this.unboundTaskDS}
        />
      </Content>
    );
  }
}
