import React, { PureComponent } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { ContentPro as Content } from '@choerodon/boot';
import UploadInfoDataSet from './stores/UploadInfoDataSet';
import AttachFileDataSet from './stores/AttachFileDataSet';
import AttachTest from './view/AttachTest';

export default class Index extends PureComponent {
  uploadDS = new DataSet(UploadInfoDataSet);
  
  fileDS = new DataSet(AttachFileDataSet);

  render() {
    return (
      <Content>
        <AttachTest uploadDS={this.uploadDS} fileDS={this.fileDS} />
      </Content>
    );
  }
}
