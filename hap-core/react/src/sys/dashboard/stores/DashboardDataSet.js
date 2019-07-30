import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'dashboardId',
  autoQuery: true,
  pageSize: 20,
  name: 'Dashboard',
  fields: [
    { name: 'dashboardCode', type: 'string', label: $l('sys.dashboard.dashboardcode'), required: true, unique: true },
    { name: 'title', type: 'intl', label: $l('sys.dashboard.dashboardtitle'), required: true },
    { name: 'description', type: 'intl', label: $l('sys.dashboard.dashboarddescription') },
    { name: 'resource', type: 'object', textField: 'name', label: $l('sys.dashboard.dashboardmainpage'), lovCode: 'LOV_RESOURCE', required: true },
    { name: 'resourceId', bind: 'resource.resourceId', type: 'number', label: '资源ID' },
    { name: 'resourceName', bind: 'resource.name', type: 'string', label: '入口页面' },
    { name: 'enabledFlag', type: 'boolean', label: $l('interface.enableflag'), defaultValue: 'Y', trueValue: 'Y', falseValue: 'N' },
  ],
  events: {},
  queryFields: [
    { name: 'dashboardCode', type: 'string', label: $l('sys.dashboard.dashboardcode') },
    { name: 'title', type: 'string', label: $l('sys.dashboard.dashboardtitle') },
  ],
};
