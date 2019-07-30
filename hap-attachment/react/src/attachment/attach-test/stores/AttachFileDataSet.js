import { $l } from '@choerodon/boot';

export default {
  autoQuery: true,
  paging: false,
  selection: false,
  fields: [
    { name: 'attachmentId', type: 'number', label: '' },
    { name: 'fileId', type: 'number', label: '' },
    { name: 'fileName', type: 'string', label: $l('sysfile.filename') },
    { name: 'filePath', type: 'string', label: '' },
    { name: 'fileSize', type: 'string', label: $l('sysfile.filesize') },
    { name: 'fileSizeDesc', type: 'string', label: '' },
    { name: 'fileType', type: 'string', label: $l('sysfile.filetype') },
    { name: 'sourceType', type: 'string' },
    { name: 'sourceKey', type: 'number' },
    { name: '_token', type: 'string' },

  ],
};
