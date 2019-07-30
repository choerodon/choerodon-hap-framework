import { ContentPro as Content } from '@choerodon/boot';
import { DataSet } from 'choerodon-ui/pro';
import { observer, inject } from 'mobx-react';
import React, { Component } from 'react';

import SysConfigDS from './stores/SysConfigDataSet';
import SysConfig from './view/SysConfig';

@inject('AppState')
@observer
export default class Index extends Component {
  sysConfigDS = new DataSet(SysConfigDS);

  render() {
    const { AppState: appState } = this.props;
    return (
      <Content>
        <SysConfig sysConfigDS={this.sysConfigDS} appState={appState} />
      </Content>
    );
  }
}
