import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'userId',
  name: 'EmployeeUser',
  fields: [
    { name: 'userId', type: 'number', label: '用户ID' },
    { name: 'userName', type: 'string', label: $l('user.username'), required: true, unique: true },
    { name: 'email', type: 'email', label: $l('user.email'), required: true },
    { name: 'phone', type: 'string', label: $l('user.phone'), required: true },
    { name: 'startActiveDate', type: 'date', label: $l('user.startactivedate') },
    { name: 'endActiveDate', type: 'date', label: $l('user.endactivedate') },
    { name: 'status', type: 'string', label: $l('user.status'), lookupCode: 'SYS.USER_STATUS', defaultValue: 'ACTV', required: true },
    { name: 'description', type: 'string', label: $l('hap.description') },
    { name: 'employee', type: 'object', lovCode: 'LOV_EMPLOYEE', textField: 'employeeCode', label: $l('employee.employeecode') },
    { name: 'employeeId', bind: 'employee.employeeId', type: 'number', label: 'employeeId' },
    { name: 'employeeCode', bind: 'employee.employeeCode', type: 'string', label: $l('employee.employeecode') },
    { name: 'employeeName', bind: 'employee.name', type: 'string', label: $l('employee.name') },
  ],
};
