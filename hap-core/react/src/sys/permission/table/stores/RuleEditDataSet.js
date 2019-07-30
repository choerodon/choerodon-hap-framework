import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'tableRuleId',
  name: 'PermissionTableRule',
  pageSize: 10,
  fields: [
    { name: 'tableRuleId', type: 'number' },
    {
      name: 'rule',
      type: 'object',
      lovCode: 'LOV_DATA_PERMISSION_RULE',
      textField: 'ruleName',
      label: $l('datapermissionrule.rulename'),
      required: true,
    },
    { name: 'ruleId', type: 'number', bind: 'rule.ruleId', unique: 'GROUP' },
    { name: 'ruleName', type: 'string', bind: 'rule.ruleName', required: true },
    { name: 'tableId', type: 'number', label: $l('datapermissiontable.tableid'), unique: 'GROUP' },
    {
      name: 'tableField',
      type: 'string',
      label: $l('datapermissiontablerule.tablefield'),
      textField: 'columnName',
      valueField: 'columnName',
      unique: 'GROUP',
      required: true,
    },
  ],
};
