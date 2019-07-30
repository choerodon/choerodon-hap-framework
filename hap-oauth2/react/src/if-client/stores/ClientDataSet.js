import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'clientId',
  name: 'Client',
  autoQuery: true,
  pageSize: 20,
  fields: [
    { name: 'clientId', type: 'string', label: $l('oauth2clientdetails.clientid'), required: true, unique: true },
    { name: 'clientSecret', type: 'string', label: $l('oauth2clientdetails.clientsecret') },
    { name: 'authorizedGrantTypes', type: 'string', label: $l('oauth2clientdetails.granttypes'), required: true, multiple: ',' },
    { name: 'accessTokenValidity', type: 'number', label: $l('oauth2clientdetails.accesstokenvalidity') },
    { name: 'refreshTokenValidity', type: 'number', label: $l('oauth2clientdetails.refreshtokenvalidity') },
    { name: 'autoApprove', type: 'string', label: $l('oauth2clientdetails.autoapprove') },
    { name: 'scope', type: 'string', label: 'scope' },
    { name: 'redirectUri', type: 'string', label: $l('oauth2clientdetails.redirecturi') },
    {
      name: 'role',
      type: 'object',
      textField: 'code',
      label: $l('oauth2clientdetails.authorities'),
      lovCode: 'LOV_PROFILE_ROLE',
      multiple: true,
    },
    { name: 'authorities', type: 'string', bind: 'role.code', multiple: ',' },
    { name: 'additionalInformation', type: 'string', label: $l('oauth2clientdetails.additionalinformation') },
    { name: 'resourceIds', type: 'string', label: '', defaultValue: 'api-resource' },
    { name: 'isEnabled', type: 'String', label: '' },
  ],
  queryFields: [
    { name: 'clientId', type: 'string', label: $l('oauth2clientdetails.clientid') },
  ],
};
