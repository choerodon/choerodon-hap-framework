import React from 'react';
import { CheckBox, Form, IntlField, Lov, TextField } from 'choerodon-ui/pro';

export default ({ dataSet }) => (
  <Form dataSet={dataSet} style={{ width: '4rem' }}>
    <TextField name="unitCode" />
    <Lov name="parent" />
    <IntlField name="name" />
    <IntlField name="description" />
    <CheckBox name="enabledFlag" value="Y" unCheckedValue="N" />
  </Form>
);
