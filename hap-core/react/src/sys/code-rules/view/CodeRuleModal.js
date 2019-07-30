import React from 'react';
import { $l } from '@choerodon/boot';
import { CheckBox, Form, NumberField, Select, Table, TextField } from 'choerodon-ui/pro';

const { Column } = Table;

export default ({ headerDS, lineDS, isEditDisabled, isEnableDisabled }) => {
  function filedValueRenderer(record) {
    if (!isEnableDisabled && (record.get('filedType') === 'CONSTANT' || record.get('filedType') === 'VARIABLE')) {
      return <TextField key="filedValue" name="filedValue" />;
    }
    return null;
  }

  function dateMaskRenderer(record) {
    if (!isEnableDisabled && record.get('filedType') === 'DATE') {
      return <Select key="dateMask" name="dateMask" />;
    }
    return null;
  }

  function resetFrequencyRenderer(record) {
    if (!isEnableDisabled && record.get('filedType') === 'SEQUENCE') {
      return <Select key="resetFrequency" name="resetFrequency" />;
    }
    return null;
  }

  function numberRenderer(record) {
    if (!isEnableDisabled && record.get('filedType') === 'SEQUENCE') {
      return <NumberField min={1} step={1} key="number" name="number" />;
    }
    return null;
  }

  function fieldSequenceRenderer() {
    if (!isEnableDisabled) {
      return <NumberField min={1} step={1} key=" fieldSequence" name=" fieldSequence" />;
    }
    return null;
  }

  function resetValue() {
    lineDS.current.set('filedValue', '');
    lineDS.current.set('dateMask', '');
    lineDS.current.set('resetFrequency', '');
    lineDS.current.set('seqLength', '');
    lineDS.current.set('startValue', '');
    lineDS.current.set('stepNumber', '');
    lineDS.current.set('currentValue', '');
  }

  function filedTypeRenderer() {
    if (!isEnableDisabled) {
      return (
        <Select
          key="filedType"
          name="filedType"
          onChange={resetValue}
          optionsFilter={
            record => !((record.get('value') === 'SEQUENCE') && (lineDS.find(r => r.get('filedType') === 'SEQUENCE')))
          }
        />
      );
    }
    return null;
  }

  let btnGroup = [];
  if (!isEnableDisabled) {
    btnGroup = ['add', 'delete'];
  }

  return (
    <div>
      <Form
        columns={2}
        abelWidth={100}
      >
        <TextField name="ruleCode" label={$l('coderulesheader.rulecode')} dataSet={headerDS} required disabled={isEditDisabled} />
        <TextField name="ruleName" label={$l('coderulesheader.rulename')} dataSet={headerDS} disabled={isEnableDisabled} />
        <TextField name="description" label={$l('hap.description')} dataSet={headerDS} colSpan={2} disabled={isEnableDisabled} />
        <CheckBox name="enableFlag" label={$l('hap.enableflag')} dataSet={headerDS} disabled={isEnableDisabled} />
      </Form>
      <Table
        buttons={btnGroup}
        dataSet={lineDS}
        header={$l('coderulesline.list')}
      >
        <Column name="fieldSequence" editor={fieldSequenceRenderer} />
        <Column name="filedType" editor={filedTypeRenderer} />
        <Column name="filedValue" editor={filedValueRenderer} />
        <Column name="dateMask" editor={dateMaskRenderer} />
        <Column name="resetFrequency" editor={resetFrequencyRenderer} />
        <Column name="seqLength" editor={numberRenderer} />
        <Column name="startValue" editor={numberRenderer} />
        <Column name="currentValue" />
        <Column name="stepNumber" editor={numberRenderer} />
      </Table>
    </div>
  );
};
