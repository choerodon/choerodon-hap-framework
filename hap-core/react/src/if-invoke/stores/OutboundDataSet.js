import { $l } from '@choerodon/boot';
import moment from 'moment';

export default {
  primaryKey: 'outboundId',
  name: 'Outbound',
  autoQuery: true,
  pageSize: 20,
  fields: [
    { name: 'outboundId', type: 'string', label: '' },
    { name: 'interfaceName', type: 'string', label: $l('interface.line.interfacename') },
    { name: 'interfaceUrl', type: 'string', label: $l('interface.lind.interfaceurl') },
    { name: 'requestTime', type: 'dateTime', label: $l('interface.invoke.requesttime') },
    { name: 'startDate', type: 'dateTime', label: $l('interface.invoke.statsenddate') },
    { name: 'endDate', type: 'dateTime', label: $l('interface.invoke.endsenddate') },
    { name: 'requestMethod', type: 'string', label: $l('interface.requestmethod') },
    { name: 'responseCode', type: 'string', label: $l('interface.invoke.responsecode') },
    { name: 'responseTime', type: 'string', label: $l('interface.invoke.responsetime') },
    { name: 'requestStatus', type: 'string', label: $l('interface.invoke.requeststatus'), lookupCode: 'SYS.IF.REQUEST_STATUS' },
    { name: 'responseContent', type: 'string', label: $l('interface.invoke.responsecontent') },
    { name: 'requestParameter', type: 'string', label: $l('interface.invoke.requestparameter') },
    { name: 'stackTrace', type: 'string', label: $l('interface.invoke.stacktrace') },

  ],
  queryFields: [
    { name: 'interfaceName', type: 'string', label: $l('interface.line.interfacename') },
    { name: 'interfaceUrl', type: 'string', label: $l('interface.lind.interfaceurl') },
    { name: 'requestStatus', type: 'string', label: $l('interface.invoke.requeststatus'), lookupCode: 'SYS.IF.REQUEST_STATUS' },
    {
      name: 'startDate',
      type: 'dateTime',
      label: $l('interface.invoke.statsenddate'),
      defaultValue: moment().clone().set({ hour: 0, minute: 0, second: 0, millisecond: 0 }),
    },
    { name: 'endDate', type: 'dateTime', label: $l('interface.invoke.endsenddate') },
  ],
};
