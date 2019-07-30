import React from 'react';
import { Table } from 'choerodon-ui/pro';

const { Column } = Table;

export default ({ dataset }) => (
  <Table
    dataSet={dataset}
  >
    <Column name="code" />
    <Column name="name" />
    <Column name="enableFlag" width={100} />
  </Table>
);
