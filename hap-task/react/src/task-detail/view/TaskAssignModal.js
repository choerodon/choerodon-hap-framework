import React from 'react';
import { $l } from '@choerodon/boot';
import moment from 'moment';
import { Button, DatePicker, Form, Modal, Table, TextField } from 'choerodon-ui/pro';
import '../index.scss';

const { Column } = Table;
const key = Modal.key();

export default ({ taskDetailDS, taskAssignDS, roleDS }) => {
  let addRoleModal;

  function renderTaskClassColumn() {
    if (taskDetailDS.current.get('type') === 'TASK') {
      return (<TextField name="taskClass" disabled colSpan={2} />);
    } else {
      return null;
    }
  }

  function maxStartDate(record) {
    if (record) {
      const time = record.get('endDate');
      if (time) {
        return moment(time);
      }
    }
  }


  function minEndDate(record) {
    if (record) {
      const time = record.get('startDate');
      if (time) {
        return moment(time);
      }
    }
  }

  function handleOnOkAddRole() {
    const { selected } = roleDS;
    selected.forEach((record) => {
      taskAssignDS.create();
      const { id, code, name, description } = record.data;
      taskAssignDS.current.set('id', id);
      taskAssignDS.current.set('code', code);
      taskAssignDS.current.set('name', name);
      taskAssignDS.current.set('description', description);
      taskAssignDS.current.set('taskId', taskDetailDS.current.get('taskId'));
      taskAssignDS.current.set('assignId', id);
      taskAssignDS.current.set('assignType', 'ROLE');
      taskAssignDS.current.set('dirty', true);
    });

    addRoleModal.close();
  }

  function handleOnClickAddRole() {
    let ids = '';
    taskAssignDS.data.forEach((record) => {
      ids = `${ids}${record.get('id')},`;
    });

    roleDS.setQueryParameter('ids', ids);
    roleDS.query();
    addRoleModal = Modal.open({
      key,
      title: $l('hap.edit'),
      drawer: true,
      destroyOnClose: true,
      style: { width: 750 },
      children: (
        <Table dataSet={roleDS}>
          <Column name="code" />
          <Column name="name" />
          <Column name="description" />
        </Table>
      ),
      okText: $l('hap.add'),
      onOk: handleOnOkAddRole,
    });
  }

  const addRoleBtn = <Button funcType="flat" color="blue" icon="add" onClick={handleOnClickAddRole}>{$l('hap.add')}</Button>;

  return (
    <div className="task-detail-taskAssign-modal">
      <Form dataSet={taskDetailDS} columns={2} labelWidth={75}>
        <TextField name="code" disabled colSpan={1} />
        <TextField name="name" disabled colSpan={1} />
        {renderTaskClassColumn()}
        <TextField name="description" disabled colSpan={2} />
      </Form>
      <div className="task-wrap">
        <div className="task-label">{$l('taskdetail.assign')}</div>
        <div className="task-table">
          <Table buttons={[addRoleBtn, 'delete']} dataSet={taskAssignDS}>
            <Column name="code" />
            <Column name="name" />
            <Column name="startDate" editor={record => (<DatePicker max={maxStartDate(record)} />)} />
            <Column name="endDate" editor={record => (<DatePicker min={minEndDate(record)} />)} />
          </Table>
        </div>
      </div>
    </div>
  );
};
