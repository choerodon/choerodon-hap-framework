/**
 * 根据configCode从sysConfigDS中获取index
 *
 * @param {DataSet} ds
 * @param {string} fieldName
 * @param {string} fieldValue
 * @returns {number} 索引值
 */
export function findIndexFromDataSet(ds, fieldName, fieldValue) {
  return ds.findIndex(r => r.get(fieldName) === fieldValue);
}

export default function getDsConfig(userInfo) {
  const { timeZone, locale } = userInfo;
  const isTabNav = userInfo.nav || 'Y';
  return {
    queryUrl: '/sys/preferences/queryPreferences',
    autoQuery: true,
    fields: [
      { name: 'preferences', type: 'string' },
      {
        name: 'preferencesValue',
        dynamicProps: ({ dataSet, record, name }) => {
          let props = {};
          const preferences = record.get('preferences');
          switch (preferences) {
            case 'timeZone':
              props = { type: 'string', lookupCode: 'SYS.TIME_ZONE' };
              break;
            case 'locale':
              props = { type: 'string', textField: 'description', valueField: 'langCode', lookupUrl: '/common/language/' };
              break;
            case 'nav':
              props = { type: 'boolean', trueValue: 'Y', falseValue: 'N', defaultValue: 'Y' };
              break;
            default:
              break;
          }
          return props;
        },
      },
    ],
    events: {
      load: ({ dataSet }) => {
        const dataList = dataSet.data;
        // 如果用户没有保存过首选项 设置为系统默认值
        if (dataList && !dataList.length) {
          dataSet.create({ preferences: 'timeZone', preferencesValue: timeZone });
          dataSet.create({ preferences: 'locale', preferencesValue: locale });
          dataSet.create({ preferences: 'nav', preferencesValue: isTabNav });
        }
      },
    },
  };
}
