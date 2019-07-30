import { ContentPro as Content } from '@choerodon/boot';
import { inject, observer } from 'mobx-react';
import { DataSet } from 'choerodon-ui/pro';
import React, { Component } from 'react';

import getDsConfig from './stores/PreferencesStore';
import PreferencesForm from './view/PreferencesForm';
import './index.less';

@observer
@inject('AppState')
export default class Index extends Component {
  preferencesDS = new DataSet(getDsConfig(this.props.AppState.getUserInfo));

  render() {
    return (
      <Content>
        <PreferencesForm preferencesDS={this.preferencesDS} />
      </Content>
    );
  }
}
