import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'profileId',
  autoQuery: true,
  name: 'Profile',
  pageSize: 20,
  queryFields: [
    { name: 'profileName', type: 'string', label: $l('profile.profilename') },
    { name: 'description', type: 'string', label: $l('profile.description') },
  ],
  fields: [
    { name: 'profileId', type: 'number' },
    { name: 'profileName', type: 'string', label: $l('profile.profilename'), required: true, unique: true },
    { name: 'description', type: 'string', label: $l('profile.description'), required: true },
  ],
};
