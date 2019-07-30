import React, { PureComponent } from 'react';
import { ContentPro as Content } from '@choerodon/boot';
import { DataSet } from 'choerodon-ui/pro';
import LovDataSet from './stores/LovDataSet';
import LovItemDataSet from './stores/LovItemDataSet';
import Lov from './view/Lov';

export default class Index extends PureComponent {
  lovItemDataSet = new DataSet(LovItemDataSet);

  lovDataSet = new DataSet({
    ...LovDataSet,
    children: {
      lovItems: this.lovItemDataSet,
    },
    events: {
      ...LovDataSet.events,
    },
  });

  render() {
    return (
      <Content>
        <Lov lovDataSet={this.lovDataSet} lovItemDataSet={this.lovItemDataSet} />
      </Content>
    );
  }
}
