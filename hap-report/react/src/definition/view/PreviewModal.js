import _ from 'lodash';
import React, { PureComponent } from 'react';
import { observer } from 'mobx-react';
import moment from 'moment';
import { withRouter } from 'react-router-dom';
import { $l, axiosPro as axios, ContentPro as Content } from '@choerodon/boot';
import '../index.scss';
import { Button, DataSet, DatePicker, Form, Lov, Select, Spin, Stores, TextField } from 'choerodon-ui/pro';

@observer
@withRouter
export default class PreviewModal extends PureComponent {
  constructor(props) {
    super(props);
    this.reportCode = _.get(this, 'props.match.params.code');
    this.initParameterRender(this.reportCode);
  }

  parameterDS = new DataSet({
    fields: [],
  });

  state = {
    loading: true,
    previewUrl: '',
    showParameter: false,
    haveParameter: false,
  };

  paramRender = [];

  paramList = {};

  conditionUrl = '';

  baseUrl = '';

  lovConfigs = {};

  paramDisplays = {};

  /**
   * 获取lov配置信息.
   * @param sourceCode lov编码
   * @param tableFieldName 报表参数名
   * @returns {Promise}
   */
  getLovConfig({ sourceCode, tableFieldName }) {
    return new Promise((resolve) => {
      Stores.LovCodeStore.fetchConfig(sourceCode)
        .then((config) => {
          resolve([tableFieldName, config]);
        });
    });
  }

  /**
   * 获取lov控件的配置信息.
   * @param parameterData 报表参数数据
   * @returns {Promise.<void>}
   */
  async getConfig(parameterData) {
    const configs = await Promise.all(parameterData
      .filter(({ display, enabled }) => (display === 'LOV' && enabled === 'Y'))
      .map(data => this.getLovConfig(data)));
    return configs.reduce((obj, [tableFieldName, config]) => {
      obj[tableFieldName] = config;
      return obj;
    }, {});
  }

  /**
   * 获取报表参数url.
   * @param obj 报表参数对象
   * @param url 报表参数初始url
   * @returns {*} 报表参数url
   */
  getConditionUrl(obj, url) {
    Object.keys(obj).forEach((key) => {
      url += `&${key}=${obj[key]}`;
    });
  }

  /**
   * 点击应用，重新查询报表数据.
   * @returns {Promise.<void>}
   */
  async apply() {
    this.conditionUrl = '';
    const { current } = this.parameterDS;
    if (await current.validate()) {
      Object.keys(this.paramList).forEach((key) => {
        const paramRealValue = this.getParamRealValue(key, current.get(key));
        this.conditionUrl += `&${key}=${paramRealValue}`;
      });
      this.setState({
        previewUrl: this.baseUrl + this.conditionUrl,
      });
    }
  }

  /**
   * 获取报表参数实际值.
   * @param tableFieldName 报表参数名
   * @param paramValue 报表参数对象
   * @returns {string} 报表参数实际值
   */
  getParamRealValue(tableFieldName, paramValue) {
    let value = '';
    if (!paramValue) {
      return value;
    }
    const display = this.paramDisplays[tableFieldName];
    const lovConfig = this.lovConfigs[tableFieldName];
    switch (display) {
      case 'textBox':
      case 'comboBox':
        value = paramValue;
        break;
      case 'datePicker':
        value = paramValue.format('YYYY-MM-DD');
        break;
      case 'LOV':
        if (lovConfig) {
          value = paramValue[lovConfig.valueField];
        }
        break;
      case 'multiSelect':
        value = paramValue.join(',');
        break;
      default:
        break;
    }
    return value;
  }

  /**
   * 如果报表定义有参数,渲染应用按钮.
   * @returns {*} 应用按钮
   */
  getApplyRender() {
    this.paramRender.push(
      <div>
        <Button
          color="blue"
          icon="test_execute"
          onClick={() => this.apply()}
        >
          {$l('report.apply')}
        </Button>
      </div>,
    );
  }

