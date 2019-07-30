import React, { Component } from 'react';
import CacheRoute, { CacheSwitch } from 'react-router-cache-route';
import { asyncRouter, nomatch } from '@choerodon/boot';

const ACCOUNT = asyncRouter(() => import('./src/mail-account'));
const TEMPLATE = asyncRouter(() => import('./src/mail-template'));
const TEST = asyncRouter(() => import('./src/mail-test'));
const STATUS = asyncRouter(() => import('./src/mail-status'));

export default class RouteIndex extends Component {
  render() {
    const { match } = this.props;
    return (
      <CacheSwitch>
        <CacheRoute exact path={`${match.url}/mail/account`} cacheKey={`${match.url}/mail/account`} component={ACCOUNT} />
        <CacheRoute exact path={`${match.url}/mail-template`} cacheKey={`${match.url}/mail-template`} component={TEMPLATE} />
        <CacheRoute exact path={`${match.url}/mail-test`} cacheKey={`${match.url}/mail-template`} component={TEST} />
        <CacheRoute exact path={`${match.url}/mail-status`} cacheKey={`${match.url}/mail-template`} component={STATUS} />
        <CacheRoute path="*" component={nomatch} />
      </CacheSwitch>
    );
  }
}
