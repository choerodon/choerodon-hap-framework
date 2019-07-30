import React, { PureComponent } from 'react';
import { DataSet, Lov } from 'choerodon-ui/pro';

export default class LovPreview extends PureComponent {
  previewDataSet;

  constructor(props) {
    super(props);
    const { textField, code } = this.props.record.data;
    this.previewDataSet = new DataSet({
      fields: [
        { name: 'lov', type: 'object', textField, lovCode: code },
      ],
    });
  }

  render() {
    return (
      <div>
        <Lov dataSet={this.previewDataSet} style={{ width: 500 }} name="lov" placeholder={this.props.record.get('description')} />
      </div>
    );
  }
}
