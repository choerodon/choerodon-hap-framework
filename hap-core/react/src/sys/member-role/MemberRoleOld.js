import React, { Component } from 'react';
import get from 'lodash/get';
import { inject, observer } from 'mobx-react';
import { Button, Form, Modal, Select, Table, Tooltip } from 'choerodon-ui';
import { withRouter } from 'react-router-dom';
import { ContentPro as Content, Header, Page } from '@choerodon/boot';
import { FormattedMessage, injectIntl } from 'react-intl';
import classnames from 'classnames';
import MemberRoleType, { pageSize } from './MemberRoleType';
import MemberRoleStore from './stores';
import './MemberRole.scss';
import '../../common/ConfirmModal.scss';

let timer;
const { Sidebar } = Modal;
const FormItem = Form.Item;
const { Option } = Select;
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
  state = this.getInitState();

  getInitState() {
    return {
      selectLoading: true,
      loading: true,
      submitting: false,
      sidebar: false,
      selectType: '',
      showMember: true,
      expandedKeys: [], // 角色展开
      roleIds: [],
      overflow: false,
      fileLoading: false,
      createMode: 'user',
      selectRoleMemberKeys: [],
      roleData: MemberRoleStore.getRoleData, // 所有角色
      roleMemberDatas: MemberRoleStore.getRoleMemberDatas, // 用户-角色表数据源
      memberDatas: [], // 用户-成员表数据源
      currentMemberData: [], // 当前成员的角色分配信息
      selectMemberRoles: {},
      selectRoleMembers: [],
      roleMemberFilters: {}, // 用户-角色表格过滤
      roleMemberParams: [], // 用户-角色表格参数
      memberRoleFilters: {}, // 用户-成员表格过滤
      params: [], // 用户-成员表格参数
      memberRolePageInfo: { // 用户-成员表格分页信息
        current: 1,
        total: 0,
        pageSize,
      },
      roleMemberFilterRole: [],
      clientMemberDatas: [],
      cilentRoleMemberDatas: MemberRoleStore.getClientRoleMemberDatas,
      clientMemberRolePageInfo: { // 客户端-成员表格分页信息
        current: 1,
        total: 0,
        pageSize,
      },
      clientMemberRoleFilters: {},
      clientMemberParams: [],
      clientRoleMemberFilters: {},
      clientParams: [],
      clientRoleMemberParams: [],
      selectClientMemberRoles: {},
      selectClientRoleMembers: [],
      clientRoleMemberFilterRole: [],
    };
  }

  init() {
    this.initMemberRole();
    this.roles.fetch();
  }

  componentWillMount() {
    this.init();
  }

  componentDidUpdate() {
    MemberRoleStore.setRoleMemberDatas(this.state.roleMemberDatas);
    MemberRoleStore.setRoleData(this.state.roleData);
  }

  componentWillUnmount() {
    clearInterval(this.timer);
    clearTimeout(timer);
    MemberRoleStore.setRoleMemberDatas([]);
    MemberRoleStore.setRoleData([]);
  }

  initMemberRole() {
    this.roles = new MemberRoleType(this);
  }

  reload = () => {
    this.setState(this.getInitState(), () => {
      this.init();
    });
  };

  formatMessage = (id, values = {}) => {
    const { intl } = this.props;
    return intl.formatMessage({
      id,
    }, values);
  };

  /**
   * [5.16] 打开弹窗
   */
  openSidebar = () => {
    this.props.form.resetFields();
    this.setState({
      roleIds: this.initFormRoleIds(),
      sidebar: true,
    });
  };

  /**
   * [5.15] 关闭弹窗
   */
  closeSidebar = () => {
    this.setState({ sidebar: false });
  };

  /**
   * [5.16] 打开弹窗时对角色Id的处理
   */
  initFormRoleIds() {
    const { selectType, currentMemberData } = this.state;
    let roleIds = [];
    if (selectType === 'edit' && currentMemberData && Array.isArray(currentMemberData.userRoles)) {
      roleIds = currentMemberData.userRoles.map(({ id }) => id);
    }
    return roleIds;
  }

  /**
   * [5.16] 批量移除角色
   */
  deleteRoleByMultiple = () => {
    const { selectMemberRoles, showMember, selectRoleMembers } = this.state;
    let content;
    if (showMember) {
      content = 'memberrole.remove.select.all.content';
    } else {
      content = 'memberrole.remove.select.content';
    }
    Modal.confirm({
      className: 'c7n-iam-confirm-modal',
      title: this.formatMessage('memberrole.remove.title'),
      content: this.formatMessage(content),
      onOk: () => {
        if (showMember) {
          return this.deleteRolesByIds(selectMemberRoles);
        } else {
          const data = {};
          selectRoleMembers.forEach(({ id, roleId }) => {
            if (!data[roleId]) {
              data[roleId] = [];
            }
            data[roleId].push(id);
          });
          return this.deleteRolesByIds(data);
        }
      },
    });
  };

  /**
   * 删除单个成员或客户端
   * @param record
   */
  handleDelete = (record) => {
    const content = this.formatMessage('memberrole.remove.all.content', { name: record.userName });
    Modal.confirm({
      className: 'c7n-iam-confirm-modal',
      title: this.formatMessage('memberrole.remove.title'),
      content,
      onOk: () => this.deleteRolesByIds({
        [record.userId]: record.userRoles.map(({ id }) => id),
      }),
    });
  };

  deleteRoleByRole = (record) => {
    const isUsersMode = MemberRoleStore.currentMode === 'user';
    let content;
    if (isUsersMode) {
      content = this.formatMessage('memberrole.remove.content', {
        member: record.loginName,
        role: record.roleName,
      });
    } else {
      content = this.formatMessage('memberrole.remove.client.content', {
        member: record.name,
        role: record.roleName,
      });
    }
    Modal.confirm({
      className: 'c7n-iam-confirm-modal',
      title: this.formatMessage('memberrole.remove.title'),
      content,
      onOk: () => this.deleteRolesByIds({ [record.roleId]: [record.id] }),
    });
  };

  /**
   * [5.16] 批量删除
   */
  deleteRolesByIds = (data) => {
    const { showMember } = this.state;
    const isUsersMode = MemberRoleStore.currentMode === 'user';
    const body = {
      // view: showMember ? 'userView' : 'roleView',
      // memberType: isUsersMode ? 'user' : 'client',
      data,
    };
    return this.roles.deleteRoleMember(body).then(({ failed, message }) => {
      if (failed) {
        Choerodon.prompt(message);
      } else {
        Choerodon.prompt(this.formatMessage('remove.success'));
        this.setState({
          selectRoleMemberKeys: [],
          selectMemberRoles: {},
        });
        if (isUsersMode) {
          this.roles.fetch();
        } else {
          this.roles.fetchClient();
        }
      }
    });
  };

  /**
   * [5.16] 根据创建还是修改，获得弹出框的标题
   */
  getSidebarTitle() {
    const { selectType } = this.state;
    if (selectType === 'create') {
      return <FormattedMessage id="memberrole.add" />;
    } else if (selectType === 'edit') {
      return <FormattedMessage id="memberrole.modify" />;
    }
  }

  /**
   * [5.16] 渲染弹出框中的内容
   */
  getSidebarContent() {
    const { roleData = [], roleIds, selectType } = this.state;
    const disabled = roleIds.findIndex((id, index) => id === undefined) !== -1
      || !roleData.filter(({ enabled, id }) => enabled && roleIds.indexOf(id) === -1).length;
    return (
      <Content>
        {this.getForm()}
        {this.getAddOtherBtn(disabled)}
      </Content>
    );
  }

  /**
   * [5.16] 渲染弹出框中的表单
   */
  getForm = () => {
    const { selectType } = this.state;
    return selectType === 'create' ? (
      <Form layout="vertical">
        {this.getProjectNameDom()}
        {this.getRoleFormItems()}
      </Form>
    ) : (
      <Form layout="vertical">
        {this.getRoleFormItems()}
      </Form>
    );
  };

  /**
   * [5.16] 渲染用户下拉框
   */
  getProjectNameDom() {
    const { selectType, currentMemberData, createMode, overflow } = this.state;
    const { form, intl } = this.props;
    const { getFieldDecorator } = form;
    const member = [];
    const style = {
      marginTop: '-15px',
    };
    if (selectType === 'edit') {
      member.push(MemberRoleStore.currentMode === 'user' ? currentMemberData.loginName : currentMemberData.id);
      style.display = 'none';
      return null;
    }

    return (
      selectType === 'create' && (
        <FormItem
          {...FormItemNumLayout}
        >
          {getFieldDecorator('member', {
            rules: [{
              required: true,
              message: intl.formatMessage({ id: 'memberrole.user.require.msg' }),
            }],
            initialValue: selectType === 'create' ? [] : member,
          })(
            <Select
              label={<FormattedMessage id="memberrole.type.user" />}
              optionLabelProp="label"
              allowClear
              style={{ width: 512 }}
              mode="multiple"
              optionFilterProp="children"
              filterOption={false}
              filter
              onFilterChange={this.handleSelectFilter}
              notFoundContent={intl.formatMessage({ id: 'memberrole.notfound.msg' })}
              loading={this.state.selectLoading}
            >
              {this.getUserOption()}
            </Select>,
          )}
        </FormItem>
      )
    );
  }

  /**
   * [5.16] 弹出框里角色选择表单部分
   */
  getRoleFormItems = () => {
    const { selectType, roleIds, overflow } = this.state;
    const { getFieldDecorator } = this.props.form;
    const formItems = roleIds.map((id, index) => {
      const key = id === undefined ? `role-index-${index}` : String(id);
      return (
        <FormItem
          {...FormItemNumLayout}
          key={key}
        >
          {getFieldDecorator(key, {
            rules: [
              {
                required: roleIds.length === 1 && selectType === 'create',
                message: this.formatMessage('memberrole.role.require.msg'),
              },
            ],
            initialValue: id,
          })(
            <Select
              className="member-role-select"
              style={{ width: 300 }}
              label={<FormattedMessage id="memberrole.role.label" />}
              filterOption={(input, option) => {
                const childNode = option.props.children;
                if (childNode && React.isValidElement(childNode)) {
                  return childNode.props.children.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
                }
                return false;
              }}
              onChange={(value) => {
                roleIds[index] = value;
              }}
              filter
            >
              {this.getOption(id)}
            </Select>,
          )}
          <Button
            size="small"
            icon="delete"
            shape="circle"
            onClick={() => this.removeRole(index)}
            disabled={roleIds.length === 1 && selectType === 'create'}
            className="delete-role"
          />
        </FormItem>
      );
    });
    return formItems;
  };

  /**
   * [5.16] 用户下拉框，查询用户
   */
  handleSelectFilter = (value) => {
    this.setState({
      selectLoading: true,
    });
    const { createMode } = this.state;
    const queryObj = {
      userName: value,
      // sort: 'id',
      // organization_id: get(this.props.AppState, 'currentMenuType.organizationId', 0),
    };

    if (timer) {
      clearTimeout(timer);
    }

    if (value) {
      timer = setTimeout(() => this.loadUsers(queryObj), 300);
    } else {
      this.loadUsers(queryObj);
    }
  }

  /**
   * [5.16] 加载用户并且写回
   */
  loadUsers = (queryObj) => {
    MemberRoleStore.loadUsers(queryObj).then((data) => {
      MemberRoleStore.setUsersData(data);
      this.setState({
        selectLoading: false,
      });
    });
  }

  /**
   * [5.16] 用户选择下拉框
   */
  getUserOption = () => {
    const usersData = MemberRoleStore.getUsersData;
    return usersData && usersData.length > 0 ? (
      usersData.map(({ userId, imageUrl, userName }) => (
        <Option key={userId} value={userId} label={`${userName}`}>
          <div className="c7n-iam-memberrole-user-option">
            <div className="c7n-iam-memberrole-user-option-avatar">
              {
                imageUrl
                  ? <img src={imageUrl} alt="userAvatar" style={{ width: '100%' }} />
                  : <span className="c7n-iam-memberrole-user-option-avatar-noavatar">{userName && userName.split('')[0]}</span>
              }
            </div>
            <span>{userName}</span>
          </div>
        </Option>
      ))
    ) : null;
  }

  /**
   * [5.16] 弹出框中角色添加下拉框的Option
   */
  getOption = (current) => {
    const { roleData = [], roleIds } = this.state;
    return roleData.reduce((options, { id, name, enabled, code }) => {
      if (roleIds.indexOf(id) === -1 || id === current) {
        if (enabled === false) {
          options.push(<Option style={{ display: 'none' }} disabled value={id} key={id}>{name}</Option>);
        } else {
          options.push(
            <Option value={id} key={id} title={name}>
              <Tooltip title={code} placement="right" align={{ offset: [20, 0] }}>
                <span style={{ display: 'inline-block', width: '100%' }}>{name}</span>
              </Tooltip>
            </Option>,
          );
        }
      }
      return options;
    }, []);
  };

  /**
   * [5.16] 弹出框中角色删除事件
   */
  removeRole = (index) => {
    const { roleIds } = this.state;
    roleIds.splice(index, 1);
    this.setState({ roleIds });
  };

  /**
   * [5.16] 弹出框中添加其他角色按钮
   */
  getAddOtherBtn(disabled) {
    return (
      <Button type="primary" disabled={disabled} className="add-other-role" icon="add" onClick={this.addRoleList}>
        <FormattedMessage id="memberrole.add.other" />
      </Button>
    );
  }

  /**
   * [5.16] 弹出框内添加其他角色按钮点击后事件
   */
  addRoleList = () => {
    const { roleIds } = this.state;
    roleIds.push(undefined);
    this.setState({ roleIds });
  };

  /**
   * [5.16] 判断弹窗内的角色信息是否修改过
   */
  isModify = () => {
    const { roleIds, currentMemberData: { roles } } = this.state;
    if (roles.length !== roleIds.length) {
      return true;
    }
    for (let i = 0; i < roles.length; i += 1) {
      if (!roleIds.includes(roles[i].id)) {
        return true;
      }
    }
    return false;
  };

  /**
   * [5.16] 弹窗点击确定事件
   */
  handleOk = (e) => {
    const { selectType, roleIds } = this.state;
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      const memberType = selectType === 'create' ? values.mode : MemberRoleStore.currentMode;
      if (!err) {
        const origin = this.initFormRoleIds();
        const after = roleIds.filter(roleId => roleId);
        const deleteArr = origin.filter(v => !after.includes(v));
        const addArr = after.filter(v => !origin.includes(v));
        const body = roleIds.filter(roleId => roleId && addArr.includes(roleId)).map((roleId, index) => ({
          roleId,
          __status: 'add',
        })).concat(deleteArr.map(v => ({
          roleId: v,
          __status: 'delete',
        })));
        const pageInfo = {
          current: 1,
          total: 0,
          pageSize,
        };
        this.setState({ submitting: true });
        if (selectType === 'create') {
          this.roles.fetchRoleMember(values.member, body, memberType)
            .then(({ failed, message }) => {
              this.setState({ submitting: false });
              if (failed) {
                Choerodon.prompt(message);
              } else {
                Choerodon.prompt(this.formatMessage('add.success'));
                this.closeSidebar();
                this.setState({
                  memberRolePageInfo: pageInfo,
                }, () => {
                  this.roles.fetch();
                });
              }
            })
            .catch((error) => {
              this.setState({ submitting: false });
              Choerodon.handleResponseError(error);
            });
        } else if (selectType === 'edit') {
          if (!deleteArr.length && !addArr.length) {
            this.setState({ submitting: false });
            this.closeSidebar();
            return;
          }
          const { currentMemberData } = this.state;
          const memberIds = [currentMemberData.userId];
          this.roles.fetchRoleMember(memberIds, body, memberType, true)
            .then(({ failed, message }) => {
              this.setState({ submitting: false });
              if (failed) {
                Choerodon.prompt(message);
              } else {
                Choerodon.prompt(this.formatMessage('modify.success'));
                this.closeSidebar();
                if (body.length) {
                  this.setState({
                    memberRolePageInfo: pageInfo,
                  }, () => {
                    this.roles.fetch();
                  });
                } else {
                  this.roles.fetch();
                }
              }
            })
            .catch((error) => {
              this.setState({ submitting: false });
              Choerodon.handleResponseError(error);
            });
        }
      }
    });
  };

  /**
   * [5.16] 点击头部创建角色事件
   */
  createRole = () => {
    this.setState({ selectType: 'create' }, () => {
      this.openSidebar();
    });
  };

  /**
   * [5.16] 修改角色
   */
  editRole = (memberData) => {
    this.setState({
      selectType: 'edit',
      currentMemberData: memberData,
    }, () => this.openSidebar());
  };

  /**
   * [5.16] 点击修改角色事件
   */
  handleEditRole = ({ userId: memberId, userName }) => {
    const member = this.state.memberDatas.find(({ userId }) => userId === memberId);
    if (!member) {
      this.roles.loadMemberDatas({
        current: 1,
        pageSize,
      }, {
        userName: [userName],
      }).then(({ list }) => {
        this.editRole(list.find(memberData => memberData.userName === userName));
      });
    } else {
      this.editRole(member);
    }
  };

  /**
   * [5.16] 点击查看方式处理事件
   */
  showMemberTable(show) {
    this.reload();
    this.setState({
      showMember: show,
    });
  }

  /**
   * [5.16] 用户表变化回调
   */
  memberRoleTableChange = (memberRolePageInfo, memberRoleFilters, sort, params) => {
    this.setState({
      memberRolePageInfo,
      memberRoleFilters,
      params,
      loading: true,
    });
    this.roles.loadMemberDatas(memberRolePageInfo, memberRoleFilters, params).then(({ list, total, pageNum, pageSize }) => {
      this.setState({
        loading: false,
        memberDatas: list,
        memberRolePageInfo: {
          current: pageNum,
          total,
          pageSize,
        },
        params,
        memberRoleFilters,
      });
    });
  };

  /**
   * [5.16] 渲染表格的普通字段行，只添加用户是否过期的判断
   */
  renderSimpleColumn = (text, { enabled }) => {
    if (enabled === false) {
      return (
        <Tooltip title={<FormattedMessage id="memberrole.member.disabled.tip" />}>
          <span className="text-disabled">
            {text}
          </span>
        </Tooltip>
      );
    }
    return text;
  };

  /**
   * [5.16] 用户方式查看表的权限字段渲染
   */
  renderRoleColumn = text => text.map(({ id, name, enabled }) => {
    let item = <span className={classnames('role-wrapper', { 'role-wrapper-enabled': enabled, 'role-wrapper-disabled': !enabled })} key={id}>{name}</span>;
    if (enabled === false) {
      item = (
        <Tooltip title={<FormattedMessage id="memberrole.role.disabled.tip" />}>
          {item}
        </Tooltip>
      );
    }
    return item;
  });

  /**
   * [5.16] 渲染操作列
   */
  renderActionColumn = (text, record) => {
    if ('roleId' in record || 'email' in record || 'secret' in record) {
      return (
        <div>
          <Tooltip
            title={<FormattedMessage id="modify" />}
            placement="bottom"
          >
            <Button
              onClick={() => {
                this.handleEditRole(record);
              }}
              size="small"
              shape="circle"
              icon="mode_edit"
            />
          </Tooltip>
          <Tooltip
            title={<FormattedMessage id="remove" />}
            placement="bottom"
          >
            <Button
              size="small"
              shape="circle"
              onClick={this.handleDelete.bind(this, record)}
              icon="delete"
            />
          </Tooltip>
        </div>
      );
    }
  };

  /**
   * [5.16] 渲染成员查看方式下的表
   */
  renderMemberTable() {
    const { selectMemberRoles, roleMemberDatas, memberRolePageInfo, memberDatas, memberRoleFilters, loading } = this.state;
    const filtersRole = [...new Set(roleMemberDatas.map(({ name }) => (name)))].map(value => ({ value, text: value }));
    const columns = [
      {
        title: <FormattedMessage id="memberrole.realname" />,
        dataIndex: 'userName',
        key: 'userName',
        width: '15%',
        filters: [],
        filteredValue: memberRoleFilters.userName || [],
        render: this.renderSimpleColumn,
      },
      {
        title: <FormattedMessage id="memberrole.role" />,
        dataIndex: 'userRoles',
        key: 'userRoles',
        filters: filtersRole,
        filteredValue: memberRoleFilters.userRoles || [],
        className: 'memberrole-roles',
        // width: '50%',
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
            const currentRecord = selectedRecords.find(r => r.userId === key) || { userRoles: [] };
            data[key] = currentRecord.userRoles.map(({ id }) => id);
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
        rowKey={({ userId }) => userId}
        noFilter
      />
    );
  }

  /**
   * [5.16] 根据是否选中，查看方式两个按钮的样式返回
   */
  getMemberRoleClass(name) {
    const { showMember } = this.state;
    // eslint-disable-next-line no-bitwise
    return classnames({ active: name === 'role' ^ showMember });
  }

  /**
   * [5.16] 渲染表格，条件判断走不同的渲染
   */
  renderTable = () => {
    const { showMember } = this.state;
    const showTable = this.renderMemberTable();
    return showTable;
  };

  render() {
    const { sidebar, selectType, roleData, selectMemberRoles, submitting } = this.state;
    const okText = selectType === 'create' ? this.formatMessage('add') : this.formatMessage('save');
    return (
      <Page>
        <Header title="角色分配">
          <Button
            onClick={this.createRole}
            icon="playlist_add"
          >
            <FormattedMessage id="add" />
          </Button>
          <Button
            onClick={this.deleteRoleByMultiple}
            icon="delete"
            disabled={!(Object.keys(selectMemberRoles)).length}
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
          {this.renderTable()}
          <Sidebar
            bodyStyle={{ padding: 0 }}
            title={this.getSidebarTitle()}
            visible={sidebar}
            okText={okText}
            confirmLoading={submitting}
            cancelText={<FormattedMessage id="cancel" />}
            onOk={this.handleOk}
            onCancel={this.closeSidebar}
          >
            {roleData.length ? this.getSidebarContent() : null}
          </Sidebar>
        </Content>
      </Page>
    );
  }
}
