import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import { inject, observer } from 'mobx-react';
import querystring from 'query-string';
import remove from 'lodash/remove';
import _ from 'lodash';
import { Icon, Button, Form, Input, Table, Tooltip, Tabs, Checkbox } from 'choerodon-ui';
import { FormattedMessage, injectIntl } from 'react-intl';
import { axiosPro as axios, ContentPro as Content, Header, Page, closeTab, getCurrentTab } from '@choerodon/boot';
import { RESOURCES_LEVEL } from '@choerodon/boot/lib/containers/common/constants';
import { get, set } from 'mobx';
import '../Role.scss';
import Sider from './Sider';
import RoleStore from '../stores/RoleStore';

const { TabPane } = Tabs;
const FormItem = Form.Item;
const { TextArea } = Input;
const intlPrefix = 'global.role';
const LEVEL_NAME = {
  site: '全局层',
  organization: '组织层',
  project: '项目层',
  user: '个人中心',
};

@Form.create({})
@withRouter
@injectIntl
@inject('AppState')
@observer
export default class CreateRole extends Component {
  constructor(props) {
    super(props);
    const queryObj = querystring.parse(props.location.search);
    this.level = queryObj.level || undefined;
    this.base = queryObj.base ? queryObj.base.split(',') : [];
    this.roleId = queryObj.roleId || undefined;
    this.isEdit = !!this.roleId;
    // eslint-disable-next-line prefer-destructuring
    this.tabLevel = RESOURCES_LEVEL.split(',')[0];

    props.cacheLifecycles.didRecover(this.componentDidRecover.bind(this));
  }

  componentDidRecover() {
    const queryObj = querystring.parse(this.props.history.location.search);
    if (!queryObj.level && !queryObj.base && !queryObj.roleId) {
      return;
    }
    this.level = queryObj.level || undefined;
    this.base = queryObj.base ? queryObj.base.split(',') : [];
    this.roleId = queryObj.roleId || undefined;
    this.isEdit = !!this.roleId;
    // eslint-disable-next-line prefer-destructuring
    this.tabLevel = RESOURCES_LEVEL.split(',')[0];
    if (!this.isEdit) {
      this.props.form.resetFields();
      RoleStore.setSelectedPermissions([]);
    }
    
    this.loadLabelsAndMenus();
  }

  componentDidMount() {
    if (!this.isEdit) {
      RoleStore.setSelectedPermissions([]);
    }
    this.loadLabelsAndMenus();
  }

  loadLabelsAndMenus = () => {
    const { tabLevel, base } = this;
    RoleStore.setTabLevel(tabLevel);
    this.loadMenu(RoleStore.tabLevel || tabLevel);
    if (base.length) {
      RoleStore.getSelectedRolePermissions(base)
        .then((res) => {
          RoleStore.setSelectedPermissions(res.map(p => p.id));
        });
    }
    if (this.isEdit) {
      RoleStore.getRoleById(this.roleId)
        .then((res) => {
          this.props.form.resetFields();
          RoleStore.setRoleMsg(res);
          RoleStore.setSelectedPermissions(res.permissions.map(p => p.id));
        });
    }
  }

  loadMenu = (tabLevel) => {
    RoleStore.loadMenu(tabLevel)
      .then((menus) => {
        set(RoleStore.menus, tabLevel, menus.subMenus);
        set(RoleStore.expandedRowKeys, tabLevel, this.getAllIdByLevel(tabLevel));
        if (!RoleStore.tabLevel) {
          RoleStore.setTabLevel(tabLevel);
        }
      });
  }

  check = (selectedPermissions, menu, sign, type) => {
    if (menu.subMenus) {
      menu.subMenus.map(menuItem => this.check(selectedPermissions, menuItem, sign, type));
    }
    this.checkOne(selectedPermissions, menu, sign, type);
  }

  checkOne = (selectedPermissions, menu, sign, type) => {
    if (type === 'all') {
      if (menu.permissions.map(p => p.id).some(pid => selectedPermissions.findIndex(v => v === pid) === -1)) {
        sign.sign = false;
      }
    } else if (type === 'none') {
      if (menu.permissions.map(p => p.id).some(pid => selectedPermissions.findIndex(v => v === pid) !== -1)) {
        sign.sign = false;
      }
    }
  }

  getTabCodes = () => {
    const LEVEL_OBJ = {
      site: ['site', 'user'],
      project: ['project'],
      organization: ['organization'],
    };
    return LEVEL_OBJ[this.level] || [];
  }

  getIds = (menu, res) => {
    res.push(menu.id);
    if (menu.subMenus) {
      menu.subMenus.map(menuItem => this.getIds(menuItem, res));
    }
  }

