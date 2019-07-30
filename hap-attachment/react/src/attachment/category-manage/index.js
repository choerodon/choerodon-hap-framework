import React, { PureComponent } from 'react';
import { observer } from 'mobx-react';
import { $l, ContentPro as Content } from '@choerodon/boot';
import { Button, DataSet, Modal, Table, Tooltip } from 'choerodon-ui/pro';
import AttachCategoryDS from './stores/AttachCategoryDS';
import CategoryEditModal from './view/CategoryEditModal';

import './index.scss';

const { Column } = Table;
const modalKey = Modal.key();

@observer
export default class Index extends PureComponent {
  constructor(props) {
    super(props);
    this.dataSet = new DataSet(AttachCategoryDS);
    this.attachCategoryDS = new DataSet(AttachCategoryDS);
    this.attachCategoryDS.queryUrl = '/sys/attachment/category/list';
    this.isCancel = true;
    this.created = null;
    this.parentCategoryId = -1;
  }


  componentDidMount() {
    this.dataSet.query();
  }

  addBtn = (
    <Button
      icon="playlist_add"
      color="blue"
      onClick={() => this.openEditModal(null, false)}
    >
      {$l('hap.new')}
    </Button>
  );

  copyBtn = (
    <Button
      key="copy"
      funcType="flat"
      color="blue"
      icon="autorenew"
      onClick={() => this.dataSet.query()}
    >
      {$l('hap.refresh')}
    </Button>
  );

  setCategory = async (record) => {
    this.dataSet.queryParameter.parentCategoryId = record.get('categoryId');
    await this.dataSet.query();
    this.attachCategoryDS.queryParameter.parentCategoryId = record.get('categoryId');
    await this.attachCategoryDS.query();
  };

  handleClick = async (record) => {
    if (record != null) {
      this.attachCategoryDS.queryParameter.parentCategoryId = record.get('categoryId');
      this.dataSet.queryParameter.parentCategoryId = record.get('categoryId');
    } else {
      this.dataSet.queryParameter.parentCategoryId = null;
      this.attachCategoryDS.queryParameter.parentCategoryId = null;
    }
    this.parentCategoryId = -1;
    await this.dataSet.query();
    await this.attachCategoryDS.query();
  };

  onCancelClose = () => {
    this.isCancel = true;
  };

  onAfterClose = () => {
    if (this.isCancel) {
      if (this.created) {
        this.dataSet.remove(this.created);
      } else {
        this.dataSet.current.reset();
      }
    }
    this.created = null;
    this.isCancel = true;
  };

  saveAttachCategory = async () => {
    if (await this.dataSet.current.validate()) {
      await this.dataSet.submit();
      this.isCancel = false;
      await this.dataSet.query();
    } else {
      return false;
    }
  };

  openEditModal(record, isEdit) {
    let isFolder = true;
    if (!isEdit) {
      this.created = this.dataSet.create();
      this.created.set('parentCategoryId', this.parentCategoryId);
      this.dataSet.unshift(this.created);
    } else {
      isFolder = record.get('leafFlag') === '0';
    }
    Modal.open({
      key: modalKey,
      title: isEdit ? $l('hap.edit') : $l('hap.new'),
      drawer: true,
      children: (
        <CategoryEditModal dataset={this.dataSet} isEdit={isEdit} isFolder={isFolder} />
      ),
      onOk: this.saveAttachCategory,
      onCancel: this.onCancelClose,
      afterClose: this.onAfterClose,
      style: { width: 550 },
      okText: isEdit ? $l('hap.save') : $l('hap.new'),
    });
  }

  renderBreadCrumbs() {
    return (
      <div className="headerdiv">
        <a onClick={this.handleClick.bind(this, null)}>
          Home
        </a>
        {
          this.attachCategoryDS.data.map((r, index) => {
            if (index !== this.attachCategoryDS.totalCount - 1) {
              return (
                <a onClick={this.handleClick.bind(this, r)}>
                  / {r.get('categoryName')}
                </a>
              );
            } else {
              this.parentCategoryId = r.get('categoryId');
              return (
                <span className="active">/ {r.get('categoryName')}</span>
              );
            }
          })
        }
      </div>
    );
  }

  isNotEmpty(obj) {
    return obj !== undefined && obj !== null && obj !== '';
  }

  /**
   * 渲染Tabs
   */
  render() {
    return (
      <Content>
        {this.renderBreadCrumbs()}
        <Table
          buttons={[this.addBtn, this.copyBtn]}
          dataSet={this.dataSet}
        >
          <Column
            name="categoryName"
            renderer={({ record, text }) => {
              if (record.get('leafFlag') === '0') {
                return (
                  <Button
                    funcType="flat"
                    color="blue"
                    onClick={() => this.setCategory(record)}
                  >
                    {text}
                  </Button>
                );
              } else {
                return <span style={{ paddingLeft: '0.1rem' }}>{text}</span>;
              }
            }}
          />
          <Column name="description" />
          <Column name="categoryPath" />
          <Column name="allowedFileType" />
          <Column name="allowedFileSizeDesc" />
          <Column name="sourceType" />
          <Column
            width={140}
            name="isUnique"
            renderer={({ record, text }) => (
              <div>{text}</div>
            )}
          />
          <Column
            align="center"
            width={80}
            minwidth={50}
            lock="right"
            name="edit"
            renderer={({ record }) => {
              if (record.children == null) {
                return (
                  <Tooltip title={$l('hap.edit')}>
                    <Button
                      funcType="flat"
                      icon="mode_edit"
                      onClick={() => this.openEditModal(record, true)}
                    />
                  </Tooltip>
                );
              } else {
                return null;
              }
            }}
            header={$l('hap.edit')}
          />
        </Table>
      </Content>
    );
  }
}
