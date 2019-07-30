import React, { Component } from 'react';
import { inject, observer } from 'mobx-react';
import { axiosPro as axios, $l } from '@choerodon/boot';
import { Form, Icon, NumberField, Select, TextField, Lov, TextArea } from 'choerodon-ui/pro';

const { Option } = Select;

export default ({ clientDS, isNew }) => {
  async function changePassword() {
    const id = clientDS.current.get('id');
    const temp = await axios.post('/common/generator/uuid', {
      id,
    });
    clientDS.current.set('clientSecret', temp);
  }

  function showPassword() {
    if (!isNew) {
      return (
        <TextField
          name="clientSecret"
          colSpan={2}
          disabled={!isNew}
          suffix={<Icon type="update" onClick={() => changePassword()} />}
        />
      );
    } else {
      return null;
    }
  }

  return (
    <Form dataSet={clientDS} columns={2} labelWidth={200} style={{ marginRight: 80 }}>
      <TextField name="clientId" colSpan={2} required disabled={!isNew} />
      {showPassword()}
      <Select name="authorizedGrantTypes" colSpan={2} required clearButton>
        <Option value="implicit">implicit</Option>
        <Option value="client_credentials">client_credentials</Option>
        <Option value="password">password</Option>
        <Option value="authorization_code">authorization_code</Option>
        <Option value="refresh_token">refresh_token</Option>
      </Select>
      <NumberField placeholder={$l('oauth2clientdetails.refreshvaliditydescribe')} name="accessTokenValidity" step={1} min={0} />
      <NumberField placeholder={$l('oauth2clientdetails.refreshvaliditydescribe')} name="refreshTokenValidity" step={1} min={0} />
      <TextField name="autoApprove" />
      <TextField name="scope" />
      <TextField name="redirectUri" colSpan={2} />
      <Lov name="role" colSpan={2} />
      <TextArea name="additionalInformation" colSpan={2} resize="both" />
    </Form>
  );
};
