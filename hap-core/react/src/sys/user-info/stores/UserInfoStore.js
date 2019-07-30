function phoneValidator(value, name, record) {
  const pattern = /^1(3|4|5|6|7|8|9)\d{9}$/;
  if (pattern.test(value)) {
    return true;
  }
  return '请输入有效的手机号码！';
}

export default {
  autoCreate: true,
  fields: [
    { name: 'userId', type: 'number' },
    { name: '_token', type: 'string' },
    { name: 'objectVersionNumber', type: 'number' },
    { name: 'userName', type: 'string', label: '用户名' },
    { name: 'phone', type: 'string', label: '手机号', required: true, validator: phoneValidator },
    { name: 'email', type: 'email', label: '邮箱', required: true },
  ],
};
