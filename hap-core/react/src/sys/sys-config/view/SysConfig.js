import '../index.less';

import { $l, axiosPro as axios } from '@choerodon/boot';
import { Button, CheckBox, DataSet, Form, message, NumberField, Output, SelectBox, TextField, Upload, UrlField } from 'choerodon-ui/pro';
import { observer } from 'mobx-react';
import React, { Component } from 'react';

const CONFIG_CODE = {
  systemTitle: 'SYS_TITLE',
  captcha: 'CAPTCHA',
  defaultPassword: 'DEFAULT_PASSWORD',
  passwordMinLength: 'PASSWORD_MIN_LENGTH',
  passwordComplexity: 'PASSWORD_COMPLEXITY',
  passwordOutdateTime: 'PASSWORD_INVALID_TIME',
  roleMerge: 'USER_ROLE_MERGE',
  defaultTargetUrl: 'DEFAULT_TARGET_URL',
  firstLoginResetPassword: 'FIRST_LOGIN_RESET_PASSWORD',
  preventDuplicateLogin: 'PROHIBIT_REPEAT_LOGIN',
  authLockTime: 'OAUTH2_AUTHENTICATION_LOCK_TIME',
  authNum: 'OAUTH2_AUTHENTICATION_NUM',
};

/**
 * 根据configCode从sysConfigDS中获取index
 *
 * @param {DataSet} sysConfigDS
 * @param {string} configCode
 * @returns {number} 索引值
 */
function findIndexFromConfigCode(sysConfigDS, configCode) {
  return sysConfigDS.findIndex(r => r.get('configCode') === configCode);
}

const VALUE_FIELD = 'configValue';
const STYLE_PREFIX = 'sys-config';

@observer
export default class SysConfig extends Component {
  constructor(props) {
    super(props);
    const { logo, favicon } = props.appState;
    this.state = {
      loading: false, // <-- 用于控制提交按钮的状态
      logo,
      favicon,
    };
  }

  handleSubmit = () => {
    const { sysConfigDS } = this.props;
    this.setState({
      loading: true,
    });
    sysConfigDS.submit().then((res) => {
      this.setState({
        loading: false,
      });
    });
  };

  handleUploadComplete = (response) => {
    const { appState } = this.props;
    const { success, message: resMessage } = JSON.parse(response);
    if (success) {
      message.success(resMessage);
      axios.post('/sys/config/system/info')
        .then((res) => {
          appState.updateSysInfo('logoImageSrc', `${res.logoImageSrc}?v=${Date.now()}`);
          appState.updateSysInfo('faviconImageSrc', `${res.faviconImageSrc}?v=${Date.now()}`);
        })
        .catch((error) => {
          message.error(resMessage);
        });
    } else {
      message.error(resMessage);
    }
  };

  handleLogoChange = (fileList) => {
    if (fileList.length) {
      this.setState({
        logo: fileList[0].url,
      });
    }
  };

  handleFaviconChange = (fileList) => {
    if (fileList.length) {
      this.setState({
        favicon: fileList[0].url,
      });
    }
  };

  get hint() {
    const { sysConfigDS } = this.props;
    if (sysConfigDS.updated.length) {
      return null;
    }
    return `(${$l('sys.config.save_button.prompt')})`;
  }

