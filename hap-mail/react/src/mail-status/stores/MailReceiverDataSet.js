import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'messageId',
  paging: false,
  autoQuery: true,
  name: 'MailReceiver',
  selection: false,
  fields: [
    { name: 'messageAddress', type: 'string', label: $l('message.receiveraddress') },
    { name: 'messageId', type: 'number', label: '' },
    { name: 'messageType', type: 'string', label: $l('message.receivertype'), lookupCode: 'SYS.MAIL_RECEIVER_TYPE' },
    { name: 'receiverId', type: 'number', label: '' },
  ],
};
