import { $l } from '@choerodon/boot';
import { Stores } from 'choerodon-ui/pro';

export default {
  name: 'LovItem',
  primaryKey: 'lovItemId',
  paging: false,
  fields: [
    { name: 'lovItemId', type: 'number', label: $l('lovitem.lovid') },
    { name: 'lovId', type: 'number', label: $l('lov.lovid') },
    { name: 'display', type: 'string', label: $l('lovitem.display'), required: true },
    { name: 'gridFieldName', type: 'string', label: $l('lovitem.gridfieldname'), required: true },
    { name: 'gridFieldWidth', type: 'number', label: $l('lovitem.gridfieldwidth'), defaultValue: 200 },
    { name: 'gridFieldAlign', type: 'string', label: $l('lovitem.gridcolumnalign'), lookupCode: 'SYS.ALIGN_TYPE', defaultValue: 'left' },
    { name: 'autocompleteField', type: 'string', label: $l('lovitem.autocompletefield') },
    { name: 'conditionField', type: 'boolean', label: $l('lovitem.conditionfield'), trueValue: 'Y', falseValue: 'N' },
    { name: 'isAutocomplete', type: 'string', label: $l('lovitem.isautocomplete') },
    { name: 'gridField', type: 'boolean', label: $l('lovitem.gridcolumn'), trueValue: 'Y', falseValue: 'N' },
    { name: 'conditionFieldWidth', type: 'number', label: $l('lovitem.conditionfieldwidth') },
    { name: 'conditionFieldLabelWidth', type: 'number', label: $l('lovitem.conditionfieldlabelwidth') },
    { name: 'conditionFieldType', type: 'string', label: $l('lovitem.conditionfieldtype'), lookupCode: 'SYS.LOV_EDITOR_TYPE' },
    { name: 'conditionFieldName', type: 'string', label: $l('lovitem.conditionfieldname') },
    { name: 'conditionFieldTextfield', type: 'string', label: $l('lovitem.conditionfieldtextfield') },
    { name: 'conditionFieldNewline', type: 'string', label: $l('lovitem.conditionfieldnewline') },
    { name: 'conditionFieldSelectUrl', type: 'string', label: $l('lovitem.conditionfieldselecturl') },
    { name: 'conditionFieldSelectVf', type: 'string', label: $l('valueField') },
    { name: 'conditionFieldSelectTf', type: 'string', label: $l('textField') },
    { name: 'lovCode', type: 'object', label: $l('lovitem.conditionfieldselectcode'), textField: 'description', lovCode: 'LOV_CODE' },
    { name: 'conditionFieldSelectCode', type: 'string', label: $l('lovitem.conditionfieldselectcode'), bind: 'lovCode.code' },
    { name: 'lovList', type: 'object', label: $l('lovitem.conditionfieldlovcode'), lovCode: 'LOV_LIST' },
    { name: 'conditionFieldLovCode', type: 'string', textField: 'description', label: $l('lovitem.conditionfieldlovcode'), bind: 'lovList.code' },
    { name: 'conditionFieldSequence', type: 'number', label: $l('lovitem.conditionfieldsequence'), defaultValue: 1 },
    { name: 'gridFieldSequence', type: 'number', label: $l('lovitem.gridfieldsequence'), defaultValue: 0 },
    { name: 'dataSourceType', type: 'string', label: $l('lovitem.ds_type'), lookupCode: 'SYS.LOV_DATA_SOURCE_TYPE' },
  ],
  events: {
    submitSuccess: ({ dataSet, data }) => {
      Stores.LovCodeStore.clearCache();
    },
  },
};
