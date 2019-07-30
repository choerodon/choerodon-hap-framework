import React, { PureComponent } from 'react';
import { inject, observer } from 'mobx-react';
import { $l, ContentPro as Content } from '@choerodon/boot';
import { withRouter } from 'react-router-dom';
import { Button, DataSet, Modal, Table, Tabs, TextField } from 'choerodon-ui/pro';
import InboundDataSet from './stores/InboundDataSet';
import OutboundDataSet from './stores/OutboundDataSet';
import InContentModal from './view/InContentModal';
import OutContentModal from './view/OutContentModal';


const { Column } = Table;
const textField = <TextField />;
const { TabPane } = Tabs;
const modalKey = Modal.key();


function time({ record }) {
  return `${record.get('responseTime')}ms`;
}

export default class Index extends PureComponent {
  created;

  modal;

  inboundDS = new DataSet(InboundDataSet);

  outboundDS = new DataSet(OutboundDataSet);


  openInContentModal(isNew = false) {
    this.modal = Modal.open({
      modalKey,
      title: $l('interface.invoke.detail'),
      drawer: true,
      destroyOnClose: true,
      children: (
        <InContentModal modal={this.modal} dataSet={this.inboundDS} />
      ),
      okText: $l('hap.close'),
      okCancel: false,
      style: {
        width: 850,
      },
    });
  }

  openOutContentModal(isNew = false) {
    this.modal = Modal.open({
      modalKey,
      title: $l('interface.invoke.detail'),
      drawer: true,
      destroyOnClose: true,
      children: (
        <OutContentModal modal={this.modal} dataSet={this.outboundDS} />
      ),
      okText: $l('hap.close'),
      okCancel: false,
      style: {
        width: 850,
      },
    });
  }


  renderInTable() {
    return (
      <Table dataSet={this.inboundDS} labelWidth={100} queryFieldsLimit={5}>
        <Column name="interfaceName" />
        <Column name="interfaceUrl" />
        <Column name="requestTime" />
        <Column name="requestMethod" />
        <Column name="ip" />
        <Column name="responseTime" renderer={time} />
        <Column name="requestStatus" />
        <Column
          renderer={({ record, text, name }) => (
            <Button funcType="flat" icon="work_log" onClick={() => this.openInContentModal()} />
          )}
          header={$l('interface.invoke.detail')}
        />
      </Table>
    );
  }

  renderOutTable() {
    return (
      <Table dataSet={this.outboundDS} labelWidth={100} queryFieldsLimit={5}>
        <Column name="interfaceName" />
        <Column name="interfaceUrl" />
        <Column name="requestTime" />
        <Column name="responseCode" />
        <Column name="responseTime" renderer={time} />
        <Column name="requestStatus" />
        <Column
          renderer={({ record, text, name }) => (
            <Button funcType="flat" icon="work_log" onClick={() => this.openOutContentModal()} />
          )}
          header={$l('interface.invoke.detail')}
        />
      </Table>
    );
  }

  render() {
    return (
      <Content>
        <Tabs type="card">
          <TabPane tab={$l('interface.invoke.inbound')} key="1">
            {this.renderInTable()}
          </TabPane>
          <TabPane tab={$l('interface.invoke.outbound')} key="2">
            {this.renderOutTable()}
          </TabPane>
        </Tabs>
      </Content>
    );
  }
}
