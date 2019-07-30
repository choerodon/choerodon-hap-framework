import { $l } from '@choerodon/boot';
import { Stores } from 'choerodon-ui/pro';

export default {
  primaryKey: 'codeValueId',
  name: 'CodeValue',
  paging: false,
  fields: [
    { name: 'codeId', type: 'number' },
    { name: 'value', type: 'string', label: $l('codevalue.value'), required: true, unique: true },
    { name: 'meaning', type: 'intl', label: $l('codevalue.meaning'), required: true },
    { name: 'description', type: 'intl', label: $l('codevalue.description') },
    { name: 'orderSeq', type: 'number', label: $l('codevalue.orderseq') },
    { name: 'codevalue', type: 'object', textField: 'meaning', label: $l('codevalue.parentcodevalueid'), lovCode: 'LOV_CODE_VALUE' },
    { name: 'parentCodeValueMeaning', bind: 'codevalue.meaning', type: 'string' },
    { name: 'parentCodeValueId', bind: 'codevalue.codeValueId', type: 'string' },
    { name: 'parentCodeValue', bind: 'codevalue.value', type: 'string' },
    { name: 'tag', type: 'string', label: $l('codevalue.tag') },
    { name: 'enabledFlag', type: 'boolean', label: $l('hap.enableflag'), defaultValue: 'Y', trueValue: 'Y', falseValue: 'N' },
  ],
  events: {
    submitSuccess: ({ dataSet, data }) => {
      Stores.LookupCodeStore.clearCache();
    },
  },
};
