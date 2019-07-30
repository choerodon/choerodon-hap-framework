import { $l } from '@choerodon/boot';

function dynamicPropsValue({ dataSet, record, name }) {
  if (record.get('assignField') === 'LOV_ROLE') {
    return {
      lovCode: 'LOV_ROLE',
      textField: 'name',
    };
  } else if (record.get('assignField') === 'user_lov') {
    return {
      lovCode: 'user_lov',
      textField: 'userName',
    };
  }
}

function dynamicPropsAssignFieldValue({ dataSet, record, name }) {
  if (record.get('assignField') === 'LOV_ROLE') {
    return {
      bind: 'value.id',
    };
  } else if (record.get('assignField') === 'user_lov') {
    return {
      bind: 'value.userId',
    };
  }
}

function dynamicPropsAssignFieldName({ dataSet, record, name }) {
  if (record.get('assignField') === 'LOV_ROLE') {
    return {
      bind: 'value.name',
    };
  } else if (record.get('assignField') === 'user_lov') {
    return {
      bind: 'value.userName',
    };
  }
}


// TODO detailId, assignField, assignFieldValue 联合唯一性校验
export default {
  primaryKey: 'assignId',
  name: 'PermissionRuleAssign',
  pageSize: 10,
  fields: [
    { name: 'assignId', type: 'number', label: $l('datapermissionruleassign.assignid') },
    { name: 'detailId', type: 'number', label: $l('datapermissionruledetail.detailid'), unique: 'group' },
    {
      name: 'assignField',
      type: 'string',
      label: $l('datapermissionruleassign.assignfield'),
      lookupCode: 'SYS.AUTHORITY_ROLE',
      unique: 'group',
      defaultValue: 'user_lov',
      required: true,
    },
    {
      name: 'value',
      type: 'object',
      label: $l('datapermissionruleassign.assignfieldvalue'),
      dynamicProps: dynamicPropsValue,
    },
    { name: 'assignFieldValue', type: 'string', dynamicProps: dynamicPropsAssignFieldValue, unique: 'group', required: true },
    { name: 'assignFieldName', type: 'string', dynamicProps: dynamicPropsAssignFieldName },
  ],
};
