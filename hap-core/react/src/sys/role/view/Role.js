import React, { Component } from 'react';
import get from 'lodash/get';
import { withRouter } from 'react-router-dom';
import { inject, observer } from 'mobx-react';
import { Button, Form, Table, Tooltip } from 'choerodon-ui';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Action, ContentPro as Content, Dropdown, Header, Page } from '@choerodon/boot';
import RoleStore from '../stores/RoleStore';
import '../Role.scss';
import StatusTag from '../../../components/statusTag';

const intlPrefix = 'global.role';
@Form.create({})
@withRouter
@injectIntl
@inject('AppState')
@observer
export default class Role extends Component {
  constructor(props) {
    super(props);
    props.cacheLifecycles.didRecover(this.componentDidRecover.bind(this));
  }

  state = this.getInitState();

  componentDidMount() {
    this.loadRole();
  }

  componentDidRecover() {
    const shouldRefresh = get(this.props, 'history.location.state.refresh', false);
    if (shouldRefresh) {
      this.handleRefresh();
    }
  }

  getInitState() {
    return {
      id: '',
      selectedRoleIds: {},
      params: [],
      filters: {},
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
      },
      sort: {
        columnKey: 'id',
        order: 'descend',
      },
      selectedData: '',
      level: 'site',
    };
  }

  getSelectedRowKeys() {
    return Object.keys(this.state.selectedRoleIds).map(id => Number(id));
  }

  showModal = (ids) => {
    this.props.history.push(`role/create?level=${this.state.level}&roleId=${ids}`);
  };

  goCreate = () => {
    RoleStore.setChosenLevel('');
    RoleStore.setLabel([]);
    RoleStore.setSelectedRolesPermission([]);
    this.props.history.push(`role/create?level=${this.state.level}`);
  };

  loadRole(paginationIn, sortIn, filtersIn, paramsIn) {
    const {
      pagination: paginationState,
      sort: sortState,
      filters: filtersState,
      params: paramsState,
      level,
    } = this.state;
    const pagination = paginationIn || paginationState;
    const sort = sortIn || sortState;
    const filters = filtersIn || filtersState;
    const params = paramsIn || paramsState;
    this.setState({ filters });
    RoleStore.loadRole(level, pagination, sort, filters, params)
      .then((data) => {
        RoleStore.setIsLoading(false);
        RoleStore.setRoles(data.list || []);
        this.setState({
          sort,
          filters,
          params,
          pagination: {
            current: data.pageNum,
            pageSize: data.pageSize,
            total: data.total,
          },
        });
      })
      .catch((error) => {
        Choerodon.handleResponseError(error);
      });
  }

  linkToChange = (url) => {
    this.props.history.push(`${url}`);
  };

  handleRefresh = () => {
    this.setState(this.getInitState(), () => {
      this.loadRole();
    });
  };

  handleEnable = (record) => {
    const { intl } = this.props;
    if (record.enabled) {
      RoleStore.disableRole(record.id).then(() => {
        Choerodon.prompt(intl.formatMessage({ id: 'disable.success' }));
        this.loadRole();
      });
    } else {
      RoleStore.enableRole(record.id).then(() => {
        Choerodon.prompt(intl.formatMessage({ id: 'enable.success' }));
        this.loadRole();
      });
    }
  };

  changeSelects = (selectedRowKeys, selectedRows) => {
    const { selectedRoleIds } = this.state;
    Object.keys(selectedRoleIds).forEach((id) => {
      if (selectedRowKeys.indexOf(Number(id)) === -1) {
        delete selectedRoleIds[id];
      }
    });
    selectedRows.forEach(({ id, level }) => {
      selectedRoleIds[id] = level;
    });
    this.setState({
      selectedRoleIds,
    });
  };

  handlePageChange = (pagination, filters, sort, params) => {
    this.loadRole(pagination, sort, filters, params);
  };

  createByThis(record) {
    this.linkToChange(`role/create?level=${this.state.level}&base=${record.id}`);
  }

  createByMultiple = () => {
    this.createBased();
  };

  createBased = () => {
    const ids = this.getSelectedRowKeys();
    this.linkToChange(`role/create?level=${this.state.level}&base=${ids.join(',')}`);
  };

  renderLevel(text) {
    if (text === 'organization') {
      return <FormattedMessage id="organization" />;
    } else if (text === 'project') {
      return <FormattedMessage id="project" />;
    } else {
      return <FormattedMessage id="global" />;
    }
  }

  render() {
    const { intl, AppState } = this.props;
    const { sort: { columnKey, order }, pagination, filters, params } = this.state;
    const selectedRowKeys = this.getSelectedRowKeys();
    const columns = [{
      dataIndex: 'id',
      key: 'id',
      hidden: true,
    }, {
      title: <FormattedMessage id="name" />,
      dataIndex: 'name',
      key: 'name',
      width: '20%',
      filters: [],
      filteredValue: filters.name || [],
    }, {
      title: <FormattedMessage id="code" />,
      dataIndex: 'code',
      key: 'code',
      width: '20%',
      filters: [],
      filteredValue: filters.code || [],
    }, {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      width: '25%',
      render: text => (
        <div style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', paddingRight: 20, width: 280 }}>
          <Tooltip title={text} placement="topLeft">
            {text}
          </Tooltip>
        </div>
      ),
    }, {
      title: <FormattedMessage id="source" />,
      dataIndex: 'builtIn',
      key: 'builtIn',
      filters: [{
        text: intl.formatMessage({ id: `${intlPrefix}.builtin.predefined` }),
        value: 'true',
      }, {
        text: intl.formatMessage({ id: `${intlPrefix}.builtin.custom` }),
        value: 'false',
      }],
      render: (text, record) => (
        <StatusTag
          mode="icon"
          name={intl.formatMessage({ id: record.builtIn ? 'predefined' : 'custom' })}
          colorCode={record.builtIn ? 'PREDEFINE' : 'CUSTOM'}
        />
      ),
      sorter: true,
      sortOrder: columnKey === 'builtIn' && order,
      filteredValue: filters.builtIn || [],
    }, {
      title: <FormattedMessage id="status" />,
      dataIndex: 'enabled',
      key: 'enabled',
      filters: [{
        text: intl.formatMessage({ id: 'enable' }),
        value: 'true',
      }, {
        text: intl.formatMessage({ id: 'disable' }),
        value: 'false',
      }],
      render: enabled => (
        <StatusTag mode="icon" name={intl.formatMessage({ id: enabled ? 'enable' : 'disable' })} colorCode={enabled ? 'COMPLETED' : 'DISABLE'} />),
      filteredValue: filters.enabled || [],
    }, {
      title: '',
      key: 'action',
      align: 'right',
      render: (text, record) => {
        const actionDatas = [{
          type: 'site',
          icon: '',
          text: intl.formatMessage({ id: `${intlPrefix}.create.byone` }),
          action: this.createByThis.bind(this, record),
        }, {
          icon: '',
          type: 'site',
          text: intl.formatMessage({ id: 'modify' }),
          action: this.showModal.bind(this, record.id),
        }];
        if (record.enabled) {
          actionDatas.push({
            icon: '',
            type: 'site',
            text: intl.formatMessage({ id: 'disable' }),
            action: this.handleEnable.bind(this, record),
          });
        } else {
          actionDatas.push({
            icon: '',
            type: 'site',
            text: intl.formatMessage({ id: 'enable' }),
            action: this.handleEnable.bind(this, record),
          });
        }
        return <Action data={actionDatas} />;
      },
    }];
    const rowSelection = {
      selectedRowKeys,
      onChange: this.changeSelects,
    };
    return (
      <Page
        service={[]}
        className="choerodon-role"
      >
        <Header
          title={<FormattedMessage id={`${intlPrefix}.header.title`} />}
        >
          <Button
            icon="playlist_add"
            onClick={this.goCreate}
          >
            <FormattedMessage id={`${intlPrefix}.create`} />
          </Button>


          <Button
            icon="content_copy"
            onClick={this.createByMultiple}
            disabled={!selectedRowKeys.length}
          >
            <FormattedMessage id={`${intlPrefix}.create.byselect`} />
          </Button>

          <Button
            onClick={this.handleRefresh}
            icon="refresh"
          >
            <FormattedMessage id="refresh" />
          </Button>
        </Header>
        <Content
          code={intlPrefix}
          values={{ name: 'HAP' }}
        >
          <Table
            columns={columns}
            dataSource={RoleStore.getRoles}
            pagination={pagination}
            rowSelection={rowSelection}
            rowKey={record => record.id}
            filters={params}
            onChange={this.handlePageChange}
            loading={RoleStore.getIsLoading}
            filterBarPlaceholder={intl.formatMessage({ id: 'filtertable' })}
          />
        </Content>
      </Page>
    );
  }
}
