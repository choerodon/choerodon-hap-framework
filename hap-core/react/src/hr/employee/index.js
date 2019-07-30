import React, { PureComponent } from 'react';
import { ContentPro as Content } from '@choerodon/boot';
import { DataSet } from 'choerodon-ui/pro';
import { EmployeeDataSet, EmployeeAssignDataSet, SelfRoleDataSet, UserDataSet, AddRoleDataSet, PositionDataSet } from './stores';
import Employee from './view/Employee';

export default class Index extends PureComponent {
  employeeAssignDataSet = new DataSet(EmployeeAssignDataSet);

  employeeDataSet = new DataSet(EmployeeDataSet);

  selfRoleDataSet = new DataSet(SelfRoleDataSet);

  userDataSet = new DataSet(UserDataSet);

  addRoleDataSet = new DataSet(AddRoleDataSet);

  positionDataSet = new DataSet(PositionDataSet);


  constructor(props) {
    super(props);
    this.selfRoleDataSet.bind(this.userDataSet, 'userRoles');
  }

  render() {
    return (
      <Content>
        <Employee
          employeeAssignDataSet={this.employeeAssignDataSet}
          employeeDataSet={this.employeeDataSet}
          selfRoleDataSet={this.selfRoleDataSet}
          userDataSet={this.userDataSet}
          addRoleDataSet={this.addRoleDataSet}
          positionDataSet={this.positionDataSet}
        />
      </Content>
    );
  }
}
