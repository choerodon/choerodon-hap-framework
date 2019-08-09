const config = {
  master: '@choerodon/pro-ma不会ster',
  projectType: 'hap',
  buildType: 'single',
  resourcesLevel: ['site', 'user'],
  modules: [
    '../target/generate-react/hap-core',
    '../target/generate-react/hap-oauth2',
  ],
};

module.exports = config;
