import React from 'react';
import { Button, Modal, Table, Tooltip } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';
import LovModal from './LovModal';
import LovPreview from './LovPreview';

const { Column } = Table;
const modalKeys = {
  editKey: Modal.key(),
  previewKey: Modal.key(),
};

export default ({ lovDataSet, lovItemDataSet }) => {
  let created;
  let previewModal;

  /**
   * 弹窗关闭时,去掉未保存的 created
   */
  async function handleLovModalOnCancel() {
    if (created) {
      await lovDataSet.remove(created);
      created = null;
    } else {
      await lovDataSet.current.reset();
      lovItemDataSet.reset();
    }
  }

  /**
   * 保存 LOV
   * @returns {Promise<boolean>}
   */
  async function handleLovModalOnOk() {
    const { sqlType, treeFlag } = lovDataSet.current.data;
    switch (sqlType) {
      case 'SQL_ID': {
        lovDataSet.current.set('customSql', '');
        lovDataSet.current.set('customUrl', '');
        break;
      }
      case 'CUSTOM_SQL': {
        lovDataSet.current.set('sqlId', '');
        lovDataSet.current.set('customUrl', '');
        break;
      }
      case 'URL': {
        lovDataSet.current.set('sqlId', '');
        lovDataSet.current.set('customSql', '');
        break;
      }
      default:
        break;
    }

    if (treeFlag === 'N') {
      lovDataSet.current.set('idField', '');
      lovDataSet.current.set('parentIdField', '');
    }

    if (await lovDataSet.current.validate()) {
      const response = await lovDataSet.submit();
      await lovDataSet.query();
      return (response && response.success);
    }
    return false;
  }

  /**
   * 打开编辑或新建 LOV 的弹窗
   * @param isNew 是否为新建
   */
  function openLovModal(isNew = false) {
    if (isNew) {
      created = lovDataSet.create();
    }

    Modal.open({
      key: modalKeys.editKey,
      title: isNew ? $l('hap.add') : $l('hap.edit'),
      okText: $l('hap.save'),
      drawer: true,
      destroyOnClose: true,
      style: { width: 1200 },
      children: (
        <LovModal lovDataSet={lovDataSet} lovItemDataSet={lovItemDataSet} isAdd={isNew} />
      ),
      onOk: handleLovModalOnOk,
      afterClose: handleLovModalOnCancel,
    });
  }

  const addButton = <Button funcType="flat" color="blue" icon="add" onClick={() => openLovModal(true)}>{$l('hap.new')}</Button>;

  function closeLovPreviewModal() {
    previewModal.close();
  }

  /**
   * 打开 LOV 预览弹窗
   */
  function openLovPreviewModal(record) {
    previewModal = Modal.open({
      key: modalKeys.previewKey,
      title: $l('lov.preview'),
      drawer: true,
      destroyOnClose: true,
      style: { width: 600 },
      footer: <Button color="blue" onClick={closeLovPreviewModal}>{$l('hap.close')}</Button>,
      children: (
        <LovPreview record={record} />
      ),
    });
  }

  /**
   * 渲染内容
   */
  return (
    <Table dataSet={lovDataSet} buttons={[addButton, 'delete']} queryFieldsLimit={2}>
      <Column name="code" minWidth={150} />
      <Column name="description" minWidth={150} />
      <Column
        header={$l('hap.action')}
        align="center"
        width={120}
        lock="right"
        renderer={({ record }) => (
          <Tooltip title={$l('hap.edit')}>
            <Button funcType="flat" icon="mode_edit" onClick={() => openLovModal()} />
          </Tooltip>
        )}
      />
      <Column
        header={$l('lov.preview')}
        width={120}
        align="center"
        lock="right"
        renderer={({ record }) => (
          <Tooltip title={$l('lov.preview')}>
            <Button funcType="flat" icon="format_align_justify" onClick={() => openLovPreviewModal(record)} />
          </Tooltip>
        )}
      />
    </Table>
  );
};
