import React, { Component } from 'react';
import { inject, observer } from 'mobx-react';
import { Table } from 'choerodon-ui/pro';

const { Column } = Table;

export default props => (
  <Table
    dataSet={props.dataSet}
    queryFieldsLimit={2}
  >
    <Column name="fileName" />
    <Column name="fileSize" />
    <Column name="fileType" />
  </Table>
);
