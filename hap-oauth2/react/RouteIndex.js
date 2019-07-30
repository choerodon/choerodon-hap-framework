import React, { Component } from 'react';
import CacheRoute, { CacheSwitch } from 'react-router-cache-route';
import { asyncRouter, nomatch } from '@choerodon/boot';

const Client = asyncRouter(() => import('./src/if-client'));
const Token = asyncRouter(() => import('./src/if-token'));

export default class RouteIndex extends Component {
  render() {
    const { match } = this.props;
    return (
      <CacheSwitch>
        <CacheRoute exact path={`${match.url}/if-client`} cacheKey={`${match.url}/if-client`} component={Client} />
        <CacheRoute exact path={`${match.url}/if-token`} cacheKey={`${match.url}/if-token`} component={Token} />
        <CacheRoute path="*" component={nomatch} />
      </CacheSwitch>
    );
  }
}
