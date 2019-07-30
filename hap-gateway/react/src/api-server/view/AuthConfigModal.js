import React, { PureComponent } from 'react';
import { Form, TextField, Select } from 'choerodon-ui/pro';
import '../index.scss';

export default class AuthConfigModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      authType: props.apiServer.current.get('authType'),
      grantType: props.apiServer.current.get('grantType'),
    };
  }

  handleChangeAuthType = (value) => {
    this.setState({
      authType: value,
    });
  };

  handleChangeGrantType = (value) => {
    this.setState({
      grantType: value,
    });
  };

  getAuthTypeColumn() {
    switch (this.state.authType) {
      case 'NONE':
        return null;
      case 'BASIC':
        return [
          <TextField name="authUsername" />,
          <TextField name="authPassword" />,
        ];
      case 'OAUTH2':
        return [
          <Select name="grantType" onChange={this.handleChangeGrantType} clearButton={false} />,
          this.getGrantTypeColumn(),
        ];
      default:
        return null;
    }
  }

  getGrantTypeColumn() {
    switch (this.state.grantType) {
      case 'PASSWORD':
        return [
          <TextField name="authUsername" />,
          <TextField name="authPassword" />,
          <TextField name="clientId" />,
          <TextField name="clientSecret" />,
          <TextField name="accessTokenUrl" />,
        ];
      case 'CLIENT':
        return [
          <TextField name="clientId" />,
          <TextField name="clientSecret" />,
          <TextField name="accessTokenUrl" />,
        ];
      default:
        return null;
    }
  }

  render() {
    return (
      <Form labelWidth={125} columns={2} dataSet={this.props.apiServer}>
        <Select name="authType" colSpan={this.state.authType === 'OAUTH2' ? 1 : 2} onChange={this.handleChangeAuthType} />
        {this.getAuthTypeColumn()}
      </Form>
    );
  }
}
