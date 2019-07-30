import React, { Component, PureComponent } from 'react';
import { inject, observer } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Button, Col, DataSet, Modal, Row, Table, TextField, Tooltip, Tree } from 'choerodon-ui/pro';
import { $l, ContentPro as Content } from '@choerodon/boot';
import TreeDataSet from './stores/TreeDataSet';
import AttachFileDataSet from './stores/AttachFileDataSet';

const { Column } = Table;
const textField = <TextField />;
const modalKey = Modal.key();

export default class Index extends PureComponent {
  afDS = new DataSet(AttachFileDataSet);

  treeDS = new DataSet({
    ...TreeDataSet,
    children: {
      afs: this.afDS,
    },
  });

  renderResultBtn = ({ record }) => {
    const fileId = record.get('fileId');
    const token = record.get('_token');
    return (
      <Tooltip title={$l('hap.download')}>
        <Button funcType="flat" icon="cloud_download" href={`/sys/attach/file/download?fileId=${fileId}&token=${token}`} download />
      </Tooltip>
    );
  };

  renderNode = ({ record }) => {
    return record.get('categoryName');
  };

  render() {
    return (
      <Content>
        <div>
          <Row>
            <Col span={5}>
              <Tree
                dataSet={this.treeDS}
                renderer={this.renderNode}
              />
            </Col>
            <Col span={19}>
              <Table
                dataSet={this.afDS}
                buttons={['delete']}
              >
                <Column name="fileName" sortable />
                <Column name="fileSize" sortable />
                <Column name="uploadDate" sortable />
                <Column name="fileType" sortable />
                <Column
                  align="center"
                  renderer={this.renderResultBtn}
                  header={$l('hap.action')}
                />
              </Table>
            </Col>
          </Row>
        </div>
      </Content>
    );
  }
}
