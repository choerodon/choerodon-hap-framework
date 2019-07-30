import React, { Component } from 'react';
import { $l } from '@choerodon/boot';
import { Form, Output, Table, Tabs, TextField } from 'choerodon-ui/pro';

const { Column } = Table;

export default (props) => {
  let executionId;
  if (props.executionDS.current) {
    executionId = props.executionDS.current.get('executionId');
  }


  return (
    <div>
      <Tabs>
        <Tabs.TabPane tab={$l('task.execution.detail')} key="1">
          <Form columns={2} labelWidth={100} style={{ marginRight: 80 }} dataSet={props.executionDS}>
            <Output name="executionNumber" />
            <Output name="status" />
            <Output name="code" />
            <Output name="name" />
            <Output name="taskClass" />
            <Output name="description" />
            <Output name="startTime" />
            <Output name="endTime" />
            <Output name="userId" />
            <Output name="executionDescription" />
          </Form>
        </Tabs.TabPane>
        <Tabs.TabPane tab={$l('task.execute.parameter')} key="2">
          <Table
            dataSet={props.parameterDS}
          >
            <Column name="name" />
            <Column name="value" />
            <Column name="text" />
          </Table>
        </Tabs.TabPane>
        <Tabs.TabPane tab={$l('api.invokerecord.stacktrace')} key="3">
          {props.stacktrace}
        </Tabs.TabPane>
        <Tabs.TabPane tab={$l('task.execute.executionlog')} key="4">
          {props.executionLog}
        </Tabs.TabPane>
      </Tabs>
    </div>
  );
};
