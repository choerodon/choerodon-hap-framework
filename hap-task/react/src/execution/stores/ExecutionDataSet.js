import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'executionId',
  autoQuery: true,
  pageSize: 20,
  name: 'TaskExecution',
  selection: false,
  autoCreate: true,
  fields: [
    { name: 'executionNumber', type: 'string', label: $l('task.execution.executionnumber') },
    { name: 'type', type: 'string', label: $l('taskdetail.type'), lookupCode: 'TASK.TYPE' },
    { name: 'endTime', type: 'dateTime', label: $l('taskdetail.endtime') },
    { name: 'startTime', type: 'dateTime', label: $l('taskdetail.starttime') },
    { name: 'status', type: 'string', label: $l('task.execution.status'), lookupCode: 'TASK.EXECUTE.STATUS' },
    { name: 'code', type: 'string', label: $l('taskdetail.code'), bind: 'taskDetail.code' },
    { name: 'name', type: 'string', label: $l('taskdetail.name') },
    { name: 'taskDetail', type: 'object', label: '' },
    { name: 'taskClass', type: 'string', bind: 'taskDetail.taskClass', label: $l('taskdetail.taskclass') },
    { name: 'description', type: 'string', bind: 'taskDetail.description', label: $l('taskdetail.description') },
    { name: 'userId', type: 'number', label: $l('task.execution.username') },
    { name: 'lastExecuteDate', type: 'dateTime', label: $l('jobrunninginfodto.previousfiretime') },
    { name: 'executionDescription', type: 'string', label: $l('task.execution.description') },

  ],
  queryFields: [
    { name: 'executionNumber', type: 'string', label: $l('task.execution.executionnumber') },
    { name: 'name', type: 'string', label: $l('taskdetail.name') },
    { name: 'type', type: 'string', label: $l('taskdetail.type'), lookupCode: 'TASK.TYPE' },
    { name: 'status', type: 'string', label: $l('task.execution.status'), lookupCode: 'TASK.EXECUTE.STATUS' },
  ],
};
