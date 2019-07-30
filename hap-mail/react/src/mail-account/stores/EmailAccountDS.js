import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'accountId',
  name: 'EmailAccount',
  pageSize: 5,
  fields: [
    { name: 'accountId', type: 'number' },
    { name: 'configId', type: 'number' },
    { name: 'accountCode', type: 'string', label: $l('messageemailconfig.accountcode'), required: true, unique: true },
    { name: 'userName', type: 'string', label: $l('messageemailaccount.username'), required: true },
    { name: 'password', type: 'string', label: $l('messageemailaccount.password'), required: true },
    { name: 'objectVersionNumber', type: 'number' },
  ],
};
