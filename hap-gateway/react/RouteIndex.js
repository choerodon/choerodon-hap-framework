import React, { Component } from 'react';
import CacheRoute, { CacheSwitch } from 'react-router-cache-route';
import { asyncRouter, nomatch } from '@choerodon/boot';

const ApiServer = asyncRouter(() => import('./src/api-server'));
const ApiApplication = asyncRouter(() => import('./src/api-application'));
const ApiInvoke = asyncRouter(() => import('./src/api-invoke'));

export default class RouteIndex extends Component {
  render() {
    const { match } = this.props;
    return (
      <CacheSwitch>
        <CacheRoute exact path={`${match.url}/api-server`} cacheKey={`${match.url}/api-server`} component={ApiServer} />
        <CacheRoute exact path={`${match.url}/api-application`} cacheKey={`${match.url}/api-application`} component={ApiApplication} />
        <CacheRoute exact path={`${match.url}/api-invoke`} cacheKey={`${match.url}/api-invoke`} component={ApiInvoke} />
        <CacheRoute path="*" component={nomatch} />
      </CacheSwitch>
    );
  }
}
