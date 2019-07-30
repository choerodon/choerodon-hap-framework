import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'fileId',
  autoQuery: true,
  paging: false,
  name: 'MailFile',
  fields: [
    { name: 'fileId', type: 'number' },
    { name: 'attachmentId', type: 'number' },
    { name: 'fileName', type: 'string', label: $l('sysfile.filename') },
    { name: 'filePath', type: 'string' },
    { name: 'fileSize', type: 'string', label: $l('sysfile.filesize') },
    { name: 'fileType', type: 'string', label: $l('sysfile.filetype') },
    { name: 'uploadDate', type: 'dateTime' },
  ],
  queryFields: [
    { name: 'fileName', type: 'string', label: $l('sysfile.filename') },
    { name: 'fileType', type: 'string', label: $l('sysfile.filetype') },
  ],
};
