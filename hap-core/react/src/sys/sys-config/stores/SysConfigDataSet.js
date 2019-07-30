export default {
  autoCreate: true,
  autoQuery: true,
  primaryKey: 'configId',
  name: 'SysConfig',
  paging: false, // <-- 不分页
  fields: [
    { name: 'configCode', type: 'string' },
    {
      name: 'configValue',
      dynamicProps: ({ dataSet, record, name }) => {
        let props = {};
        const configCode = record.get('configCode');
        switch (configCode) {
          case 'CAPTCHA':
            props = { type: 'string', lookupCode: 'SYS.CAPTCHA_POLICY' };
            break;
          case 'PASSWORD_COMPLEXITY':
            props = { type: 'string', lookupCode: 'SYS.PASSWORD_COMPLEXITY' };
            break;
          default:
            break;
        }
        return props;
      },
    },
  ],
};