  render() {
    const { sysConfigDS } = this.props;
    const { logo, favicon, loading } = this.state;

    const logoUpload = (
      <div className="flex-wrapper">
        <div className="preview">
          <img height="70" width="100" src={logo} alt="logo" />
        </div>
        <div className="upload-wrapper">
          <Upload
            action="/sys/config/system/logo/upload"
            name="logo"
            uploadImmediately={false}
            multiple={false}
            onUploadSuccess={this.handleUploadComplete}
            extra={$l('sys.config.systemlogo.desciption')}
            showPreviewImage={false}
            onFileChange={this.handleLogoChange}
          />
        </div>
      </div>
    );

    const faviconUpload = (
      <div className="flex-wrapper">
        <div className="preview">
          <img height="32" width="32" src={favicon} alt="favicon" />
        </div>
        <div className="upload-wrapper">
          <Upload
            action="/sys/config/system/favicon/upload"
            name="favicon"
            uploadImmediately={false}
            multiple={false}
            onUploadSuccess={this.handleUploadComplete}
            extra={$l('sys.config.favicon.desciption')}
            showPreviewImage={false}
            onFileChange={this.handleFaviconChange}
          />
        </div>
      </div>
    );

    // 使用React.Fragments包裹多个Form
    return (
      <React.Fragment>
        <Form style={{ marginLeft: '2.5rem', width: '5rem' }}>
          <TextField
            dataSet={sysConfigDS}
            dataIndex={findIndexFromConfigCode(sysConfigDS, CONFIG_CODE.systemTitle)}
            name={VALUE_FIELD}
            label={$l('sys.config.system_title')}
          />
        </Form>
        <Form
          labelWidth={250}
          header={$l('sys.config.style')}
          className={`${STYLE_PREFIX}-upload ${STYLE_PREFIX}-form`}
        >
          <Output
            label={$l('sys.config.systemlogo')}
            renderer={() => logoUpload}
          />
          <hr />
          <Output
            label={$l('sys.config.favicon')}
            renderer={() => faviconUpload}
          />
        </Form>
        <Form
          labelWidth={250}
          header={$l('sys.config.safety')}
          className={`${STYLE_PREFIX}-form`}
        >
          <SelectBox
            vertical
            dataSet={sysConfigDS}
            dataIndex={findIndexFromConfigCode(sysConfigDS, CONFIG_CODE.captcha)}
            name={VALUE_FIELD}
            label={$l('sys.config.captcha')}
          />
          <NumberField
            dataSet={sysConfigDS}
            dataIndex={findIndexFromConfigCode(sysConfigDS, CONFIG_CODE.authNum)}
            name={VALUE_FIELD}
            step={1}
            min={0}
            label={$l('sys.config.oauth2_authentication_num')}
            className={`${STYLE_PREFIX}-short-input`}
          />
          <NumberField
            dataSet={sysConfigDS}
            dataIndex={findIndexFromConfigCode(sysConfigDS, CONFIG_CODE.authLockTime)}
            name={VALUE_FIELD}
            step={1}
            min={0}
            label={$l('sys.config.oauth2_authentication_lock_time')}
            className={`${STYLE_PREFIX}-short-input`}
          />
        </Form>
        <Form
          labelWidth={250}
          header={$l('sys.config.other')} // <-- weriod name 'other'
          className={`${STYLE_PREFIX}-form`}
        >
          <TextField
            dataSet={sysConfigDS}
            dataIndex={findIndexFromConfigCode(sysConfigDS, CONFIG_CODE.defaultPassword)}
            name={VALUE_FIELD}
            label={$l('sys.config.default_password')}
            className={`${STYLE_PREFIX}-short-input`}
          />
          <NumberField
            dataSet={sysConfigDS}
            dataIndex={findIndexFromConfigCode(sysConfigDS, CONFIG_CODE.passwordMinLength)}
            name={VALUE_FIELD}
            step={1}
            min={0}
            label={$l('sys.config.password_min_length')}
            className={`${STYLE_PREFIX}-short-input`}
          />
          <SelectBox
            vertical
            dataSet={sysConfigDS}
            dataIndex={findIndexFromConfigCode(sysConfigDS, CONFIG_CODE.passwordComplexity)}
            name={VALUE_FIELD}
            label={$l('sys.config.password_complexity')}
          />
          <NumberField
            dataSet={sysConfigDS}
            dataIndex={findIndexFromConfigCode(sysConfigDS, CONFIG_CODE.passwordOutdateTime)}
            name={VALUE_FIELD}
            step={1}
            min={0}
            label={$l('sys.config.password_invalid_time')}
            help={$l('sys.config.password_invalid_describe')}
            className={`${STYLE_PREFIX}-short-input`}
          />
        </Form>
        <Form
          labelWidth={250}
          header={$l('sys.login.safety')}
          className={`${STYLE_PREFIX}-form`}
        >
          <CheckBox
            dataSet={sysConfigDS}
            dataIndex={findIndexFromConfigCode(sysConfigDS, CONFIG_CODE.firstLoginResetPassword)}
            name={VALUE_FIELD}
            value="Y"
            unCheckedValue="N"
            label={$l('sys.config.resetpassword')}
            type="string"
          >
            {$l('sys.config.resetpassword.describe')}
          </CheckBox>
          <CheckBox
            dataSet={sysConfigDS}
            dataIndex={findIndexFromConfigCode(sysConfigDS, CONFIG_CODE.preventDuplicateLogin)}
            name={VALUE_FIELD}
            value="Y"
            unCheckedValue="N"
            label={$l('sys.config.prohibit_repeat_login')}
          >
            {$l('sys.config.prohibit_repeat_login.describe')}
          </CheckBox>
          <CheckBox
            dataSet={sysConfigDS}
            dataIndex={findIndexFromConfigCode(sysConfigDS, CONFIG_CODE.roleMerge)}
            name={VALUE_FIELD}
            value="Y"
            unCheckedValue="N"
            label={$l('sys.config.rolemerge')}
          >
            {$l('sys.config.user_role_merge.describe')}
          </CheckBox>
          <UrlField
            dataSet={sysConfigDS}
            dataIndex={findIndexFromConfigCode(sysConfigDS, CONFIG_CODE.defaultTargetUrl)}
            name={VALUE_FIELD}
            label={$l('sys.config.default_target_url')}
            help={$l('sys.config.default_target_url.describe')}
            className={`${STYLE_PREFIX}-short-input`}
          />
          <Button
            color="blue"
            onClick={this.handleSubmit}
            loading={loading}
            disabled={!sysConfigDS.updated.length}
          >
            {$l('hap.save')}{this.hint}
          </Button>
        </Form>
      </React.Fragment>
    );
  }
}
