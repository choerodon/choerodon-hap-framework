import React, { PureComponent } from 'react';
import { observer } from 'mobx-react';
import { observable } from 'mobx';
import { CheckBox, Form, IntlField, NumberField, Select, SelectBox, Table, TextField } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';

const { Column } = Table;
const { Option } = Select;

@observer
export default class CategoryEditModal extends PureComponent {
  constructor(props) {
    super(props);
    this.dataset = this.props.dataset;
    this.isEdit = this.props.isEdit;
    this.state = {
      isFolder: this.props.isFolder,
    };
    this.setRequired(!this.state.isFolder);
  }

  setRequired(isRequired) {
    this.dataset.getField('sourceType').set('required', isRequired);
    this.dataset.getField('categoryPath').set('required', isRequired);
    this.dataset.getField('allowedFileType').set('required', isRequired);
    this.dataset.getField('allowedFileSize').set('required', isRequired);
    this.dataset.current.isEnabled = isRequired;
  }

  handleChange = (value, oldValue) => {
    this.setState({
      isFolder: value === '0',
    });
    const isRequired = value === '1';
    this.dataset.current.isEnabled = isRequired;
    this.setRequired(isRequired);
  };

  render() {
    const { isFolder } = this.state;
    return (
      <div>
        <Form dataSet={this.dataset} style={{ width: '5rem' }} labelWidth={150}>
          <SelectBox name="leafFlag" disabled={this.isEdit} onChange={this.handleChange}>
            <Option value="0">{$l('attachcategory.categorytype.folder')} </Option>
            <Option value="1">{$l('attachcategory.categorytype.filefolder')}</Option>
          </SelectBox>
          <IntlField name="categoryName" help={$l('attachcategory.categoryname.help')} />
          <IntlField name="description" />
          <TextField name="sourceType" disabled={isFolder} help={$l('attachcategory.sourcetype.help')} />
          <TextField name="categoryPath" disabled={isFolder} help={$l('attachcategory.categorypath.help')} />
          <TextField name="allowedFileType" disabled={isFolder} help={$l('attachcategory.allowedfiletype.help')} />
          <NumberField name="allowedFileSize" disabled={isFolder} step={1} help={$l('attachcategory.allowedfilesize.help')} />
          <CheckBox name="isUnique" disabled={isFolder} />
        </Form>
      </div>
    );
  }
}
