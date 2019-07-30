import React from 'react';
import { Button, CheckBox, Modal, Select, Table, TextField } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';
import CopyModal from './CopyModal';

const { Column } = Table;
const textField = <TextField />;
const checkBox = <CheckBox value="Y" unCheckedValue="N" />;
const modalKey = Modal.key();

function editorRenderer(record) {
  return record.status === 'add' ? textField : null;
}

export default ({ dataSet }) => {
  function typeRenderer() {
    return <Select dataSet={dataSet} name="unitType" clearButton={false} />;
  }

  function categoryRenderer() {
    return <Select dataSet={dataSet} name="unitCategory" clearButton={false} />;
  }

  let isCancel = true;
  let created;

  /**
   * 数据校验成功时保存
   * @returns {Promise<boolean>}
   */
  async function handleOnOkCopyDrawer() {
    if (await dataSet.current.validate()) {
      isCancel = false;
      await dataSet.submit();
    } else {
      return false;
    }
  }

  function handleOnCancelCopyDrawer() {
    isCancel = true;
  }

  function handleOnCloseCopyDrawer() {
    if (isCancel) {
      dataSet.remove(created);
    }
  }

  function openCopyModal() {
    const { selected } = dataSet;
    if (selected.length === 1) {
      created = selected[0].clone();
      dataSet.unshift(created);
      dataSet.current = created;
      Modal.open({
        key: modalKey,
        title: $l('hap.new'),
        drawer: true,
        children: (
          <CopyModal dataSet={dataSet} />
        ),
        onOk: handleOnOkCopyDrawer,
        oncancel: handleOnCancelCopyDrawer,
        afterClose: handleOnCloseCopyDrawer,
        style: { width: 500 },
        okText: $l('hap.new'),
      });
    } else {
      Modal.info('请选择一行');
    }
  }

  function saveOrgUnit() {
    const rows = dataSet.toData();
    let hasConflict = false;
    if (rows && rows.length) {
      for (let i = 0; i < rows.length; i += 1) {
        // 检查上级组织是否为自身
        if (rows[i].unitId && rows[i].parentId && rows[i].unitId === rows[i].parentId) {
          hasConflict = true;
          break;
        }
        for (let j = i + 1; j < rows.length; j += 1) {
          // 检查上级组织是否存在循环
          if (rows[i].unitId && rows[i].unitId === rows[j].parentId && rows[i].parentId === rows[j].unitId) {
            hasConflict = true;
            break;
          }
        }
        if (hasConflict) {
          break;
        }
      }
    }
    if (hasConflict) {
      Modal.error($l('hap.error.conflict'));
    } else {
      dataSet.submit();
    }
  }

  const copyBtn = (
    <Button
      key="copy"
      funcType="flat"
      color="blue"
      icon="content_copy"
      onClick={openCopyModal}
    >
      {$l('hap.copy')}
    </Button>
  );

  const saveBtn = (
    <Button
      key="save"
      funcType="flat"
      color="blue"
      icon="save"
      onClick={saveOrgUnit}
    >
      {$l('hap.save')}
    </Button>
  );

  /**
   * 渲染表格内容
   */
  return (
    <Table
      buttons={['add', saveBtn, 'delete', copyBtn, 'export']}
      dataSet={dataSet}
      queryFieldsLimit={2}
    >
      <Column name="unitCode" editor={editorRenderer} sortable />
      <Column name="name" editor width={200} />
      <Column name="unitCategory" editor={categoryRenderer} width={100} sortable />
      <Column name="unitType" editor={typeRenderer} width={100} sortable />
      <Column name="parentCode" width={200} sortable />
      <Column name="parent" editor width={200} />
      <Column name="companyCode" sortable />
      <Column name="company" editor />
      <Column name="position" editor />
      <Column name="description" editor width={200} />
      <Column name="enabledFlag" editor={checkBox} align="center" sortable width={100} />
    </Table>
  );
};
