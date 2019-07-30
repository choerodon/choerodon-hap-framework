import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'detailId',
  name: 'PermissionRuleDetailRole',
  pageSize: 10,
  fields: [
    { name: 'detailId', type: 'number', label: $l('datapermissionruledetail.detailid') },
    { name: 'ruleId', type: 'string', label: $l('datapermissionruledetail.ruleid') },
    { name: 'permissionField', type: 'object', lovCode: 'LOV_ROLE', textField: 'roleName', label: $l('datapermissionruledetail.permissionfieldvalue') },
    { name: 'permissionFieldValue', type: 'string', bind: 'permissionField.id', label: $l('datapermissionruledetail.permissionfieldvalue') },
    { name: 'permissionFieldName', type: 'string', bind: 'permissionField.name', label: $l('datapermissionruledetail.permissionfieldvalue') },
    { name: 'permissionFieldSqlValue', type: 'string', label: $l('datapermissionruledetail.permissionfieldsqlvalue') },
  ],
};
