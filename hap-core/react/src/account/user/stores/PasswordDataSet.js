import { $l } from '@choerodon/boot';

const passwordValidator = (value, name, record) => {
  if (record.status === 'add') {
    if (record.get('password') !== value) {
      return $l('error.user.two_password_not_match');
    }
  }
  return true;
};

export default {
  primaryKey: 'userId',
  name: 'AccountPassword',
  fields: [
    { name: 'userId', type: 'number' },
    { name: 'userName', type: 'string', label: $l('user.username'), required: true },
    { name: 'password', type: 'string', label: $l('user.new_password'), required: true },
    { name: 'passwordAgain', type: 'string', label: $l('user.again_new_password'), required: true, validator: passwordValidator },
  ],
};
