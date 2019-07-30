import React, { PureComponent } from 'react';
import { observer } from 'mobx-react';
import { DataSet } from 'choerodon-ui/pro';
import { ContentPro as Content } from '@choerodon/boot';
import UserDataSet from './stores/UserDataSet';
import PasswordDataSet from './stores/PasswordDataSet';
import User from './view/User';

@observer
export default class Index extends PureComponent {
  userDS = new DataSet(UserDataSet);

  passwordDS = new DataSet(PasswordDataSet);

  render() {
    return (
      <Content>
        <User userDS={this.userDS} passwordDS={this.passwordDS} />
      </Content>
    );
  }
}
