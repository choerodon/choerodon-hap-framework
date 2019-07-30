import React, { PureComponent } from 'react';
import { DataSet, Table, TextField } from 'choerodon-ui/pro';
import { ContentPro as Content } from '@choerodon/boot';
import LanguageDataSet from './stores/LanguageDataSet';

const { Column } = Table;

export default class Index extends PureComponent {
  languageDS = new DataSet(LanguageDataSet);

  /**
   * 渲染表格内容
   */
  render() {
    return (
      <Content>
        <Table
          buttons={['add', 'save', 'delete']}
          dataSet={this.languageDS}
          queryFieldsLimit={2}
        >
          <Column name="langCode" editor />
          <Column name="description" editor />
        </Table>
      </Content>
    );
  }
}
