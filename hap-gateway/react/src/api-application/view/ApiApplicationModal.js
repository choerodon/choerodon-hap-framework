import React from 'react';
import { Button, Modal, Table, Tooltip } from 'choerodon-ui/pro';
import { $l, axiosPro as axios } from '@choerodon/boot';
import ApiAppliEditModal from './ApiAppliEditModal';

const { Column } = Table;
const modalKey = Modal.key();

export default ({ apiServiceDS, apiApplicationDS }) => {
  let isCancel = true;
  let created = null;

  async function saveApiApplication() {
    isCancel = false;
    if (await apiApplicationDS.current.validate()) {
      const scope = apiServiceDS.data.map(record => record.get('code')).join(',');
      apiApplicationDS.current.set('client.scope', scope);
      apiApplicationDS.submit();
    } else {
      return false;
    }
  }

  function onCancelClose() {
    isCancel = true;
  }

  function onAfterClose() {
    if (isCancel) {
      if (created) {
        apiApplicationDS.remove(created);
      } else {
        apiApplicationDS.current.reset();
      }
    }
    created = null;
    isCancel = true;
  }

  function openApiApplicationModal(isEdit) {
    if (!isEdit) {
      created = apiApplicationDS.create();
      axios.post('/sys/application/app/generatorClientInfo')
        .then((obj) => {
          created.set('client.clientSecret', obj.clientSecret);
          created.set('client.clientId', obj.clientId);
        });
      apiApplicationDS.unshift(created);
    }
    Modal.open({
      key: modalKey,
      title: isEdit ? $l('hap.edit') : $l('hap.new'),
      drawer: true,
      children: (
        <ApiAppliEditModal apiApplicationDS={apiApplicationDS} apiServiceDS={apiServiceDS} isEdit={isEdit} />
      ),
      onOk: saveApiApplication,
      onCancel: onCancelClose,
      afterClose: onAfterClose,
      style: { width: 900 },
      okText: isEdit ? $l('hap.save') : $l('hap.new'),
    });
  }

  const addBtn = (
    <Button
      key="copy"
      funcType="flat"
      color="blue"
      icon="add"
      onClick={() => openApiApplicationModal(false)}
    >
      {$l('hap.new')}
    </Button>
  );

  /**
   * 渲染表格内容
   */
  return (
    <Table
      buttons={[addBtn, 'delete']}
      dataSet={apiApplicationDS}
      queryFieldsLimit={2}
    >
      <Column name="code" />
      <Column name="name" />
      <Column name="clientId" width={300} />
      <Column name="clientSecret" width={300} />
      <Column
        name="edit"
        align="center"
        width={100}
        header={$l('hap.edit')}
        renderer={() => (
          <Tooltip title={$l('hap.edit')}>
            <Button
              funcType="flat"
              icon="mode_edit"
              onClick={() => openApiApplicationModal(true)}
            />
          </Tooltip>
        )}
      />
    </Table>
  );
};
