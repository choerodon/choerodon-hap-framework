import React, { PureComponent } from 'react';
import { Form, Lov, NumberField, Select, TextField } from 'choerodon-ui/pro';

export default class QueryConfigurationModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: this.props.dataSet.current.get('dataSourceType'),
      FieldType: this.props.dataSet.current.get('conditionFieldType'),
    };
  }

  handleChangeFieldType = (value) => {
    this.setState({
      FieldType: value,
    });
  };

  handleChangeDataSource = (value) => {
    this.setState({
      dataSource: value,
    });
  };

  renderSelect() {
    if (this.state.FieldType === 'SELECT') {
      return [
        <Select name="dataSourceType" onChange={this.handleChangeDataSource} />,
        this.renderDataSource(),
      ];
    } else if (this.state.FieldType === 'POPUP') {
      return (<Lov name="lovList" />);
    }
  }

  renderDataSource() {
    if (this.state.dataSource === 'URL') {
      return [
        <TextField name="conditionFieldSelectUrl" />,
        <TextField name="conditionFieldSelectVf" />,
        <TextField name="conditionFieldSelectTf" />,
      ];
    }
    if (this.state.dataSource === 'CODE') {
      return <Lov name="lovCode" />;
    }
  }

  renderHead() {
    return (
      <Form columns={1} dataSet={this.props.dataSet}>
        <Select name="conditionFieldType" onChange={this.handleChangeFieldType} defaultValue="INT" />

        <NumberField name="conditionFieldLabelWidth" step={1} min={0} />

        <NumberField name="conditionFieldWidth" step={1} min={0} defaultValue={200} />

        <NumberField name="conditionFieldSequence" step={1} min={0} />

        <TextField name="conditionFieldName" />

        {this.renderSelect()}

      </Form>
    );
  }

  render() {
    return (
      <Form columns={1} dataSet={this.props.dataSet}>
        <Select name="conditionFieldType" onChange={this.handleChangeFieldType} defaultValue="INT" />

        <NumberField name="conditionFieldLabelWidth" step={1} min={0} />

        <NumberField name="conditionFieldWidth" step={1} min={0} defaultValue={200} />

        <NumberField name="conditionFieldSequence" step={1} min={0} />

        <TextField name="conditionFieldName" />

        {this.renderSelect()}

      </Form>
    );
  }
}
