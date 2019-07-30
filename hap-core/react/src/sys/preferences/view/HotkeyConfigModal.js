import { axiosPro as axios, transformHotkey, $l } from '@choerodon/boot';
import { Form, message, TextField } from 'choerodon-ui/pro';
import { observer } from 'mobx-react';
import React, { Component } from 'react';

const VALUE_FILED = 'hotkey';

@observer
export default class HotkeyConfigModal extends Component {
  handleKeyDown = (event, index) => {
    const { ds } = this.props;
    const transformInput = transformHotkey(event);
    const record = ds.get(index);
    record.set('hotkey', transformInput);
  }

  componentDidMount() {
    const { ds } = this.props;

    axios.post('/sys/personalPreference/hotkey/query', {})
      .then((res) => {
        ds.loadData(res);
      })
      .catch(error => message.error($l('preference.hotkey.query.error')));
  }

  getTextFields = () => {
    const { ds } = this.props;
    return ds.map((record, index, arr) => {
      const textField = (
        <TextField
          dataSet={ds}
          dataIndex={index}
          name={VALUE_FILED}
          label={record.data.code}
          onKeyDown={event => this.handleKeyDown(event, index)}
          help={record.data.description}
          key={record.data.code}
        />
      );
      return textField;
    });
  };

  render() {
    return (
      <React.Fragment>
        <h4>
          {$l('hotkey.preference.prompt')}
          <span className="preferences-key">Shift</span> + <span className="preferences-key">C</span>
        </h4>
        <Form columns={2} labelWidth={150}>
          {this.getTextFields()}
        </Form>
      </React.Fragment>
    );
  }
}
