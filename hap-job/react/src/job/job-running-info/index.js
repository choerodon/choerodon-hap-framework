import React, { PureComponent } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { ContentPro as Content } from '@choerodon/boot';
import JobRunningInfoDS from './stores/JobRunningInfoDS';

const { Column } = Table;

export default class Index extends PureComponent {
  jobRunningInfoDS = new DataSet(JobRunningInfoDS);

  jobStatus({ record }) {
    if (record.get('jobStatus') === 'Finish') {
      return <span style={{ color: 'green' }}>FINISH</span>;
    } else if (record.get('jobStatus') === 'Failed') {
      return <span style={{ color: 'red' }}>FAILED</span>;
    } else if (record.get('jobStatus') === 'Vetoed') {
      return <span style={{ color: 'orange' }}>VETOED</span>;
    }
  }

  render() {
    return (
      <Content>
        <Table
          dataSet={this.jobRunningInfoDS}
          queryFieldsLimit={3}
        >
          <Column name="jobName" />
          <Column name="jobGroup" width={140} />
          <Column name="ipAddress" width={160} />
          <Column name="jobStatus" renderer={this.jobStatus} width={100} />
          <Column name="executionSummary" />
          <Column name="previousFireTime" align="center" />
          <Column name="scheduledFireTime" align="center" />
          <Column name="nextFireTime" align="center" />
          <Column name="fireTime" align="center" />
        </Table>
      </Content>
    );
  }

};
