import { $l } from '@choerodon/boot';

export default {
  autoQuery: true,
  paging: false,
  selection: false,
  fields: [
    { name: 'name', type: 'string', label: $l('task.execute.parameter') },
    { name: 'key', type: 'number', label: '' },
    { name: 'value', type: 'string', label: $l('task.execute.value') },
    { name: 'text', type: 'string', label: $l('task.execute.text') },
  ],
};
