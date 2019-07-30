import React, { PureComponent } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { ContentPro as Content } from '@choerodon/boot';
import ApiApplicationDS from './stores/ApiApplicationDS';
import ApiServiceDS from './stores/ApiServiceDS';
import ApiApplicationModal from './view/ApiApplicationModal';

export default class Index extends PureComponent {
  apiServiceDS = new DataSet(ApiServiceDS);
  apiApplicationDS = new DataSet({
    ...ApiApplicationDS,
    children: {
      servers: this.apiServiceDS,
    },
  });

  render() {
    return (
      <Content>
        <ApiApplicationModal apiServiceDS={this.apiServiceDS} apiApplicationDS={this.apiApplicationDS} />
      </Content>
    );
  }
}