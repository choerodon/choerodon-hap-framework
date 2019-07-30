import { $l } from '@choerodon/boot';

export default {
  name: 'ApiInterface',
  paging: false,
  fields: [
    { name: 'interfaceId', type: 'number', label: 'interface ID' },
    { name: 'serverId', type: 'number', label: 'server ID' },
    { name: 'code', type: 'string', label: $l('interface.line.interfacecode'), required: true },
    { name: 'name', type: 'string', label: $l('interface.line.interfacename'), required: true },
    { name: 'interfaceUrl', type: 'string', label: $l('interface.interfaceurl'), required: true },
    { name: 'mappingUrl', type: 'string', label: $l('server.mappingurl'), required: true },
    { name: 'enableFlag', type: 'string', label: $l('hap.enableflag'), defaultValue: 'Y', lookupCode: 'SYS.YES_NO' },
    { name: 'soapVersion', type: 'string', label: $l('server.soapversion'), defaultValue: 'SOAP12', lookupCode: 'API.SOAP_VERSION' },
    { name: 'mappingClass', type: 'string', label: $l('interface.mapperclass') },
    { name: 'requestMethod', type: 'string', label: $l('interface.requestmethod'), defaultValue: 'GET', lookupCode: 'API.REQUEST_METHOD' },
    { name: 'requestHead', type: 'string', label: $l('ContentType'), lookupCode: 'API.REQUEST_CONTENT_TYPE' },
    { name: 'description', type: 'string', label: $l('hap.description') },
    { name: 'soapAction', type: 'string', label: $l('hapinterfaceheader.soapaction') },
    { name: 'invokeRecordDetails', type: 'string', label: $l('interface.invokerecorddetails'), defaultValue: 'N', lookupCode: 'SYS.YES_NO' },
    { name: 'apiAccessLimit', type: 'object', label: $l('api access limit') },
  ],
};
