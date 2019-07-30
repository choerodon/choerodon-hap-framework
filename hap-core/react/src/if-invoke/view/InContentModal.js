import React, { Component } from 'react';
import { inject, observer } from 'mobx-react';
import { Form, Select, TextArea, TextField } from 'choerodon-ui/pro';

const { Option } = Select;

export default props => (
  <Form dataSet={props.dataSet} columns={2} labelWidth={100} style={{ marginRight: 80 }}>
    <TextField name="referer" disabled />
    <TextField name="userAgent" disabled />
    <TextField name="requestHeaderParameter" colSpan={2} disabled />
    <TextArea name="requestBodyParameter" style={{ height: 150 }} resize="both" disabled />
    <TextArea name="responseContent" style={{ height: 150 }} resize="both" disabled />
    <TextArea name="stackTrace" colSpan={2} style={{ height: 150 }} resize="both" disabled />
  </Form>
);
