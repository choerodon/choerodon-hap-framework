import { $l } from '@choerodon/boot';

function setFieldRequired(record, filedValue, dateMask, resetFrequency, seqLength, startValue) {
  record.getField('filedValue').set('required', filedValue);
  record.getField('dateMask').set('required', dateMask);
  record.getField('resetFrequency').set('required', resetFrequency);
  record.getField('seqLength').set('required', seqLength);
  record.getField('startValue').set('required', startValue);
}

function initRequired(record, filedType) {
  if (filedType === 'CONSTANT' || filedType === 'VARIABLE') {
    setFieldRequired(record, true, false, false, false, false);
  } else if (filedType === 'DATE') {
    setFieldRequired(record, false, true, false, false, false);
  } else if (filedType === 'SEQUENCE') {
    setFieldRequired(record, false, false, true, true, true);
  } else if (filedType === 'UUID') {
    setFieldRequired(record, false, false, false, false, false);
  }
}

export default {
  name: 'CodeRuleLine',
  fields: [
    { name: 'fieldSequence', type: 'number', label: $l('coderulesline.fieldsequence'), defaultValue: 1, required: true },
    { name: 'filedType', type: 'string', label: $l('coderulesline.filedtype'), lookupCode: 'SYS.CODERULE.FIEDLTYPE', required: true },
    {
      name: 'filedValue',
      type: 'string',
      label: $l('coderulesline.filedvalue'),
      validator: (value, name, record) => {
        const passValidation = !/\s/.test(value);
        if (!passValidation) {
          return $l('msg.error.coderulesline.filed_value_validation');
        }
        return true;
      },
    },
    { name: 'dateMask', type: 'string', label: $l('coderulesline.datemask'), lookupCode: 'SYS.CODERULE.DATEMASK' },
    { name: 'resetFrequency', type: 'string', label: $l('coderulesline.resetfrequency'), lookupCode: 'SYS.CODERULE.RESTPERIOD' },
    {
      name: 'seqLength',
      type: 'number',
      label: $l('coderulesline.seqlength'),
      validator: (value, name, record) => {
        const seqLen = value || '';
        const startValue = record.get('startValue');
        if (!startValue) return true;
        const passValidation = /^\d{0,20}$/.test(seqLen) && startValue.toString().length <= Number(seqLen);
        if (!passValidation) {
          return $l('msg.error.coderulesline.seq_length_validation');
        }
        return true;
      },
    },
    {
      name: 'startValue',
      type: 'number',
      label: $l('coderulesline.startvalue'),
      validator: (value, name, record) => {
        const startValue = value || '';
        const seqLen = record.get('seqLength');
        if (!seqLen) return true;
        const passValidation = /^\d{0,20}$/.test(startValue) && startValue.toString().length <= Number(seqLen);
        if (!passValidation) {
          return $l('msg.error.coderulesline.start_value_validation');
        }
        return true;
      },
    },
    { name: 'currentValue', type: 'number', label: $l('coderulesline.currentvalue') },
    { name: 'stepNumber', type: 'number', label: $l('coderulesline.stepnumber') },
  ],
  events: {
    load: (dataItem) => {
      const dataList = dataItem.dataSet.data;
      if (dataList.length) {
        dataList.forEach((item) => {
          // 根据段类型 初始化必输字段
          initRequired(item, item.get('filedType'));
          if (item.get('filedType') === 'SEQUENCE') {
            // 如果是序列，刚加载时保留startValue，在修改提交时，与刚加载时的开始值比较，
            // 如果修改了startValue，重置currentValue
            item.set('oldStartValue', item.get('startValue'));
          }
        });
      }
    },
    update: (dataItem) => {
      if (dataItem.name === 'filedType') {
        // 改变段类型的时候 重置必输字段
        initRequired(dataItem.record, dataItem.value);
      }
    },
  },
};
