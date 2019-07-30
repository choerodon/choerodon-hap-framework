import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'categoryId',
  name: 'AttachCategory',
  autoQuery: false,
  selection: false,
  fields: [
    { name: 'categoryId', type: 'number', label: '目录id' },
    { name: 'categoryName', type: 'intl', label: $l('attachcategory.categoryname'), required: true },
    { name: 'description', type: 'intl', label: $l('attachcategory.description') },
    { name: 'categoryPath', type: 'string', label: $l('attachcategory.categorypath') },
    { name: 'allowedFileType', type: 'string', label: $l('attachcategory.allowedfiletype') },
    { name: 'allowedFileSizeDesc', type: 'string', label: $l('attachcategory.allowedfilesizedesc') },
    { name: 'allowedFileSize', type: 'number', label: $l('attachcategory.allowedfilesizedesc') },
    { name: 'sourceType', type: 'string', label: $l('attachcategory.sourcetype') },
    { name: 'isUnique', type: 'boolean', trueValue: 'Y', falseValue: 'N', label: $l('attachcategory.isunique') },
    { name: 'objectVersionNumber', type: 'number' },
    { name: 'parentCategoryId', type: 'number', defaultValue: '-1' },
    { name: 'isEnabled', type: 'boolean', defaultValue: false },
    { name: 'leafFlag', type: 'string', label: $l('attachcategory.categorytype'), required: true, defaultValue: '0' },
  ],
};
