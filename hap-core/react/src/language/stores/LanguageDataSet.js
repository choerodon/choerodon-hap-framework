import { $l, updateLocaleContext } from '@choerodon/boot';

export default {
  primaryKey: 'langCode',
  name: 'Language',
  autoQuery: true,
  pageSize: 20,
  fields: [
    { name: 'langCode', type: 'string', label: $l('language.langcode'), required: true, unique: true },
    { name: 'description', type: 'string', label: $l('language.description'), required: true },
  ],
  queryFields: [
    { name: 'langCode', type: 'string', label: $l('language.langcode') },
    { name: 'description', type: 'string', label: $l('language.description') },
  ],
  events: {
    submitSuccess: ({ dataSet, data }) => {
      const { success, rows, total } = data;
      if (success && total > 0) {
        updateLocaleContext(rows);
      }
    },
  },
};
