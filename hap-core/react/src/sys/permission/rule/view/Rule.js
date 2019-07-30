import React from 'react';
import { Button, Form, Lov, Modal, Select, Table, TextArea, TextField, Tooltip } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';

const { Column } = Table;
const modalKey = {
  detailUserModalKey: Modal.key(),
  detailRoleModalKey: Modal.key(),
  detailSQLModalKey: Modal.key(),
  detailAssignModalKey: Modal.key(),
};

function editorRenderer(record) {
  return record.status === 'add' ? <TextField /> : null;
}

function insertSelectEditor(record) {
  return record.status === 'add' ? <Select /> : null;
}

function dynamicLovEditor(record) {
  if (record.get('assignField')) {
    return <Lov />;
  } else {
    return null;
  }
}

export default ({ ruleDS, ruleAssignDS, ruleDetailRoleDS, ruleDetailSqlDS, ruleDetailUserDS }) => {
  let ruleAssignModal;

  let ruleDetailModal;

  function handleDetailSave(dataSet) {
    dataSet.data.forEach(record => record.set('ruleId', ruleDS.current.get('ruleId')));
    dataSet.submit();
  }


  function handleDetailAssignSave(detailRecord) {
    ruleAssignDS.data.forEach((record) => {
      record.set('detailId', detailRecord.get('detailId'));
      record.set('ruleId', ruleDS.current.get('ruleId'));
    });
    ruleAssignDS.submit();
  }

  function handleDetailAssignDelete(detailRecord) {
    ruleAssignDS.data.forEach((record) => {
      record.set('detailId', detailRecord.get('detailId'));
      record.set('ruleId', ruleDS.current.get('ruleId'));
    });
    ruleAssignDS.delete(ruleAssignDS.selected);
  }


  async function openDetailAssignModal(detailRecord) {
    if (!detailRecord || detailRecord.status === 'add') {
      Modal.error('请先保存!');
      return;
    }

    ruleAssignDS.setQueryParameter('detailId', detailRecord.get('detailId'));
    await ruleAssignDS.query();

    ruleAssignModal = Modal.open({
      drawer: true,
      key: modalKey.detailAssignModalKey,
      title: $l('datapermission.assign'),
      destroyOnClose: true,
      footer: (
        <Button color="blue" onClick={() => ruleAssignModal.close()}>{$l('hap.close')}</Button>
      ),
      children: (
        <Table
          dataSet={ruleAssignDS}
          buttons={[
            'add',
            <Button funcType="flat" icon="save" color="blue" onClick={() => handleDetailAssignSave(detailRecord)}>{$l('hap.save')}</Button>,
            <Button funcType="flat" icon="delete" color="blue" onClick={() => handleDetailAssignDelete(detailRecord)}>{$l('hap.delete')}</Button>,
          ]}
        >
          <Column name="assignField" editor={<Select clearButton={false} />} />
          <Column name="value" editor={dynamicLovEditor} />
        </Table>
      ),
    });
  }

  const saveUserBtn = (<Button funcType="flat" icon="save" color="blue" onClick={() => handleDetailSave(ruleDetailUserDS)}>{$l('hap.save')}</Button>);

  const saveRoleBtn = (<Button funcType="flat" icon="save" color="blue" onClick={() => handleDetailSave(ruleDetailRoleDS)}>{$l('hap.save')}</Button>);

  const fieldDataMap = {
    user_lov: {
      dataSet: ruleDetailUserDS,
      modalData: {
        drawer: true,
        key: modalKey.detailUserModalKey,
        destroyOnClose: true,
        title: $l('datapermission.editrule'),
        footer: (
          <Button color="blue" onClick={() => ruleDetailModal.close()}>{$l('hap.close')}</Button>
        ),
        children: (
          <Table dataSet={ruleDetailUserDS} buttons={['add', saveUserBtn, 'delete']} queryFieldsLimit={2}>
            <Column name="permissionField" editor={<Lov />} />
            <Column
              name="assignEdit"
              align="center"
              header={$l('datapermission.assign')}
              renderer={({ record }) => {
                if (record.status !== 'add') {
                  return (<Button funcType="flat" icon="assignment" color="blue" onClick={() => openDetailAssignModal(record)} />);
                }
              }}
            />
          </Table>
        ),
      },
    },
    LOV_ROLE: {
      dataSet: ruleDetailRoleDS,
      modalData: {
        drawer: true,
        key: modalKey.detailRoleModalKey,
        title: $l('datapermission.editrule'),
        destroyOnClose: true,
        footer: (
          <Button color="blue" onClick={() => ruleDetailModal.close()}>{$l('hap.close')}</Button>
        ),
        children: (
          <Table dataSet={ruleDetailRoleDS} buttons={['add', saveRoleBtn, 'delete']} queryFieldsLimit={2}>
            <Column name="permissionField" editor={<Lov />} />
            <Column
              name="assignEdit"
              align="center"
              header={$l('datapermission.assign')}
              renderer={({ record }) => {
                if (record.status !== 'add') {
                  return (<Button funcType="flat" icon="assignment" color="blue" onClick={() => openDetailAssignModal(record)} />);
                }
              }}
            />
          </Table>
        ),
      },
    },
    _PERMISSION_CUSTOM_SQL: {
      dataSet: ruleDetailSqlDS,
      modalData: {
        drawer: true,
        key: modalKey.detailSQLModalKey,
        title: $l('datapermission.editrule'),
        destroyOnClose: true,
        footer: (
          <Button color="blue" onClick={() => ruleDetailModal.close()}>{$l('hap.close')}</Button>
        ),
        children: (
          <React.Fragment>
            <Button funcType="flat" icon="save" color="blue" onClick={() => handleDetailSave(ruleDetailSqlDS)}>{$l('hap.save')}</Button>
            <Button
              funcType="flat"
              icon="assignment"
              color="blue"
              onClick={() => openDetailAssignModal(ruleDetailSqlDS.current)}
            >
              {$l('datapermission.assign')}
            </Button>
            <div style={{ margin: '0.1rem' }}>1. 动态SQL为一个字段的条件查询，可嵌套SQL.<br />
              2. 支持动态参数{'#{}'}（参数必须在IRequest中）.<br />
              示例：columnA in (select AcolumnB from tableA where AcolumnC = {'#{varA}'})
            </div>
            <Form dataSet={ruleDetailSqlDS}>
              <TextArea name="permissionFieldSqlValue" />
            </Form>
          </React.Fragment>
        ),
      },
    },
  };

  function openDetailModal(record) {
    const { ruleId, permissionField } = record.data;

    fieldDataMap[permissionField].dataSet.reset();
    fieldDataMap[permissionField].dataSet.setQueryParameter('ruleId', ruleId);
    fieldDataMap[permissionField].dataSet.query();
    ruleDetailModal = Modal.open(fieldDataMap[permissionField].modalData);
  }

  return (
    <Table dataSet={ruleDS} buttons={['add', 'save', 'delete']} queryFieldsLimit={2}>
      <Column name="ruleCode" editor={editorRenderer} />
      <Column name="ruleName" editor={<TextField />} />
      <Column name="permissionField" editor={insertSelectEditor} />
      <Column
        name="ruleEdit"
        align="center"
        width={140}
        header={$l('datapermission.editrule')}
        renderer={({ record }) => {
          if (record.status !== 'add') {
            return (
              <Tooltip title={$l('datapermission.editrule')}>
                <Button funcType="flat" icon="mode_edit" color="blue" onClick={() => openDetailModal(record)} />
              </Tooltip>
            );
          }
        }}
      />
    </Table>
  );
};
