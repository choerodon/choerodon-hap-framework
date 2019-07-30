import React, { PureComponent } from 'react';
import { Form, TextField, Table, Select, CheckBox, Button, Modal } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';
import ApiInterfaceModal from './ApiInterfaceModal';
import AuthConfigModal from './AuthConfigModal';
import '../index.scss';

const { Column } = Table;
const modalKey = {
  serverKey: Modal.key(),
  authKey: Modal.key(),
  interfaceKey: Modal.key(),
};
const baseMappingUrl = '/api/rest';

export default class ApiServerModal extends PureComponent {
  constructor(props) {
    super(props);
    const { serviceType, wssPasswordType, mappingUrl } = props.apiServer.current.data;
    this.state = {
      serviceType,
      wssPasswordType,
    };
    this.serverMappingUrl = mappingUrl;
  }

  serverMappingUrl;

  created = null;

  handleChangeServiceType = (value) => {
    this.setState({
      serviceType: value,
    });
  };

  handleChangeWssPasswordType = (value) => {
    this.setState({
      wssPasswordType: value,
    });
  };

  handleChangeMappingUrl = (value) => {
    this.serverMappingUrl = value;
  };

  getServiceTypeColumn() {
    if (this.state.serviceType === 'SOAP') {
      return [
        <TextField name="namespace" />,
        <CheckBox name="elementFormDefault" value="qualified" unCheckedValue="unqualified" />,
        <Select name="wssPasswordType" onChange={this.handleChangeWssPasswordType} clearButton={false} />,
      ];
    } else {
      return [];
    }
  }

  getWssPasswordTypeColumn() {
    const { wssPasswordType } = this.state;
    if (wssPasswordType === 'PasswordText' || wssPasswordType === 'PasswordDigest') {
      return [
        <TextField name="username" />,
        <TextField name="password" />,
      ];
    } else {
      return [];
    }
  }

  handleOnOkApiInterfaceModal = () => (this.props.apiInterface.current.validate());

  handleOnCancelApiInterfaceModal = () => {
    const { apiInterface } = this.props;
    if (this.created) {
      apiInterface.remove(this.created);
      this.created = null;
    } else if (apiInterface.current.status !== 'add') {
      apiInterface.current.reset();
    }
  };

  /**
   * 打开 ApiInterface 弹窗
   */
  openApiInterfaceModal(isNew = false) {
    const { apiInterface } = this.props;
    if (isNew) {
      this.created = apiInterface.create();
    }
    let baseUrl;
    if (this.serverMappingUrl) {
      baseUrl = `${baseMappingUrl}/${this.serverMappingUrl}/`;
    } else {
      baseUrl = `${baseMappingUrl}/`;
    }

    Modal.open({
      key: modalKey.interfaceKey,
      title: $l('hap.edit'),
      drawer: true,
      destroyOnClose: true,
      onOk: this.handleOnOkApiInterfaceModal,
      onCancel: this.handleOnCancelApiInterfaceModal,
      children: (
        <ApiInterfaceModal
          interfaceDataSet={apiInterface}
          serviceType={this.props.apiServer.current.get('serviceType')}
          baseUrl={baseUrl}
        />
      ),
      style: {
        width: 600,
      },
    });
  }

  AuthConfigModalCancel = () => {
    const { apiServer } = this.props;

    apiServer.current.reset();
  };

  /**
   * 打开 AuthConfig 弹窗
   */
  openAuthConfigModal() {
    Modal.open({
      key: modalKey.authKey,
      title: $l('server.auth.config'),
      drawer: true,
      destroyOnClose: true,
      children: (
        <AuthConfigModal apiServer={this.props.apiServer} />
      ),
      style: {
        width: 600,
      },
      onCancel: this.AuthConfigModalCancel,
    });
  }

  addBtn = <Button icon="playlist_add" funcType="flat" color="blue" onClick={() => this.openApiInterfaceModal(true)}>{$l('hap.new')}</Button>;

  render() {
    return (
      <div>
        <Form columns={3} labelWidth={100} dataSet={this.props.apiServer}>
          <TextField name="code" disabled={!this.props.isNew} />
          <TextField name="name" />
          <Select name="serviceType" onChange={this.handleChangeServiceType} disabled={!this.props.isNew} clearButton={false} />
          <TextField name="domainUrl" />
          <TextField addonBefore={baseMappingUrl} name="mappingUrl" onChange={this.handleChangeMappingUrl} />
          <CheckBox name="enableFlag" value="Y" unCheckedValue="N" />

          {this.getServiceTypeColumn().filter(v => v)}

          {this.getWssPasswordTypeColumn().filter(v => v)}

          <Button
            label={$l('server.auth')}
            color="blue"
            icon="settings"
            onClick={() => this.openAuthConfigModal()}
          >
            {$l('server.auth.config')}
          </Button>
          <Table
            label={$l('interface.list')}
            buttons={[this.addBtn, 'delete']}
            dataSet={this.props.apiInterface}
            newLine
            colSpan={3}
          >
            <Column name="code" />
            <Column name="interfaceUrl" />
            <Column
              name="mappingUrl"
              renderer={({ record }) => {
                if (this.serverMappingUrl) {
                  return `${baseMappingUrl}/${this.serverMappingUrl}/${record.get('mappingUrl')}`;
                } else {
                  return `${baseMappingUrl}/${record.get('mappingUrl')}`;
                }
              }}
            />
            <Column name="enableFlag" />
            <Column
              header={$l('hap.action')}
              align="center"
              width={100}
              renderer={() => (
                <Button
                  funcType="flat"
                  icon="mode_edit"
                  onClick={() => this.openApiInterfaceModal()}
                />
              )}
            />
          </Table>
        </Form>
      </div>
    );
  }
}
