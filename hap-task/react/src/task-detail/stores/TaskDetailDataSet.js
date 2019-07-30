import { $l } from '@choerodon/boot';

function dynamicPropsTaskClass({ dataSet, record, name }) {
  return {
    required: record.get('type') === 'TASK',
  };
}

export default {
  name: 'TaskDetail',
  primaryKey: 'taskId',
  pageSize: 20,
  fields: [
    { name: 'taskId', type: 'number', label: 'taskId' },
    { name: 'type', type: 'string', label: $l('taskdetail.type'), defaultValue: 'TASK', lookupCode: 'TASK.TYPE' },
    { name: 'code', type: 'string', label: $l('taskdetail.code'), required: true, unique: true },
    { name: 'name', type: 'intl', label: $l('taskdetail.name'), required: true },
    { name: 'taskClass', type: 'string', label: $l('taskdetail.taskclass'), dynamicProps: dynamicPropsTaskClass },
    { name: 'description', type: 'intl', label: $l('taskdetail.description'), required: true },
    { name: 'startDate', type: 'date', label: $l('taskdetail.startdate'), defaultValue: null },
    { name: 'endDate', type: 'date', label: $l('taskdetail.enddate'), defaultValue: null },
    { name: 'ids', type: 'string', label: '子任务id' },
    { name: 'order', type: 'string', label: $l('taskdetail.executionorder') },
  ],
  queryFields: [
    { name: 'code', type: 'string', label: $l('taskdetail.code') },
    { name: 'name', type: 'string', label: $l('taskdetail.name') },
    { name: 'type', type: 'string', label: $l('taskdetail.type'), lookupCode: 'TASK.TYPE' },
  ],
};
