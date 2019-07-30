import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'headerId',
  name: 'CodeRuleHeader',
  autoQuery: true,
  pageSize: 20,
  fields: [
    { name: 'ruleCode', type: 'string', label: $l('coderulesheader.rulecode'), required: true, unique: true },
    { name: 'ruleName', type: 'string', label: $l('coderulesheader.rulename') },
    { name: 'description', type: 'string', label: $l('hap.description') },
    { name: 'enableFlag', type: 'boolean', label: $l('hap.enableflag'), trueValue: 'Y', falseValue: 'N', defaultValue: 'Y' },
  ],
  queryFields: [
    { name: 'ruleCode', type: 'string', label: $l('coderulesheader.rulecode') },
    { name: 'ruleName', type: 'string', label: $l('coderulesheader.rulename') },
  ],
  events: {
    submit: (dataItem) => {
      const { lines } = dataItem.data[0];
      if (lines) {
        for (let i = 0; i < lines.length; i += 1) {
          const data = lines[i];
          // 如果修改了startValue 重置currentValue
          if (data.filedType === 'SEQUENCE' && (!data.oldStartValue || data.oldStartValue !== data.startValue)) {
            data.currentValue = data.startValue - 1;
            break;
          }
        }
      }
    },
  },
};
