import React from 'react';
import { $l } from '@choerodon/boot';
import moment from 'moment';
import { Modal, Table, TextField, Select, DatePicker, Tooltip, Icon, Button } from 'choerodon-ui/pro';
import TaskAssign from './TaskAssignModal';
import SubtaskModal from './SubtaskModal';
import ParameterModal from './ParameterModal';

const { Column } = Table;
const modelKey = {
  assignKey: Modal.key(),
  operationKey: Modal.key(),
  subtaskKey: Modal.key(),
  parameterKey: Modal.key(),
};

export default ({ taskDetailDS, taskAssignDS, roleDS, parameterDS, childrenTaskDS, unboundTaskDS }) => {
  let parameterModal;

  /**
   * 返回最大时间
   * @param record 当前记录
   * @returns {moment.Moment} 设置的时间
   */
  function maxStartDate(record) {
    if (record) {
      const time = record.get('endDate');
      if (time) {
        return moment(time);
      }
    }
  }

  /**
   * 返回最小时间
   * @param record 当前记录
   * @returns {moment.Moment} 设置的时间
   */
  function minEndDate(record) {
    if (record) {
      const time = record.get('startDate');
      if (time) {
        return moment(time);
      }
    }
  }

  /**
   * 提交角色分配
   * @returns {boolean} 失败返回 false
   */
  async function handleOnOkSaveAssign() {
    if (taskAssignDS.current.validate()) {
      const response = await taskAssignDS.submit();
      return (response && response.success);
    }
    return false;
  }

  /**
   * 打开任务的角色分配
   */
  function handleOnClickOpenTaskAssign() {
    taskAssignDS.setQueryParameter('taskId', taskDetailDS.current.get('taskId'));
    taskAssignDS.query();

    Modal.open({
      title: $l('hap.edit'),
      key: modelKey.assignKey,
      drawer: true,
      okText: $l('hap.save'),
      style: { width: 900 },
      destroyOnClose: true,
      onOk: handleOnOkSaveAssign,
      children: (
        <TaskAssign taskDetailDS={taskDetailDS} taskAssignDS={taskAssignDS} roleDS={roleDS} />
      ),
    });
  }

  /**
   * 打开参数编辑
   */
  async function handleOnClickOpenParameterModal(record) {
    const { taskId } = record.data;
    const code = 'TASK';

    parameterDS.queryParameter = {
      code,
      targetId: taskId,
    };
    await parameterDS.query();

    parameterModal = Modal.open({
      title: $l('hap.edit'),
      key: modelKey.parameterKey,
      drawer: true,
      okText: $l('hap.save'),
      style: { width: 1200 },
      destroyOnClose: true,
      footer: (
        <Button color="blue" onClick={() => parameterModal.close()}>{$l('hap.close')}</Button>
      ),
      children: (
        <ParameterModal targetId={taskId} code={code} parameterDS={parameterDS} reportData={record.data} />
      ),
    });
  }

  async function handleOnOkSaveChildrenDS() {
    if (taskDetailDS.validate()) {
      const response = await taskDetailDS.submit();
      return (response && response.success);
    }
    return false;
  }

  /**
   * 打开编辑子任务
   */
  function handleOnClickOpenSubTaskModal() {
    Modal.open({
      title: $l('hap.edit'),
      key: modelKey.subtaskKey,
      drawer: true,
      okText: $l('hap.save'),
      style: { width: 900 },
      destroyOnClose: true,
      onOk: handleOnOkSaveChildrenDS,
      children: (
        <SubtaskModal taskDetailDS={taskDetailDS} childrenTaskDS={childrenTaskDS} unboundTaskDS={unboundTaskDS} />
      ),
    });
  }

  function handleOnChangeDSType(value) {
    if (value === 'GROUP') {
      taskDetailDS.current.set('taskClass', '');
    }
  }

  function renderTaskClassColumn(record) {
    const { type } = record.data;
    if (type !== 'TASK') {
      return null;
    } else {
      return (<TextField pattern="[a-zA-Z]+[0-9a-zA-Z_]*(\.[a-zA-Z]+[0-9a-zA-Z_]*)*\.[a-zA-Z]+[0-9a-zA-Z_]*" />);
    }
  }

  function renderTypeColumn(record) {
    if (record.status === 'add') {
      return (<Select onChange={value => handleOnChangeDSType(value)} clearButton={false} />);
    } else {
      return null;
    }
  }

  return (
    <Table dataSet={taskDetailDS} buttons={['add', 'save', 'delete']} queryFieldsLimit={3}>
      <Column
        name="type"
        editor={record => renderTypeColumn(record)}
      />
      <Column name="code" editor />
      <Column name="name" editor />
      <Column
        name="taskClass"
        editor={record => renderTaskClassColumn(record)}
        minWidth={200}
      />
      <Column name="description" editor minWidth={200} />
      <Column name="startDate" editor={record => (<DatePicker max={maxStartDate(record)} />)} />
      <Column name="endDate" editor={record => (<DatePicker min={minEndDate(record)} />)} />
      <Column
        header={$l('hap.action')}
        align="center"
        width={75}
        lock="right"
        renderer={({ record }) => {
          if (record.get('taskId')) {
            if (record.get('type') === 'TASK') {
              return (
                <a onClick={() => handleOnClickOpenParameterModal(record)}>
                  <Tooltip title={$l('taskdetail.edittask')}>
                    <Icon type="mode_edit" />
                  </Tooltip>
                </a>
              );
            } else if (record.get('type') === 'GROUP') {
              return (
                <a onClick={handleOnClickOpenSubTaskModal}>
                  <Tooltip title={$l('taskdetail.editgroup')}>
                    <Icon type="operation_subtask" />
                  </Tooltip>
                </a>
              );
            }
          }
        }}
      />
      <Column
        header={$l('taskdetail.assign')}
        align="center"
        width={75}
        lock="right"
        renderer={({ record }) => {
          if (record.get('taskId')) {
            return (
              <a onClick={handleOnClickOpenTaskAssign}>
                <Tooltip title={$l('taskdetail.role.assign')}>
                  <Icon type="assignment_ind" />
                </Tooltip>
              </a>
            );
          } else {
            return null;
          }
        }}
      />
    </Table>
  );
};
