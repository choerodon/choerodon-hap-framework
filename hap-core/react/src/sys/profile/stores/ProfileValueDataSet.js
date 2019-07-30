import { $l } from '@choerodon/boot';

function getLevelValueProps({ record }) {
  const value = record.get('levelId');
  if (value === '10') {
    return { type: 'string', unique: 'group', defaultValue: 'GLOBAL' };
  } else if (value === '20') {
    return { bind: 'levelObj.id', type: 'number', unique: 'group' };
  } else if (value === '30') {
    return { bind: 'levelObj.userId', type: 'number', unique: 'group' };
  }
}

function getLevelNameProps({ record }) {
  const value = record.get('levelId');
  if (value === '10') {
    return { type: 'string', unique: 'group', defaultValue: 'GLOBAL' };
  } else if (value === '20') {
    return { bind: 'levelObj.name', type: 'string', unique: 'group' };
  } else if (value === '30') {
    return { bind: 'levelObj.userName', type: 'string', unique: 'group' };
  }
}

function getLevelObjProps({ record }) {
  const value = record.get('levelId');
  if (value === '10') {
    return { type: 'object', label: $l('profilevalue.levelvalue') };
  } else if (value === '20') {
    return { type: 'object', label: $l('profilevalue.levelvalue'), required: true, lovCode: 'LOV_ROLE', textField: 'name' };
  } else if (value === '30') {
    return { type: 'object', label: $l('profilevalue.levelvalue'), required: true, lovCode: 'user_lov', textField: 'userName' };
  }
}

export default {
  name: 'ProfileValue',
  paging: false,
  fields: [
    { name: 'levelId', type: 'string', label: $l('profilevalue.levelid'), lookupCode: 'SYS.PROFILE_LEVEL_ID', required: true, unique: 'group' },
    { name: 'levelObj', dynamicProps: getLevelObjProps },
    { name: 'levelValue', dynamicProps: getLevelValueProps },
    { name: 'levelName', dynamicProps: getLevelNameProps },
    { name: 'profileValue', type: 'string', label: $l('profilevalue.profilevalue'), required: true },
  ],
};
