import React, { PureComponent } from 'react';
import { Button, DataSet, Form, Icon, Modal, NumberField, Select, Table, TextField, Tooltip } from 'choerodon-ui/pro';
import { $l, axiosPro as axios } from '@choerodon/boot';
import '../index.scss';
import ApiServerModal from './ApiServerModal';
import ApiInterfaceModal from './ApiInterfaceModal';
import ApiInterfaceDS from '../stores/ApiInterfaceDS';
import ApiServiceDS from '../stores/ApiServiceDS';


const { Column } = Table;
const { Option } = Select;
const modalKey = Modal.key();
const modalKey02 = Modal.key();

export default class ApiAppliEditModal extends PureComponent {
  constructor(props) {
    super(props);
    this.apiApplicationDS = props.apiApplicationDS;
    this.apiServiceDS = props.apiServiceDS;
    this.isEdit = props.isEdit;

    this.apiServiceDS02 = new DataSet(ApiServiceDS);
    this.apiInterfaceDS = new DataSet(ApiInterfaceDS);
    this.apiServerId = -1;
  }

  addServer = () => {
    const { selected } = this.apiServiceDS02;
    selected.forEach(record => (
      this.apiServiceDS.unshift(record)
    ));
  };


  bindServer = () => {
    const exitsCodes = this.apiServiceDS.data.map(record => record.get('code')).join(',');
    if (exitsCodes.length > 0) {
      this.apiServiceDS02.queryUrl = `/sys/application/app/fetchNotServer?exitsCodes=${exitsCodes}&`;
    } else {
      this.apiServiceDS02.queryUrl = '/sys/application/app/fetchNotServer?';
    }
    this.apiServiceDS02.query();
    Modal.open({
      key: modalKey,
      title: $l('hap.edit'),
      drawer: true,
      children: (
        <ApiServerModal dataset={this.apiServiceDS02} />
      ),
      style: { width: 600 },
      okText: $l('hap.confirm'),
      onOk: this.addServer,
    });
  };

  unbindServer = () => {
    const { selected } = this.apiServiceDS;
    selected.forEach(record => (
      this.apiServiceDS.remove(record)
    ));
  };

  savaInterface = () => {
    const interfaces = this.apiInterfaceDS.data.map((record) => {
      const obj = {
        accessFlag: record.get('accessFlag'),
        accessFrequency: record.get('accessFrequency'),
        clientId: record.get('clientId'),
        id: record.get('id'),
        objectVersionNumber: record.get('objectVersionNumber'),
      };
      return {
        apiAccessLimit: obj,
        code: record.get('code'),
      };
    });
    this.apiServiceDS.current.set('interfaces', interfaces);
  };

  handleCancel = () => {
    this.apiInterfaceDS.reset();
  };


  openApiInterfaceModal(record) {
    const clientId = this.apiApplicationDS.current.get('clientId');
    const serverId = record.get('serverId');
    if (this.apiServerId !== serverId) {
      this.apiInterfaceDS.queryUrl = `sys/gateway/interface/getInterfacesByServerCode/${clientId}/${serverId}`;
      this.apiInterfaceDS.query();
      this.apiServerId = serverId;
    }
    Modal.open({
      key: modalKey02,
      title: $l('hap.edit'),
      drawer: true,
      children: (
        <ApiInterfaceModal dataset={this.apiInterfaceDS} />
      ),
      style: { width: 700 },
      okText: $l('hap.confirm'),
      onOk: this.savaInterface,
      onCancel: this.handleCancel,
    });
  }

  addBtn = (
    <Button
      funcType="flat"
      color="blue"
      icon="add_box"
      onClick={() => this.bindServer()}
    >
      {$l('api.server.binding')}
    </Button>
  );

  delBtn = (
    <Button
      funcType="flat"
      color="blue"
      icon="delete"
      onClick={() => this.unbindServer()}
    >
      {$l('api.server.unbinding')}
    </Button>
  );

  refreshClientSecret = () => axios.post('/common/generator/uuid')
    .then(valid => this.apiApplicationDS.current.set('client.clientSecret', valid));

  openRefreshClientSecret() {
    Modal.confirm({
      style: {
        width: '3rem',
      },
      children: $l('oauth2clientdetails.resetpasswd'),
      onOk: this.refreshClientSecret,
    });
  }

  render() {
    return (
      <div>
        <Form dataSet={this.apiApplicationDS} style={{ width: '8rem' }} columns={2} labelWidth={150}>
          <TextField name="code" disabled={this.isEdit} colSpan={1} />
          <TextField name="name" />
          <TextField name="clientId" disabled />
          <TextField name="clientSecret" disabled suffix={<Icon type="autorenew" onClick={() => this.openRefreshClientSecret()} />} />
          <NumberField name="client.accessTokenValidity" step={1} placeholder={$l('oauth2clientdetails.refreshvaliditydescribe')} />
          <NumberField name="client.refreshTokenValidity" step={1} placeholder={$l('oauth2clientdetails.refreshvaliditydescribe')} />
          <TextField name="client.redirectUri" colSpan={2} />
          <Select name="client.authorizedGrantTypes" colSpan={2}>
            <Option value="implicit">implicit</Option>
            <Option value="client_credentials">client_credentials</Option>
            <Option value="password">password</Option>
            <Option value="authorization_code">authorization_code</Option>
            <Option value="refresh_token">refresh_token</Option>
          </Select>
        </Form>
        <div className="gateway-wrap">
          <div className="gateway-label">{$l('api.applicationserver.serverlist')}</div>
          <div className="gateway-table">
            <Table
              buttons={[this.addBtn, this.delBtn]}
              dataSet={this.apiServiceDS}
            >
              <Column name="code" />
              <Column name="name" />
              <Column name="enableFlag" width={100} />
              <Column
                name="edit"
                align="center"
                width={100}
                header={$l('hap.action')}
                renderer={({ record }) => (
                  <Tooltip title={$l('hap.edit')}>
                    <Button
                      funcType="flat"
                      icon="mode_edit"
                      onClick={() => this.openApiInterfaceModal(record)}
                    />
                  </Tooltip>
                )}
              />
            </Table>
          </div>
        </div>
      </div>
    );
  }
}
