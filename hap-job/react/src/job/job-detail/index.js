import React, { PureComponent } from 'react';
import { ContentPro as Content } from '@choerodon/boot';
import { DataSet } from 'choerodon-ui/pro';
import { JobInfoDetailDataSet, JobDataDataSet } from './stores';
import JobDetail from './view/JobDetail';
import './index.less';

export default class Index extends PureComponent {
  jobInfoDetailDataSet = new DataSet(JobInfoDetailDataSet);

  jobDataDataSet = new DataSet(JobDataDataSet);

  constructor(props) {
    super(props);
    this.jobDataDataSet.bind(this.jobInfoDetailDataSet, 'jobDatas');
  }

  render() {
    return (
      <Content>
        <JobDetail jobInfo={this.jobInfoDetailDataSet} jobData={this.jobDataDataSet} />
      </Content>
    );
  }
}
