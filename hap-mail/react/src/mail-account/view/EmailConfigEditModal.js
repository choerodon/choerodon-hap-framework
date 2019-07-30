import React from 'react';
import { Password, Table, Tabs } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';

const { Column } = Table;
const { TabPane } = Tabs;
const password = <Password />;

export default ({ emailAccountDS, propertyListsDS, emailWhiteList }) => {
  /**
   * 渲染EmailAccount表格内容
   */
  function renderAccountTable() {
    return (
      <Table
        buttons={['add', 'delete']}
        dataSet={emailAccountDS}
      >
        <Column name="accountCode" editor />
        <Column name="userName" editor />
        <Column name="password"
          editor={password}
          renderer={({ record }) => {
            if (record.get('password') != null) {
              return '******';
            } else {
              return null;
            }
          }}
        />
      </Table>
    );
  }

  /**
   * 渲染EmailWhiteList表格内容
   */
  function renderWhiteListTable() {
    return (
      <Table
        buttons={['add', 'delete']}
        dataSet={emailWhiteList}
      >
        <Column name="address" editor />
      </Table>
    );
  }

  /**
   * 渲染EmailProperty表格内容
   */
  function renderPropertyTable() {
    return (
      <Table
        buttons={['add', 'delete']}
        dataSet={propertyListsDS}
      >
        <Column name="propertyName" editor />
        <Column name="propertyCode" editor />
      </Table>
    );
  }


  return (
    <Tabs defaultActiveKey="1">
      <TabPane tab={$l('messageemailconfig.account')} key="1"> {renderAccountTable()} </TabPane>
      <TabPane tab={$l('messageemailconfig.whitelist')} key="2"> {renderWhiteListTable()}</TabPane>
      <TabPane tab={$l('messageemailconfig.serverproperty')} key="3">{renderPropertyTable()}</TabPane>
    </Tabs>
  );
};
