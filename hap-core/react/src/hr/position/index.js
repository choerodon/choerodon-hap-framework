import React, { PureComponent } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { ContentPro as Content } from '@choerodon/boot';
import PositionDataSet from './stores/PositionDataSet';

const { Column } = Table;

export default class Index extends PureComponent {
  positionDS = new DataSet(PositionDataSet);

  render() {
    return (
      <Content>
        <Table
          buttons={['add', 'save', 'delete', 'export']}
          dataSet={this.positionDS}
          queryFieldsLimit={2}
        >
          <Column name="positionCode" editor sortable />
          <Column name="name" editor sortable />
          <Column name="description" editor />
          <Column name="hrorgunit" editor sortable />
          <Column name="position" editor sortable />
        </Table>
      </Content>
    );
  }
}
