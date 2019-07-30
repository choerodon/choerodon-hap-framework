import React, { PureComponent } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { ContentPro as Content } from '@choerodon/boot';
import ReportDataSet from './stores/ReportDataSet';
import ParameterDataSet from './stores/ParameterDataSet';
import Report from './view/Report';

export default class Index extends PureComponent {
  reportDS = new DataSet(ReportDataSet);

  parameterDS = new DataSet(ParameterDataSet);

  render() {
    return (
      <Content>
        <Report reportDS={this.reportDS} parameterDS={this.parameterDS} />
      </Content>
    );
  }
}
