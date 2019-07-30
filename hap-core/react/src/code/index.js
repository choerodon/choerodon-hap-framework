import React, { PureComponent } from 'react';
import { set } from 'mobx';
import { inject, observer } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Button, CheckBox, DataSet, IntlField, Lov, NumberField, Table, TextField } from 'choerodon-ui/pro';
import { $l, ContentPro as Content } from '@choerodon/boot';
import CodeValueDataSet from './stores/CodeValueDataSet';
import CodeDataSet from './stores/CodeDataSet';

const { Column } = Table;

function statusRenderer(record) {
  return record.status === 'add' ? <TextField /> : null;
}

function statusValueRenderer(record) {
  return record.status === 'add' ? <TextField /> : null;
}

@observer
export default class Index extends PureComponent {
  codeValueDS = new DataSet(CodeValueDataSet);

  codeDS = new DataSet({
    ...CodeDataSet,
    children: {
      codeValues: this.codeValueDS,
    },
    events: {
      ...CodeDataSet.events,
      update: ({ dataSet, record, name, value, oldValue }) => {
        if (name === 'parent') {
          this.codeValueDS.getField('codevalue').setLovPara('codeId', value ? value.codeId : null);
          this.codeValueDS.data.forEach(r => r.set('codevalue', {}));
        }
      },
      indexChange: ({ dataSet, record, previous }) => {
        this.codeValueDS.getField('codevalue').setLovPara('codeId', record.get('parentCodeId'));
        const editor = record && record.get('type') !== 'SYSTEM';
        set(this.codeValueDS, 'selection', editor ? 'multiple' : false);
      },
    },
  });


  render() {
    const { codeDS } = this;
    const editor = codeDS.current && codeDS.current.get('type') !== 'SYSTEM';
    const addBtn = (
      <Button
        funcType="flat"
        color="blue"
        disabled={!editor}
        icon="playlist_add"
        onClick={() => this.codeValueDS.create()}
      >
        {$l('hap.add')}
      </Button>
    );

    const deleteBtn = (
      <Button
        funcType="flat"
        color="blue"
        disabled={!editor}
        icon="delete"
        onClick={() => this.codeValueDS.delete(this.codeValueDS.selected)}
      >
        {$l('hap.delete')}
      </Button>
    );


    let btnsArr = [];
    if (!editor) {
      btnsArr = ['export'];
    } else if (!codeDS.current.get('codeId')) {
      btnsArr = [addBtn, deleteBtn];
    } else {
      btnsArr = [addBtn, deleteBtn, 'export'];
    }

    return (
      <Content>
        <Table dataSet={this.codeDS} buttons={['add', 'save', 'delete', 'export']} queryFieldsLimit={2}>
          <Column name="code" editor={statusRenderer} />
          <Column name="parent" editor />
          <Column name="description" editor />
          <Column name="enabledFlag" editor width={180} />
        </Table>
        <Table dataSet={this.codeValueDS} buttons={btnsArr}>
          <Column name="value" editor={statusValueRenderer} />
          <Column name="meaning" editor={editor ? <IntlField clearButton /> : null} />
          <Column name="description" editor={editor ? <IntlField clearButton /> : null} />
          <Column name="codevalue" editor={this.codeDS.current && this.codeDS.current.get('parentCodeId') ? <Lov /> : null} />
          <Column name="tag" editor={editor ? <TextField clearButton /> : null} />
          <Column name="orderSeq" editor={editor ? <NumberField step={1} /> : null} align="center" width={140} />
          <Column name="enabledFlag" editor={editor ? <CheckBox /> : null} width={180} />
        </Table>
      </Content>
    );
  }
}
