import React, { PureComponent } from 'react';
import { DataSet, Table, TextField } from 'choerodon-ui/pro';
import { ContentPro as Content } from '@choerodon/boot';
import PromptDataSet from './stores/PromptDataSet';

const { Column } = Table;

function editorRenderer(record) {
  return record.status === 'add' ? <TextField /> : null;
}

export default class Index extends PureComponent {
  promptDS = new DataSet(PromptDataSet);

  render() {
    return (
      <Content>
        <Table
          buttons={['add', 'save', 'delete']}
          dataSet={this.promptDS}
          queryFieldsLimit={4}
        >
          <Column name="promptCode" editor={editorRenderer} sortable />
          <Column name="lang" editor />
          <Column name="description" editor />
          <Column name="moduleCode" editor />
        </Table>
      </Content>
    );
  }
}
