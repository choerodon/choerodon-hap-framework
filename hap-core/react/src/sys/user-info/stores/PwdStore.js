import { stores } from '@choerodon/boot';

const { AppState } = stores;

function passwordValidator(value, name, record) {
  const userInfoObj = AppState.getUserInfo;
  const { passwordComplexity, passwordMinLength } = userInfoObj;
  if (value === record.get('oldPwd')) {
    return '新密码与原密码相同!';
  }
  if (value && (value.length >= passwordMinLength || !passwordMinLength)) {
    if (passwordComplexity === 'no_limit') {
      return true;
    } else if (passwordComplexity === 'digits_and_letters') {
      const res = /[a-zA-Z]/.test(value) && /\d/.test(value);
      return res || '密码只能包含混合数字和字母！';
    } else if (passwordComplexity === 'digits_and_case_letters') {
      const res = /a-z/.test(value) && /[A-Z]/.test(value) && /\d/.test(value);
      return res || '密码需要包含数字和大小写字母！';
    }
    return true;
  } else {
    return `密码最小长度为${passwordMinLength}！`;
  }
}

function confirmPasswordValidator(value, name, record) {
  const userInfoObj = AppState.getUserInfo;
  const { passwordComplexity, passwordMinLength } = userInfoObj;
  if (value !== record.get('newPwd')) {
    return '您两次输入的密码不一致，请重新输入';
  }
  if (value && (value.length >= passwordMinLength || !passwordMinLength)) {
    if (passwordComplexity === 'no_limit') {
      return true;
    } else if (passwordComplexity === 'digits_and_letters') {
      const res = /[a-zA-Z]/.test(value) && /\d/.test(value);
      return res || '密码只能包含混合数字和字母！';
    } else if (passwordComplexity === 'digits_and_case_letters') {
      const res = /a-z/.test(value) && /[A-Z]/.test(value) && /\d/.test(value);
      return res || '密码需要包含数字和大小写字母！';
    }
    return true;
  } else {
    return false;
  }
}

export default {
  autoCreate: true,
  fields: [
    { name: 'oldPwd', type: 'string', label: '当前密码', required: true },
    { name: 'newPwd', type: 'string', label: '新密码', required: true, validator: passwordValidator },
    { name: 'newPwdAgain', type: 'string', label: '确认新密码', required: true, validator: confirmPasswordValidator },
  ],
};
