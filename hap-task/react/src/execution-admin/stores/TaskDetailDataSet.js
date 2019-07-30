import { $l } from '@choerodon/boot';

export default {
  autoQuery: true,
  pageSize: 20,
  name: 'TaskExecution',
  selection: false,
  fields: [
    { name: 'type', type: 'string', label: $l('taskdetail.type'), lookupCode: 'TASK.TYPE', bind: 'taskDetail.type' },
    { name: 'taskId', type: 'number', label: '' },
    { name: 'status', type: 'string', label: $l('task.execution.status'), lookupCode: 'TASK.EXECUTE.STATUS' },
    { name: 'code', type: 'string', label: $l('taskdetail.code'), bind: 'taskDetail.code' },
    { name: 'name', type: 'string', label: $l('taskdetail.name'), bind: 'taskDetail.name' },
    { name: 'taskClass', type: 'string', bind: 'taskDetail.taskClass', label: $l('taskdetail.taskclass') },

  ],
};
