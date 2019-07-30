import { ContentPro as Content } from '@choerodon/boot';
import { inject, observer } from 'mobx-react';
import React, { Component } from 'react';

import UserInfo from './view/UserInfo';
import Pwd from './view/Pwd';
// import './index.less';

@observer
@inject('AppState')
export default class Index extends Component {
  render() {
    return (
      <Content>
        <UserInfo />
        <Pwd />
      </Content>
    );
  }
}
