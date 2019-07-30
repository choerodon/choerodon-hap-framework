import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'configId',
  name: 'EmailConfig',
  pageSize: 5,
  autoQuery: true,
  fields: [
    { name: 'configId', type: 'number' },
    { name: 'configCode', type: 'string', label: $l('messageemailconfig.configcode'), required: true, unique: true },
    { name: 'description', type: 'string', label: $l('messageemailconfig.description') },
    { name: 'host', type: 'string', label: $l('messageemailconfig.host'), required: true },
    { name: 'port', type: 'number', label: $l('messageemailconfig.port'), required: true },
    { name: 'useWhiteList', type: 'boolean', label: $l('messageemailconfig.usewhitelist'), defaultValue: 'N', trueValue: 'Y', falseValue: 'N' },
    { name: 'enable', type: 'boolean', label: $l('hap.enableflag'), required: true, defaultValue: 'Y', trueValue: 'Y', falseValue: 'N' },
    { name: 'objectVersionNumber', type: 'number' },
  ],
  queryFields: [
    { name: 'configCode', type: 'string', label: $l('messageemailconfig.configcode') },
    { name: 'description', type: 'string', label: $l('messageemailconfig.description') },
  ],
};
