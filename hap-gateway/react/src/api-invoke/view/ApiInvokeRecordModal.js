import React from 'react';
import { Button, DataSet, Modal, Table, Tooltip } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';
import ApiInvokeRecordDS from '../stores/ApiInvokeRecordDS';
import ApiInvokeRecordEditModal from './ApiInvokeRecordEditModal';

const { Column } = Table;
const modalKey = Modal.key();

export default ({ dataset, requestStatusdata }) => {
  function openApiInvokeRecordEditModal(record) {
    const apiInvokeRecordDS = new DataSet(ApiInvokeRecordDS);
    apiInvokeRecordDS.queryParameter = {
      recordId: record.get('recordId'),
    };
    apiInvokeRecordDS.query();
    Modal.open({
      key: modalKey,
      title: $l('hap.details'),
      drawer: true,
      children: (
        <ApiInvokeRecordEditModal dataset={apiInvokeRecordDS} />
      ),
      style: { width: 850 },
      okCancel: false,
      okText: $l('hap.close'),
    });
  }

  function responseStatus({ record, text }) {
    const color = record.get('responseStatus') === 'success' ? '#00AA00' : '#DD0000';
    return <span style={{ color }}>{text}</span>;
  }

  return (
    <Table
      dataSet={dataset}
      queryFieldsLimit={3}
    >
      <Column name="applicationCode" />
      <Column name="serverCode" />
      <Column name="clientId" width={240} />
      <Column name="invokeId" width={260} />
      <Column name="requestTime" />
      <Column name="responseStatus" renderer={responseStatus} width={100} />
      <Column
        name="edit"
        align="center"
        minwidth={100}
        width={100}
        header={$l('hap.action')}
        renderer={({ record }) => (
          <Tooltip title={$l('hap.details')}>
            <Button
              funcType="flat"
              icon="details"
              onClick={() => openApiInvokeRecordEditModal(record)}
            />
          </Tooltip>
        )}
      />
    </Table>
  );
};
