import { $l } from '@choerodon/boot';

const JOB_DATA_TYPE_MAP = {
  job_internal_email_template: 'job_internal_email_template',
  job_internal_emailAddress: 'job_internal_emailAddress',
  job_internal_notification: 'job_internal_notification',
  job_internal_email_account: 'job_internal_email_account',
};

function dynamicFieldProperties({ dataSet, record, name }) {
  if (name === 'job_internal_email_template') {
    return {
      textFiled: 'templateCode',
      lovCode: 'MESSAGE_TEMPLATE',
    };
  }
}

export default {
  autoQuery: false,
  fields: [
    { name: 'name', type: 'string', label: $l('job.attributename') },
    { name: 'value', type: 'string', label: $l('job.attributevalue'), dynamicProps: dynamicFieldProperties },
    { name: 'template', type: 'object', label: $l('job.infodetail.jobinfo'), textField: 'templateCode', lovCode: 'MESSAGE_TEMPLATE' },
  ],
  events: {
    update: ({ dataSet, record, name, value, oldValue }) => {
      if (name === 'template') {
        record.set('value', value.templateCode);
      }
    },
    load: ({ dataSet }) => {
      const dataList = dataSet.data;
      if (dataList && dataList.findIndex(r => r.get('preferences') === JOB_DATA_TYPE_MAP.job_internal_email_template) === -1) {
        dataSet.create({ name: JOB_DATA_TYPE_MAP.job_internal_email_template, value: undefined });
      }
      if (dataList && dataList.findIndex(r => r.get('preferences') === JOB_DATA_TYPE_MAP.job_internal_emailAddress) === -1) {
        dataSet.create({ name: JOB_DATA_TYPE_MAP.job_internal_emailAddress, value: undefined });
      }
      if (dataList && dataList.findIndex(r => r.get('preferences') === JOB_DATA_TYPE_MAP.job_internal_notification) === -1) {
        dataSet.create({ name: JOB_DATA_TYPE_MAP.job_internal_notification, value: undefined });
      }
      if (dataList && dataList.findIndex(r => r.get('preferences') === JOB_DATA_TYPE_MAP.job_internal_email_account) === -1) {
        dataSet.create({ name: JOB_DATA_TYPE_MAP.job_internal_email_account, value: undefined });
      }
    },
  },
};
