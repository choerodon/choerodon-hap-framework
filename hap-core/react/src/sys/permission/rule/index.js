import React, { PureComponent } from 'react';
import { observer } from 'mobx-react';
import { DataSet } from 'choerodon-ui/pro';
import { Content } from '@choerodon/boot';
import { RuleDataSet, RuleAssignDataSet, RuleDetailUserDataSet, RuleDetailSQLDataSet, RuleDetailRoleDataSet } from './stores';
import Rule from './view/Rule';

@observer
export default class Index extends PureComponent {
  ruleDS = new DataSet(RuleDataSet);

  ruleAssignDS = new DataSet(RuleAssignDataSet);

  ruleDetailRoleDS = new DataSet(RuleDetailRoleDataSet);

  ruleDetailSqlDS = new DataSet(RuleDetailSQLDataSet);

  ruleDetailUserDS = new DataSet(RuleDetailUserDataSet);

  render() {
    return (
      <Content>
        <Rule
          ruleDS={this.ruleDS}
          ruleAssignDS={this.ruleAssignDS}
          ruleDetailRoleDS={this.ruleDetailRoleDS}
          ruleDetailSqlDS={this.ruleDetailSqlDS}
          ruleDetailUserDS={this.ruleDetailUserDS}
        />
      </Content>
    );
  }
}