  getAllIdByLevel = (level) => {
    const menus = get(RoleStore.menus, level) || [];
    const res = [];
    menus.map(menu => this.getIds(menu, res));
    return res;
  }

  getOneMenuPermissons = (menu, res) => {
    res.res = res.res.concat(menu.permissions.map(p => p.id));
  }

  getPermissions = (menu, res) => {
    if (menu.subMenus) {
      menu.subMenus.map(menuItem => this.getPermissions(menuItem, res));
    }
    this.getOneMenuPermissons(menu, res);
  }

  getAllPermissionsByRecord = (record, originRes) => {
    const res = originRes || { res: [] };
    this.getPermissions(record, res);
    if (!originRes) {
      res.res = [...new Set(res.res)];
      return res.res;
    }
  }

  getAllPermissionsByLevel = (level) => {
    const menus = get(RoleStore.menus, level) || [];
    const res = { res: [] };
    menus.map(menu => this.getAllPermissionsByRecord(menu, res));
    res.res = [...new Set(res.res)];
    return res.res;
  }

  getCheckState = (type, selectedPermissions, record) => {
    const sign = { sign: true };
    this.check(selectedPermissions, record, sign, type);
    return sign.sign;
  }

  checkCode = (rule, value, callback) => {
    const { isEdit } = this;
    if (isEdit) {
      callback();
      return;
    }
    const params = { code: value };
    axios.post('/v1/roles/check', JSON.stringify(params)).then((mes) => {
      if (mes.success === false) {
        const { intl } = this.props;
        callback(intl.formatMessage({ id: `${intlPrefix}.code.exist.msg` }));
      } else {
        callback();
      }
    });
  };

  linkToChange = (url, refresh) => {
    const { history } = this.props;
    history.push({
      pathname: url,
      state: {
        refresh: true,
      },
    });
  };

  handleExpand = (expanded, record) => {
    const expandedRowKeys = get(RoleStore.expandedRowKeys, RoleStore.tabLevel) || [];
    if (expanded) {
      expandedRowKeys.push(record.id);
    } else {
      remove(expandedRowKeys, v => v === record.id);
    }
    set(RoleStore.expandedRowKeys, RoleStore.tabLevel, expandedRowKeys);
  }

  handleCheckboxAllClick = (checkedAll, checkedNone, checkedSome, e) => {
    const allPermissionsByRecord = this.getAllPermissionsByLevel(RoleStore.tabLevel);
    const { selectedPermissions } = RoleStore;
    let sp = selectedPermissions.slice();
    if (checkedNone || checkedSome) {
      sp = sp.concat(allPermissionsByRecord);
      sp = [...new Set(sp)];
    } else {
      remove(sp, p => allPermissionsByRecord.includes(p));
    }
    RoleStore.setSelectedPermissions(sp);
  }

  handleCheckboxClick = (record, checkedAll, checkedNone, checkedSome, e) => {
    const allPermissionsByRecord = this.getAllPermissionsByRecord(record);
    const { selectedPermissions } = RoleStore;
    let sp = selectedPermissions.slice();
    if (checkedNone || checkedSome) {
      sp = sp.concat(allPermissionsByRecord);
      sp = [...new Set(sp)];
    } else {
      remove(sp, p => allPermissionsByRecord.includes(p));
    }
    RoleStore.setSelectedPermissions(sp);
  }

