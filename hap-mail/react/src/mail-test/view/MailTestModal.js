import React, { Component } from 'react';
import { inject, observer } from 'mobx-react';
import { Button, Modal, Select, Table, Tooltip } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';
import MailAttachmentModal from './MailAttachmentModal';

const { Option } = Select;
const { Column } = Table;
const modalKey = Modal.key();

export default (props) => {
  let modals;

  function handleSaveFile() {
    const { selected } = props.dataSet2;
    selected.forEach((value) => {
      const fileId = value.get('fileId');
      const record = props.dataSet1.find(r => r.get('fileId') === fileId);
      if (!record) {
        props.dataSet1.create();
        props.dataSet1.current.set('attachmentId', value.get('attachmentId'));
        props.dataSet1.current.set('fileId', value.get('fileId'));
        props.dataSet1.current.set('fileName', value.get('fileName'));
        props.dataSet1.current.set('fileSize', value.get('fileSize'));
        props.dataSet1.current.set('fileType', value.get('fileType'));
        props.dataSet1.current.set('filePath', value.get('filePath'));
      }
    });
    // return true;
    modals.close();
  }

  function closeModal() {
    modals.close();
  }

  async function openFileModal() {
    modals = Modal.open({
      modalKey,
      title: $l('mailtest.choseattachment'),
      drawer: true,
      destroyOnClose: true,
      children: (
        <MailAttachmentModal modals={modals} dataSet={props.dataSet2} />
      ),
      footer: [
        <Button color="blue" onClick={() => handleSaveFile()}>{$l('hap.add')}</Button>,
        <Button style={{ marginLeft: 10 }} onClick={() => closeModal()}>
          {$l('hap.close')}
        </Button>,
      ],
      style: {
        width: 700,
      },
    });
  }

  return (
    <Table
      dataSet={props.dataSet1}
      buttons={[
        <Button funcType="flat" color="blue" icon="playlist_add" onClick={() => openFileModal()}>{$l('mailtest.choseattachment')}</Button>,
        <Button funcType="flat" icon="delete" color="blue" onClick={() => props.dataSet1.delete(props.dataSet1.selected)}>{$l('hap.delete')}</Button>,
      ]}
    >
      <Column name="fileName" />
      <Column name="fileSize" />
      <Column name="fileType" />
    </Table>
  );
};
