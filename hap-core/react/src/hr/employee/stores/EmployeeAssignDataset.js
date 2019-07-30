import { $l } from '@choerodon/boot';

export default {
  name: 'EmployeeAssign',
  autoQuery: true,
  primaryKey: 'assignId',
  paging: false,
  fields: [
    { name: 'assignId', type: 'number', label: '岗位分配编号' },
    { name: 'employeeId', type: 'number', label: $l('position.name') },
    { name: 'positionId', type: 'number', label: '岗位编号' },
    { name: 'positionName', type: 'string', label: $l('position.name') },
    { name: 'unitName', type: 'string', label: $l('hrorgunit.name') },
    { name: 'primaryPositionFlag', type: 'string', label: $l('employee.ismainposition'), defaultValue: 'N' },
    { name: 'enabledFlag', type: 'string', label: $l('employee.enabledflag'), defaultValue: 'Y' },
  ],
};
