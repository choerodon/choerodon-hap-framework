import { inject, observer } from 'mobx-react';
import React, { Component } from 'react';
import { axiosPro as axios } from '@choerodon/boot';
import { Form, Password, Button, DataSet, message } from 'choerodon-ui/pro';

import PwdStore from '../stores/PwdStore';

@observer
@inject('AppState')
export default class Index extends Component {
  state = {
    loading: false,
  }

  ds = new DataSet({
    ...PwdStore,
  });

  handleSubmitPwd = async () => {
    const { current } = this.ds;
    current.validate(true).then((res) => {
      if (res) {
        this.submit();
      }
    });
  }

  submit = async () => {
    const { current } = this.ds;
    try {
      const obj = current.toJSONData();
      const res = await axios.post(`/sys/user/password/update?newPwd=${obj.newPwd}&newPwdAgain=${obj.newPwdAgain}&oldPwd=${obj.oldPwd}`, {});
      this.setState({ loading: true });
      if (res && res.success) {
        message.success('修改密码成功');
      } else {
        message.error(res.message);
      }
    } catch (err) {
      message.error(err);
    } finally {
      this.setState({ loading: false });
    }
  }

  render() {
    const { loading } = this.state;
    return (
      <Form dataSet={this.ds} header="修改密码" style={{ width: '4rem', margin: '0 auto' }}>
        <Password name="oldPwd" autoComplete="off" />
        <Password name="newPwd" />
        <Password name="newPwdAgain" />
        <div>
          <Button onClick={this.handleSubmitPwd} color="blue" loading={loading}>保存</Button>
        </div>
      </Form>
    );
  }
}
