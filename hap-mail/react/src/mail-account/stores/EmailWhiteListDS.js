import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'id',
  name: 'EmailWhiteList',
  pageSize: 5,
  fields: [
    { name: 'id', type: 'number', label: 'id' },
    { name: 'configId', type: 'number' },
    { name: 'address', type: 'string', label: $l('messageemailconfig.address') },
    { name: 'objectVersionNumber', type: 'number' },
  ],
};
