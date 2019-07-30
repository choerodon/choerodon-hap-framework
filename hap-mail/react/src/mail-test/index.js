import React, { PureComponent } from 'react';
import { inject, observer } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Button, DataSet, Form, Lov, Modal, Table, Tabs, TextField } from 'choerodon-ui/pro';
import { axiosPro as axios, $l } from '@choerodon/boot';
import MailTestDataSet from './stores/MailTestDataSet';
import MailTestModal from './view/MailTestModal';
import MailFileDataSet from './stores/MailFileDataSet';
import MailAttachmentDataSet from './stores/MailAttachmentDataSet';

const { Column } = Table;
const textField = <TextField />;
const { TabPane } = Tabs;
const modalKey = Modal.key();


export default class Index extends PureComponent {
  state = {
    disable: false,
  };

  created;

  modal;

  mailTestDS = new DataSet(MailTestDataSet);

  mailFileDS = new DataSet(MailFileDataSet);

  mailAttachmentDS = new DataSet(MailAttachmentDataSet);

  handleOnCancelTestDrawer = () => {
    if (this.created) {
      this.mailTestDS.remove(this.created);
    }
  };

  handleOnOkTestDrawer = async () => {
    if (await this.mailTestDS.current.validate()) {
      await this.mailTestDS.submit();
      await this.mailTestDS.query();
    } else {
      return false;
    }
  };

  handleOnSave() {
    this.setState({
      disable: true,
    });
    const receivers = this.mailTestDS.current.get('receivers');
    const attachments = this.mailTestDS.current.get('attachments');
    const mode = this.mailTestDS.current.get('mode');
    const templateCode = this.mailTestDS.current.get('templateCode');
    axios.post('/sys/message/sendTest', {
      attachments,
      mode,
      receivers,
      templateCode,
    }).then((res) => {
      this.setState({ disable: false });
      if (res.success === true) {
        Modal.confirm($l('hap.tip.success'));
      } else {
        Modal.confirm(res.message);
      }
    });
  }

  closeModal() {
    this.modal.close();
  };

  handleSave = async () => {
    const selected = this.mailAttachmentDS.data;
    const arr = [];
    selected.forEach((record) => {
      const fileId = record.get('fileId');
      arr.push(fileId);
    });
    this.mailTestDS.current.set('attachments', arr);
    await this.mailAttachmentDS.submit();
    await this.mailFileDS.unSelectAll();
    await this.mailFileDS.query();
    this.modal.close();
  };

  openTestModal(isNew = false) {
    const { disable } = this.state;
    this.modal = Modal.open({
      modalKey,
      title: $l('mailtest.addattachment'),
      drawer: true,
      destroyOnClose: true,
      children: (
        <MailTestModal modal={this.modal} dataSet1={this.mailAttachmentDS} dataSet2={this.mailFileDS} />
      ),
      footer: [
        <Button disabled={disable} color="blue" onClick={() => this.handleSave()}>{$l('hap.save')}</Button>,
        <Button style={{ marginLeft: 10 }} onClick={() => this.closeModal()}>
          {$l('hap.close')}
        </Button>,
      ],
      onCancel: this.handleOnCancelTestDrawer,
      onOk: this.handleOnOkTestDrawer,
      style: {
        width: 750,
      },
    });
  }

  /**
   * 渲染EmailAccount表格内容
   */
  renderAccountTable() {
    const { disable } = this.state;
    return (
      <Form dataSet={this.mailTestDS} labelWidth={100} style={{ marginLeft: 'auto', marginRight: 'auto', width: 400 }}>
        <TextField name="receivers" required="true" />
        <Lov name="template" required="true" clearButton />
        <Button color="blue" onClick={() => this.openTestModal()}>{$l('mailtest.addattachment')}</Button>
        <Button color="blue" onClick={() => this.handleOnSave()} loading={disable}>{$l('hap.submit')}</Button>
      </Form>
    );
  }

  render() {
    return (
      <Tabs type="card">
        <TabPane tab={$l('message.templatetest')} key="1">
          {this.renderAccountTable()}
        </TabPane>
      </Tabs>
    );
  }
}
