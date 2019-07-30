import { inject, observer } from 'mobx-react';
import React, { Component } from 'react';
import { axiosPro as axios } from '@choerodon/boot';
import { Form, TextField, EmailField, Button, Output, DataSet, message } from 'choerodon-ui/pro';

import UserInfoStore from '../stores/UserInfoStore';

@observer
@inject('AppState')
export default class Index extends Component {
  state = {
    loading: false,
  };

  ds = new DataSet({
    ...UserInfoStore,
  });

  componentDidMount() {
    const { AppState } = this.props;
    const userInfoObj = AppState.getUserInfo;
    const { userId, userName, email, phone, token, objectVersionNumber } = userInfoObj;
    const userInfoObjFilterd = {
      userId,
      userName,
      email,
      phone,
      _token: token,
      objectVersionNumber,
    };
    this.ds.loadData([userInfoObjFilterd]);
  }

  handleSubmitInfo = () => {
    const { current } = this.ds;
    current.validate(true).then((res) => {
      if (res) {
        this.submit();
      }
    });
  }

  submit = async () => {
    const { current } = this.ds;
    const { AppState } = this.props;
    try {
      this.setState({ loading: true });
      const res = await axios.post('/sys/user/update', current.toJSONData());
      if (res && res.success) {
        message.success('修改个人信息成功');
        setTimeout(() => {
          window.location.reload();
        }, 1000);
      } else {
        message.error(res.message || res.code);
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
      <Form dataSet={this.ds} style={{ width: '4rem', margin: '0 auto' }}>
        <Output name="userName" />
        <TextField name="phone" />
        <EmailField name="email" />
        <div>
          <Button onClick={this.handleSubmitInfo} color="blue" loading={loading}>保存</Button>
        </div>
      </Form>
    );
  }
}
