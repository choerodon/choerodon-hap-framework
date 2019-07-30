import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'id',
  autoQuery: true,
  pageSize: 20,
  name: 'Token',
  selection: false,
  fields: [
    { name: 'clientId', type: 'string', label: $l('oauth2clientdetails.clientid') },
    { name: 'user', type: 'object', textField: 'userId', label: $l('tokenlogs.userid'), lovCode: 'user_lov' },
    { name: 'userId', type: 'number', bind: 'user.userId', label: $l('tokenlogs.userid') },
    { name: 'userName', type: 'string', bind: 'user.userName' },
    { name: 'token', type: 'string', label: 'token' },
    { name: 'tokenAccessTime', type: 'dateTime', label: $l('tokenlogs.tokenaccesstime') },
    { name: 'tokenExpiresTime', type: 'dateTime', label: $l('tokenlogs.tokenexpirestime') },
    { name: 'tokenAccessType', type: 'string', label: $l('tokenlogs.tokenaccesstype') },
    { name: 'tokenStatus', type: 'string', label: $l('tokenlogs.tokenaccessstatus'), lookupCode: 'IF.GRANT_STATUS' },
  ],
  queryFields: [
    { name: 'user', type: 'object', textField: 'userId', label: $l('tokenlogs.userid'), lovCode: 'user_lov' },
    { name: 'userId', type: 'number', bind: 'user.userId' },
    { name: 'userName', type: 'string', bind: 'user.userName' },
    { name: 'clientId', type: 'string', label: $l('oauth2clientdetails.clientid') },
    { name: 'tokenStatus', type: 'string', label: $l('tokenlogs.tokenaccessstatus'), lookupCode: 'IF.GRANT_STATUS', defaultValue: 'valid' },
    { name: 'token', type: 'string', label: 'token' },

  ],
};
