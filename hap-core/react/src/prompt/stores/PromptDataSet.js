import { $l, updateIntl } from '@choerodon/boot';

export default {
  primaryKey: 'promptId',
  name: 'Prompt',
  pageSize: 20,
  autoQuery: true,
  autoLocate: false,
  fields: [
    { name: 'promptCode', type: 'string', label: $l('prompt.promptcode'), required: true, unique: 'group' },
    {
      name: 'lang',
      type: 'string',
      label: $l('prompt.sourcelang'),
      textField: 'description',
      valueField: 'langCode',
      lookupUrl: '/common/language/',
      required: true,
      unique: 'group',
    },
    { name: 'description', type: 'string', label: $l('prompt.description'), required: true },
    { name: 'moduleCode', type: 'string', label: $l('prompt.modulecode'), lookupCode: 'SYS.MODULE', required: true },
  ],
  queryFields: [
    { name: 'promptCode', type: 'string', label: $l('prompt.promptcode') },
    { name: 'description', type: 'string', label: $l('prompt.description') },
    {
      name: 'lang',
      type: 'string',
      label: $l('prompt.sourcelang'),
      textField: 'description',
      valueField: 'langCode',
      lookupCode: 'language',
      lookupUrl: '/common/language/',
    },
    { name: 'moduleCode', type: 'string', label: $l('prompt.modulecode'), lookupCode: 'SYS.MODULE' },
  ],
  events: {
    submitSuccess: ({ dataSet, data }) => {
      const { success, rows, total } = data;
      if (success && total > 0) {
        updateIntl(rows);
      }
    },
  },
};
