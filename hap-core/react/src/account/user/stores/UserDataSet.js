import {$l} from '@choerodon/boot';

function phoneValidator(value) {
    const pattern = /^1(3|4|5|6|7|8|9)\d{9}$/;
    if (pattern.test(value)) {
        return true;
    }
    return '请输入有效的手机号码！';
}


export default {
    primaryKey: 'userId',
    name: 'AccountUser',
    pageSize: 20,
    autoQuery: true,
    fields: [
        {name: 'userId', type: 'number'},
        {name: 'userName', type: 'string', label: $l('user.username'), required: true, unique: true},
        {name: 'email', type: 'email', label: $l('user.email'), required: true},
        {name: 'phone', type: 'string', label: $l('user.phone'), required: true, validator: phoneValidator},
        {name: 'startActiveDate', type: 'date', label: $l('user.startactivedate'), defaultValue: new Date()},
        {name: 'endActiveDate', type: 'date', label: $l('user.endactivedate')},
        {name: 'status', type: 'string', label: $l('user.status'), lookupCode: 'SYS.USER_STATUS', defaultValue: 'ACTV'},
        {name: 'description', type: 'string', label: $l('hap.description')},
        {
            name: 'employee',
            type: 'object',
            lovCode: 'LOV_EMPLOYEE',
            textField: 'employeeCode',
            label: $l('employee.employeecode')
        },
        {name: 'employeeId', bind: 'employee.employeeId', type: 'number', label: 'employeeId'},
        {name: 'employeeCode', bind: 'employee.employeeCode', type: 'string', label: $l('employee.employeecode')},
        {name: 'employeeName', bind: 'employee.name', type: 'string', label: $l('employee.name')},
    ],
    queryFields: [
        {name: 'role', type: 'object', textField: 'name', lovCode: 'LOV_ROLE', label: $l('role.rolename')},
        {name: 'roleId', bind: 'role.id', type: 'number'},
        {name: 'roleName', bind: 'role.name', type: 'string'},
        {name: 'userName', type: 'string', label: $l('user.username')},
        {name: 'employeeCode', type: 'string', label: $l('employee.employeecode')},
        {name: 'employeeName', type: 'string', label: $l('employee.name')},
        {name: 'status', type: 'string', lookupCode: 'SYS.USER_STATUS', label: $l('user.status')},
    ],
};
