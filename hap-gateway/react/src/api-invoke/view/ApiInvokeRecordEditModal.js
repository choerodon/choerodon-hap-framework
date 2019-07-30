import React from 'react';
import { Form, Output, Tabs, TextArea } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';
import '../index.scss';

export default ({ dataset }) => (
  <Tabs>
    <Tabs.TabPane tab={$l('api.invokerecord.record')} key="1">
      <Form dataSet={dataset} style={{ width: '8rem' }} columns={2}>
        <Output name="invokeId" disabled />
        <Output name="applicationCode" disabled />
        <Output name="serverCode" disabled />
        <Output name="serverName" disabled />
        <Output name="clientId" disabled />
        <Output name="requestTime" disabled />
        <Output name="ip" disabled />
        <Output name="requestMethod" disabled />
        <Output name="apiResponseTime" disabled />
        <Output name="responseTime" disabled />
        <Output name="interfaceType" disabled />
        <Output name="responseStatus" disabled />
        <Output name="apiUrl" disabled colSpan={2} />
        <Output name="requestUrl" disabled colSpan={2} />
        <Output name="userAgent" disabled colSpan={2} />
        <Output name="referer" disabled colSpan={2} />
      </Form>
    </Tabs.TabPane>
    <Tabs.TabPane tab={$l('api.invokerecord.details')} key="2">
      <Form dataSet={dataset} style={{ width: '8rem' }} columns={2} labelLayout="vertical">
        <TextArea name="apiRequestBodyParameter" rows={5} disabled resize="both" />
        <TextArea name="requestBodyParameter" rows={5} disabled resize="both" />
        <TextArea name="apiResponseContent" rows={5} disabled resize="both" />
        <TextArea name="responseContent" rows={5} disabled resize="both" />
      </Form>
    </Tabs.TabPane>
    <Tabs.TabPane tab={$l('api.invokerecord.stacktrace')} key="3">
      <Form dataSet={dataset} style={{ width: '8rem' }} labelLayout="placeholder">
        <TextArea name="stacktrace" rows={20} label={$l('api.invokerecord.info')} className="apil-area" resize="both" />
      </Form>
    </Tabs.TabPane>
  </Tabs>
);
