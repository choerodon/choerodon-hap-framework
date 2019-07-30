import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'propertyId',
  name: 'EmailProperty',
  pageSize: 5,
  fields: [
    { name: 'propertyId', type: 'number' },
    { name: 'configId', type: 'number' },
    { name: 'propertyName', type: 'string', label: $l('messageemailproperty.propertyname') },
    { name: 'propertyCode', type: 'string', label: $l('messageemailproperty.propertycode') },
    { name: 'objectVersionNumber', type: 'number' },
  ],
};
