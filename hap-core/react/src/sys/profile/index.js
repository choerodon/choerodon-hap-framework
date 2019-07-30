import React, { PureComponent } from 'react';
import { withRouter } from 'react-router-dom';
import { Button, DataSet, Modal, Table, Tooltip } from 'choerodon-ui/pro';
import { $l, ContentPro as Content } from '@choerodon/boot';
import ProfileDataSet from './stores/ProfileDataSet';
import ProfileModal from './view/ProfileModal';
import ProfileValueDataSet from './stores/ProfileValueDataSet';

const modalKey = Modal.key();
const { Column } = Table;


export default class Index extends PureComponent {
  profileValueDS = new DataSet(ProfileValueDataSet);

  profileDS = new DataSet({
    ...ProfileDataSet,
    children: {
      profileValues: this.profileValueDS,
    },
  });

  created;

  /**
   * 关闭弹窗函数
   * 处理新建时关闭，删除该条情况
   */

  onAfterClose = () => {
    if (this.created) {
      this.profileDS.remove(this.created);
    } else {
      this.profileDS.current.reset();
      this.profileValueDS.reset();
    }
  };

  handleOnOkDrawer = async () => {
    if (await this.profileDS.current.validate()) {
      await this.profileDS.submit();
      await this.profileDS.query();
    } else {
      return false;
    }
  };

  /**
   * 打开record弹窗
   * @param {*} isNew 是否为新建
   */
  openProfileModal(isNew = false) {
    if (isNew) {
      this.created = this.profileDS.create();
    } else {
      this.created = null;
    }
    Modal.open({
      modalKey,
      title: isNew ? $l('hap.new') : $l('hap.edit'),
      drawer: true,
      destroyOnClose: true,
      children: (
        <ProfileModal isNew={isNew} profileDS={this.profileDS} profileValueDS={this.profileValueDS} />
      ),
      okText: $l('hap.save'),
      onOk: this.handleOnOkDrawer,
      afterClose: this.onAfterClose,
      style: {
        width: 900,
      },
    });
  }

  render() {
    return (
      <Content>
        <Table
          dataSet={this.profileDS}
          buttons={[
            <Button funcType="flat" color="blue" icon="playlist_add" onClick={() => this.openProfileModal(true)}>{$l('hap.add')}</Button>,
            <Button
              funcType="flat"
              color="blue"
              icon="delete"
              onClick={() => this.profileDS.delete(this.profileDS.selected)}
            >{$l('hap.delete')}
            </Button>,
            'export',
          ]}
          queryFieldsLimit={2}
        >
          <Column name="profileName" />
          <Column name="description" />
          <Column
            header={$l('hap.action')}
            align="center"
            width={120}
            lock="right"
            renderer={({ record, text, name }) => (
              <Tooltip title={$l('hap.edit')}>
                <Button funcType="flat" icon="mode_edit" onClick={() => this.openProfileModal()} />
              </Tooltip>
            )}
          />
        </Table>
      </Content>
    );
  }
}
