import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'messageId',
  autoQuery: true,
  pageSize: 20,
  name: 'Message',
  selection: false,
  fields: [
    { name: 'content', type: 'string', label: $l('message.content') },
    { name: 'messageId', type: 'number', label: '' },
    { name: 'creationDate', type: 'dateTime', label: $l('messagetemplate.creationdate') },
    { name: 'lastUpdateDate', type: 'dateTime', label: $l('messagetemplate.lastupdatedate') },
    { name: 'messageFrom', type: 'string', label: $l('messageemailconfig.accountcode') },
    { name: 'messageHost', type: 'string' },
    { name: 'messageSource', type: 'string', label: '' },
    { name: 'messageType', type: 'string', label: '' },
    { name: 'subject', type: 'string', label: $l('message.subject') },
    { name: 'sendFlag', type: 'string', label: $l('message.sendflag'), lookupCode: 'SYS.MAIL_STATUS' },
  ],
  queryFields: [
    { name: 'messageFrom', type: 'string', label: $l('messageemailconfig.accountcode') },
    { name: 'subject', type: 'string', label: $l('message.subject') },
    { name: 'sendFlag', type: 'string', label: $l('message.sendflag'), lookupCode: 'SYS.MAIL_STATUS' },
  ],
};
