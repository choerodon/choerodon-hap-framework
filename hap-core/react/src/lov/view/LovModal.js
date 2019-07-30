import React, { PureComponent } from 'react';
import { Button, CheckBox, Form, Modal, NumberField, Select, Table, TextArea, TextField, Tooltip } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';
import QueryConfigurationModal from './QueryConfigurationModal';
import '../index.scss';

const { Column } = Table;
const key = Modal.key();
const numberField = <NumberField step={1} min={0} />;

export default class LovModal extends PureComponent {
  constructor(props) {
    super(props);
    let sqlType = 'SQL_ID';
    if (props.lovDataSet.current.get('customSql')) {
      sqlType = 'CUSTOM_SQL';
    } else if (props.lovDataSet.current.get('customUrl')) {
      sqlType = 'URL';
    }
    props.lovDataSet.current.set('sqlType', sqlType);
    this.state = {
      sqlType,
      treeFlag: this.props.lovDataSet.current.get('treeFlag'),
    };
  }

  changeSQLType = (value) => {
    this.setState({
      sqlType: value,
    });
  };

  changeTreeFlag = (value) => {
    this.setState({
      treeFlag: value,
    });
  };

  openQueryConfigurationModal() {
    Modal.open({
      key,
      drawer: true,
      destroyOnClose: true,
      children: (
        <QueryConfigurationModal dataSet={this.props.lovItemDataSet} />
      ),
      okText: $l('hap.save'),
      style: {
        width: 600,
      },
    });
  }

  getFieldBySQLType() {
    switch (this.state.sqlType) {
      case 'SQL_ID':
        return (<TextField rowSpan={3} name="sqlId" colSpan={2} />);
      case 'CUSTOM_SQL':
        return (<TextArea rowSpan={3} name="customSql" colSpan={2} />);
      case 'URL':
        return (<TextField rowSpan={3} name="customUrl" colSpan={2} />);
      default:
        return null;
    }
  }

  getFieldByTreeFlag() {
    if (this.state.treeFlag === 'Y') {
      return [
        <TextField name="idField" colSpan={2} />,
        <TextField name="parentIdField" colSpan={2} />,
      ];
    }
    return null;
  }

  render() {
    return (
      <div>
        <Form labelWidth={125} columns={6} style={{ marginRight: 75 }} dataSet={this.props.lovDataSet}>
          <TextField name="code" disabled={!this.props.isAdd} colSpan={2} />
          <TextField name="valueField" colSpan={2} />
          <Select name="lovPageSize" defaultValue="20" colSpan={2} />

          <TextField name="description" colSpan={2} />
          <TextField name="textField" colSpan={2} />
          <Select name="sqlType" onChange={this.changeSQLType} colSpan={2} clearButton={false} />

          <TextField name="title" colSpan={2} />
          <NumberField name="height" step={1} min={0} colSpan={2} />

          {this.getFieldBySQLType()}

          <TextField name="placeholder" colSpan={2} />
          <NumberField name="width" step={1} min={0} colSpan={2} />

          <CheckBox name="editableFlag" value="Y" unCheckedValue="N" />
          <CheckBox name="treeFlag" value="Y" unCheckedValue="N" onChange={this.changeTreeFlag} />
          <NumberField name="queryColumns" step={1} min={0} colSpan={2} />

          {this.getFieldByTreeFlag()}
        </Form>
        <div className="core-lov-wrap">
          <div className="core-lov-label">{$l('lov.linenumberinformation')}</div>
          <div className="core-lov-table">
            <Table buttons={['add', 'delete']} dataSet={this.props.lovItemDataSet}>
              <Column name="display" minWidth={125} editor />
              <Column name="gridFieldName" minWidth={75} editor />
              <Column name="gridFieldWidth" minWidth={75} editor={numberField} />
              <Column name="gridField" minWidth={75} editor />
              <Column name="gridFieldAlign" minWidth={75} editor />
              <Column name="conditionField" minWidth={75} editor />
              <Column name="gridFieldSequence" minWidth={75} editor={numberField} />
              <Column
                align="center"
                renderer={({ record }) => {
                  if (record.get('conditionField') === 'Y') {
                    return (
                      <Tooltip title={$l('lovitem.type')}>
                        <Button funcType="flat" color="blue" icon="more_vert" onClick={() => this.openQueryConfigurationModal()} />
                      </Tooltip>
                    );
                  }
                }}
                header={$l('lovitem.type')}
                width={100}
              />
            </Table>
          </div>
        </div>
      </div>
    );
  }
}
