import React, { Component } from 'react';
import { Form, Select, TextArea, TextField } from 'choerodon-ui/pro';

import boundStore from '../stores/BoundStore';

export default class InContentModal extends Component {
  constructor(props) {
    super(props);
    this.dataSet = props.dataSet;
    if (!this.dataSet.current.get('responseContent')) {
      const { current } = this.dataSet;
      const currentInbound = boundStore.queryInbound(current.get('inboundId'));
      currentInbound.then((r) => {
        current.set('userAgent', r.userAgent);
        current.set('referer', r.referer);
        current.set('requestHeaderParameter', r.requestHeaderParameter);
        current.set('requestBodyParameter', r.requestBodyParameter);
        current.set('responseContent', r.responseContent);
        current.set('stackTrace', r.stackTrace);
      });
    }
  }

  render() {
    return (
      <Form dataSet={this.dataSet} columns={2} labelWidth={100} style={{ marginRight: 80 }}>
        <TextField name="referer" disabled />
        <TextField name="userAgent" disabled />
        <TextField name="requestHeaderParameter" colSpan={2} disabled />
        <TextArea name="requestBodyParameter" style={{ height: 150 }} resize="both" disabled />
        <TextArea name="responseContent" style={{ height: 150 }} resize="both" disabled />
        <TextArea name="stackTrace" colSpan={2} style={{ height: 150 }} resize="both" disabled />
      </Form>
    );
  }
}
