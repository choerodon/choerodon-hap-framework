import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'serverId',
  name: 'ApiServer',
  autoQuery: true,
  pageSize: 20,
  fields: [
    { name: 'serverId', type: 'number', label: 'server ID' },
    { name: 'code', type: 'string', label: $l('server.code'), required: true, unique: true },
    { name: 'name', type: 'string', label: $l('server.name'), required: true },
    { name: 'domainUrl', type: 'string', label: $l('server.domainurl'), required: true },
    { name: 'serviceType', type: 'string', label: $l('server.servicetype'), lookupCode: 'API.SERVICE_TYPE', defaultValue: 'REST', required: true },
    { name: 'enableFlag', type: 'string', label: $l('hap.enableflag'), lookupCode: 'SYS.YES_NO', defaultValue: 'Y' },
    { name: 'mappingUrl', type: 'string', label: $l('server.mappingurl'), required: true, unique: true },
    { name: 'namespace', type: 'string', label: $l('interface.namespace') },
    { name: 'publishType', type: 'string', label: $l('') },
    { name: 'authType', type: 'string', label: $l('server.authtype'), lookupCode: 'API.AUTH_TYPE', defaultValue: 'NONE' },
    { name: 'grantType', type: 'string', label: $l('oauth2clientdetails.granttypes'), lookupCode: 'API.GRANT_TYPE', defaultValue: 'CLIENT' },
    { name: 'accessTokenUrl', type: 'string', label: $l('server.accesstokenurl') },
    { name: 'clientId', type: 'string', label: $l('interface.clientid') },
    { name: 'clientSecret', type: 'string', label: $l('interface.clientsecret') },
    { name: 'authUsername', type: 'string', label: $l('hapinterfaceheader.authusername') },
    { name: 'authPassword', type: 'string', label: $l('hapinterfaceheader.authpasswrod', '密码') },
    { name: 'scope', type: 'string', label: $l('oauth2clientdetails.scope') },
    { name: 'elementFormDefault', type: 'string', label: $l('server.elementformdefault') },
    { name: 'wssPasswordType', type: 'string', label: $l('server.wsspasswordtype'), lookupCode: 'API.WSS_PASSWORD_TYPE', defaultValue: 'None' },
    { name: 'username', type: 'string', label: $l('interface.checkusername') },
    { name: 'password', type: 'string', label: $l('interface.checkpassword') },
    { name: 'importUrl', type: 'string', label: $l('server.importurl') },
    { name: 'interfaces', type: 'object', label: $l('') },
    { name: 'apiInterface', type: 'object', label: $l('') },
  ],
  queryFields: [
    { name: 'code', type: 'string', label: $l('server.code') },
    { name: 'name', type: 'string', label: $l('server.name') },
    { name: 'serviceType', type: 'string', label: $l('server.servicetype'), lookupCode: 'API.SERVICE_TYPE' },
    { name: 'enableFlag', type: 'string', label: $l('hap.enableflag'), lookupCode: 'SYS.YES_NO' },
  ],
  events: {
    update: ({ dataSet, record, name, value, oldValue }) => {
      if (name === 'authType') {
        switch (value) {
          case 'NONE': {
            record.set('authUsername', '');
            record.set('authPassword', '');
            record.set('clientId', '');
            record.set('clientSecret', '');
            record.set('accessTokenUrl', '');
            break;
          }
          case 'BASIC': {
            record.set('clientId', '');
            record.set('clientSecret', '');
            record.set('accessTokenUrl', '');
            break;
          }
          case 'OAUTH2': {
            break;
          }
        }
      } else if (name === 'grantType' && value === 'CLIENT') {
        record.set('authUsername', '');
        record.set('authPassword', '');
      }
    },
  },
};
