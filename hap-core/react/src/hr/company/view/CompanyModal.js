import React, { PureComponent } from 'react';
import moment from 'moment';
import { observer } from 'mobx-react';
import { DatePicker, Form, IntlField, Lov, Select, TextField } from 'choerodon-ui/pro';


@observer
export default class CompanyModal extends PureComponent {
  constructor(props) {
    super(props);
    this.dataSet = this.props.dataSet;
    this.isNew = this.props.isNew;
  }

  setStartDateActiveMax() {
    const { current } = this.dataSet;
    if (current) {
      const time = current.get('endDateActive');
      if (time) {
        return moment(time);
      }
    }
  }

  setEndDateActiveMin() {
    const { current } = this.dataSet;
    if (current) {
      const time = current.get('startDateActive');
      if (time) {
        return moment(time);
      }
    }
  }

  render() {
    return (
      <Form dataSet={this.dataSet} columns={2} labelWidth={100} style={{ marginRight: 80 }}>
        <TextField name="companyCode" required disabled={!this.isNew} />
        <IntlField name="companyShortName" clearButton required />
        <IntlField name="companyFullName" colSpan={2} clearButton required />
        <Select name="companyType" />
        <Select name="companyLevelId" />
        <Lov name="company" />
        <Lov name="position" />
        <DatePicker name="startDateActive" viewMode="date" max={this.setStartDateActiveMax()} />
        <DatePicker step={1} name="endDateActive" viewMode="date" min={this.setEndDateActiveMin()} />
        <TextField name="zipcode" />
        <TextField name="fax" />
        <TextField name="phone" />
        <TextField name="contactPerson" />
        <TextField name="address" colSpan={2} />
      </Form>
    );
  }
}
