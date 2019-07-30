import { $l } from '@choerodon/boot';

export default {
  autoCreate: true,
  fields: [
    {
      name: 'attach',
      type: 'object',
      label: $l('attachcategory.sourcetype'),
      lovCode: 'ATTACH_SOURCE_TYPE',
      textField: 'sourceType',
      required: true,
    },
    { name: 'sourceType', type: 'string', bind: 'attach.sourceType' },
    { name: 'allowedFileType', type: 'string', bind: 'attach.allowedFileType' },
    { name: 'sourceKey', type: 'string', label: $l('attachcategory.sourcekey'), defaultValue: 1 },
  ],
};
