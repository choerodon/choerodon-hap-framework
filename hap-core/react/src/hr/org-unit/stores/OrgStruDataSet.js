import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'unitId',
  name: 'OrgUnit',
  autoQuery: true,
  parentField: 'parentId',
  idField: 'unitId',
  selection: false,
  fields: [
    { name: 'unitId', type: 'number' },
    { name: 'unitCode', type: 'string', label: $l('hrorgunit.unitcode') },
    { name: 'name', type: 'string', label: $l('hrorgunit.name') },
    { name: 'parentId', type: 'number' },
    { name: 'positionName', type: 'string', label: $l('hrorgunit.department_manager') },
    { name: 'description', type: 'string', label: $l('hrorgunit.description') },
  ],
};
