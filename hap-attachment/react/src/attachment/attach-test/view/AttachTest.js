import React, { PureComponent } from 'react';
import { Button, Form, Lov, Output, Table, TextField, Upload } from 'choerodon-ui/pro';
import { message } from 'choerodon-ui';
import { $l } from '@choerodon/boot';

const { Column } = Table;

export default class AttachTest extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      sourceType: '',
      sourceKey: 1,
      allowedFileType: '*',
    };
  }

  upload;

  async queryAttachFileDataSet() {
    const { sourceType, sourceKey } = this.state;
    let baseQueryUrl = '/sys/attachment/query?1=1';
    if (sourceType && sourceType !== '') {
      baseQueryUrl = `${baseQueryUrl}&sourceType=${sourceType}`;
    }
    if (sourceKey) {
      baseQueryUrl = `${baseQueryUrl}&sourceKey=${sourceKey}`;
    }

    this.props.fileDS.queryUrl = baseQueryUrl;
    await this.props.fileDS.query();
  }

  getUploadConfig() {
    const { sourceType, sourceKey, allowedFileType } = this.state;
    return {
      headers: {
        'Access-Control-Allow-Origin': '*',
      },
      action: '/sys/attach/upload',
      multiple: true,
      accept: allowedFileType.split(';'),
      uploadImmediately: false,
      showUploadBtn: false,
      showPreviewImage: false,
      data: {
        sourceType,
        sourceKey,
      },
      onUploadSuccess: (res) => {
        if (typeof res === 'string' && res.indexOf('success') !== -1) {
          const resObj = JSON.parse(res);
          const { success } = resObj;
          if (success) {
            message.success('上传成功');
            this.queryAttachFileDataSet();
          } else {
            message.error(resObj.message);
          }
        }
      },
      onUploadError: (error, res) => {
        message.error('上传失败，请稍后再试');
      },
    };
  }

  saveUpload = (node) => {
    this.upload = node;
  };

  handleSubmitFile = async () => {
    await this.upload.startUpload();
  };

  handleChangeSourceKey = (value) => {
    this.setState({
      sourceKey: value,
    });
  };

  handleChangeAttach = (value) => {
    this.setState({
      sourceType: value.sourceType || '',
      allowedFileType: value.allowedFileType || '*',
    });
  };

  renderAttachTable() {
    return (
      <Table dataSet={this.props.fileDS}>
        <Column
          header={$l('attachcategory.attachment')}
          renderer={({ record }) => (
            <a
              target="_blank"
              href={`/sys/attach/file/download?fileId=${record.get('fileId')}&token=${record.get('_token')}`}
              rel="noopener noreferrer"
            >
              {record.get('fileName')}
            </a>
          )}
        />
      </Table>
    );
  }

  render() {
    return (
      <Form dataSet={this.props.uploadDS} style={{ width: 600, margin: 'auto' }} labelWidth={150}>
        <Lov name="attach" onChange={value => this.handleChangeAttach(value)} onBlur={this.queryAttachFileDataSet()} />
        <TextField name="sourceKey" onChange={value => this.handleChangeSourceKey(value)} onBlur={this.queryAttachFileDataSet()} />
        <Output
          label={$l('attachcategory.attachment')}
          renderer={() => <Upload ref={this.saveUpload} {...this.getUploadConfig()} name="files" />}
          help={$l('attach.test', '附件是根据业务编码和业务主键值查询')}
        />
        <Button style={{ marginBottom: 10 }} color="blue" onClick={this.handleSubmitFile}>{$l('hap.submit')}</Button>
        {this.renderAttachTable()}
      </Form>
    );
  }
}
