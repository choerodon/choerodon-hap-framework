import React from 'react';
import { Button, DatePicker, EmailField, Form, Modal, Select, Table, TextField } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';
import '../index.scss';

const key = Modal.key();
const { Column } = Table;

export default ({ isNewUser, userDataSet, selfRoleDataSet, addRoleDataSet }) => {
  /**
   * 将选择的记录去重以后添加到 rolDataSet
   * @returns {Promise<void>}
   */
  async function saveAddRoleDS() {
    const { selected } = addRoleDataSet;
    selected.forEach((record) => {
      const { id, code, name, description } = record.data;
      const value = selfRoleDataSet.find(r => r.get('code') === code);
      if (!value) {
        selfRoleDataSet.create({
          id,
          code,
          name,
          description,
        });
      }
    });
  }

  async function deleteSelfRole() {
    const { selected } = selfRoleDataSet;
    if (selected.length > 0) {
      selected.forEach((record) => {
        // 查询的时候 selfRoleDataSet返回的iam_role信息
        // 删除的时候 selfRoleDataSet需要提交iam_member_role信息 所以需要做下数据转换
        if (record.get('memberRoleId')) {
          record.set('id', record.get('memberRoleId'));
        }
      });
      await selfRoleDataSet.delete(selected);
    }
  }

  /**
   * 打开添加角色的弹窗
   */
  async function openAddRoleModal() {
    if (!isNewUser) {
      addRoleDataSet.setQueryParameter('userId', userDataSet.current.get('userId'));
    } else {
      addRoleDataSet.setQueryParameter('userId', null);
    }
    await addRoleDataSet.query();

    Modal.open({
      key,
      title: $l('user.allocationrole'),
      drawer: true,
      destroyOnClose: true,
      children: (
        <Table dataSet={addRoleDataSet} queryFieldsLimit={2}>
          <Column name="code" />
          <Column name="name" />
          <Column name="description" />
        </Table>
      ),
      okText: $l('hap.add'),
      onOk: saveAddRoleDS,
    });
  }

  const deleteBtn = (
    <Button
      funcType="flat"
      color="blue"
      icon="delete"
      onClick={deleteSelfRole}
    >
      {$l('hap.delete')}
    </Button>
  );

  const addBtn = (
    <Button
      funcType="flat"
      color="blue"
      icon="add"
      onClick={openAddRoleModal}
    >
      {$l('hap.add')}
    </Button>
  );

  /**
   * 渲染内容
   */
  return (
    <div>
      <Form labelWidth={100} columns={2} style={{ marginRight: 37 }} dataSet={userDataSet}>
        <TextField name="employeeCode" disabled />
        <TextField name="employeeName" disabled />

        <TextField name="userName" disabled={!isNewUser} />
        <Select name="status" placeholder={$l('hap.tip.pleaseselect')} disabled={!isNewUser} />

        <TextField pattern="1[3-9]\d{9}" name="phone" disabled={!isNewUser} />
        <EmailField name="email" disabled={!isNewUser} />

        <DatePicker name="startActiveDate" placeholder={$l('hap.tip.pleaseselect')} disabled={!isNewUser} />
        <DatePicker name="endActiveDate" placeholder={$l('hap.tip.pleaseselect')} disabled={!isNewUser} />
      </Form>
      <div className="core-employee-wrap">
        <div className="core-employee-label">{$l('user.userrole')}</div>
        <div className="core-employee-table">
          <Table dataSet={selfRoleDataSet} buttons={[addBtn, deleteBtn]}>
            <Column name="code" />
            <Column name="name" />
          </Table>
        </div>
      </div>
    </div>
  );
};
