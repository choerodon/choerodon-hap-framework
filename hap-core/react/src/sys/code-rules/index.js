import React, { PureComponent } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { ContentPro as Content } from '@choerodon/boot';
import HeadDataSet from './store/HeadDataSet';
import LineDataSet from './store/LineDataSet';
import CodeRule from './view/CodeRule';

export default class Index extends PureComponent {
  lineDS = new DataSet(LineDataSet);

  headerDS = new DataSet({
    ...HeadDataSet,
    children: {
      lines: this.lineDS,
    },

  });

  render() {
    return (
      <Content>
        <CodeRule headerDS={this.headerDS} lineDS={this.lineDS} />
      </Content>
    );
  }
}
