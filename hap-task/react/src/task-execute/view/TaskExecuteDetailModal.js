import React, { PureComponent } from 'react';
import { observer } from 'mobx-react';
import { $l, axios } from '@choerodon/boot';
import { Button, DataSet, DatePicker, Form, Lov, message, Modal, Select, Spin, Stores, TextField } from 'choerodon-ui/pro';
import _ from 'lodash';
import moment from 'moment';
import TaskDetailModal from '../../execution/view/TaskDetailModal';
import GroupModal from '../../execution/view/GroupModal';
import ExecutionDataSet from '../../execution/stores/ExecutionDataSet';
import ParameterDataSet from '../../execution/stores/ParameterDataSet';
import TaskGroupDataSet from '../../execution/stores/TaskGroupDataSet';

const modalKey = Modal.key();

@observer
export default class TaskExecuteDetailModal extends PureComponent {
  getLovConfig({ sourceCode, tableFieldName }) {
    return new Promise((resolve) => {
      Stores.LovCodeStore.fetchConfig(sourceCode)
        .then(config => resolve([tableFieldName, config]));
    });
  }

  getSelectDs({ sourceCode, sourceType, tableFieldName }) {
    const option = new DataSet({
      selection: 'single',
      queryUrl: `/sys/parameter/config/${this.getDynamicDataSourceUrl(sourceType)}?sourceCode=${sourceCode}`,
      paging: false,
    });
    return new Promise((resolve) => {
      option.query().then(() => resolve([tableFieldName, option]));
    });
  }

  async getConfig(record) {
    const configs = await Promise.all(record.get('parameterConfigs')
      .filter(({ display }) => display === 'LOV')
      .map(data => this.getLovConfig(data)));
    return configs.reduce((obj, [tableFieldName, config]) => {
      obj[tableFieldName] = config;
      return obj;
    }, {});
  }

  async getConfigAll() {
    const configs = await Promise.all(
      this.childrenTasks.data.flatMap(record => record.get('parameterConfigs')
        .filter(({ display }) => display === 'LOV')
        .map(data => this.getLovConfig(data))),
    );
    return configs.reduce((obj, [tableFieldName, config]) => {
      obj[tableFieldName] = config;
      return obj;
    }, {});
  }

