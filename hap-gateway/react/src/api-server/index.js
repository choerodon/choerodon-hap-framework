import React, { PureComponent } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { ContentPro as Content } from '@choerodon/boot';
import { ApiInterfaceDataSet, ApiServerDataSet } from './stores';
import ApiServer from './view/ApiServer';
import './index.scss';

export default class Index extends PureComponent {
  apiServerDataSet = new DataSet(ApiServerDataSet);
  apiInterfaceDataSet = new DataSet(ApiInterfaceDataSet);

  constructor(props) {
    super(props);
    this.apiInterfaceDataSet.bind(this.apiServerDataSet, 'interfaces');
  }

  render() {
    return (
      <Content>
        <ApiServer apiServerDataSet={this.apiServerDataSet} apiInterfaceDataSet={this.apiInterfaceDataSet} />
      </Content>
    );
  }
}
