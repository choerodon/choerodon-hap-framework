import React, { Component } from 'react';
import CacheRoute, { CacheSwitch } from 'react-router-cache-route';
import { asyncLocaleProvider, asyncRouter, nomatch, stores } from '@choerodon/boot';

const Employee = asyncRouter(() => import('./src/hr/employee'));
const Position = asyncRouter(() => import('./src/hr/position'));
const OrgUnit = asyncRouter(() => import('./src/hr/org-unit'));
const User = asyncRouter(() => import('./src/account/user'));
const Company = asyncRouter(() => import('./src/hr/company'));
const CodeRules = asyncRouter(() => import('./src/sys/code-rules'));
const Profile = asyncRouter(() => import('./src/sys/profile'));
const HotKey = asyncRouter(() => import('./src/sys/hotkey'));
const SysConfig = asyncRouter(() => import('./src/sys/sys-config'));
const Preferences = asyncRouter(() => import('./src/sys/preferences'));
const Invoke = asyncRouter(() => import('./src/if-invoke'));
const Role = asyncRouter(() => import('./src/sys/role'));
const MenuSetting = asyncRouter(() => import('./src/sys/menu-setting'));
const RoleCreate = asyncRouter(() => import('./src/sys/role/view/RoleMsg'));
const MemberRole = asyncRouter(() => import('./src/sys/member-role'));
const PermissionRule = asyncRouter(() => import('./src/sys/permission/rule'));
const PermissionTable = asyncRouter(() => import('./src/sys/permission/table'));
const UserInfo = asyncRouter(() => import('./src/sys/user-info'));
const Code = asyncRouter(() => import('./src/code'));
const Prompt = asyncRouter(() => import('./src/prompt'));
const Language = asyncRouter(() => import('./src/language'));
const Lov = asyncRouter(() => import('./src/lov'));

const { AppState } = stores;
const language = AppState.currentLang;
const IntlProviderAsync = asyncLocaleProvider(language, () => import(`./locale/${language}`));

class HAPIndex extends Component {
  render() {
    const { match } = this.props;
    return (
      <IntlProviderAsync>
        <CacheSwitch>
          <CacheRoute exact path={`${match.url}/hr/orgunit`} cacheKey={`${match.url}/hr/orgunit`} component={OrgUnit} />
          <CacheRoute exact path={`${match.url}/hr/company`} cacheKey={`${match.url}/hr/company`} component={Company} />
          <CacheRoute exact path={`${match.url}/hr/position`} cacheKey={`${match.url}/hr/position`} component={Position} />
          <CacheRoute exact path={`${match.url}/hr/employee`} cacheKey={`${match.url}/hr/employee`} component={Employee} />
          <CacheRoute exact path={`${match.url}/sys/preferences`} cacheKey={`${match.url}/sys/preferences`} component={Preferences} />
          <CacheRoute exact path={`${match.url}/sys/config`} cacheKey={`${match.url}/sys/config`} component={SysConfig} />
          <CacheRoute exact path={`${match.url}/account/user`} cacheKey={`${match.url}/account/user`} component={User} />
          <CacheRoute exact path={`${match.url}/sys/code_rules`} cacheKey={`${match.url}/sys/code_rules`} component={CodeRules} />
          <CacheRoute exact path={`${match.url}/sys/profile`} cacheKey={`${match.url}/sys/profile`} component={Profile} />
          <CacheRoute exact path={`${match.url}/sys/hotkey`} cacheKey={`${match.url}/sys/hotkey`} component={HotKey} />
          <CacheRoute exact path={`${match.url}/if-invoke`} cacheKey={`${match.url}/if-invoke`} component={Invoke} />
          <CacheRoute exact path={`${match.url}/role`} cacheKey={`${match.url}/role`} component={Role} />
          <CacheRoute exact path={`${match.url}/permission/rule`} cacheKey={`${match.url}/permission/rule`} component={PermissionRule} />
          <CacheRoute exact path={`${match.url}/permission/table`} cacheKey={`${match.url}/permission/table`} component={PermissionTable} />
          <CacheRoute exact path={`${match.url}/menu-setting`} cacheKey={`${match.url}/menu-setting`} component={MenuSetting} />
          <CacheRoute exact path={`${match.url}/role/create`} cacheKey={`${match.url}/role/create`} component={RoleCreate} />
          <CacheRoute exact path={`${match.url}/member-role`} cacheKey={`${match.url}/member-role`} component={MemberRole} />
          <CacheRoute exact path={`${match.url}/sys/user-info`} cacheKey={`${match.url}/sys/user-info`} component={UserInfo} />
          <CacheRoute exact path={`${match.url}/code`} cacheKey={`${match.url}/code`} component={Code} />
          <CacheRoute exact path={`${match.url}/prompt`} cacheKey={`${match.url}/prompt`} component={Prompt} />
          <CacheRoute exact path={`${match.url}/language`} cacheKey={`${match.url}/language`} component={Language} />
          <CacheRoute exact path={`${match.url}/lov`} cacheKey={`${match.url}/lov`} component={Lov} />
          <CacheRoute path="*" component={nomatch} />
        </CacheSwitch>
      </IntlProviderAsync>
    );
  }
}
export default HAPIndex;
