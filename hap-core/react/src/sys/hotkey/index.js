import React, { PureComponent } from 'react';
import { Table, TextField, IntlField, DataSet } from 'choerodon-ui/pro';
import { ContentPro as Content, transformHotkey, $l } from '@choerodon/boot';
import HotkeyDataSet from './stores/HotkeyDataSet';

const { Column } = Table;

function insertColumnCodeEditor(record) {
  return record.status === 'add' ? <TextField /> : null;
}

export default class Index extends PureComponent {
  hotkeyDS = new DataSet(HotkeyDataSet);

  handleOnKeyDown = (event) => {
    const transformInput = transformHotkey(event);
    const record = this.hotkeyDS.current;
    if (record) {
      record.set('hotkey', transformInput);
    }
    event.stopPropagation();
    event.preventDefault();
  };

  render() {
    const { hotkeyDS } = this;

    return (
      <Content
        hotkeys={{
          hotkey_create: () => {
            window.console.log('[Hotkey module, from `Hotkey`]: i emit hotkey ctrl + g');
          },
        }}
      >
        <Table
          dataSet={hotkeyDS}
          buttons={['add', 'save', 'delete']}
        >
          <Column name="code" editor={insertColumnCodeEditor} help={$l('hotkey.code.prompt')} />
          <Column name="hotkey" editor={<TextField onKeyDown={this.handleOnKeyDown} help={$l('hotkey.preference.prompt')} />} />
          <Column name="description" editor={<IntlField />} />
        </Table>
      </Content>
    );
  }
}
