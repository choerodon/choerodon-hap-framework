import React, { Component } from 'react';
import CacheRoute, { CacheSwitch } from 'react-router-cache-route';
import { asyncRouter, nomatch } from '@choerodon/boot';


const Execution = asyncRouter(() => import('./src/execution'));
const ExecutionAdmin = asyncRouter(() => import('./src/execution-admin'));
const TaskDetail = asyncRouter(() => import('./src/task-detail'));
const TaskExecution = asyncRouter(() => import('./src/task-execute'));

export default class RouteIndex extends Component {
  render() {
    const { match } = this.props;
    return (
      <CacheSwitch>
        <CacheRoute exact path={`${match.url}/execution`} cacheKey={`${match.url}/execution`} component={Execution} />
        <CacheRoute exact path={`${match.url}/execution-admin`} cacheKey={`${match.url}/execution-admin`} component={ExecutionAdmin} />
        <CacheRoute exact path={`${match.url}/task-detail`} cacheKey={`${match.url}/task-detail`} component={TaskDetail} />
        <CacheRoute exact path={`${match.url}/task-execute`} cacheKey={`${match.url}/task-execute`} component={TaskExecution} />
        <CacheRoute path="*" component={nomatch} />
      </CacheSwitch>
    );
  }
}
