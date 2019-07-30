import React from 'react';
import CacheRoute, { CacheSwitch } from 'react-router-cache-route';
import { asyncRouter } from '@choerodon/boot';

const AttachTest = asyncRouter(() => import('./src/attachment/attach-test'));
const CategoryManage = asyncRouter(() => import('./src/attachment/category-manage'));
const Attach = asyncRouter(() => import('./src/attachment/attach-file'));

export default ({ match }) => (
  <CacheSwitch>
    <CacheRoute exact path={`${match.url}/attach/attach-test`} cacheKey={`${match.url}/attach/attach-test`} component={AttachTest} />
    <CacheRoute exact path={`${match.url}/attach/category-manage`} cacheKey={`${match.url}/attach/category-manage`} component={CategoryManage} />
    <CacheRoute exact path={`${match.url}/attach/attach-file`} cacheKey={`${match.url}/attach/attach-file`} component={Attach} />
  </CacheSwitch>
);
