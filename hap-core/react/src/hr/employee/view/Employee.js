import React from 'react';
import { $l } from '@choerodon/boot';
import { Button, Modal, Table, Tooltip } from 'choerodon-ui/pro';
import EmployeeModal from './EditEmployeeModal';
import UserModal from './UserModal';
import EmployeeAssign from './EmployeeAssignModal';

const { Column } = Table;
const modalKeys = {
  employeeKey: Modal.key(),
  addUserKey: Modal.key(),
  assignKey: Modal.key(),
};

export default ({ employeeAssignDataSet, employeeDataSet, selfRoleDataSet, userDataSet, addRoleDataSet, positionDataSet }) => {
  let created;

  function afterCloseEmployeeModal() {
    if (created) {
      employeeDataSet.remove(created);
      created = null;
    } else {
      employeeDataSet.current.reset();
    }
  }

  async function onOkEmployeeModal() {
    if (await employeeDataSet.current.validate()) {
      const response = await employeeDataSet.submit();
      if (response && response.success) {
        await employeeDataSet.query();
        return true;
      }
    } else {
      return false;
    }
  }

  /**
   * 打开新建或者编辑员工的弹窗
   * @param isNew 是否为新建
   * @param record 当为编辑时传入的 record 记录
   */
  function openEmployeeModal(isNew = false, record) {
    if (isNew) {
      created = employeeDataSet.create();
    }

    Modal.open({
      key: modalKeys.employeeKey,
      title: isNew ? $l('hap.add') : $l('hap.edit'),
      drawer: true,
      destroyOnClose: true,
      okText: $l('hap.save'),
      children: (
        <EmployeeModal isNew={isNew} employeeDataSet={employeeDataSet} />
      ),
      afterClose: afterCloseEmployeeModal,
      onOk: onOkEmployeeModal,
      style: {
        width: 900,
      },
    });
  }

  async function handleOnOkUserModal() {
    if (await userDataSet.current.validate()) {
      const response = await userDataSet.submit();
      return (response && response.success);
    }
    return false;
  }

  /**
   * 打开新建用户的弹窗
   */
  async function openUserModal() {
    const { selected } = employeeDataSet;

    if (selected.length === 1) {
      const { employeeCode, email, mobil, name, employeeId } = employeeDataSet.selected[0].data;
      userDataSet.setQueryParameter('userName', employeeCode);
      await userDataSet.query();

      const isNewUser = (userDataSet.length === 0);
      if (isNewUser) {
        userDataSet.create({
          userName: employeeCode,
          email,
          phone: mobil,
          employeeCode,
          employeeId,
          employeeName: name,
        });
      } else {
        userDataSet.current.set('employeeCode', employeeCode);
        userDataSet.current.set('employeeName', name);
      }

      Modal.open({
        key: modalKeys.addUserKey,
        title: $l('user.createuser'),
        drawer: true,
        destroyOnClose: true,
        okText: $l('hap.save'),
        children: (
          <UserModal
            employeeCode={employeeCode}
            employeeName={name}
            isNewUser={isNewUser}
            userDataSet={userDataSet}
            selfRoleDataSet={selfRoleDataSet}
            addRoleDataSet={addRoleDataSet}
          />
        ),
        onOk: handleOnOkUserModal,
        style: { width: 900 },
      });
    } else {
      Modal.warning($l('hap.tip.selectrow'));
    }
  }

  async function handleOnOKPositionAssignModal() {
    const { data } = employeeAssignDataSet;
    if (!data.length) {
      return true;
    }
    let ppf = 0;
    data.forEach((record) => {
      if (record.get('primaryPositionFlag') === 'Y') {
        ppf += 1;
      }
    });
    // 员工 有且只能有一个主岗位
    if (ppf !== 1) {
      Modal.error($l('hap.error.moreunit'));
      return false;
    } else {
      await employeeAssignDataSet.submit();
      return true;
    }
  }

  function closePositionAssignModal() {
    employeeAssignDataSet.reset();
  }

  async function openPositionAssignModal() {
    const { employeeId } = employeeDataSet.current.data;
    employeeAssignDataSet.setQueryParameter('employeeId', employeeId);
    await employeeAssignDataSet.query();
    Modal.open({
      key: modalKeys.assignKey,
      title: $l('employee.positionallocation'),
      drawer: true,
      destroyOnClose: true,
      okText: $l('hap.save'),
      onOk: handleOnOKPositionAssignModal,
      onCancel: closePositionAssignModal,
      children: (
        <EmployeeAssign
          employeeAssignDataSet={employeeAssignDataSet}
          employeeId={employeeDataSet.current.get('employeeId')}
          positionDataSet={positionDataSet}
        />
      ),
      style: { width: 900 },
    });
  }

  const addEmployeeButton = (
    <Button
      funcType="flat"
      color="blue"
      icon="add"
      onClick={() => openEmployeeModal(true)}
    >
      {$l('hap.add')}
    </Button>
  );

  const addUserButton = (
    <Button
      funcType="flat"
      color="blue"
      icon="group_add"
      onClick={() => openUserModal()}
    >
      {$l('employee.createnewuser')}
    </Button>
  );

  return (
    <Table buttons={[addEmployeeButton, addUserButton, 'delete', 'export']} dataSet={employeeDataSet} queryFieldsLimit={2}>
      <Column name="employeeCode" minWidth={100} sortable />
      <Column name="name" minWidth={100} sortable />
      <Column name="gender" minWidth={75} sortable />
      <Column name="bornDate" minWidth={75} sortable />
      <Column name="email" minWidth={175} sortable />
      <Column name="mobil" minWidth={125} sortable />
      <Column name="status" minWidth={75} sortable />
      <Column name="joinDate" minWidth={75} sortable />
      <Column name="enabledFlag" minWidth={75} sortable />
      <Column
        renderer={() => (
          <Tooltip title={$l('hap.edit')}>
            <Button
              funcType="flat"
              icon="mode_edit"
              onClick={() => openEmployeeModal(false, employeeDataSet)}
            />
          </Tooltip>
        )}
        header={$l('hap.edit')}
        width={75}
        align="center"
        lock="right"
      />
      <Column
        renderer={() => (
          <Tooltip title={$l('employee.positionallocation')}>
            <Button
              funcType="flat"
              icon="format_align_justify"
              onClick={() => openPositionAssignModal()}
            />
          </Tooltip>
        )}
        header={$l('employee.positionallocation')}
        width={100}
        align="center"
        lock="right"
      />
    </Table>
  );
};
