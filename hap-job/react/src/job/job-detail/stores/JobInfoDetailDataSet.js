import { $l } from '@choerodon/boot';

function requiredOne(value, name, record) {
  if (!record.get('cronExpression') && !record.get('repeatInterval')) {
    return '两者唯一';
  }
  return true;
}

export default {
  name: 'JobInfoDetail',
  autoQuery: true,
  pageSize: 20,
  fields: [
    { name: 'schedName', type: 'string', label: 'schedName' },
    { name: 'jobName', type: 'string', label: $l('jobdetaildto.jobname'), required: true, unique: 'group' },
    { name: 'jobGroup', type: 'string', label: $l('jobdetaildto.jobgroup'), defaultValue: 'DEFAULT', required: true, unique: 'group' },
    { name: 'description', type: 'string', label: $l('jobdetaildto.description') },
    {
      name: 'jobClassName',
      type: 'string',
      label: $l('jobdetaildto.jobclassname'),
      pattern: '[a-zA-Z]+[0-9a-zA-Z_]*(\\.[a-zA-Z]+[0-9a-zA-Z_]*)*\\.[a-zA-Z]+[0-9a-zA-Z_]*',
      required: true,
    },
    { name: 'isDurable', type: 'string', label: $l('is Durable') },
    { name: 'isNonconcurrent', type: 'string', label: $l('is Nonconcurrent') },
    { name: 'isUpdateData', type: 'string', label: $l('is Update Data') },
    { name: 'requestsRecovery', type: 'string', label: $l('requests Recovery') },
    { name: 'startTime', type: 'dateTime', label: $l('triggerdto.starttime') },
    { name: 'endTime', type: 'dateTime', label: $l('triggerdto.endtime') },
    { name: 'triggerType', type: 'string', label: $l('trigger Type') },
    { name: 'cronExpression', type: 'string', label: $l('crontriggerdto.cronexpression'), defaultValue: '0 * * * * ?', validator: requiredOne },
    { name: 'repeatCount', type: 'int', label: $l('simpletriggerdto.repeatcount'), defaultValue: '0' },
    { name: 'repeatInterval', type: 'bigint', label: $l('simpletriggerdto.repeatinterval'), defaultValue: '60', validator: requiredOne },
    { name: 'jobResult', type: 'string', label: $l('job Result') },
    { name: 'jobStatus', type: 'string', label: $l('jobrunninginfodto.jobstatus') },
    { name: 'jobStatusMessage', type: 'string', label: $l('job Status Message') },
    { name: 'triggerName', type: 'string', label: $l('trigger Name') },
    { name: 'triggerGroup', type: 'string', label: $l('trigger Group') },
    { name: 'triggerPriority', type: 'int', label: $l('jobcreatedto.priority'), defaultValue: '5' },
    { name: 'previousFireTime', type: 'dateTime', label: $l('jobrunninginfodto.previousfiretime') },
    { name: 'fireTime', type: 'dateTime', label: $l('jobrunninginfodto.firetime') },
    { name: 'nextFireTime', type: 'dateTime', label: $l('jobrunninginfodto.nextfiretime') },
    { name: 'refireCount', type: 'bigint', label: $l('refire Count') },
    { name: 'fireInstanceId', type: 'string', label: $l('fire Instance Id') },
    { name: 'schedulerInstanceId', type: 'string', label: $l('scheduler Instance Id') },
    { name: 'scheduledFireTime', type: 'string', label: $l('jobrunninginfodto.scheduledfiretime') },
    { name: 'runningState', type: 'string', label: $l('jobrunninginfodto.jobstatus') },
    { name: 'jobDatas', type: 'object', label: $l('jobDatas') },
    { name: 'empty', type: 'object' },
  ],
  queryFields: [
    { name: 'jobName', type: 'string', label: $l('jobdetaildto.jobname') },
    { name: 'jobGroup', type: 'string', label: $l('jobdetaildto.jobgroup') },
  ],
};
