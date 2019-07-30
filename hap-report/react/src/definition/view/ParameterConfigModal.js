import React, { PureComponent } from 'react';
import { Button, DataSet, DatePicker, Form, Lov, Modal, Select, Stores, TextArea, TextField, Tooltip } from 'choerodon-ui/pro';
import { $l, axiosPro as axios } from '@choerodon/boot';
import { observer } from 'mobx-react';
import moment from 'moment';
import ParameterConfigDataSet from '../stores/ParameterConfigDataSet';

@observer
export default class ParameterConfigModal extends PureComponent {
  constructor(props) {
    super(props);
    this.props.modal.handleOk(this.handleOK.bind(this));
    this.parameterConfigDS = new DataSet(ParameterConfigDataSet);
    this.defaultValueDS = new DataSet({
      paging: false,
    });
    this.parameterData = this.props.parameterData;
    this.reportData = this.props.reportData;
    this.state = {
      sourceType: this.parameterData.sourceType,
      defaultType: this.parameterData.defaultType,
      sourceCode: this.parameterData.sourceCode,
    };
    this.initParameterConfig();
  }

  /**
   * 初始化参数配置数据.
   */
  initParameterConfig() {
    const { display } = this.parameterData;
    const configRecord = this.parameterConfigDS.current;
    const parameterRecord = this.parameterData;
    switch (display) {
      case 'textBox':
        this.initTextBoxProps(configRecord, parameterRecord);
        break;
      case 'datePicker':
        this.initDatePickerProps(configRecord, parameterRecord);
        break;
      case 'LOV':
        this.initLovProps(configRecord, parameterRecord);
        break;
      case 'multiSelect':
        this.initMultiSelectProps(configRecord, parameterRecord);
        break;
      case 'comboBox':
        this.initComboBoxProps(configRecord, parameterRecord);
        break;
      default:
        break;
    }
  }

