import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'parameterId',
  name: 'ParameterConfig',
  autoQuery: true,
  paging: false,
  fields: [
    { name: 'code', type: 'string' },
    { name: 'targetId', type: 'string' },
    {
      name: 'display',
      type: 'string',
      label: $l('parameterconfig.display'),
      lookupCode: 'PARAMETER.DISPLAY',
      required: true,
    },
    {
      name: 'tableFieldName',
      type: 'string',
      textFiled: 'meaning',
      valueField: 'value',
      label: $l('parameterconfig.tablefieldname'),
      required: true,
      unique: true,
    },
    { name: 'title', type: 'string', label: $l('parameterconfig.title'), required: true },
    { name: 'description', type: 'string', label: $l('parameterconfig.description') },
    { name: 'lineNumber', type: 'number', label: $l('parameterconfig.linenumber'), defaultValue: 1 },
    { name: 'required', type: 'boolean', label: $l('parameterconfig.required'), trueValue: 'Y', falseValue: 'N', defaultValue: 'N' },
    { name: 'readOnly', type: 'boolean', label: $l('parameterconfig.readonly'), trueValue: 'Y', falseValue: 'N', defaultValue: 'N' },
    { name: 'enabled', type: 'boolean', label: $l('parameterconfig.enabled'), trueValue: 'Y', falseValue: 'N', defaultValue: 'Y' },
    { name: 'extraAttribute.datePickerFrom', type: 'date' },
    { name: 'extraAttribute.datePickerTo', type: 'date' },
    { name: 'extraAttribute.codeValueField', type: 'string' },
  ],
  events: {
    load: (dataItem) => {
      const dataList = dataItem.dataSet.data;
      if (dataList.length) {
        dataList.forEach((item) => {
          let extraAttribute = item.get('extraAttribute') || {};
          // 将extraAttribute json字符串转换为json对象
          if (typeof (extraAttribute) === 'string') {
            extraAttribute = JSON.parse(item.get('extraAttribute')) || {};
            item.set('extraAttribute', extraAttribute);
          }
        });
      }
    },
    submit: (dataItem) => {
      const dataList = dataItem.data;
      if (dataList.length) {
        dataList.forEach((item) => {
          item.extraAttribute = item.extraAttribute || {};
          // 将extraAttribute json对象转换为json字符串
          if (typeof (item.extraAttribute) === 'object') {
            item.extraAttribute = JSON.stringify(item.extraAttribute);
          }
        });
      }
    },
  },
};
