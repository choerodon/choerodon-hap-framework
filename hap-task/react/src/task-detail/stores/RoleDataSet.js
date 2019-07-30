import { $l } from '@choerodon/boot';

export default {
  queryUrl: '/sys/task/assign/selectUnbound/v2',
  paging: false,
  fields: [
    { name: 'id', type: 'number' },
    { name: 'code', type: 'string', label: $l('taskdetail.role.code') },
    { name: 'name', type: 'intl', label: $l('taskdetail.role.name') },
    { name: 'description', type: 'string', label: $l('taskdetail.role.description') },
    { name: 'ids', type: 'string' },
  ],
};
