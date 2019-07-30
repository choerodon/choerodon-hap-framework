import React from 'react';
import { $l } from '@choerodon/boot';
import { Button, Modal, Table, Form, TextField, NumberField } from 'choerodon-ui/pro';

const key = Modal.key();
const { Column } = Table;
let maxOrder = 0;

export default ({ taskDetailDS, childrenTaskDS, unboundTaskDS }) => {
  let addSubtaskModal;

  function findMaxOrder() {
    childrenTaskDS.filter((record) => {
      if (record.get('order') > maxOrder) {
        maxOrder = record.get('order');
      }
      return null;
    });
  }

  function handleOnOkAddSubtask() {
    const { selected } = unboundTaskDS;
    selected.forEach((value) => {
      maxOrder += 1;
      value.set('order', maxOrder);
      childrenTaskDS.push(value.clone());
    });
    addSubtaskModal.close();
  }

  function handleOnClickOpenAddSubTaskModal() {
    let ids = '';
    childrenTaskDS.data.forEach((record) => {
      ids += `${record.get('taskId')},`;
    });
    unboundTaskDS.queryUrl = `/sys/task/detail/selectUnboundTasks?ids=${ids}&`;
    unboundTaskDS.query();

    addSubtaskModal = Modal.open({
      title: $l('hap.add'),
      key,
      drawer: true,
      okText: $l('hap.confirm'),
      style: { width: 900 },
      destroyOnClose: true,
      onOk: handleOnOkAddSubtask,
      children: (
        <Table dataSet={unboundTaskDS}>
          <Column name="code" />
          <Column name="name" />
          <Column name="description" />
          <Column name="startDate" />
          <Column name="endDate" />
        </Table>
      ),
    });
  }

  const addSubTaskBtn = (<Button funcType="flat" color="blue" icon="add" onClick={handleOnClickOpenAddSubTaskModal}>{$l('hap.add')}</Button>);

  findMaxOrder();

  return [
    <Form dataSet={taskDetailDS} columns={2} labelWidth={75}>
      <TextField name="code" disabled />
      <TextField name="name" disabled />
      <TextField name="description" disabled />
    </Form>,
    <div className="task-subtask-wrap">
      <div className="task-subtask-label">{$l('taskdetail.assign')}</div>
      <div className="task-subtask-table">
        <Table dataSet={childrenTaskDS} buttons={[addSubTaskBtn, 'delete']} showQueryBar={false}>
          <Column name="code" />
          <Column name="name" />
          <Column name="description" />
          <Column name="startDate" />
          <Column name="endDate" />
          <Column name="order" editor={<NumberField step={1} min={0} />} />
        </Table>
      </div>
    </div>,
  ];
};
