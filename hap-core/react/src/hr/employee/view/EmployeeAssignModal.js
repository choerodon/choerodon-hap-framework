import React from 'react';
import { Table, CheckBox, Modal, Button } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';

const { Column } = Table;
const key = Modal.key();

export default ({ employeeAssignDataSet, employeeId, positionDataSet }) => {
  /**
   * 将选择的岗位去重后添加到 employeeAssignDataSet
   * @returns {boolean}
   */
  function savePosition() {
    const { selected } = positionDataSet;

    selected.forEach((value) => {
      const { positionId, name, unitName } = value.data;
      const record1 = employeeAssignDataSet.find(record => record.get('positionId') === positionId);
      if (!record1) {
        employeeAssignDataSet.create({
          employeeId,
          positionId,
          positionName: name,
          unitName,
        });
      }
    });
    // 去掉勾选
    selected.forEach(record => (positionDataSet.unSelect(record)));
    return true;
  }

  function resetPosition() {
    positionDataSet.selected.forEach(record => (positionDataSet.unSelect(record)));
  }

  /**
   * 打开增加岗位弹窗
   */
  function openAddPositionModal() {
    Modal.open({
      key,
      title: $l('position.selectposition'),
      drawer: true,
      destroyOnClose: true,
      children: (
        <Table dataSet={positionDataSet} queryFieldsLimit={2}>
          <Column name="positionCode" />
          <Column name="name" />
          <Column name="description" />
          <Column name="unitName" />
        </Table>
      ),
      okText: $l('hap.add'),
      onOk: savePosition,
      onCancel: resetPosition,
      style: { width: 900 },
    });
  }

  function changePrimaryPositionFlag() {
    employeeAssignDataSet.data.forEach((value) => {
      if (value !== employeeAssignDataSet.current && value.get('primaryPositionFlag') === 'Y') {
        value.set('primaryPositionFlag', 'N');
      }
    });
  }

  const addBtn = (
    <Button
      funcType="flat"
      color="blue"
      icon="playlist_add"
      onClick={() => openAddPositionModal()}
    >
      {$l('hap.add')}
    </Button>
  );

  return (
    <Table dataSet={employeeAssignDataSet} buttons={[addBtn, 'delete']}>
      <Column name="positionName" />
      <Column name="unitName" />
      <Column name="primaryPositionFlag" editor={<CheckBox value="Y" unCheckedValue="N" onChange={changePrimaryPositionFlag} />} />
      <Column name="enabledFlag" editor={<CheckBox value="Y" unCheckedValue="N" />} />
    </Table>
  );
};
