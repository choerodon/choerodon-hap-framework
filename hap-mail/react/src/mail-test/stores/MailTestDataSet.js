import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'messageId',
  autoCreate: true,
  pageSize: 20,
  fields: [
    { name: 'attachments', type: 'number' },
    { name: 'template', type: 'object', textField: 'templateCode', label: $l('messagetemplate.templatecode'), lovCode: 'MESSAGE_TEMPLATE' },
    { name: 'templateCode', bind: 'template.templateCode', type: 'string' },
    { name: 'receivers', type: 'string', label: $l('message.receivers') },
    { name: 'mode', type: 'string', defaultValue: 'template' },
  ],
};