  /**
   * 渲染报表参数控件.
   * @param reportCode 报表编码
   * @returns {Promise.<void>}
   */
  async initParameterRender(reportCode) {
    // 获取报表设计时 设置的总参数列表
    const reportFileParamResult = await axios.post(`/sys/report/queryReportFileParams?reportCode=${reportCode}`);
    if (reportFileParamResult.success) {
      reportFileParamResult.rows.map((key) => {
        this.paramList[key] = '';
        return null;
      });
    }
    // 获取报表定义时 设置的参数列表
    const queryReportParamResult = await axios.post(`/sys/parameter/config/queryReportParameter?reportCode=${reportCode}`);
    let requiredHasDefaultValue = true;
    if (queryReportParamResult.success) {
      const { rows } = queryReportParamResult;
      // 获取LOV控件类型的LOV配置信息
      this.lovConfigs = await this.getConfig(rows);
      for (let i = 0; i < rows.length; i += 1) {
        const parameterData = rows[i];
        if (parameterData.enabled === 'Y') {
          const { display, tableFieldName, required, defaultValue } = parameterData;
          this.paramList[tableFieldName] = defaultValue || '';
          this.paramDisplays[tableFieldName] = display;
          // 如果查询条件为必输 但是没有默认值 不在预览界面打开时就查询
          if (required === 'Y' && !defaultValue) {
            requiredHasDefaultValue = false;
          }
          this.setParameterRender(parameterData, this.lovConfigs);
        }
      }
      this.parameterDS.create();
      let showParameter;
      let haveParameter;
      // 判断是否有参数 有则在右侧显示参数控件列表  否则全屏预览
      if (queryReportParamResult.total) {
        showParameter = true;
        haveParameter = true;
        this.getApplyRender();
      } else {
        haveParameter = false;
        showParameter = false;
      }
      // 获取报表定义信息
      const reportPropsResult = await axios.post(`/sys/report/queryByCode?reportCode=${reportCode}`);
      let previewUrl = '';
      if (reportPropsResult && reportPropsResult.success && reportPropsResult.rows[0]) {
        const { fileName, defaultQuery } = reportPropsResult.rows[0];
        this.getConditionUrl(this.paramList, this.conditionUrl);
        this.baseUrl = `/ureport/preview?_u=database:${fileName}&_i=1&_r=1`;
        // 如果设置为打开默认查询且必输字段全部赋值 则在预览界面打开时就查询报表数据
        if (defaultQuery === 'Y' && requiredHasDefaultValue) {
          previewUrl = this.baseUrl + this.conditionUrl;
        }
      }
      this.setState({
        previewUrl,
        showParameter,
        loading: false,
        haveParameter,
      });
    }
  }

  /**
   * 获取动态dataSet请求路径.
   * @param display 控件类型
   * @param sourceType 数据类型
   * @param sourceCode 数据来源
   * @param codeValueField 快码值字段
   * @returns {string} 动态dataSet请求路径
   */
  getDynamicDataSetUrl(display, sourceType, sourceCode, codeValueField) {
    if (sourceType === 'LOV') {
      return `/sys/parameter/config/getLov?sourceCode=${sourceCode}`;
    }
    if (sourceType === 'CODE') {
      if (display === 'comboBox') {
        return `/sys/parameter/config/getCode?sourceCode=${sourceCode}&codeValueField=${codeValueField}`;
      } else {
        return `/sys/parameter/config/getCode?sourceCode=${sourceCode}`;
      }
    }
  }

  /**
   * 将extraAttribute json字符串转换为json对象
   * @param extraAttribute extraAttribute json字符串
   * @returns {*|{}} extraAttribute json对象
   */
  getExtraAttribute(extraAttribute) {
    let result = extraAttribute || {};
    if (typeof (result) === 'string') {
      result = JSON.parse(result) || {};
    }
    return result;
  }

