import React, { Component } from 'react';
import { Table } from 'choerodon-ui/pro';

const { Column } = Table;

export default ({ dataSet }) => (
  <Table
    dataSet={dataSet}
  >
    <Column name="messageType" />
    <Column name="messageAddress" />
  </Table>
);
