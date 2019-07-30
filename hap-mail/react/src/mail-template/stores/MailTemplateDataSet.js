import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'templateId',
  autoQuery: true,
  pageSize: 20,
  name: 'MailTemplate',
  fields: [
    { name: 'templateCode', type: 'string', label: $l('messagetemplate.templatecode'), required: true, unique: true },
    { name: 'templateType', type: 'string', label: $l('messagetemplate.templatetype'), lookupCode: 'SYS.TEMPLATE_TYPE', required: true },
    { name: 'description', type: 'string', label: $l('messagetemplate.description') },
    { name: 'sendType', type: 'string', label: $l('messagetemplate.sendtype'), lookupCode: 'SYS.SEND_TYPE', required: true },
    {
      name: 'account',
      type: 'object',
      textField: 'userName',
      label: $l('messageemailconfig.emailaccounts'),
      lovCode: 'LOV_MESSAGE_ACCOUNT',
      required: true,
    },
    { name: 'accountId', bind: 'account.accountId', type: 'number' },
    { name: 'userName', bind: 'account.userName', type: 'string' },
    { name: 'subject', type: 'string', label: $l('messagetemplate.subject'), required: true },
    { name: 'content', type: 'string', label: $l('messagetemplate.content'), required: true },

  ],
  queryFields: [
    { name: 'templateCode', type: 'string', label: $l('messagetemplate.templatecode') },
    { name: 'description', type: 'string', label: $l('messagetemplate.description') },
  ],
};
