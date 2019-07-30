import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'detailId',
  name: 'PermissionRuleDetailSQL',
  pageSize: 10,
  fields: [
    { name: 'detailId', type: 'number', label: $l('datapermissionruledetail.detailid') },
    { name: 'ruleId', type: 'string', label: $l('datapermissionruledetail.ruleid') },
    { name: 'permissionFieldSqlValue', type: 'string', label: $l('datapermissionruledetail.permissionfieldsqlvalue') },
  ],
};
