import React, { PureComponent } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { ContentPro as Content } from '@choerodon/boot';
import ApiInvokeRecordDS from './stores/ApiInvokeRecordDS';
import ApiInvokeRecordModal from './view/ApiInvokeRecordModal';


export default class Index extends PureComponent {
  apiInvokeRecordDS = new DataSet(ApiInvokeRecordDS);

  render() {
    return (
      <Content>
        <ApiInvokeRecordModal dataset={this.apiInvokeRecordDS} />
      </Content>
    );
  }
}
