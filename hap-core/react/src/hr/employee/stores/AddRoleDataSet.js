import { $l } from '@choerodon/boot';

export default {
  name: 'AccountAddRole',
  primaryKey: 'id',
  paging: false,
  fields: [
    { name: 'id', type: 'number' },
    { name: 'code', type: 'string', label: $l('role.rolecode') },
    { name: 'name', type: 'intl', label: $l('role.rolename') },
    { name: 'description', type: 'intl', label: $l('role.roledescription') },
  ],
  queryFields: [
    { name: 'code', type: 'string', label: $l('role.rolecode') },
    { name: 'name', type: 'string', label: $l('role.rolename') },
  ],
};
