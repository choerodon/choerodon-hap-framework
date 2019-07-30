import React, { Component } from 'react';
import { Form, Lov, Select, TextArea, TextField } from 'choerodon-ui/pro';


export default props => (
  <Form dataSet={props.dataSet} columns={2} labelWidth={100} style={{ marginRight: 80 }}>
    <TextField name="templateCode" required disabled={!props.isNew} />
    <Select name="templateType" required clearButton />
    <TextField name="description" colSpan={2} />
    <Select name="sendType" required />
    <Lov name="account" required />
    <TextField name="subject" colSpan={2} required />
    <TextArea name="content" colSpan={2} required rows={5} />
  </Form>
);
