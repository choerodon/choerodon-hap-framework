import { $l } from '@choerodon/boot';
import { Stores } from 'choerodon-ui/pro';

function dynamicPropsByTreeFlag({ record, name }) {
  return {
    required: record.get('treeFlag') === 'Y',
  };
}

function dynamicPropsBySqlType({ record, name }) {
  switch (name) {
    case 'customSql':
      return {
        required: record.get('sqlType') === 'CUSTOM_SQL',
      };
    case 'sqlId':
      return {
        required: record.get('sqlType') === 'SQL_ID',
      };
    case 'customUrl':
      return {
        required: record.get('sqlType') === 'URL',
      };
    default:
      break;
  }
}

export default {
  primaryKey: 'lovId',
  name: 'Lov',
  autoQuery: true,
  autoLocate: false,
  pageSize: 20,
  fields: [
    { name: 'code', type: 'string', label: $l('lov.code'), required: true, unique: true },
    { name: 'description', type: 'string', label: $l('lov.description') },
    { name: 'height', type: 'number', label: $l('lov.height') },
    { name: 'width', type: 'number', label: $l('lov.width') },
    { name: 'lovId', type: 'number', label: $l('lov.lovid') },
    { name: 'sqlType', type: 'string', label: $l('lov.sqltype'), lookupCode: 'SYS.LOV_SQL_TYPE', defaultValue: 'SQL_ID' },
    { name: 'placeholder', type: 'string', label: $l('lov.placeholder') },
    { name: 'sqlId', type: 'string', label: $l('lov.sqlid'), dynamicProps: dynamicPropsBySqlType },
    { name: 'customSql', type: 'string', label: $l('lov.customsql'), dynamicProps: dynamicPropsBySqlType },
    { name: 'queryColumns', type: 'number', label: $l('lov.querycolumns') },
    { name: 'customUrl', type: 'String', label: $l('lov.customurl'), dynamicProps: dynamicPropsBySqlType },
    { name: 'textField', type: 'string', label: $l('lov.textfield'), required: true },
    { name: 'title', type: 'string', label: $l('lov.title') },
    { name: 'valueField', type: 'String', label: $l('lov.valuefield'), required: true },
    { name: 'delayLoad', type: 'String', label: $l('') },
    { name: 'needQueryParam', type: 'String', label: $l('lov.needqueryparam') },
    { name: 'editableFlag', type: 'String', label: $l('lov.editableflag') },
    { name: 'canPopup', type: 'String', label: $l('lov.canpopup') },
    { name: 'lovPageSize', type: 'String', label: $l('lov.pagesize'), lookupCode: 'SYS.LOV_PAGE_SIZE', defaultValue: '10' },
    { name: 'treeFlag', type: 'String', label: $l('lov.istree'), defaultValue: 'N' },
    { name: 'idField', type: 'String', label: $l('IDField'), dynamicProps: dynamicPropsByTreeFlag },
    { name: 'parentIdField', type: 'string', label: $l('ParentIDField'), dynamicProps: dynamicPropsByTreeFlag },
  ],
  queryFields: [
    { name: 'code', type: 'string', label: $l('lov.code') },
    { name: 'description', type: 'string', label: $l('lov.description') },
  ],
  events: {
    submitSuccess: ({ dataSet, data }) => {
      Stores.LovCodeStore.clearCache();
    },
  },
};
