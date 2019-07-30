import React, { Component } from 'react';
import CacheRoute, { CacheSwitch } from 'react-router-cache-route';
import { asyncRouter, nomatch } from '@choerodon/boot';

const JOBDETAIL = asyncRouter(() => import('./src/job/job-detail'));
const JOBRUNNINGINFO = asyncRouter(() => import('./src/job/job-running-info'));


export default class RouteIndex extends Component {
  render() {
    const { match } = this.props;
    return (
      <CacheSwitch>
        <CacheRoute exact path={`${match.url}/job/job-detail`} cacheKey={`${match.url}/job/job-detail`} component={JOBDETAIL} />
        <CacheRoute exact path={`${match.url}/job/job-running-info`} cacheKey={`${match.url}/job/job-running-info`} component={JOBRUNNINGINFO} />
        <CacheRoute path="*" component={nomatch} />
      </CacheSwitch>
    );
  }
}