  handleCreate = (e) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err) => {
      if (!err) {
        const role = {
          name: this.props.form.getFieldValue('name').trim(),
          code: this.props.form.getFieldValue('code').trim(),
          description: this.props.form.getFieldValue('description').trim(),
          level: this.level,
          permissions: RoleStore.selectedPermissions.slice().map(p => ({ id: p })),
          objectVersionNumber: RoleStore.roleMsg.objectVersionNumber,
        };
        const { intl } = this.props;
        if (!role.permissions || !role.permissions.length) {
          Choerodon.prompt('该角色没有一个权限，请至少选择一个权限');
          return;
        }
        if (this.isEdit) {
          RoleStore.editRoleByid(this.roleId, role)
            .then((data) => {
              if (!data.failed && data.id) {
                Choerodon.prompt(intl.formatMessage({ id: 'modify.success' }));
                this.linkToChange('/hap-core/role');
              } else {
                Choerodon.prompt(data.message || '保存失败');
              }
            })
            .catch((error) => {
              Choerodon.prompt(error || '保存失败');
            });
        } else {
          RoleStore.createRole(role)
            .then((data) => {
              if (data && data.id) {
                Choerodon.prompt(intl.formatMessage({ id: 'create.success' }));
                this.linkToChange('/hap-core/role', true);
                closeTab();
              } else {
                Choerodon.prompt(data.message);
              }
            })
            .catch((errors) => {
              if (errors.response.data.message === 'error.role.roleNameExist') {
                Choerodon.prompt(intl.formatMessage({ id: `${intlPrefix}.name.exist.msg` }));
              } else {
                Choerodon.prompt(intl.formatMessage({ id: 'create.error' }));
              }
            });
        }
      }
    });
  };

  handleReset = () => {
    this.linkToChange('/hap-core/role');
    closeTab();
  };

  handleChangeTabLevel = (key) => {
    RoleStore.setTabLevel(key);
    if (!get(RoleStore.menus, key)) {
      this.loadMenu(key);
    }
  }

  handleOpenSider = (record) => {
    RoleStore.setCurrentMenu(record);
    RoleStore.setSiderVisible(true);
  }

  handleSiderOk = (selectedPermissions) => {
    const { isEdit } = this;
    const isDefault = isEdit && RoleStore.roleMsg.builtIn;
    if (isDefault) {
      RoleStore.setSiderVisible(false);
      return;
    }
    RoleStore.setSelectedPermissions(selectedPermissions);
    RoleStore.setSiderVisible(false);
  }

  handleSiderCancel = () => {
    RoleStore.setSiderVisible(false);
  }

  handleClickExpandBtn = () => {
    const tabLevel = RoleStore.tabLevel || this.tabLevel;
    const expand = get(RoleStore.expand, tabLevel);
    if (expand) {
      // 需要展开
      set(RoleStore.expandedRowKeys, tabLevel, this.getAllIdByLevel(tabLevel));
    } else {
      // 需要收起
      set(RoleStore.expandedRowKeys, tabLevel, []);
    }
    set(RoleStore.expand, tabLevel, !expand);
  }

  renderCheckbox = (isDefault) => {
    const { selectedPermissions } = RoleStore;
    const allPermissionsByLevel = this.getAllPermissionsByLevel(RoleStore.tabLevel);
    const checkedAll = allPermissionsByLevel.every(p => selectedPermissions.includes(p));
    const checkedNone = allPermissionsByLevel.every(p => !selectedPermissions.includes(p));
    const checkedSome = !checkedAll && !checkedNone;
    return (
      <Checkbox
        indeterminate={checkedSome}
        onChange={this.handleCheckboxAllClick.bind(this, checkedAll, checkedNone, checkedSome)}
        checked={!checkedNone}
        disabled={isDefault}
      />
    );
  }

  renderForm = () => {
    const { props: { intl, form: { getFieldDecorator } }, isEdit } = this;
    const isDefault = isEdit && RoleStore.roleMsg.builtIn;
    const formItemLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 100 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 10 },
      },
    };

    return (
      <Form layout="vertical">
        <FormItem {...formItemLayout} style={{ display: 'inline-block', marginRight: 12 }}>
          {getFieldDecorator('code', {
            rules: [{
              required: true,
              whitespace: true,
              message: intl.formatMessage({ id: `${intlPrefix}.code.require.msg` }),
            }, {
              validator: this.checkCode,
            }],
            validateFirst: true,
            initialValue: isEdit ? RoleStore.roleMsg.code : undefined,
            validateTrigger: 'onBlur',
          })(
            <Input
              autoComplete="off"
              label={<FormattedMessage id={`${intlPrefix}.code`} />}
              size="default"
              style={{ width: 250 }}
              disabled={isEdit}
              maxLength={64}
              showLengthInfo={false}
            />,
          )}
        </FormItem>
        <FormItem {...formItemLayout} style={{ display: 'inline-block' }}>
          {getFieldDecorator('name', {
            rules: [{
              required: true,
              whitespace: true,
              message: intl.formatMessage({ id: `${intlPrefix}.name.require.msg` }),
            }],
            initialValue: isEdit ? RoleStore.roleMsg.name : undefined,
          })(
            <Input
              autoComplete="off"
              label={<FormattedMessage id={`${intlPrefix}.name`} />}
              style={{ width: 250 }}
              maxLength={64}
              showLengthInfo={false}
              disabled={isDefault}
            />,
          )}
        </FormItem>
        <FormItem {...formItemLayout} style={{ width: 512 }}>
          {getFieldDecorator('description', {
            initialValue: isEdit ? RoleStore.roleMsg.description : undefined,
          })(
            <TextArea
              autoComplete="off"
              label="描述"
              style={{ width: 512 }}
            />,
          )}
        </FormItem>
      </Form>
    );
  }

  renderTable = (level) => {
    const data = get(RoleStore.menus, level);
    const expandedRowKeys = get(RoleStore.expandedRowKeys, level) || [];
    const { isEdit } = this;
    const isDefault = isEdit && RoleStore.roleMsg.builtIn;
    const columns = [{
      title: '菜单',
      dataIndex: 'name',
      key: 'name',
      width: '35%',
      render: (text, record) => (
        <span>
          <Icon type={record.icon} style={{ marginRight: 8, verticalAlign: 'top' }} />
          {text}
        </span>
      ),
    }, {
      title: this.renderCheckbox(isDefault),
      dataIndex: 'id',
      key: 'id',
      width: '36px',
      render: (text, record) => {
        const { selectedPermissions } = RoleStore;
        const checkedAll = this.getCheckState('all', selectedPermissions, record);
        const checkedNone = this.getCheckState('none', selectedPermissions, record);
        const checkedSome = !checkedAll && !checkedNone;
        return (
          <Checkbox
            indeterminate={checkedSome}
            onChange={this.handleCheckboxClick.bind(this, record, checkedAll, checkedNone, checkedSome)}
            checked={!checkedNone}
            disabled={isDefault}
          />
        );
      },
    }, {
      title: '页面入口',
      dataIndex: 'route',
      key: 'route',
    }, {
      title: '',
      width: '50px',
      key: 'action',
      align: 'right',
      render: (text, record) => {
        const { subMenus } = record;
        if (!subMenus || !subMenus.length) {
          return (
            <Tooltip
              title={<FormattedMessage id="detail" />}
              placement="bottom"
            >
              <Button
                shape="circle"
                icon="predefine"
                size="small"
                onClick={this.handleOpenSider.bind(this, record)}
              />
            </Tooltip>
          );
        }
        return null;
      },
    }];
    return (
      <Table
        loading={false}
        filterBar={false}
        pagination={false}
        columns={columns}
        defaultExpandAllRows
        dataSource={data ? data.slice() : []}
        childrenColumnName="subMenus"
        rowKey={record => record.id}
        expandedRowKeys={expandedRowKeys.slice()}
        onExpand={this.handleExpand}
        indentSize={25}
      />
    );
  }

  renderBtns = () => {
    const { isEdit } = this;
    const isDefault = isEdit && RoleStore.roleMsg.builtIn;
    return (
      <div style={{ marginTop: 32 }}>
        <Button
          funcType="raised"
          type="primary"
          onClick={this.handleCreate}
          style={{ marginRight: 12 }}
        >
          <FormattedMessage id={!this.isEdit ? 'create' : 'save'} />
        </Button>
        <Button
          funcType="raised"
          onClick={this.handleReset}
          style={{ color: '#3F51B5' }}
        >
          <FormattedMessage id="cancel" />
        </Button>
      </div>
    );
  }

  renderSider = () => {
    const { siderVisible, currentMenu, selectedPermissions } = RoleStore;
    const { isEdit } = this;
    const isDefault = isEdit && RoleStore.roleMsg.builtIn;
    if (siderVisible) {
      return (
        <Sider
          selectedPermissions={selectedPermissions}
          menu={currentMenu}
          onOk={this.handleSiderOk}
          onCancel={this.handleSiderCancel}
          disabled={isDefault}
        />
      );
    }
    return null;
  }

  renderTab = () => {
    const tabLevel = RoleStore.tabLevel || this.tabLevel;
    const expand = get(RoleStore.expand, tabLevel);
    return (
      <React.Fragment>
        <div>
          <span style={{ marginRight: 80, fontSize: '16px' }}>菜单分配</span>
          <Button
            type="primary"
            funcType="flat"
            icon={expand ? 'expand_more' : 'expand_less'}
            onClick={this.handleClickExpandBtn}
          >
            全部{expand ? '展开' : '收起'}
          </Button>
        </div>
        <Tabs onChange={this.handleChangeTabLevel} activeKey={tabLevel}>
          {this.getTabCodes().map(level => (
            <TabPane tab={LEVEL_NAME[level] || level} key={level}>
              {this.renderTable(level)}
            </TabPane>
          ))}
        </Tabs>
      </React.Fragment>
    );
  }

  render() {
    return (
      <Page className="c7n-roleMsg">
        <Header
          title={`${!this.isEdit ? '创建' : '修改'}角色`}
          backPath="/hap-core/role"
        />
        <Content>
          {this.renderForm()}
          {this.renderTab()}
          {this.renderBtns()}
          {this.renderSider()}
        </Content>
      </Page>
    );
  }
}
