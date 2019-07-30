import {$l} from '@choerodon/boot';

export default {
    primaryKey: 'ruleId',
    name: 'PermissionRule',
    pageSize: 20,
    autoQuery: true,
    fields: [
        {name: 'ruleId', type: 'number', label: $l('datapermissionrule.ruleid')},
        {name: 'ruleCode', type: 'string', label: $l('datapermissionrule.rulecode'), unique: true, required: true},
        {name: 'ruleName', type: 'string', label: $l('datapermissionrule.rulename'), required: true},
        {
            name: 'permissionField',
            type: 'string',
            label: $l('datapermissionrule.permissionfield'),
            lookupCode: 'SYS.AUTHORITY_SEGMENT',
            required: true
        },
    ],
    queryFields: [
        {name: 'ruleCode', type: 'string', label: $l('datapermissionrule.rulecode')},
        {name: 'ruleName', type: 'string', label: $l('datapermissionrule.rulename')},
    ],
};
