import React, { PureComponent } from 'react';
import { CheckBox, Form, Icon, Select, TextField, Tooltip } from 'choerodon-ui/pro';
import '../index.scss';


export default class ApiInterfaceModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      soapVersion: props.interfaceDataSet.current.get('soapVersion'),
    };
  }

  prefixIcon = (
    <Tooltip title={this.props.baseUrl}>
      <Icon type="link" />
    </Tooltip>
  );

  handleChangeSoapVersion = (value) => {
    this.setState({
      soapVersion: value,
    });
  };

  getSoapActionColumn() {
    if (this.state.soapVersion === 'SOAP11') {
      return <TextField name="soapAction" label="soap action" />;
    } else {
      return null;
    }
  }

  getFormColumn() {
    const { serviceType } = this.props;
    if (serviceType === 'REST') {
      return [
        <TextField name="mappingClass" />,
        <Select name="requestMethod" clearButton={false} />,
        <Select name="requestHead" />,
      ];
    } else if (serviceType === 'SOAP') {
      return [
        <Select name="soapVersion" onChange={this.handleChangeSoapVersion} clearButton={false} />,
        this.getSoapActionColumn(),
      ];
    }
  }

  render() {
    return (
      <Form labelWidth={100} columns={2} dataSet={this.props.interfaceDataSet}>
        <TextField required name="code" />
        <TextField required name="name" />

        <TextField required name="interfaceUrl" />
        <TextField required name="mappingUrl" addonBefore={this.prefixIcon} />

        {this.getFormColumn().filter(v => v)}

        <CheckBox name="enableFlag" value="Y" unCheckedValue="N" />
        <CheckBox name="invokeRecordDetails" value="Y" unCheckedValue="N" />
      </Form>
    );
  }
}
