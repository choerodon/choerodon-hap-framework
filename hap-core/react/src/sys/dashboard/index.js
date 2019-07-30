import React, { PureComponent } from 'react';
import { inject, observer } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { CheckBox, DataSet, IntlField, Lov, Table, TextField } from 'choerodon-ui/pro';
import { ContentPro as Content } from '@choerodon/boot';
import DashboardDataSet from './stores/DashboardDataSet';

const { Column } = Table;
const textField = <TextField />;


function editorRenderer(record) {
  return record.status === 'add' ? textField : null;
}

export default class Index extends PureComponent {
  dashboardDS = new DataSet(DashboardDataSet);

  render() {
    return (
      <Content>
        <Table
          buttons={['add', 'save', 'delete']}
          dataSet={this.dashboardDS}
          queryFieldsLimit={2}
          funcType="flat"
          color="blue"
        >
          <Column name="dashboardCode" editor={editorRenderer} sortable />
          <Column name="title" editor={<IntlField clearButton />} />
          <Column name="description" editor={<IntlField clearButton />} />
          <Column name="resource" editor />
          <Column name="enabledFlag" editor width={120} />
        </Table>
      </Content>
    );
  }
}
