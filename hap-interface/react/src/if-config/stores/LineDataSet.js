import { $l } from '@choerodon/boot';

export default {
  name: 'InterfaceLine',
  paging: false,
  autoQuery: true,
  fields: [
    { name: 'lineCode', type: 'string', label: $l('interface.line.interfacecode'), required: true },
    { name: 'lineName', type: 'string', label: $l('interface.line.interfacename'), required: true },
    { name: 'lineDescription', type: 'string', label: '' },
    { name: 'lineId', type: 'string', label: 'ID' },
    { name: 'iftUrl', type: 'string', label: $l('interface.lind.interfaceurl'), required: true },
    { name: 'enableFlag', type: 'string', label: $l('interface.enableflag'), trueValue: 'Y', falseValue: 'N', defaultValue: 'Y' },
  ],
};
