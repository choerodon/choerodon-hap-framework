import { $l } from '@choerodon/boot';

export default {
  fields: [
    { name: 'taskId', type: 'number', label: 'taskId' },
    { name: 'type', type: 'string', label: $l('taskdetail.type'), defaultValue: 'TASK', lookupCode: 'TASK.TYPE' },
    { name: 'code', type: 'string', label: $l('taskdetail.code'), required: true },
    { name: 'name', type: 'intl', label: $l('taskdetail.name'), required: true },
    { name: 'taskClass', type: 'string', label: $l('taskdetail.taskclass'), required: true },
    { name: 'description', type: 'intl', label: $l('taskdetail.description') },
    { name: 'startDate', type: 'date', label: $l('taskdetail.startdate'), defaultValue: null },
    { name: 'endDate', type: 'date', label: $l('taskdetail.enddate'), defaultValue: null },
    { name: 'ids', type: 'string', label: 'ids' },
    { name: 'order', type: 'string', label: $l('taskdetail.executionorder'), defaultValue: 1 },
  ],
};
