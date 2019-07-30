import React from 'react';
import { CheckBox, NumberField, Table, TextField } from 'choerodon-ui/pro';

const { Column } = Table;
const textField = <TextField />;
const numberField = <NumberField />;
const checkBox = <CheckBox value="Y" unCheckedValue="N" />;

function editorRenderer(record) {
  return record.status === 'add' ? textField : null;
}

export default ({ dataset }) => (
  <Table
    buttons={['add', 'save', 'delete']}
    dataSet={dataset}
    queryFieldsLimit={2}
  >
    <Column name="configCode" editor />
    <Column name="description" editor />
    <Column name="host" editor />
    <Column name="port" editor />
    <Column name="useWhiteList" editor align="center" />
    <Column name="enable" editor align="center" />
  </Table>
);
