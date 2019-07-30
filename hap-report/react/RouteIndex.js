import React, { Component } from 'react';
import CacheRoute, { CacheSwitch } from 'react-router-cache-route';
import { asyncRouter, nomatch, createRouteWrapper } from '@choerodon/boot';

const Definition = asyncRouter(() => import('./src/definition'));
const PreviewModal = asyncRouter(() => import('./src/definition/view/PreviewModal'));
const Preview = createRouteWrapper('/hap-report/preview', PreviewModal);

export default class RouteIndex extends Component {
  render() {
    const { match } = this.props;
    return (
      <CacheSwitch>
        <CacheRoute exact path={`${match.url}/definition`} cacheKey={`${match.url}/definition`} component={Definition} />
        <CacheRoute exact path={`${match.url}/preview/:code`} cacheKey={`${match.url}/preview`} component={Preview} />
        <CacheRoute path="*" component={nomatch} />
      </CacheSwitch>
    );
  }
}
