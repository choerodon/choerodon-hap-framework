import React, { Component } from 'react';
import { $l } from '@choerodon/boot';
import { CheckBox, Form, IntlField, Modal, Select, Table, TextArea, TextField } from 'choerodon-ui/pro';
import '../index.scss';

const { Column } = Table;
const key = Modal.key();
const textField = <TextField />;
const intlField = <IntlField />;
const checkBox = <CheckBox />;
const select = <Select />;

export default class LineModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      interfaceType: this.props.headerDS.current.get('interfaceType'),
      authFlag: this.props.headerDS.current.get('authFlag'),
      authType: this.props.headerDS.current.get('authType'),
      grantType: this.props.headerDS.current.get('grantType'),
    };
  }

  handleChangeInterfaceType = (value) => {
    this.setState({
      interfaceType: value,
    });
  };

  handleChangeAuthFlag = (value) => {
    this.setState({
      authFlag: value,
    });
  };

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


  getFieldByInterfaceType() {
    switch (this.state.interfaceType) {
      case 'PLSQL':
        return (
          [
            <TextField label={$l('interface.mapperclass')} dataSet={this.props.headerDS} name="mapperClass" colSpan={2} />,
            <CheckBox label={$l('hap.enableflag')} dataSet={this.props.headerDS} name="enableFlag" colSpan={2} />,
          ]
        );
      case 'SOAP':
        return (
          [
            <TextField label={$l('interface.namespace')} dataSet={this.props.headerDS} name="namespace" colSpan={2} />,
            <TextArea
              dataSet={this.props.headerDS}
              label={$l('interface.soapheader')}
              rows={6}
              name="bodyHeader"
              rowSpan={3}
              resizable
              colSpan={2}
            />,
            <TextArea dataSet={this.props.headerDS} label={$l('interface.soaptail')} rows={6} name="bodyTail" rowSpan={3} resizable colSpan={2} />,
            <TextField label="soapAction" dataSet={this.props.headerDS} name="soapAction" colSpan={2} />,
            <CheckBox label={$l('hap.enableflag')} dataSet={this.props.headerDS} name="enableFlag" colSpan={2} />,
            <Select
              label={$l('interface.authflag')}
              dataSet={this.props.headerDS}
              onChange={this.handleChangeAuthFlag}
              name="authFlag"
              colSpan={2}
              clearButton={false}
            />,
            this.getFieldByAuthFlag(),
          ]
        );
      case 'REST':
        return (
          [
            <TextField label={$l('interface.mapperclass')} dataSet={this.props.headerDS} name="mapperClass" colSpan={2} />,
            <TextField label={$l('interface.requestcontenttype')} dataSet={this.props.headerDS} name="requestContentType" colSpan={2} />,
            <TextField dataSet={this.props.headerDS} label={$l('interface.requestaccept')} name="requestAccept" colSpan={2} />,
            <CheckBox label={$l('hap.enableflag')} dataSet={this.props.headerDS} name="enableFlag" colSpan={2} />,
            <Select label={$l('interface.requestmethod')} dataSet={this.props.headerDS} name="requestMethod" colSpan={2} clearButton={false} />,
            <Select
              label={$l('interface.authflag')}
              dataSet={this.props.headerDS}
              name="authFlag"
              onChange={this.handleChangeAuthFlag}
              colSpan={2}
              clearButton={false}
            />,
            this.getFieldByAuthFlag(),
          ]
        );
      default:
        return null;
    }
  }

  getFieldByAuthFlag() {
    switch (this.state.authFlag) {
      case 'N':
        return null;
      case 'Y':
        return (
          [
            <Select
              label={$l('interface.authtype')}
              dataSet={this.props.headerDS}
              onChange={this.handleChangeAuthType}
              name="authType"
              colSpan={2}
              clearButton={false}
            />,
            this.getFieldByAuthType(),
          ]
        );
      default:
        return null;
    }
  }

  getFieldByAuthType() {
    switch (this.state.authType) {
      case 'BASIC_AUTH':
        return (
          [
            <TextField dataSet={this.props.headerDS} label={$l('interface.checkusername')} name="authUsername" colSpan={2} />,
            <TextField dataSet={this.props.headerDS} label={$l('interface.checkpassword')} name="authPassword" colSpan={2} />,
          ]
        );
      case 'OAUTH2':
        return (
          [
            <TextField dataSet={this.props.headerDS} label={$l('interface.accesstokenurl')} name="accessTokenUrl" colSpan={2} />,
            <Select
              dataSet={this.props.headerDS}
              label={$l('interface.granttype')}
              name="grantType"
              onChange={this.handleChangeGrantType}
              colSpan={2}
              clearButton={false}
            />,
            <TextField dataSet={this.props.headerDS} label={$l('interface.clientid')} name="clientId" colSpan={2} />,
            <TextField dataSet={this.props.headerDS} label={$l('interface.clientsecret')} name="clientSecret" colSpan={2} />,
            this.getFieldByGrantType(),
          ]
        );
      default:
        return null;
    }
  }

  getFieldByGrantType() {
    switch (this.state.grantType) {
      case 'password':
        return (
          [
            <TextField dataSet={this.props.headerDS} label={$l('interface.checkusername')} name="authUsername" colSpan={2} />,
            <TextField dataSet={this.props.headerDS} label={$l('interface.checkpassword')} name="authPassword" colSpan={2} />,
            <TextField dataSet={this.props.headerDS} label="scope" name="scope" colSpan={2} />,
          ]
        );
      case 'client_credentials':
        return (<TextField dataSet={this.props.headerDS} label="scope" name="scope" colSpan={2} />);
      default:
        return null;
    }
  }

  renderHead() {
    return (
      <Form labelWidth={120} columns={6} style={{ marginRight: 75 }}>
        <TextField
          label={$l('interface.systemcode')}
          dataSet={this.props.headerDS}
          name="interfaceCode"
          required
          colSpan={2}
          disabled={!this.props.isNew}
        />
        <IntlField label={$l('interface.systemname')} dataSet={this.props.headerDS} name="name" required colSpan={2} />
        <Select
          label={$l('interface.interfacetype')}
          dataSet={this.props.headerDS}
          name="interfaceType"
          onChange={this.handleChangeInterfaceType}
          required
          colSpan={2}
          clearButton={false}
        />

        <TextField label={$l('interface.systemurl')} dataSet={this.props.headerDS} name="domainUrl" required colSpan={2} />
        <Select label={$l('interface.requestformat')} dataSet={this.props.headerDS} name="requestFormat" colSpan={2} clearButton={false} />

        {this.getFieldByInterfaceType()}


      </Form>
    );
  }

  renderTable() {
    return (
      <div className="wrap">
        <div className="label">{$l('interface.list')}</div>
        <div className="table">
          <Table buttons={['add', 'delete']} dataSet={this.props.lineDS}>
            <Column name="lineCode" width={180} editor={textField} />
            <Column name="lineName" width={180} editor={intlField} />
            <Column name="iftUrl" width={180} editor={textField} />
            <Column name="enableFlag" width={80} editor={checkBox} />
          </Table>
        </div>
      </div>
    );
  }

  render() {
    return (
      <div className="interface-config-modal">
        {this.renderHead()}
        {this.renderTable()}
      </div>
    );
  }
}
