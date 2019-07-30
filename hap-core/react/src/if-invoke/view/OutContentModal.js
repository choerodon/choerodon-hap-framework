import React, { Component } from 'react';
import { inject, observer } from 'mobx-react';
import { Form, Select, TextArea } from 'choerodon-ui/pro';

const { Option } = Select;

export default props => (
  <Form dataSet={props.dataSet} columns={2} labelWidth={100} style={{ marginRight: 80 }}>

    <TextArea name="requestParameter" style={{ height: 150 }} resize="both" disabled />
    <TextArea name="responseContent" style={{ height: 150 }} resize="both" disabled />
    <TextArea name="stackTrace" colSpan={2} style={{ height: 150 }} resize="both" disabled />
  </Form>
);
