export default {
  autoQuery: true,
  name: 'AttachTree',
  primaryKey: 'categoryId',
  parentField: 'parentCategoryId',
  idField: 'categoryId',
  expandField: 'expandFlag',
  selection: 'single',
  fields: [
    { name: 'allowedFileSize', type: 'number', label: '' },
    { name: 'allowedFileSizeDesc', type: 'string', label: '' },
    { name: 'allowedFileType', type: 'string', label: '' },
    { name: 'categoryId', type: 'number', label: '' },
    { name: 'categoryName', type: 'string', label: '' },
    { name: 'categoryPath', type: 'string', label: '' },
    { name: 'description', type: 'string', label: '' },
    { name: 'isUnique', type: 'string', label: '' },
    { name: 'leafFlag', type: 'string', label: '' },
    { name: 'parentCategoryId', type: 'number', label: '' },
    { name: 'path', type: 'string', label: '' },
    { name: 'sourceType', type: 'string', label: '' },
    { name: 'status', type: 'string', label: '' },
    { name: 'expandFlag', type: 'boolean' },
  ],
  events: {
    select: ({ record, dataSet }) => {
      const index = dataSet.findIndex(r => r.id === record.id);
      if (index !== -1) {
        dataSet.locate(index);
      }
    },
  },
};