  async getLovdefaultValue() {
    if (this.current) {
      if (this.current.get('type') === 'GROUP') {
        this.configs = await this.getConfigAll();
        // this.options = await this.getOptionDsAll();
        this.childrenTasks.data.forEach((record) => {
          record.get('parameterConfigs').forEach((data) => {
            this.getDataset(data, this.configs);
          });
        });
      } else {
        this.configs = await this.getConfig(this.current);
        // this.options = await this.getOptionDs(this.current);
        this.current.get('parameterConfigs').forEach((data) => {
          this.getDataset(data, this.configs);
        });
      }
      this.setState({
        pending: false,
      });
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

  getDataset(data, configs) {
    const { display } = data;
    let defaultValue = data.defaultValue || '';
    const defaultText = data.defaultText || '';
    const { tableFieldName } = data;
    const { sourceCode } = data;
    const { title } = data;
    const { sourceType } = data;
    const extraAttribute = this.getExtraAttribute(data.extraAttribute);
    const { cascadeFrom, cascadeFromField, codeValueField } = extraAttribute;

    const required = data.required === 'Y';
    if (display === 'multiSelect') {
      defaultValue = this.isNotEmpty(defaultValue) ? defaultValue : null;
      const field = {
        name: tableFieldName,
        label: title,
        defaultValue,
        required,
        multiple: ',',
      };
      if (sourceType === 'LOV') {
        field.lovCode = sourceCode;
      } else if (sourceType === 'CODE') {
        field.lookupCode = sourceCode;
        field.textField = 'meaning';
        field.valueField = codeValueField;
      }
      this.ds.addField(tableFieldName, field);
      this.ds.create();
    } else if (display === 'comboBox') {
      const field = {
        name: tableFieldName,
        label: title,
        defaultValue,
        required,
      };
      if (sourceType === 'LOV') {
        field.lovCode = sourceCode;
      } else if (sourceType === 'CODE') {
        field.lookupCode = sourceCode;
        field.textField = 'meaning';
        field.valueField = codeValueField;
      }
      if (cascadeFrom && cascadeFromField) {
        const cascadeObj = {};
        cascadeObj[cascadeFromField] = cascadeFrom;
        field.cascadeMap = cascadeObj;
      }
      this.ds.addField(tableFieldName, field);
      this.ds.create();
    } else if (display === 'textBox') {
      this.ds.addField(tableFieldName,
        {
          name: tableFieldName,
          label: title,
          type: 'string',
          defaultValue,
          required,
        });
      this.ds.create();
    } else if (display === 'LOV') {
      const config = configs[tableFieldName];
      const defaultValueObj = {};
      if (this.isNotEmpty(config)) {
        defaultValueObj[config.textField] = defaultText;
        defaultValueObj[config.valueField] = defaultValue;
      }
      this.ds.addField(tableFieldName,
        {
          name: tableFieldName,
          label: title,
          type: 'object',
          required,
          lovCode: sourceCode,
          textField: config.textField,
          defaultValue: defaultValueObj,
        });
      this.ds.create();
    } else if (display === 'datePicker') {
      this.ds.addField(tableFieldName, {
        name: tableFieldName,
        label: title,
        type: 'string',
        defaultValue,
        required,
      });
      this.ds.create();
    }
  }

  constructor(props) {
    super(props);
    this.ds = new DataSet();
    this.current = this.props.dataset.current;
    this.childrenTasks = this.props.childrenTasks;
    this.optionList = {};
    this.state = {
      pending: true,
    };
    this.configs = {};
    this.getLovdefaultValue();
    this.modal = null;
  }

  taskGroupDS = new DataSet(TaskGroupDataSet);

  executionDS = new DataSet(ExecutionDataSet);

  parameterDS = new DataSet(ParameterDataSet);

  /**
   * 表单头部渲染
   * @returns {*}
   */
  createTitle(record) {
    let group = '';
    if (record) {
      if (record.get('type') === 'GROUP') {
        group = record.get('type');
      }
      return (
        <div>
          <span style={{ color: '#efa90d', fontSize: 16, marginRight: 20 }}>{group}</span>
          <span style={{ fontSize: 14, fontWeight: 'bold', marginRight: 20 }}>{record.get('name')}</span>
          <span style={{ fontSize: 12, color: '#808080' }}>{record.get('code')}</span>
        </div>
      );
    }
  }

  /**
   * 非空判定
   * @param obj
   * @returns {boolean}
   */
  isNotEmpty(obj) {
    return obj !== undefined && obj !== null && obj !== '';
  }

  translateDate(date) {
    if (this.isNotEmpty(date)) {
      const temp = date.split(' ');
      return temp[0];
    } else {
      return '';
    }
  }


  getDynamicDataSourceUrl(sourceType) {
    if (sourceType === 'LOV') {
      return 'getLov';
    } else if (sourceType === 'CODE') {
      return 'getCode';
    }
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
   * 按列渲染控件
   * @param row
   * @returns {*}
   */
  renderCmp(data) {
    const { display } = data;
    const extraAttribute = this.getExtraAttribute(data.extraAttribute);
    const datePickerFrom = extraAttribute.datePickerFrom || '';
    const datePickerTo = extraAttribute.datePickerTo || '';
    const { tableFieldName } = data;

    if (display === 'multiSelect') {
      return (
        <Select
          multiple
          key={tableFieldName}
          name={tableFieldName}
          disabled={data.readOnly === 'Y'}
        />
      );
    } else if (display === 'comboBox') {
      return (
        <Select
          key={tableFieldName}
          name={tableFieldName}
          disabled={data.readOnly === 'Y'}
        />
      );
    } else if (display === 'textBox') {
      return (
        <TextField
          key={tableFieldName}
          name={tableFieldName}
          maxLength={data.dataLength}
          disabled={data.readOnly === 'Y'}
        />
      );
    } else if (display === 'LOV') {
      return (
        <Lov
          key={tableFieldName}
          name={tableFieldName}
          disabled={data.readOnly === 'Y'}
        />
      );
    } else if (display === 'datePicker') {
      return (
        <DatePicker
          key={tableFieldName}
          name={tableFieldName}
          min={this.setDatePickerToMin(datePickerFrom)}
          max={this.setDatePickerFromMax(datePickerTo)}
          disabled={data.readOnly === 'Y'}
        />
      );
    }
  }

  /**
   * 按行排序渲染控件
   * @param row
   * @returns {*}
   */
  renderRow(row) {
    return (
      <Form dataSet={this.ds}>
        {
          row.map(r => this.renderCmp(r))
        }
      </Form>
    );
  }

  /**
   * 控件行列排序
   *
   * @param configArr
   */
  sortConfig(configArr) {
    const sortedArr = _.sortBy(configArr, ['lineNumber', 'columnNumber']);
    const rowArr = _.uniq(sortedArr.map(r => r.lineNumber));
    return (
      <div>
        {
          rowArr.map(row => this.renderRow(sortedArr.filter(r => r.lineNumber === row)))
        }
      </div>
    );
  }

  createForm() {
    const domArr = [];
    this.childrenTasks.data.forEach((record) => {
      domArr.push(this.createTitle(record));
      domArr.push(this.sortConfig(record.get('parameterConfigs')));
    });
    if (this.current) {
      return (
        <div>
          {this.createTitle(this.current)}
          {this.sortConfig(this.current.get('parameterConfigs'))}
          {domArr}
        </div>
      );
    }
  }

  TextFieldDS = new DataSet({
    autoCreate: true,
    fields: [
      { name: 'description', type: 'string' },
    ],
  });

  getTextFiled(optionDs, valueFiled) {
    if (this.isNotEmpty(valueFiled)) {
      const record = optionDs.find(r => r.get('valueField') === valueFiled);
      if (record) {
        return record.get('textField');
      }
      return null;
    }
  }

  getTextFiledMul(data, valueFiled) {
    if (this.isNotEmpty(valueFiled)) {
      const valueFiledArr = valueFiled.slice();
      const resArr = valueFiledArr.map(r => this.getTextFiled(data, r));
      return resArr.filter(v => v !== null).join(',');
    }
  }

  getTaskParam(task) {
    const resultData = [];
    task.get('parameterConfigs').forEach((param) => {
      const config = {};
      config.key = param.tableFieldName;
      config.name = param.title;
      const { display } = param;
      const { tableFieldName } = param;
      const value = this.ds.current.get(tableFieldName);
      config.value = value;
      if (display === 'datePicker' || display === 'textBox') {
        config.text = value;
      } else if (display === 'LOV') {
        const data = this.configs[tableFieldName];
        config.text = value[data.textField];
        config.value = value[data.valueField];
      } else if (display === 'comboBox') {
        config.text = this.ds.getField(tableFieldName).getText(value);
      } else if (display === 'multiSelect') {
        config.text = value.map(r => this.ds.getField(tableFieldName).getText(r)).join(',');
        config.value = this.isNotEmpty(value) ? value.join(',') : '';
      }
      resultData.push(config);
    });
    return resultData;
  }

  setData() {
    const obj = {};
    obj.taskId = this.current.get('taskId');
    obj.description = this.TextFieldDS.current.get('description');
    obj.taskClass = this.current.get('taskClass');
    obj.type = this.current.get('type');
    if (this.current.get('type') === 'GROUP') {
      const taskDatas = [];
      this.childrenTasks.data.map((task) => {
        const taskData = {};
        taskData.taskId = task.get('taskId');
        taskData.taskClass = task.get('taskClass');
        taskData.param = this.getTaskParam(task);
        taskDatas.push(taskData);
        return null;
      });
      obj.taskDatas = taskDatas;
    } else {
      obj.param = this.getTaskParam(this.current);
    }
    return JSON.stringify(obj);
  }

  flag = 0;

  setDataSetData(data) {
    this.parameterDS.loadData(data);
  }

  closeModal() {
    this.modal.close();
  }

  openTaskEditModal = async (executionId) => {
    const { taskExecutionDetail } = (await axios.post('/sys/task/execution/detail', { executionId })).rows[0];
    await this.executionDS.query();
    const { executionLog } = taskExecutionDetail;
    const { stacktrace } = taskExecutionDetail;
    this.setDataSetData(JSON.parse(taskExecutionDetail.parameter));
    this.modal = Modal.open({
      key: modalKey,
      drawer: true,
      destroyOnClose: true,
      children: (
        <TaskDetailModal executionDS={this.executionDS} parameterDS={this.parameterDS} executionLog={executionLog} stacktrace={stacktrace} />
      ),
      footer: [
        <Button color="blue" icon="playlist_add" href={`/sys/task/execution/detailDownload?executionId=${executionId}`} download>
          {$l('task.execution.download')}
        </Button>,
        <Button style={{ marginLeft: 10 }} onClick={() => this.closeModal()}>
          {$l('hap.close')}
        </Button>,
      ],
      style: {
        width: 850,
      },
    });
  };

  openGroupModal = async (executionId, executionNumber) => {
    this.taskGroupDS.queryParameter.executionId = executionId;
    this.taskGroupDS.query();
    await this.executionDS.query();
    Modal.open({
      key: modalKey,
      title: $l('task.execution.detail'),
      drawer: true,
      destroyOnClose: true,
      children: (
        <GroupModal executionDS={this.executionDS} taskGroupDS={this.taskGroupDS} parameterDS={this.parameterDS} />
      ),
      okCancel: false,
      okText: $l('hap.close'),
      style: {
        width: 850,
      },
    });
  };

  executeTask() {
    this.props.modal.close();
    axios.post('/sys/task/detail/execute', this.setData(), {
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
      },
    })
      .then((args) => {
        if (args.success === false) {
          message.error(args.message.substring(0, 40));
          return;
        }
        Modal.confirm({
          title: $l('hap.prompt'),
          children: (
            <div>
              <p>{$l('task.execution.executionnumber')}:{args.rows[0]}</p>
            </div>
          ),
          okText: $l('task.execution.show'),
          cancelText: $l('task.execution.continue'),
        }).then((button) => {
          if (button === 'ok') {
            if (this.current.get('type') === 'GROUP') {
              this.openGroupModal(args.rows[1], args.rows[0]);
            } else {
              this.openTaskEditModal(args.rows[1]);
            }
          }
        });
      });
  }

  render() {
    return this.state.pending ? <Spin /> : (
      <div>
        {this.createForm()}
        <Form columns={3} dataSet={this.TextFieldDS} className="task-taskExecute-detailModal">
          <TextField placeholder={$l('task.execution.description')} colSpan={2} name="description" />
          <div colSpan={1}>
            <Button color="blue" onClick={() => this.executeTask()}>{$l('task.execute')}</Button>
            <Button funcType="flat" color="blue" style={{ marginLeft: 8 }} onClick={() => this.props.modal.close()}>{$l('hap.cancel')}</Button>
          </div>
        </Form>
      </div>
    );
  }
}
