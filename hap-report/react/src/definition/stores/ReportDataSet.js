import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'reportId',
  name: 'Report',
  autoQuery: true,
  pageSize: 20,
  fields: [
    { name: 'reportCode', type: 'string', label: $l('report.reportcode'), required: true, unique: true },
    { name: 'reportFile', type: 'object', textField: 'name', lovCode: 'LOV_REPORT_FILE', label: $l('report.fileid'), required: true },
    { name: 'fileId', bind: 'reportFile.fileId', type: 'number' },
    { name: 'fileName', bind: 'reportFile.name', type: 'string' },
    { name: 'name', type: 'string', label: $l('report.name'), required: true },
    { name: 'description', type: 'string', label: $l('report.description') },
    { name: 'defaultQuery', type: 'boolean', label: $l('report.defaultquery'), trueValue: 'Y', falseValue: 'N', defaultValue: 'Y' },
  ],
  queryFields: [
    { name: 'reportCode', type: 'string', label: $l('report.reportcode') },
    { name: 'name', type: 'string', label: $l('report.name') },
  ],
  events: {
    submitSuccess: (dataItem) => {
      dataItem.dataSet.query();
    },
  },
};
