import { $l } from '@choerodon/boot';

export default {
  autoQuery: true,
  pageSize: 20,
  name: 'AttachFile',
  fields: [
    { name: 'attachmentId', type: 'number', label: '' },
    { name: 'fileId', type: 'number', label: '' },
    { name: 'categoryId', type: 'number', label: '' },
    { name: 'fileName', type: 'string', label: $l('sysfile.filename') },
    { name: 'filePath', type: 'string', label: '' },
    { name: 'fileSize', type: 'string', label: $l('sysfile.filesize') },
    { name: 'fileSizeDesc', type: 'string', label: '' },
    { name: 'fileType', type: 'string', label: $l('sysfile.filetype') },
    { name: 'uploadDate', type: 'dateTime', label: $l('sysfile.uploaddate') },
  ],
};
