import React, { Component } from 'react';
import get from 'lodash/get';
import { findDOMNode } from 'react-dom';
import { inject, observer } from 'mobx-react';
import { Button, Form, Modal, Progress, Select, Table, Tooltip, Upload, Spin, Radio } from 'choerodon-ui';
import { withRouter } from 'react-router-dom';
import { ContentPro as Content, Header, Page, Permission } from '@choerodon/boot';
import { FormattedMessage, injectIntl } from 'react-intl';
import classnames from 'classnames';
import MemberRoleType, { pageSize } from './MemberRoleType';
import MemberRoleStore from './stores';
import './MemberRole.scss';
import '../../common/ConfirmModal.scss';

let timer;
const { Sidebar } = Modal;
const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
const FormItemNumLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 100 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 10 },
  },
};
const intlPrefix = 'memberrole';

@Form.create({})
@withRouter
@injectIntl
@inject('AppState')
@observer
export default class MemberRole extends Component {
  renderUserTable = () => {
    const { selectMemberRoles, roleMemberDatas, memberRolePageInfo, memberDatas, memberRoleFilters, loading } = this.state;
    const filtersRole = [...new Set(roleMemberDatas.map(({ name }) => (name)))].map(value => ({ value, text: value }));
    const columns = [
      {
        title: <FormattedMessage id="memberrole.loginname" />,
        dataIndex: 'loginName',
        key: 'loginName',
        width: '15%',
        filters: [],
        filteredValue: memberRoleFilters.loginName || [],
        render: this.renderSimpleColumn,
      },
      {
        title: <FormattedMessage id="memberrole.realname" />,
        dataIndex: 'realName',
        key: 'realName',
        width: '15%',
        filters: [],
        filteredValue: memberRoleFilters.realName || [],
        render: this.renderSimpleColumn,
      },
      {
        title: <FormattedMessage id="memberrole.role" />,
        dataIndex: 'roles',
        key: 'roles',
        filters: filtersRole,
        filteredValue: memberRoleFilters.roles || [],
        className: 'memberrole-roles',
        width: '50%',
        render: this.renderRoleColumn,
      },
      {
        title: '',
        width: '20%',
        align: 'right',
        render: this.renderActionColumn,
      },
    ];
    const rowSelection = {
      selectedRowKeys: Object.keys(selectMemberRoles).map(key => Number(key)),
      onChange: (selectedRowkeys, selectedRecords) => {
        this.setState({
          selectMemberRoles: selectedRowkeys.reduce((data, key, index) => {
            data[key] = selectedRecords[index].roles.map(({ id }) => id);
            return data;
          }, {}),
        });
      },
    };
    return (
      <Table
        key="member-role"
        className="member-role-table"
        loading={loading}
        rowSelection={rowSelection}
        pagination={memberRolePageInfo}
        columns={columns}
        filters={this.state.params}
        onChange={this.memberRoleTableChange}
        dataSource={memberDatas}
        filterBarPlaceholder={this.formatMessage('filtertable')}
        rowKey={({ id }) => id}
        noFilter
      />
    );
  }

  renderRoleTable = () => {
    
  }

  renderTable = () => {
    const { type } = MemberRoleStore;
    if (type === 'user') {
      return this.renderUserTable();
    } else {
      return this.renderRoleTable();
    }
  }

  render() {
    return (
      <Page>
        <Header title={<FormattedMessage id={`${this.roles.code}.header.title`} />}>
          <Button
            onClick={this.createRole}
            icon="playlist_add"
          >
            <FormattedMessage id="add" />
          </Button>
          <Button
            onClick={this.deleteRoleByMultiple}
            icon="delete"
            // disabled={!(showMember ? Object.keys(selectMemberRoles) : selectRoleMemberKeys).length}
          >
            <FormattedMessage id="remove" />
          </Button>
          <Button
            onClick={this.reload}
            icon="refresh"
          >
            <FormattedMessage id="refresh" />
          </Button>
        </Header>
        <Content>
          <div className="member-role-btns">
            <span className="text">
              <FormattedMessage id="memberrole.view" />：
            </span>
            <Button
              className={this.getMemberRoleClass('member')}
              onClick={() => {
                this.showMemberTable(true);
              }}
              type="primary"
            >
              <FormattedMessage id="memberrole.member" />
            </Button>
            <Button
              className={this.getMemberRoleClass('role')}
              onClick={() => {
                this.showMemberTable(false);
              }}
              type="primary"
            >
              <FormattedMessage id="memberrole.role" />
            </Button>
          </div>
          {this.renderTable()}
        </Content>
      </Page>
    );
  }
}
