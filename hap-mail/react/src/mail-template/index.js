import React, { PureComponent } from 'react';
import { withRouter } from 'react-router-dom';
import { Button, DataSet, Modal, Table, TextField, Tooltip } from 'choerodon-ui/pro';
import { $l, ContentPro as Content } from '@choerodon/boot';
import MailTemplateDataSet from './stores/MailTemplateDataSet';
import MailTemplateModal from './view/MailTemplateModal';

const { Column } = Table;
const textField = <TextField />;

const modalKey = Modal.key();

export default class Index extends PureComponent {
  created;

  isCancel;

  mailTemplateDS = new DataSet(MailTemplateDataSet);

  handleOnCancelTemplateDrawer = () => {
    this.isCancel = true;
  };

  handleOnOkTemplateDrawer = async () => {
    this.isCancel = false;
    if (await this.mailTemplateDS.current.validate()) {
      await this.mailTemplateDS.submit();
      await this.mailTemplateDS.query();
    } else {
      return false;
    }
  };

  handleOnCloseTemplateModal = () => {
    if (this.isCancel) {
      // 新建时取消，移除dataSet记录
      if (this.created) {
        this.mailTemplateDS.remove(this.created);
      } else {
        // 修改时取消 重置当前记录数据
        this.mailTemplateDS.current.reset();
      }
    }
  };

  openTemplateModal(isNew = false) {
    if (isNew) {
      this.created = this.mailTemplateDS.create();
    } else {
      this.created = null;
    }
    Modal.open({
      modalKey,
      title: isNew ? $l('hap.add') : $l('hap.edit'),
      drawer: true,
      destroyOnClose: true,
      children: (
        <MailTemplateModal isNew={isNew} dataSet={this.mailTemplateDS} />
      ),
      okText: $l('hap.save'),
      onCancel: this.handleOnCancelTemplateDrawer,
      onOk: this.handleOnOkTemplateDrawer,
      afterClose: this.handleOnCloseTemplateModal,
      style: {
        width: 750,
      },
    });
  }

  add = (
    <Button
      funcType="flat"
      color="blue"
      icon="playlist_add"
      onClick={() => this.openTemplateModal(true)}
    >
      {$l('hap.add')}
    </Button>
  );

  render() {
    return (
      <Content>
        <Table
          dataSet={this.mailTemplateDS}
          queryFieldsLimit={2}
          buttons={[this.add, 'delete']
          }
        >
          <Column name="templateCode" />
          <Column name="description" />
          <Column
            header={$l('hap.action')}
            align="center"
            width={120}
            lock="right"
            renderer={({ record, text, name }) => (
              <Tooltip title={$l('hap.edit')}>
                <Button funcType="flat" icon="mode_edit" onClick={() => this.openTemplateModal()} />
              </Tooltip>
            )}
          />
        </Table>
      </Content>
    );
  }
}
