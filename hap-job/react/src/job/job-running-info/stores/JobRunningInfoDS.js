import { $l } from '@choerodon/boot';

export default {
  primaryKey: 'jobRunningInfoId',
  name: 'JobRunningInfo',
  pageSize: 20,
  autoQuery: true,
  selection: false,
  fields: [
    { name: 'jobRunningInfoId', type: 'number', label: 'JobId' },
    { name: 'jobName', type: 'string', label: $l('jobdetaildto.jobname') },
    { name: 'jobGroup', type: 'string', label: $l('jobdetaildto.jobgroup') },
    { name: 'ipAddress', type: 'string', label: $l('jobdetaildto.executeip') },
    { name: 'jobStatus', type: 'string', label: $l('jobrunninginfodto.jobstatus') },
    { name: 'executionSummary', type: 'string', label: $l('jobrunninginfodto.executionsummary') },
    { name: 'previousFireTime', type: 'string', label: $l('jobrunninginfodto.previousfiretime') },
    { name: 'scheduledFireTime', type: 'string', label: $l('jobrunninginfodto.scheduledfiretime') },
    { name: 'nextFireTime', type: 'string', label: $l('jobrunninginfodto.nextfiretime') },
    { name: 'fireTime', type: 'string', label: $l('jobrunninginfodto.firetime') },
  ],
  queryFields: [
    { name: 'jobName', type: 'string', label: $l('jobdetaildto.jobname') },
    { name: 'jobGroup', type: 'string', label: $l('jobdetaildto.jobgroup') },
    { name: 'jobStatus', type: 'string', label: $l('jobrunninginfodto.jobstatus') },
  ],
};
