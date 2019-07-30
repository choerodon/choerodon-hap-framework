import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'detailId',
  name: 'PermissionRuleDetailUser',
  pageSize: 10,
  fields: [
    { name: 'detailId', type: 'number', label: $l('datapermissionruledetail.detailid') },
    { name: 'ruleId', type: 'string', label: $l('datapermissionruledetail.ruleid') },
    { name: 'permissionField', type: 'object', lovCode: 'user_lov', textField: 'userName', label: $l('datapermissionruledetail.permissionfieldvalue') },
    { name: 'permissionFieldValue', type: 'string', bind: 'permissionField.userId', label: $l('datapermissionruledetail.permissionfieldvalue') },
    { name: 'permissionFieldName', type: 'string', bind: 'permissionField.userName', label: $l('datapermissionruledetail.permissionfieldvalue') },
    { name: 'permissionFieldSqlValue', type: 'string', label: $l('datapermissionruledetail.permissionfieldsqlvalue') },
  ],
};
