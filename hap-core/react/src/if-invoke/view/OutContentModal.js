import React, { Component } from 'react';
import { Form, Select, TextArea, TextField } from 'choerodon-ui/pro';

import boundStore from '../stores/BoundStore';

export default class OutContentModal extends Component {
  constructor(props) {
    super(props);
    this.dataSet = props.dataSet;
    if (!this.dataSet.current.get('responseContent')) {
      const { current } = this.dataSet;
      const currentInbound = boundStore.queryOutbound(current.get('outboundId'));
      currentInbound.then((r) => {
        current.set('requestParameter', r.requestParameter);
        current.set('responseContent', r.responseContent);
        current.set('stackTrace', r.stackTrace);
        current.status = 'sync';
      });
    }
  }

  render() {
    return (
      <Form dataSet={this.dataSet} columns={2} labelWidth={100} style={{ marginRight: 80 }}>
        <TextArea name="requestParameter" style={{ height: 150 }} resize="both" disabled />
        <TextArea name="responseContent" style={{ height: 150 }} resize="both" disabled />
        <TextArea name="stackTrace" colSpan={2} style={{ height: 150 }} resize="both" disabled />
      </Form>
    );
  }
}
