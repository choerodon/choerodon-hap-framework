import React, { PureComponent } from 'react';
import { ContentPro as Content, $l } from '@choerodon/boot';
import { Button, DataSet, Modal, Table } from 'choerodon-ui/pro';
import ClientDataSet from './stores/ClientDataSet';
import ClientModal from './view/ClientModal';

const { Column } = Table;
const modalKeys = Modal.key();

export default class Index extends PureComponent {
  clientDS = new DataSet(ClientDataSet);

  created;

  /**
   * 弹窗关闭时,去掉未保存的 created
   */
  handleLineModalOnCancel = async () => {
    if (this.created) {
      this.clientDS.remove(this.created);
    } else {
      this.clientDS.current.reset();
    }
  };

  /**
   * 保存 LOV
   * @returns {Promise<boolean>}
   */
  handleLineModalOnOk = async () => {
    if (await this.clientDS.current.validate()) {
      await this.clientDS.submit();
      await this.clientDS.query();
    } else {
      return false;
    }
  };

  /**
   * 打开编辑或新建 LOV 的弹窗
   * @param isNew 是否为新建
   */
  openLineModal(isNew = false) {
    if (isNew) {
      this.created = this.clientDS.create();
    } else {
      this.created = null;
    }

    Modal.open({
      modalKeys,
      title: isNew ? $l('hap.new') : $l('hap.edit'),
      okText: $l('hap.save'),
      drawer: true,
      destroyOnClose: true,
      style: { width: 850 },
      children: (
        <ClientModal clientDS={this.clientDS} isNew={isNew} />
      ),
      onOk: this.handleLineModalOnOk,
      onCancel: this.handleLineModalOnCancel,
    });
  }

  addButton = <Button funcType="flat" color="blue" icon="add" onClick={() => this.openLineModal(true)}>{$l('hap.add')}</Button>;


  /**
   * 渲染内容
   */
  render() {
    return (
      <Content>
        <Table dataSet={this.clientDS} buttons={[this.addButton, 'delete']} queryFieldsLimit={2}>
          <Column name="clientId" />
          <Column name="clientSecret" />
          <Column name="authorizedGrantTypes" />
          <Column
            renderer={({ record, text, name }) => (
              <Button funcType="flat" icon="mode_edit" onClick={() => this.openLineModal()} />
            )}
            header={$l('hap.edit')}
            align="center"
            lock="right"
            width={150}
          />
        </Table>
      </Content>
    );
  }
}