  /**
   * 生成参数控件.
   * @param parameter 参数
   * @param lovConfigs lov配置信息
   * @returns {Promise.<void>}
   */
  async setParameterRender(parameter, lovConfigs) {
    const { display } = parameter;
    switch (display) {
      case 'textBox':
        this.createTextBox(parameter);
        break;
      case 'datePicker':
        this.createDatePicker(parameter);
        break;
      case 'LOV':
        this.createLov(parameter, lovConfigs);
        break;
      case 'multiSelect':
        this.createMultiSelect(parameter);
        break;
      case 'comboBox':
        this.createComboBox(parameter);
        break;
      default:
        break;
    }
  }

  /**
   * 创建文本框参数.
   * @returns {[XML]}
   */
  createTextBox(parameter) {
    const { tableFieldName, title, description, defaultValue } = parameter;
    this.parameterDS.addField(tableFieldName,
      {
        name: tableFieldName,
        type: 'string',
        defaultValue,
        required: parameter.required === 'Y',
      });
    this.paramRender.push(
      <div label={title}>
        <TextField
          style={{ width: 183 }}
          key={tableFieldName}
          name={tableFieldName}
          dataSet={this.parameterDS}
          disabled={parameter.readOnly === 'Y'}
        />
        <br/>
        <label className="preview-description">
          {description}
        </label>
      </div>,
    );
  }

  /**
   * 控制可选日期最大值.
   * @returns {*|moment.Moment}
   */
  setDatePickerFromMax(datePickerTo) {
    if (datePickerTo) {
      return moment(datePickerTo);
    }
  }

  /**
   * 控制可选日期最小值.
   * @returns {*|moment.Moment}
   */
  setDatePickerToMin(datePickerFrom) {
    if (datePickerFrom) {
      return moment(datePickerFrom);
    }
  }

  /**
   * 创建日期框参数.
   * @returns {[XML]}
   */
  createDatePicker(parameter) {
    const extraAttribute = this.getExtraAttribute(parameter.extraAttribute);
    const datePickerFrom = extraAttribute.datePickerFrom || '';
    const datePickerTo = extraAttribute.datePickerTo || '';
    const { tableFieldName, title, description, defaultValue } = parameter;
    this.parameterDS.addField(tableFieldName,
      {
        name: tableFieldName,
        type: 'date',
        required: parameter.required === 'Y',
        defaultValue,
      });
    this.paramRender.push(
      <div label={title}>
        <DatePicker
          style={{ width: 183 }}
          key={tableFieldName}
          name={tableFieldName}
          dataSet={this.parameterDS}
          disabled={parameter.readOnly === 'Y'}
          mode="date"
          min={this.setDatePickerToMin(datePickerFrom)}
          max={this.setDatePickerFromMax(datePickerTo)}
        />
        <br/>
        <label className="preview-description">
          {description}
        </label>
      </div>,
    );
  }

  /**
   * 创建LOV参数.
   * @returns {[XML]}
   */
  async createLov(parameter, lovConfigs) {
    const { tableFieldName, title, description, sourceCode, defaultValue, defaultText } = parameter;
    const lovConfig = lovConfigs[tableFieldName];
    const defaultValueObj = {};
    defaultValueObj[lovConfig.textField] = defaultText;
    defaultValueObj[lovConfig.valueField] = defaultValue;
    this.parameterDS.addField(tableFieldName,
      {
        name: tableFieldName,
        type: 'object',
        required: parameter.required === 'Y',
        defaultValue: defaultValueObj,
        lovCode: sourceCode,
        valueField: lovConfig.valueField,
        textField: lovConfig.textField,
      });
    this.paramRender.push(
      <div label={title}>
        <Lov
          style={{ width: 183 }}
          key={tableFieldName}
          name={tableFieldName}
          dataSet={this.parameterDS}
          disabled={parameter.readOnly === 'Y'}
        />
        <br/>
        <label className="preview-description">
          {description}
        </label>
      </div>,
    );
  }

