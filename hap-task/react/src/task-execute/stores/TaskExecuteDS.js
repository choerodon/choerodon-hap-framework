import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'taskId',
  name: 'TaskExecute',
  autoQuery: false,
  selection: false,
  pageSize: 20,
  fields: [
    { name: 'code', type: 'string', label: $l('taskdetail.code') },
    { name: 'name', type: 'string', label: $l('taskdetail.name') },
    { name: 'type', type: 'string', label: $l('taskdetail.type'), lookupCode: 'TASK.TYPE' },
    { name: 'description', type: 'string', label: $l('taskdetail.description') },
    { name: 'startDate', type: 'dateTime' },
    { name: 'endDate', type: 'dateTime' },
  ],
  queryFields: [
    { name: 'code', type: 'string', label: $l('taskdetail.code') },
    { name: 'name', type: 'string', label: $l('taskdetail.name') },
    { name: 'type', type: 'string', label: $l('taskdetail.type'), lookupCode: 'TASK.TYPE' },
  ],
};
