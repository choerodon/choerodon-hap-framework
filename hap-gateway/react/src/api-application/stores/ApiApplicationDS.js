import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'applicationId',
  name: 'ApiApplication',
  autoQuery: true,
  pageSize: 20,
  fields: [
    { name: 'applicationId', type: 'number' },
    { name: 'code', type: 'string', label: $l('api.application.code'), required: true, unique: true },
    { name: 'name', type: 'string', label: $l('api.application.name'), required: true },
    { name: 'clientId', type: 'string', label: 'Client ID', bind: 'client.clientId', required: true },
    { name: 'client', type: 'object' },
    { name: 'clientSecret', type: 'string', label: 'Client Secret', bind: 'client.clientSecret', required: true },
    { name: 'client.accessTokenValidity', type: 'number', label: $l('oauth2clientdetails.accesstokenvalidity') },
    { name: 'client.refreshTokenValidity', type: 'number', label: $l('oauth2clientdetails.refreshtokenvalidity') },
    { name: 'client.redirectUri', type: 'string', label: $l('oauth2clientdetails.redirecturi') },
    { name: 'client.authorizedGrantTypes', type: 'string', multiple: ',', required: true, label: $l('oauth2clientdetails.granttypes') },
    { name: 'id', type: 'string', label: 'number', bind: 'client.id' },
    { name: 'resourceIds', type: 'string', label: 'number', bind: 'client.resourceIds' },
    { name: 'scope', type: 'string', label: 'number', bind: 'client.scope' },
  ],
  queryFields: [
    { name: 'code', type: 'string', label: $l('api.application.code') },
    { name: 'name', type: 'string', label: $l('api.application.name') },
  ],
};
