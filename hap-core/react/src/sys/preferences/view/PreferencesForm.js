import { $l, axiosPro as axios } from '@choerodon/boot';
import { Button, CheckBox, DataSet, Form, message, Modal, Output, Select } from 'choerodon-ui/pro';
import { inject, observer } from 'mobx-react';
import React, { Component } from 'react';

import { findIndexFromDataSet } from '../stores/PreferencesStore';
import HotkeyConfigModal from './HotkeyConfigModal';
import HotkeyDSConfig from '../stores/HotkeyStore';

const hotkeyModalKey = Modal.key();

const PREFERFENCES_MAPPER = {
  locale: 'locale',
  timeZone: 'timeZone',
  isTabNav: 'nav', // <-- 是否启用标签页导航
};
const FIND_INDEX_FIELD = 'preferences';
const VALUE_FIELD = 'preferencesValue';

@inject('AppState')
@observer
export default class PreferencesForm extends Component {
  hotkeyDS = new DataSet(HotkeyDSConfig);

  state = { loading: false }; // <-- control submit button's loading prop

  openHotkeyConfigModal = () => {
    Modal.open({
      key: hotkeyModalKey,
      style: { width: '800px' },
      title: $l('preference.hotkey.title'),
      children: <HotkeyConfigModal ds={this.hotkeyDS} />,
      onOk: this.handleHotkeySave,
    });
  };

  handleSubmitBtnClick = () => {
    const { preferencesDS } = this.props;
    const reqBody = preferencesDS.data.slice().map(r => r.data);

    this.setState({
      loading: true,
    });

    axios.post('/sys/preferences/savePreferences', reqBody)
      .then((res) => {
        if (res.success) {
          message.success($l('preference.submit.success'));
          window.location.reload();
        } else {
          message.error($l('preference.submit.failure'));
        }
      })
      .catch((error) => {
        message.error($l('preference.submit.error'));
      });
  };

  handleHotkeySave = () => {
    const { hotkeyDS } = this;
    const { userId } = this.props.AppState.getUserInfo;

    hotkeyDS.validate(false, false).then((validity) => {
      if (validity) {
        const reqBody = hotkeyDS.updated.map((r) => {
          const data = {
            ...r.data,
            hotkeyLevel: 'user',
            hotkeyId: String(r.data.hotkeyId), // <-- must convert to string
            hotkeyLevelId: String(userId), // <-- must convert to string
            __status: r.data.hotkeyLevel === 'user' ? 'update' : 'add', // <-- required for API to work
          };
          return data;
        });
        axios.post('/sys/hotkey/submit', reqBody)
          .then((res) => {
            if (res.success) {
              message.success($l('preference.submit.success'));
            } else {
              message.error($l('preference.submit.failure'));
            }
            this.setState({
              loading: false,
            });
          })
          .catch(error => message.error($l('preference.submit.error')));
      }
    });
  };

  handleHotkeyClose = () => {
    this.hotkeyConfigModal.close();
  };

  renderHotkeyConfigBtn = () => (
    <Button onClick={this.openHotkeyConfigModal}>{$l('preference.configuration')}</Button>
  );

  render() {
    const { preferencesDS, AppState } = this.props;
    const { loading } = this.state;
    const [timeZoneIndex, localIndex, isTabNavIndex] = [
      findIndexFromDataSet(preferencesDS, FIND_INDEX_FIELD, PREFERFENCES_MAPPER.timeZone),
      findIndexFromDataSet(preferencesDS, FIND_INDEX_FIELD, PREFERFENCES_MAPPER.locale),
      findIndexFromDataSet(preferencesDS, FIND_INDEX_FIELD, PREFERFENCES_MAPPER.isTabNav),
    ];
    return (
      <Form style={{ marginLeft: '2rem', width: '4rem' }} labelWidth={150}>
        <Select
          dataSet={preferencesDS}
          dataIndex={timeZoneIndex}
          name={VALUE_FIELD}
          label={$l('preference.timezone')}
          help={$l('preference.timezone.description')}
          clearButton={false}
        />

        <Select
          dataSet={preferencesDS}
          dataIndex={localIndex}
          name={VALUE_FIELD}
          label={$l('preference.locale')}
          help={$l('preference.locale.description')}
          clearButton={false}
        />
        <CheckBox
          dataSet={preferencesDS}
          dataIndex={isTabNavIndex}
          name={VALUE_FIELD}
          label={$l('preference.tabstrip')}
        >
          {$l('preference.tabstrip.enable')}
        </CheckBox>
        <Output
          label={$l('hotkeyconfiguration')}
          renderer={this.renderHotkeyConfigBtn}
        />
        <Button color="blue" onClick={this.handleSubmitBtnClick} loading={loading}>{$l('hap.save')}</Button>
      </Form>
    );
  }
}
