import React, { Component } from 'react';
import CacheRoute, { CacheSwitch } from 'react-router-cache-route';
import { asyncRouter, nomatch } from '@choerodon/boot';

const Config = asyncRouter(() => import('./src/if-config'));

export default class RouteIndex extends Component {
  render() {
    const { match } = this.props;
    return (
      <CacheSwitch>
        <CacheRoute exact path={`${match.url}/if-config`} cacheKey={`${match.url}/if-config`} component={Config} />
        <CacheRoute path="*" component={nomatch} />
      </CacheSwitch>
    );
  }
}
