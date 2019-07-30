import React, { PureComponent } from 'react';
import { DataSet, Tabs } from 'choerodon-ui/pro';
import { $l, ContentPro as Content } from '@choerodon/boot';
import OrgInfo from './view/OrgInfo';
import OrgStru from './view/OrgStru';
import OrgStruDataSet from './stores/OrgStruDataSet';
import OrgUnitDataSet from './stores/OrgUnitDataSet';

export default class Index extends PureComponent {
  orgStruDS = new DataSet(OrgStruDataSet);

  orgInfoDS = new DataSet({
    ...OrgUnitDataSet,
    events: {
      submitSuccess: () => {
        this.orgStruDS.query();
      },
    },
  });

  /**
   * 渲染Tabs
   */
  render() {
    return (
      <Content>
        <Tabs>
          <Tabs.TabPane tab={$l('hrorgunit.unitinfo')} key="1"><OrgInfo dataSet={this.orgInfoDS} /></Tabs.TabPane>
          <Tabs.TabPane tab={$l('hrorgunit.unittreelist')} key="2"><OrgStru dataSet={this.orgStruDS} /></Tabs.TabPane>
        </Tabs>
      </Content>
    );
  }
}
