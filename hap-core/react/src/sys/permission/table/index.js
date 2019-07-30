import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Button, Modal, Lov, Select, Table, TextField, Tooltip, DataSet } from 'choerodon-ui/pro';
import { Content, $l } from '@choerodon/boot';
import TableDataSet from './stores/TableDataSet';
import RuleEditDataSet from './stores/RuleEditDataSet';

const { Column } = Table;

function insertSelectEditor(record) {
  return record.status === 'add' ? <Select searchable combo/> : null;
}

function tableFieldSelectEditor(record) {
  return record.get('tableField') === '_PERMISSION_CUSTOM_SQL' ? null : <Select searchable />;
}

const ruleEditModalKey = Modal.key();

@observer
export default class Index extends Component {
  tableDS = new DataSet(TableDataSet);

  ruleDS = new DataSet(RuleEditDataSet);

  ruleEditModal;

  ruleOnChange(value, oldValue, form) {
    if (value && value.permissionField === '_PERMISSION_CUSTOM_SQL') {
      this.ruleDS.current.set('tableField', '_PERMISSION_CUSTOM_SQL');
    } else {
      this.ruleDS.current.set('tableField', null);
    }
  }

  handleRuleEditSave(tableRecord) {
    const { tableId, tableName } = tableRecord.data;
    this.ruleDS.data.forEach((record) => {
      record.set('tableId', tableId);
      record.set('tableName', tableName);
    });
    this.ruleDS.submit();
  }

  handleRuleEditDelete(tableRecord) {
    const { tableId, tableName } = tableRecord.data;
    this.ruleDS.data.forEach((record) => {
      record.set('tableId', tableId);
      record.set('tableName', tableName);
    });
    this.ruleDS.delete(this.ruleDS.selected);
  }

  async openRuleEditModal(tableRecord) {
    const { tableId, tableName } = tableRecord.data;
    this.ruleDS.setQueryParameter('tableId', tableId);
    await this.ruleDS.query();
    this.ruleDS.getField('tableField').set('lookupUrl', `/fnd/flex/column/queryAllObj?tableName=${tableName}`);
    await this.ruleDS.getField('tableField').fetchLookup();

    this.ruleEditModal = Modal.open({
      drawer: true,
      key: ruleEditModalKey,
      title: $l('datapermission.assignrule', '分配屏蔽规则'),
      footer: (
        <Button color="blue" onClick={() => this.ruleEditModal.close()}>{$l('hap.close')}</Button>
      ),
      children: (
        <Table
          dataSet={this.ruleDS}
          buttons={[
            'add',
            <Button funcType="flat" icon="save" color="blue" onClick={() => this.handleRuleEditSave(tableRecord)}>{$l('hap.save')}</Button>,
            <Button funcType="flat" icon="delete" color="blue" onClick={() => this.handleRuleEditDelete(tableRecord)}>{$l('hap.delete')}</Button>,
          ]}
        >
          <Column name="rule" editor={<Lov onChange={(value, oldValue, form) => this.ruleOnChange(value, oldValue, form)} />} />
          <Column name="tableField" editor={tableFieldSelectEditor} />
        </Table>
      ),
    });
  }

  render() {
    return (
      <Content>
        <Table dataSet={this.tableDS} buttons={['add', 'save', 'delete']} queryFieldsLimit={2}>
          <Column name="tableName" editor={insertSelectEditor} />
          <Column name="description" editor={<TextField />} />
          <Column
            header={$l('datapermission.assignrule', '分配屏蔽规则')}
            align="center"
            width={140}
            renderer={({ record }) => {
              if (record.status !== 'add') {
                return (
                  <Tooltip title={$l('datapermission.assignrule', '分配屏蔽规则')}>
                    <Button
                      funcType="flat"
                      icon="mode_edit"
                      color="blue"
                      onClick={() => this.openRuleEditModal(record)}
                      disabled={record.status === 'add'}
                    />
                  </Tooltip>
                );
              }
            }}
          />
        </Table>
      </Content>
    );
  }
}
