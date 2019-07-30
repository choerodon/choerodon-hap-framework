import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'positionId',
  autoQuery: true,
  pageSize: 20,
  name: 'Position',
  fields: [
    { name: 'positionCode', type: 'string', label: $l('position.positioncode') },
    { name: 'name', type: 'intl', label: $l('position.name') },
    { name: 'description', type: 'intl', label: $l('position.description') },
    { name: 'hrorgunit', type: 'object', textField: 'name', label: $l('hrorgunit.name'), lovCode: 'LOV_UNIT' },
    { name: 'unitId', bind: 'hrorgunit.unitId', type: 'number', label: $l('hrorgunit.unitcode') },
    { name: 'unitName', require: true, bind: 'hrorgunit.name', type: 'string', label: $l('hrorgunit.name') },
    { name: 'position', type: 'object', textField: 'name', label: $l('position.parentpositionname'), lovCode: 'LOV_POSITION' },
    { name: 'parentPositionId', bind: 'position.positionId', type: 'number', label: $l('parentPositionId', '上级岗位ID') },
    { name: 'parentPositionName', bind: 'position.name', type: 'string', label: $l('position.parentpositionname') },
  ],
  queryFields: [
    { name: 'positionCode', type: 'string', label: $l('position.positioncode') },
    { name: 'name', type: 'string', label: $l('position.name') },
  ],
};
