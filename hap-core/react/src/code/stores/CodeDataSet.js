import { $l } from '@choerodon/boot';
import { Stores } from 'choerodon-ui/pro';

export default {
  primaryKey: 'codeId',
  autoQuery: true,
  pageSize: 10,
  name: 'Code',
  fields: [
    { name: 'codeId', type: 'number' },
    { name: 'code', type: 'string', label: $l('code.code'), unique: true, required: true },
    { name: 'description', type: 'intl', label: $l('code.description'), required: true },
    { name: 'enabledFlag', type: 'boolean', label: $l('hap.enableflag'), defaultValue: 'Y', trueValue: 'Y', falseValue: 'N' },
    { name: 'parent', type: 'object', textField: 'description', label: $l('code.parentcodeid'), lovCode: 'LOV_CODE_ID' },
    { name: 'parentCodeDescription', bind: 'parent.description', type: 'string' },
    { name: 'parentCodeId', bind: 'parent.codeId', type: 'string' },
    { name: 'type', type: 'string', defaultValue: 'USER' },
  ],
  queryFields: [
    { name: 'code', type: 'string', label: $l('code.code') },
    { name: 'description', type: 'string', label: $l('code.description') },
  ],
  events: {
    submitSuccess: ({ dataSet, data }) => {
      Stores.LookupCodeStore.clearCache();
    },
  },
};