  /**
   * 初始化当前参数配置的值(如果是第一次修改 取dataSet上的默认值 否则取上一次保存的结果).
   * @param value 值
   * @param field 参数字段
   * @param record 当前参数配置
   */
  initConfigFieldValue(record, field, value) {
    if (value) {
      record.set(field, value);
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
   * 初始化‘文本框’配置数据.
   * @param configRecord  当前参数配置
   * @param parameterRecord 当前参数
   */
  initTextBoxProps(configRecord, parameterRecord) {
    const { defaultType, defaultValue } = parameterRecord;
    this.initConfigFieldValue(configRecord, 'defaultType', defaultType);
    configRecord.set('defaultValue', defaultValue);
  }

  /**
   * 初始化‘日期框’配置数据.
   * @param configRecord  当前参数配置
   * @param parameterRecord 当前参数
   */
  initDatePickerProps(configRecord, parameterRecord) {
    const { defaultType, defaultValue } = parameterRecord;
    this.initConfigFieldValue(configRecord, 'defaultType', defaultType);
    if (defaultType === 'const') {
      configRecord.getField('defaultValue').set('type', 'date');
      configRecord.set('defaultValue', defaultValue);
    } else if (defaultType === 'sql') {
      configRecord.set('defaultValue', defaultValue);
    }
    const extraAttribute = this.getExtraAttribute(parameterRecord.extraAttribute);
    configRecord.set('datePickerFrom', extraAttribute.datePickerFrom);
    configRecord.set('datePickerTo', extraAttribute.datePickerTo);
  }

  /**
   * 初始化‘LOV’配置数据.
   * @param configRecord  当前参数配置
   * @param parameterRecord 当前参数
   */
  async initLovProps(configRecord, parameterRecord) {
    const { sourceType, sourceCode, sourceName, defaultType, defaultValue, defaultText } = parameterRecord;
    this.initConfigFieldValue(configRecord, 'sourceType', sourceType);
    this.initConfigFieldValue(configRecord, 'defaultType', defaultType);
    configRecord.set('sourceCodeObject', { code: sourceCode, description: sourceName });
    if (defaultType === 'const') {
      const config = await Stores.LovCodeStore.fetchConfig(sourceCode);
      this.state.lovDefaultTextField = config.textField;
      this.state.lovDefaultValueField = config.valueField;
      const field = configRecord.getField('defaultValue');
      field.set('textField', config.textField);
      field.set('lovCode', sourceCode);
      field.set('type', 'object');
      const defaultValueObj = {};
      defaultValueObj[config.textField] = defaultText;
      defaultValueObj[config.valueField] = defaultValue;
      configRecord.set('defaultValue', defaultValueObj);
    } else if (defaultType === 'sql') {
      configRecord.set('defaultValue', defaultValue);
    }
  }

  /**
   * 初始化‘多选框’配置数据.
   * @param configRecord  当前参数配置
   * @param parameterRecord 当前参数
   */
  initMultiSelectProps(configRecord, parameterRecord) {
    const { display, sourceType, sourceCode, sourceName, defaultType } = parameterRecord;
    let { defaultValue } = parameterRecord;
    this.initConfigFieldValue(configRecord, 'sourceType', sourceType);
    this.initConfigFieldValue(configRecord, 'defaultType', defaultType);
    configRecord.set('sourceCodeObject', { code: sourceCode, description: sourceName });
    if (defaultType === 'const') {
      const field = configRecord.getField('defaultValue');
      field.set('multiple', ',');
      if (sourceType === 'LOV') {
        field.set('type', 'auto');
        field.set('lovCode', sourceCode);
        field.set('lookupCode', null);
      } else if (sourceType === 'CODE') {
        field.set('type', 'auto');
        field.set('lovCode', null);
        field.set('lookupCode', sourceCode);
      }
      defaultValue = defaultValue !== '' ? defaultValue : null;
      configRecord.set('defaultValue', defaultValue);
    } else if (defaultType === 'sql') {
      configRecord.set('defaultValue', defaultValue);
    }
  }

  /**
   * 初始化‘下拉框’配置数据.
   * @param configRecord  当前参数配置
   * @param parameterRecord 当前参数
   */
  initComboBoxProps(configRecord, parameterRecord) {
    const { display, sourceType, sourceCode, sourceName, defaultType, defaultValue } = parameterRecord;
    this.initConfigFieldValue(configRecord, 'sourceType', sourceType);
    this.initConfigFieldValue(configRecord, 'defaultType', defaultType);
    const extraAttribute = this.getExtraAttribute(parameterRecord.extraAttribute);
    configRecord.set('cascadeFrom', extraAttribute.cascadeFrom);
    configRecord.set('cascadeFromField', extraAttribute.cascadeFromField);
    const codeValueField = extraAttribute.codeValueField || 'codeValueId';
    this.initConfigFieldValue(configRecord, 'codeValueField', codeValueField);
    configRecord.set('sourceCodeObject', { code: sourceCode, description: sourceName });
    if (defaultType === 'const') {
      const field = configRecord.getField('defaultValue');
      if (sourceType === 'LOV') {
        field.set('type', 'auto');
        field.set('lovCode', sourceCode);
        field.set('lookupCode', null);
      } else if (sourceType === 'CODE') {
        field.set('type', 'auto');
        field.set('lovCode', null);
        field.set('lookupCode', sourceCode);
        field.set('textField', 'meaning');
        field.set('valueField', codeValueField);
      }
      configRecord.set('defaultValue', defaultValue);
    } else if (defaultType === 'sql') {
      configRecord.set('defaultValue', defaultValue);
    }
  }

  /**
   * 保存参数配置数据.
   */
  handleOK() {
    const configRecord = this.parameterConfigDS.current;
    const parameterRecord = this.props.parameterDS.current;
    const { display } = this.parameterData;
    switch (display) {
      case 'textBox':
        this.handleTextBoxProps(configRecord, parameterRecord);
        break;
      case 'datePicker':
        this.handleDatePickerProps(configRecord, parameterRecord);
        break;
      case 'LOV':
        this.handleLovProps(configRecord, parameterRecord);
        break;
      case 'multiSelect':
        this.handleMultiSelectProps(configRecord, parameterRecord);
        break;
      case 'comboBox':
        this.handleComboBoxProps(configRecord, parameterRecord);
        break;
      default:
        break;
    }
  }


  /**
   * 保存时处理输入框属性.
   * @param configRecord  当前参数配置
   * @param parameterRecord 当前参数
   */
  handleTextBoxProps(configRecord, parameterRecord) {
    const defaultType = configRecord.get('defaultType') || '';
    const defaultValue = configRecord.get('defaultValue') || '';
    parameterRecord.set('defaultType', defaultType);
    parameterRecord.set('defaultValue', defaultValue);
    parameterRecord.set('extraAttribute', {});
  }

  /**
   * 保存时处理日期框属性.
   * @param configRecord  当前参数配置
   * @param parameterRecord 当前参数
   */
  handleDatePickerProps(configRecord, parameterRecord) {
    const datePickerFrom = configRecord.get('datePickerFrom') || '';
    const datePickerTo = configRecord.get('datePickerTo') || '';
    const defaultType = configRecord.get('defaultType') || '';
    const defaultValue = configRecord.get('defaultValue') || '';
    const extraAttribute = {};
    extraAttribute.datePickerFrom = datePickerFrom;
    extraAttribute.datePickerTo = datePickerTo;
    parameterRecord.set('extraAttribute', extraAttribute);
    parameterRecord.set('defaultType', defaultType);
    parameterRecord.set('defaultValue', defaultValue);
  }

  /**
   * 保存时处理lov属性.
   * @param configRecord 当前参数配置
   * @param parameterRecord 当前参数
   */
  handleLovProps(configRecord, parameterRecord) {
    const sourceType = configRecord.get('sourceType') || '';
    const sourceCode = configRecord.get('sourceCode') || '';
    const sourceName = configRecord.get('sourceName') || '';
    const defaultType = configRecord.get('defaultType') || '';
    let defaultValue;
    let defaultText;
    if (defaultType === 'sql') {
      defaultValue = configRecord.get('defaultValue');
      defaultText = '';
    } else if (defaultType === 'const') {
      defaultText = configRecord.get('defaultValue') ? configRecord.get('defaultValue')[this.state.lovDefaultTextField] : '';
      defaultValue = configRecord.get('defaultValue') ? configRecord.get('defaultValue')[this.state.lovDefaultValueField] : '';
    }
    parameterRecord.set('sourceType', sourceType);
    parameterRecord.set('sourceCode', sourceCode);
    parameterRecord.set('sourceName', sourceName);
    parameterRecord.set('defaultType', defaultType);
    parameterRecord.set('defaultText', defaultText);
    parameterRecord.set('defaultValue', defaultValue);
    parameterRecord.set('extraAttribute', {});
  }

  /**
   * 保存时处理多选框属性.
   * @param configRecord  当前参数配置
   * @param parameterRecord 当前参数
   */
  handleMultiSelectProps(configRecord, parameterRecord) {
    const sourceType = configRecord.get('sourceType') || '';
    const sourceCode = configRecord.get('sourceCode') || '';
    const sourceName = configRecord.get('sourceName') || '';
    const defaultType = configRecord.get('defaultType') || '';
    let defaultValue;
    if (defaultType === 'sql') {
      defaultValue = configRecord.get('defaultValue');
    } else if (defaultType === 'const') {
      defaultValue = configRecord.get('defaultValue') ? configRecord.get('defaultValue').join(',') : null;
    }
    parameterRecord.set('sourceType', sourceType);
    parameterRecord.set('sourceCode', sourceCode);
    parameterRecord.set('sourceName', sourceName);
    parameterRecord.set('defaultType', defaultType);
    parameterRecord.set('defaultValue', defaultValue);
    parameterRecord.set('extraAttribute', {});
  }

  /**
   * 保存时处理下拉框属性.
   * @param configRecord  当前参数配置
   * @param parameterRecord 当前参数
   */
  handleComboBoxProps(configRecord, parameterRecord) {
    const sourceType = configRecord.get('sourceType') || '';
    const sourceCode = configRecord.get('sourceCode') || '';
    const sourceName = configRecord.get('sourceName') || '';
    const defaultType = configRecord.get('defaultType') || '';
    const defaultValue = configRecord.get('defaultValue') || '';
    const cascadeFrom = configRecord.get('cascadeFrom') || '';
    const cascadeFromField = configRecord.get('cascadeFromField') || '';
    const codeValueField = configRecord.get('codeValueField') || '';
    const extraAttribute = {};
    extraAttribute.cascadeFrom = cascadeFrom;
    extraAttribute.cascadeFromField = cascadeFromField;
    extraAttribute.codeValueField = codeValueField;
    parameterRecord.set('extraAttribute', extraAttribute);
    parameterRecord.set('sourceType', sourceType);
    parameterRecord.set('sourceCode', sourceCode);
    parameterRecord.set('sourceName', sourceName);
    parameterRecord.set('defaultType', defaultType);
    parameterRecord.set('defaultValue', defaultValue);
  }


  /**
   * 控制可选日期最大值.
   * @returns {*|moment.Moment}
   */
  setDatePickerFromMax() {
    const { current } = this.parameterConfigDS;
    if (current) {
      const time = current.get('datePickerTo');
      if (time) {
        return moment(time);
      }
    }
  }

  /**
   * 控制可选日期最小值.
   * @returns {*|moment.Moment}
   */
  setDatePickerToMin() {
    const { current } = this.parameterConfigDS;
    if (current) {
      const time = current.get('datePickerFrom');
      if (time) {
        return moment(time);
      }
    }
  }

  /**
   *  更新日期从时 重置默认值.
   */
  handleChangeDatePickerFrom = () => {
    this.parameterConfigDS.current.set('defaultValue', '');
  };

  /**
   *  更新日期至时 重置默认值
   */
  handleChangeDatePickerTo = () => {
    this.parameterConfigDS.current.set('defaultValue', '');
  };

  /**
   * 如果为‘日期框控件’设置日期从和日期至.
   * @param display 控件类型
   * @returns {*}
   */
  getDatePickerConfig(display) {
    if (display === 'datePicker') {
      return [
        <DatePicker
          key="datePickerFrom"
          name="datePickerFrom"
          label={$l('parameterconfig.datepickerfrom')}
          dataSet={this.parameterConfigDS}
          mode="date"
          max={this.setDatePickerFromMax()}
          onChange={this.handleChangeDatePickerFrom}
        />,
        <DatePicker
          key="datePickerTo"
          name="datePickerTo"
          label={$l('parameterconfig.datepickerto')}
          dataSet={this.parameterConfigDS}
          mode="date"
          min={this.setDatePickerToMin()}
          onChange={this.handleChangeDatePickerTo}
        />,
      ];
    }
  }


  /**
   * 改变‘数据类型’时重新渲染界面.
   * @param value  选中的数据类型值
   */
  handleChangeSourceType = (value) => {
    this.parameterConfigDS.current.set('sourceCode', '');
    this.parameterConfigDS.current.set('sourceName', '');
    this.setState({
      sourceType: value,
    });
  };

  /**
   * 改变‘数据来源’时重新渲染界面.
   * @param value 选中的数据来源值
   */
  handleChangeSourceCode = async (value) => {
    if (value) {
      const { display } = this.parameterData;
      const configRecord = this.parameterConfigDS.current;
      // 改变数据来源时 重置默认值
      configRecord.set('defaultValue', null);
      const field = configRecord.getField('defaultValue');
      const defaultType = configRecord.get('defaultType');
      const sourceCode = configRecord.get('sourceCode');
      const sourceType = configRecord.get('sourceType');
      const codeValueField = configRecord.get('codeValueField');
      if (display === 'LOV') {
        // 获取LOV配置 重置默认值的属性
        const config = await Stores.LovCodeStore.fetchConfig(value.code);
        const lovCode = configRecord.get('sourceCode');
        field.set('textField', config.textField);
        field.set('lovCode', lovCode);
        field.set('type', 'object');
        this.setState({
          sourceCode: value.code,
          lovDefaultTextField: config.textField,
          lovDefaultValueField: config.valueField,
        });
      } else if (display === 'multiSelect') {
        // 重置默认值的属性
        field.set('multiple', ',');
        if (sourceType === 'LOV') {
          field.set('type', 'auto');
          field.set('lovCode', sourceCode);
          field.set('lookupCode', null);
        } else if (sourceType === 'CODE') {
          field.set('type', 'auto');
          field.set('lovCode', null);
          field.set('lookupCode', sourceCode);
        }
        this.setState({
          sourceCode: value.code,
        });
      } else if (display === 'comboBox') {
        // 重置默认值的属性
        field.set('multiple', false);
        if (sourceType === 'LOV') {
          field.set('type', 'auto');
          field.set('lovCode', sourceCode);
          field.set('lookupCode', null);
        } else if (sourceType === 'CODE') {
          field.set('type', 'auto');
          field.set('lovCode', null);
          field.set('lookupCode', sourceCode);
          field.set('textField', 'meaning');
          field.set('valueField', codeValueField);
        }
        this.setState({
          sourceCode: value.code,
        });
      }
    } else {
      this.setState({
        sourceCode: value,
      });
    }
  };

  /**
   * 改变‘默认类型’时重新渲染界面.
   * @param value 选中的默认类型值
   */
  handleChangeDefaultType = (value) => {
    this.parameterConfigDS.current.set('defaultValue', null);
    this.parameterConfigDS.current.getField('defaultValue').set('type', 'string');
    this.setState({
      defaultType: value,
    });
  };


  /**
   * 如果为‘下拉框控件’渲染快码值字段.
   * @param display 控件类型
   * @returns {*}
   */
  getCodeValueFieldRender(display) {
    const sourceType = this.parameterConfigDS.current.get('sourceType');
    if (display === 'comboBox' && sourceType === 'CODE') {
      return [
        <Select
          key="codeValueField"
          name="codeValueField"
          label={$l('parameterconfig.codevaluefield')}
          dataSet={this.parameterConfigDS}
          clearButton={false}
        />,
      ];
    }
    return null;
  }

  /**
   * 如果为‘下拉框控件’渲染级联来源.
   * @param display 控件类型
   * @returns {*}
   */
  getCascadeFromRender(display) {
    if (display === 'comboBox') {
      const { sourceType, reportCode } = this.reportData;
      const field = this.parameterConfigDS.current.getField('cascadeFrom');
      if (sourceType === 'buildin' || sourceType === 'jdbc') {
        const lookupUrl = `/sys/report/getReportFileParams?reportCode=${reportCode}`;
        field.set('lookupUrl', lookupUrl);
        field.set('textField', 'meaning');
        field.set('valueField', 'value');
        return [
          <Select
            key="cascadeFrom"
            name="cascadeFrom"
            label={$l('parameterconfig.cascadefrom')}
            dataSet={this.parameterConfigDS}
          />,
        ];
      } else {
        return [
          <TextField
            key="cascadeFrom"
            name="cascadeFrom"
            label={$l('parameterconfig.cascadefrom')}
            dataSet={this.parameterConfigDS}
          />,
        ];
      }
    }
    return null;
  }

  /**
   * 如果为‘下拉框控件’渲染级联字段.
   * @param display 控件类型
   * @returns {*}
   */
  getCascadeFromFieldRender(display) {
    if (display === 'comboBox') {
      const parameterSourceType = this.parameterConfigDS.current.get('sourceType');
      if (parameterSourceType === 'LOV') {
        return [
          <TextField
            key="cascadeFromField"
            name="cascadeFromField"
            label={$l('parameterconfig.cascadefromfield')}
            dataSet={this.parameterConfigDS}
          />,
        ];
      } else if (parameterSourceType === 'CODE') {
        return [
          <Select
            key="cascadeFromField"
            name="cascadeFromField"
            label={$l('parameterconfig.cascadefromfield')}
            dataSet={this.parameterConfigDS}
          />,
        ];
      }
    }
    return null;
  }

  /**
   * 根据‘控件类型’渲染数据类型.
   * @param display 控件类型
   * @returns {*}
   */
  getSourceTypeRender(display) {
    if (display === 'LOV' || display === 'comboBox' || display === 'multiSelect') {
      return [
        <Select
          key="sourceType"
          name="sourceType"
          label={$l('parameterconfig.sourcetype')}
          dataSet={this.parameterConfigDS}
          onChange={this.handleChangeSourceType}
          clearButton={false}
          optionsFilter={
            // 如果是LOV 值为'快速编码'时 返回false 其余情况返回true
            record => !((record.get('value') === 'CODE') && (display === 'LOV'))
          }
        />,
      ];
    }
    return null;
  }

  /**
   * 根据‘控件类型’渲染数据来源.
   * @param display 控件类型
   * @returns {*}
   */
  getSourceCodeRender(display) {
    const lovCode = this.parameterConfigDS.current.get('sourceType') === 'CODE' ? 'LOV_CODE' : 'LOV_REPORT_SOURCE';
    this.parameterConfigDS.current.getField('sourceCodeObject').set('lovCode', lovCode);
    if (display === 'LOV' || display === 'comboBox' || display === 'multiSelect') {
      return [
        <Lov
          key="sourceCodeObject"
          name="sourceCodeObject"
          label={$l('parameterconfig.sourcecode')}
          dataSet={this.parameterConfigDS}
          onChange={this.handleChangeSourceCode}
        />,
      ];
    }
    return null;
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
   * 根据‘控件类型’渲染默认类型.
   * @param display 控件类型
   * @returns {*}
   */
  getDefaultTypeRender(display) {
    const sourceCode = this.parameterConfigDS.current.get('sourceCode');
    const defaultType = this.parameterConfigDS.current.get('defaultType');
    // 如果控件有数据源选项、默认类型为常量且未选择数据源  隐藏默认类型
    const hideDefaultType = (display === 'LOV' || display === 'multiSelect' || display === 'comboBox') && defaultType === 'const' && !sourceCode;
    if (hideDefaultType) {
      return null;
    }
    return [
      <Select
        key="defaultType"
        name="defaultType"
        label={$l('parameterconfig.defaulttype')}
        dataSet={this.parameterConfigDS}
        onChange={this.handleChangeDefaultType}
        clearButton={false}
        optionsFilter={
          // 如果不是日期框控件 值为'当前日期'时 返回false 其余情况返回true
          record => !((record.get('value') === 'currentDate') && (display !== 'datePicker'))
        }
      />,
    ];
  }


  /**
   * 根据‘控件类型’渲染默认值.
   * @param display 控件类型
   * @returns {*}
   */
  getDefaultValueRender(display) {
    const defaultType = this.parameterConfigDS.current.get('defaultType');
    const sourceCode = this.parameterConfigDS.current.get('sourceCode');
    // 控件类型为输入框
    if (display === 'textBox') {
      if (defaultType === 'const') {
        return [
          <TextField
            key="defaultValue"
            name="defaultValue"
            label={$l('parameterconfig.defaultvalue')}
            dataSet={this.parameterConfigDS}
          />,
        ];
      }
    }
    // 控件类型为日期框
    if (display === 'datePicker') {
      if (defaultType === 'const') {
        return [
          <DatePicker
            key="defaultValue"
            name="defaultValue"
            label={$l('parameterconfig.defaultvalue')}
            dataSet={this.parameterConfigDS}
            mode="date"
            min={this.setDatePickerToMin()}
            max={this.setDatePickerFromMax()}
          />,
        ];
      } else if (defaultType === 'currentDate') {
        return null;
      }
    }
    // 控件类型为LOV
    if (display === 'LOV') {
      if (defaultType === 'const') {
        if (!sourceCode) {
          return null;
        }
        return [
          <Lov
            key="defaultValue"
            name="defaultValue"
            label={$l('parameterconfig.defaultvalue')}
            dataSet={this.parameterConfigDS}
          />,
        ];
      }
    }
    // 控件类型为多选框
    if (display === 'multiSelect') {
      if (defaultType === 'const') {
        if (!sourceCode) {
          return null;
        }
        return [
          <Select
            key="defaultValue"
            name="defaultValue"
            label={$l('parameterconfig.defaultvalue')}
            dataSet={this.parameterConfigDS}
          />,
        ];
      }
    }
    // 控件类型为下拉框
    if (display === 'comboBox') {
      if (defaultType === 'const') {
        if (!sourceCode) {
          return null;
        }
        return [
          <Select
            key="defaultValue"
            name="defaultValue"
            label={$l('parameterconfig.defaultvalue')}
            dataSet={this.parameterConfigDS}
          />,
        ];
      }
    }
    return [
      <TextArea
        help={$l('report.sqlhelp')}
        showHelp="newLine"
        key="defaultValue"
        name="defaultValue"
        label={$l('parameterconfig.defaultvalue')}
        dataSet={this.parameterConfigDS}
        rows={10}
      />,
    ];
  }

  /**
   * 执行默认值sql.
   */
  checkDefaultValue() {
    const sql = this.parameterConfigDS.current.get('defaultValue') || '';
    if (sql) {
      axios.post('/sys/parameter/config/executeDefaultValue', { defaultValue: sql })
        .then((args) => {
          if (args.success) {
            Modal.info(args.message);
          } else {
            Modal.error(args.message);
          }
        })
        .catch((args) => {
          Modal.error(args.message);
        });
    }
  }

  /**
   * 如果默认类型为SQL,渲染执行sql按钮.
   * @returns {*}
   */
  getCheckDefaultValueRender() {
    const defaultType = this.parameterConfigDS.current.get('defaultType');
    if (defaultType === 'sql') {
      return [
        <Tooltip title={$l('parameterconfig.executesql')}>
          <Button color="blue" icon="test_execute" onClick={() => this.checkDefaultValue()}>
            {$l('parameterconfig.execute')}
          </Button>
        </Tooltip>,
      ];
    }
    return null;
  }

  render() {
    const { display } = this.parameterData;
    return (
      <div>
        <Form columns={1}>
          {this.getDatePickerConfig(display)}
          {this.getSourceTypeRender(display)}
          {this.getSourceCodeRender(display)}
          {this.getCodeValueFieldRender(display)}
          {this.getDefaultTypeRender(display)}
          {this.getDefaultValueRender(display)}
          {this.getCheckDefaultValueRender()}
          {this.getCascadeFromRender(display)}
          {this.getCascadeFromFieldRender(display)}
        </Form>
      </div>
    );
  }
}
