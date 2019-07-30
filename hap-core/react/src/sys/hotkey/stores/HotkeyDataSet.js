import { updateHotkeys, $l } from '@choerodon/boot';

export default {
  name: 'Hotkey',
  autoQuery: true,
  primaryKey: 'hotkeyId',
  paging: false,
  fields: [
    { name: 'hotkeyId', type: 'number', label: $l('hotkey.hotkeyId') },
    { name: 'code', type: 'string', label: $l('hotkey.code'), required: true, unique: true },
    { name: 'hotkeyLevel', type: 'string', label: '', defaultValue: 'SYSTEM' },
    { name: 'hotkeyLevelId', type: 'number', label: '' },
    { name: 'hotkey', type: 'string', label: $l('hotkey.hotkey'), unique: $l('hotkey.hotkey.duplicated'), required: true },
    { name: 'description', type: 'string', label: $l('hotkey.description') },
  ],
  events: {
    submitSuccess: ({ dataSet, data }) => {
      const { success, rows, total } = data;
      if (success && total > 0) {
        updateHotkeys(rows);
      }
    },
  },
};
