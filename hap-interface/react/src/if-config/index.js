import React, { PureComponent } from 'react';
import { ContentPro as Content, $l } from '@choerodon/boot';
import { Button, CheckBox, DataSet, Modal, Table } from 'choerodon-ui/pro';
import HeaderDataSet from './stores/HeaderDataSet';
import LineDataSet from './stores/LineDataSet';
import LineModal from './view/LineModal';

const { Column } = Table;
const modalKeys = Modal.key();

export default class Index extends PureComponent {
  lineDS = new DataSet(LineDataSet);

  headerDS = new DataSet({
    ...HeaderDataSet,
    children: {
      lineList: this.lineDS,
    },
  });

  created;

  /**
   * 弹窗关闭时,去掉未保存的 created
   */
  handleLineModalOnCancel = async () => {
    if (this.created) {
      this.headerDS.remove(this.created);
    } else {
      this.headerDS.current.reset();
      this.lineDS.reset();
    }
  };

  /**
   * 保存 LOV
   * @returns {Promise<boolean>}
   */
  handleLineModalOnOk = async () => {
    if (await this.headerDS.current.validate()) {
      await this.headerDS.submit();
      await this.headerDS.query();
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
      this.created = this.headerDS.create();
    } else {
      this.created = null;
    }

    Modal.open({
      modalKeys,
      title: isNew ? $l('hap.new') : $l('hap.edit'),
      okText: $l('hap.save'),
      drawer: true,
      destroyOnClose: true,
      style: { width: 900 },
      children: (
        <LineModal headerDS={this.headerDS} lineDS={this.lineDS} isNew={isNew} />
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
        <Table dataSet={this.headerDS} buttons={[this.addButton, 'delete']} queryFieldsLimit={2}>
          <Column name="interfaceCode" />
          <Column name="name" />
          <Column name="interfaceType" />
          <Column name="domainUrl" />
          <Column name="enableFlag" width={120} />
          <Column name="authFlag" width={120} />
          <Column
            renderer={({ record, text, name }) => (
              <Button funcType="flat" icon="mode_edit" onClick={() => this.openLineModal()} />
            )}
            width={120}
            header={$l('hap.action')}
            align="center"
            lock="right"
          />
        </Table>
      </Content>
    );
  }
}
