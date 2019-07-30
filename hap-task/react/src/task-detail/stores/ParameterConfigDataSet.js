import { $l } from '@choerodon/boot';

export default {
  autoCreate: true,
  fields: [
    { name: 'datePickerFrom', type: 'date', label: $l('parameterconfig.datepickerfrom') },
    { name: 'datePickerTo', type: 'date', label: $l('parameterconfig.datepickerfrom') },
    {
      name: 'sourceType',
      type: 'string',
      textFiled: 'meaning',
      valueField: 'value',
      label: $l('parameterconfig.sourcetype'),
      lookupCode: 'PARAMETER.SOURCE.TYPE',
      defaultValue: 'LOV',
    },
    {
      name: 'sourceCodeObject',
      type: 'object',
      label: $l('parameterconfig.sourcecode'),
      textField: 'description',
    },
    { name: 'sourceCode', type: 'string', bind: 'sourceCodeObject.code' },
    { name: 'sourceName', type: 'string', bind: 'sourceCodeObject.description' },
    {
      name: 'codeValueField',
      type: 'string',
      textFiled: 'meaning',
      valueField: 'value',
      label: $l('parameterconfig.codevaluefield'),
      lookupCode: 'PARAMETER.CODE.VALUE.FIELD',
      defaultValue: 'codeValueId',
    },
    {
      name: 'defaultType',
      type: 'string',
      textFiled: 'meaning',
      valueField: 'value',
      label: $l('parameterconfig.defaulttype'),
      lookupCode: 'PARAMETER.DEFAULT.TYPE',
      defaultValue: 'const',
    },
    { name: 'defaultValue', type: 'string', label: $l('parameterconfig.defaultvalue') },
    { name: 'cascadeFrom', type: 'string', label: $l('parameterconfig.cascadefrom') },
    {
      name: 'cascadeFromField',
      type: 'string',
      textFiled: 'meaning',
      valueField: 'value',
      label: $l('parameterconfig.cascadefromfield'),
      lookupCode: 'PARAMETER.CASCADEFROM.FIELD',
    },
  ],
};
