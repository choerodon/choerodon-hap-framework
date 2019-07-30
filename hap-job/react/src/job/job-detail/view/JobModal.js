import React from 'react';
import { observer } from 'mobx-react';
import moment from 'moment';
import { CheckBox, DatePicker, EmailField, Form, NumberField, Table, Tabs, TextField, Lov } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';

const { Column } = Table;
const { TabPane } = Tabs;
const JOB_DATA_TYPE_MAP = {
  job_internal_email_template: 'job_internal_email_template',
  job_internal_emailAddress: 'job_internal_emailAddress',
  job_internal_notification: 'job_internal_notification',
  job_internal_email_account: 'job_internal_email_account',
};

export default observer(({ operation, job, jobInfo, jobData }) => {
  /**
   * 查找 JobInfo 对应字段值的 index
   * @param fieldName 查找的字段的值
   * @returns {*} index 值
   */
  function findIndexFromJobData(data, fieldValue) {
    const index = data.findIndex(r => r.get('name') === fieldValue);
    return index;
  }

  /**
   * 过滤在 Table 中显示的值,只显示 name 字段中值不属于 JOB_DATA_TYPE_MAP
   * @param record
   * @returns {boolean}
   */
  function filterJobData(record) {
    return !(record.get('name') in JOB_DATA_TYPE_MAP);
  }

  function startTimeMax() {
    const { current } = jobInfo;
    if (current) {
      const time = current.get('endTime');
      if (time) {
        return moment(time);
      }
    }
  }

  function endTimeMin() {
    const { current } = jobInfo;
    if (current) {
      const time = current.get('startTime');
      if (time) {
        return moment(time);
      }
    }
  }

  function renderJobTypeColumn() {
    if (job === 'simple') {
      return [
        <NumberField
          name="repeatInterval"
          colSpan={2}
          step={1}
          min={0}
          disabled={operation === 'view'}
        />,
        <NumberField name="repeatCount" colSpan={2} disabled={operation === 'view'} />,
        <NumberField name="triggerPriority" colSpan={2} disabled={operation === 'view'} />,
      ];
    }
    if (job === 'cron') {
      return [
        <TextField name="cronExpression" colSpan={3} disabled={operation === 'view'} />,
        <NumberField name="triggerPriority" colSpan={3} disabled={operation === 'view'} />,
      ];
    }
  }

  function renderJobTimeColumn() {
    if (operation === 'view') {
      return ([
        <DatePicker name="previousFireTime" mode="dateTime" colSpan={3} disabled={operation === 'view'} />,
        <DatePicker name="scheduledFireTime" mode="dateTime" colSpan={3} disabled={operation === 'view'} />,
        <DatePicker name="nextFireTime" mode="dateTime" colSpan={3} disabled={operation === 'view'} />,
        <DatePicker name="fireTime" mode="dateTime" colSpan={3} disabled={operation === 'view'} />,
      ]);
    } else {
      return [];
    }
  }

  function renderTableColumn() {
    if (operation === 'new') {
      return (
        <Lov
          label={$l('job.infodetail.templatecode')}
          name="template"
          dataSet={jobData}
          dataIndex={findIndexFromJobData(jobData, JOB_DATA_TYPE_MAP.job_internal_email_template)}
          help={$l('sys.job.emailtemplate.desciption')}
        />
      );
    } else {
      return (
        <TextField
          label={$l('job.infodetail.templatecode')}
          name="value"
          dataSet={jobData}
          dataIndex={findIndexFromJobData(jobData, JOB_DATA_TYPE_MAP.job_internal_email_template)}
          disabled
        />
      );
    }
  }

  return (
    <Tabs defaultActiveKey="1">
      <TabPane tab={$l('job.infodetail.jobinfo')} key="1">
        <Form labelWidth={135} columns={6} dataSet={jobInfo}>
          <TextField name="jobName" colSpan={3} disabled={operation === 'view'} />
          <TextField name="jobGroup" colSpan={3} disabled={operation === 'view'} />

          <TextField name="description" colSpan={6} disabled={operation === 'view'} />

          <TextField name="jobClassName" colSpan={6} disabled={operation === 'view'} />

          <DatePicker
            name="startTime"
            mode="dateTime"
            max={startTimeMax()}
            colSpan={3}
            disabled={operation === 'view'}
          />
          <DatePicker name="endTime" mode="dateTime" min={endTimeMin()} colSpan={3} disabled={operation === 'view'} />

          {renderJobTimeColumn().filter(v => v)}
          {renderJobTypeColumn()}
        </Form>

        <Table dataSet={jobData} filter={record => filterJobData(record)} buttons={operation === 'view' ? null : ['add', 'delete']}>
          <Column
            name="name"
            editor={() => {
              if (operation === 'new') {
                return <TextField />;
              } else {
                return null;
              }
            }}
          />
          <Column
            name="value"
            editor={() => {
              if (operation === 'new') {
                return <TextField />;
              } else {
                return null;
              }
            }}
          />
        </Table>
      </TabPane>
      <TabPane tab={$l('job.infodetail.notification')} key="2">
        <Form labelWidth={140} style={{ width: 450 }}>
          <CheckBox
            label={$l('job.infodetail.enable_notification')}
            name="value"
            dataSet={jobData}
            dataIndex={findIndexFromJobData(jobData, JOB_DATA_TYPE_MAP.job_internal_notification)}
            value="true"
            unCheckedValue="false"
            disabled={operation === 'view'}
          />
          <EmailField
            label={$l('job.infodetail.email_address')}
            name="value"
            dataSet={jobData}
            dataIndex={findIndexFromJobData(jobData, JOB_DATA_TYPE_MAP.job_internal_emailAddress)}
            disabled={operation === 'view'}
          />
          {renderTableColumn()}
        </Form>
      </TabPane>
    </Tabs>
  );
});
