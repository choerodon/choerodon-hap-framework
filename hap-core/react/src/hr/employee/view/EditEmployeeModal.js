import React from 'react';
import { CheckBox, TextField, DatePicker, Select, EmailField, Form } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';
import moment from 'moment';
import { observer } from 'mobx-react';

export default observer(
  ({ isNew, employeeDataSet }) => {
    /**
     * 返回最大时间
     * @param record
     * @returns {moment.Moment} 设置的时间
     */
    function maxStartDate(record) {
      if (record) {
        const time = record.get('effectiveEndDate');
        if (time) {
          return moment(time);
        }
      }
    }

    /**
     * 返回最小时间
     * @returns {moment.Moment} 设置的时间
     */
    function minEndDate(record) {
      if (record) {
        const time = record.get('effectiveStartDate');
        if (time) {
          return moment(time);
        }
      }
    }

    return (
      <Form labelWidth={125} columns={2} style={{ marginRight: 37 }} dataSet={employeeDataSet}>
        <TextField name="employeeCode" required disabled={!isNew} />
        <TextField name="name" />

        <DatePicker name="effectiveStartDate" max={maxStartDate(employeeDataSet.current)} />
        <DatePicker name="bornDate" />

        <DatePicker name="effectiveEndDate" min={minEndDate(employeeDataSet.current)} />

        <DatePicker name="joinDate" />
        <Select name="certificateType" placeholder={$l('hap.tip.pleaseselect')} />

        <Select name="gender" placeholder={$l('hap.tip.pleaseselect')} />
        <TextField step={1} name="certificateId" />

        <Select name="status" placeholder={$l('hap.tip.pleaseselect')} />
        <TextField pattern="1[3-9]\d{9}" name="mobil" />

        <EmailField dataSet={employeeDataSet} name="email" />
        <CheckBox name="enabledFlag" dataSet={employeeDataSet} value="Y" unCheckedValue="N" />
      </Form>
    );
  },
);
