import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'tableId',
  name: 'PermissionTable',
  pageSize: 20,
  autoQuery: true,
  fields: [
    { name: 'tableId', type: 'number', label: $l('datapermissiontable.tableid') },
    {
      name: 'tableName',
      type: 'string',
      label: $l('datapermissiontable.tablename'),
      textField: 'tableName',
      valueField: 'tableName',
      lookupUrl: '/generator/alltablesobj',
      required: true,
      readeOnly: true,
      unique: true,
    },
    { name: 'description', type: 'string', label: $l('hap.description') },
  ],
  queryFields: [
    { name: 'tableName', type: 'string', label: $l('datapermissiontable.tablename') },
    { name: 'description', type: 'string', label: $l('hap.description') },
  ],
};
