import React from 'react';
import { NumberField, Table } from 'choerodon-ui/pro';

const { Column } = Table;
const numberField = <NumberField step={1} />;

export default ({ dataset }) => (
  <Table
    dataSet={dataset}
  >
    <Column name="accessFlag" editor align="center" />
    <Column name="accessFrequency" editor={numberField} width={100} />
    <Column name="enableFlag" width={100} align="center" />
    <Column name="code" />
    <Column name="name" />
  </Table>
);
