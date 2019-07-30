import React, { Component, PureComponent } from 'react';
import { withRouter } from 'react-router-dom';
import { Button, DataSet, Modal, Table, Tooltip } from 'choerodon-ui/pro';
import { $l, ContentPro as Content } from '@choerodon/boot';
import CompanyDataSet from './stores/CompanyDataSet';
import CompanyModal from './view/CompanyModal';

const { Column } = Table;
const modalKey = Modal.key();

export default class Index extends PureComponent {
  created;

  companyDS = new DataSet(CompanyDataSet);


  onAfterClose = () => {
    if (this.created) {
      this.companyDS.remove(this.created);
    } else {
      this.companyDS.current.reset();
    }
  };

  handleOnOkCompanyDrawer = async () => {
    if (await this.companyDS.current.validate()) {
      await this.companyDS.submit();
      await this.companyDS.query();
    } else {
      return false;
    }
  };

  openCompanyModal(isNew = false) {
    if (isNew) {
      this.created = this.companyDS.create();
    } else {
      this.created = null;
    }
    Modal.open({
      modalKey,
      title: isNew ? $l('hap.new') : $l('hap.edit'),
      drawer: true,
      destroyOnClose: true,
      okText: $l('hap.save'),
      children: (
        <CompanyModal isNew={isNew} dataSet={this.companyDS} />
      ),
      onOk: this.handleOnOkCompanyDrawer,
      afterClose: this.onAfterClose,
      style: {
        width: 900,
      },
    });
  }

  add = (
    <Button
      funcType="flat"
      color="blue"
      icon="playlist_add"
      onClick={() => this.openCompanyModal(true)}
    >
      {$l('hap.add')}
    </Button>
  );

  render() {
    return (
      <Content>
        <Table
          dataSet={this.companyDS}
          queryFieldsLimit={4}
          buttons={[this.add, 'delete']}
        >
          <Column name="companyCode" />
          <Column name="companyShortName" />
          <Column name="companyFullName" />
          <Column name="companyLevelId" />
          <Column name="companyType" />
          <Column name="positionName" />
          <Column name="parentCompanyName" />
          <Column name="startDateActive" />
          <Column name="endDateActive" />
          <Column
            header={$l('hap.action')}
            align="center"
            width={120}
            lock="right"
            renderer={({ record, text, name }) => (
              <Tooltip title={$l('hap.edit')}>
                <Button funcType="flat" icon="mode_edit" onClick={() => this.openCompanyModal()} />
              </Tooltip>
            )}
          />
        </Table>
      </Content>
    );
  }
}
