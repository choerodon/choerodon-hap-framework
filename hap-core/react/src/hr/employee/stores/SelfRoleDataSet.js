import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'id',
  name: 'AccountSelfRole',
  paging: false,
  fields: [
    { name: 'id', type: 'number' },
    { name: 'code', type: 'string', label: $l('role.rolecode') },
    { name: 'name', type: 'intl', label: $l('role.rolename') },
    { name: 'description', type: 'intl', label: $l('role.roledescription') },
    { name: 'memberRoleId', type: 'number' },
  ],
};
