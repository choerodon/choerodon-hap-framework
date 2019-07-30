import { $l } from '@choerodon/boot';

export default {
  name: 'TaskAssign',
  primaryKey: 'taskAssignId',
  fields: [
    { name: 'taskAssignId', type: 'number', label: 'taskAssignId' },
    { name: 'taskId', type: 'number', label: 'taskId', unique: 'group' },
    { name: 'assignId', type: 'number', label: 'assignId', unique: 'group' },
    { name: 'assignType', type: 'string', label: $l('taskdetail.type'), defaultValue: 'TASK', lookupCode: 'TASK.TYPE', unique: 'group' },
    { name: 'startDate', type: 'date', label: $l('taskdetail.startdate'), defaultValue: null },
    { name: 'endDate', type: 'date', label: $l('taskdetail.enddate'), defaultValue: null },
    { name: 'role', type: 'object' },
    { name: 'id', type: 'number', bind: 'role.id' },
    { name: 'name', type: 'string', bind: 'role.name', label: $l('taskdetail.role.name') },
    { name: 'code', type: 'string', bind: 'role.code', label: $l('taskdetail.role.code') },
    { name: 'description', type: 'string', bind: 'role.description', label: $l('taskdetail.role.description') },

  ],
};