  /**
   * 创建多选框参数.
   * @returns {[XML]}
   */
  createMultiSelect(parameter) {
    const { tableFieldName, title, description } = parameter;
    this.parameterDS.addField(tableFieldName, this.getSelectField(parameter, true));
    this.paramRender.push(
      <div label={title}>
        <Select
          style={{ width: 183 }}
          key={tableFieldName}
          name={tableFieldName}
          dataSet={this.parameterDS}
          disabled={parameter.readOnly === 'Y'}
        />
        <br/>
        <label className="preview-description">
          {description}
        </label>
      </div>,
    );
  }

  getSelectField(parameter, isMulti) {
    const { tableFieldName, sourceType, sourceCode } = parameter;
    const extraAttribute = this.getExtraAttribute(parameter.extraAttribute);
    const { cascadeFrom, cascadeFromField, codeValueField } = extraAttribute;
    let { defaultValue } = parameter;
    defaultValue = defaultValue !== '' ? defaultValue : null;
    const field = {
      name: tableFieldName,
      required: parameter.required === 'Y',
      defaultValue,
    };
    if (isMulti) {
      field.multiple = ',';
    }
    if (sourceType === 'LOV') {
      field.lovCode = sourceCode;
    } else if (sourceType === 'CODE') {
      field.lookupCode = sourceCode;
      if (!isMulti) {
        field.textField = 'meaning';
        field.valueField = codeValueField;
      }
    }
    // 设置下拉框级联属性
    if (cascadeFrom && cascadeFromField) {
      const cascadeObj = {};
      cascadeObj[cascadeFromField] = cascadeFrom;
      field.cascadeMap = cascadeObj;
    }
    return field;
  }

  /**
   * 创建下拉框参数.
   * @returns {[XML]}
   */
  createComboBox(parameter) {
    const { tableFieldName, title, description, defaultValue } = parameter;
    this.parameterDS.addField(tableFieldName, this.getSelectField(parameter, false));
    this.paramRender.push(
      <div label={title}>
        <Select
          style={{ width: 183 }}
          key={tableFieldName}
          name={tableFieldName}
          dataSet={this.parameterDS}
          disabled={parameter.readOnly === 'Y'}
        />
        <br />
        <label className="preview-description">
          {description}
        </label>
      </div>,
    );
  }

  handleClickIcon = () => {
    const { showParameter } = this.state;
    this.setState({
      showParameter: !showParameter,
    });
  };

  renderIcon(showParameter) {
    return (
      <div className="icon-wrapper" style={{ right: showParameter ? 257 : 2 }}>
        <Button
          size="large"
          funcType="flat"
          icon={showParameter ? 'navigate_next' : 'navigate_before'}
          onClick={this.handleClickIcon}
        />
      </div>
    );
  }

  renderLeft(previewUrl, haveParameter, showParameter) {
    return (
      <div className="left">
        <iframe
          title={$l('report.preview')}
          id="previewIFrame"
          name="previewIFrame"
          src={previewUrl ? `${previewUrl}` : null}
          height="100%"
          width="100%"
        />
        {
          haveParameter ? this.renderIcon(showParameter) : null
        }
      </div>
    );
  }

  renderRight(loading, showParameter) {
    const classNameString = showParameter ? 'right-show' : 'right-hidden';
    return (
      <div className={`${classNameString}`}>

        {
          loading ? null : (
            <Spin spinning={loading}>
              <Form labelLayout="vertical" dataSet={this.parameterDS}>
                {this.paramRender}
              </Form>
            </Spin>
          )
        }
      </div>
    );
  }

  render() {
    const { previewUrl, loading, showParameter, haveParameter } = this.state;
    return (
      <Content className="report-definition" style={{ padding: 0 }}>
        <div className="wrap">
          {this.renderLeft(previewUrl, haveParameter, showParameter)}
          {this.renderRight(loading, showParameter)}
        </div>
      </Content>
    );
  }
}
