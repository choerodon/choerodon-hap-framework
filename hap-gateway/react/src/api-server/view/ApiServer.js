import React from 'react';
import { axiosPro as axios, $l } from '@choerodon/boot';
import { Button, Modal, Table, Tooltip } from 'choerodon-ui/pro';
import ApiServerModal from './ApiServerModal';
import ImportModal from './ImportModal';
import '../index.scss';

const { Column } = Table;
const modalKey = {
  serverKey: Modal.key(),
  importKey: Modal.key(),
};

export default ({ apiInterfaceDataSet, apiServerDataSet }) => {
  let created;
  let importUrl;
  let importModal;

  /**
   * 弹窗取消时去掉 created
   */
  async function handleOnCancelApiServerModal() {
    if (created) {
      apiServerDataSet.remove(created);
      created = null;
    } else {
      apiServerDataSet.current.reset();
    }
  }

  /**
   * 验证数据是否有效,是则提交
   * @returns {Promise<boolean>} 失败返回 false
   */
  const handleOnOkApiServerModal = async () => {
    if (await apiServerDataSet.current.validate()) {
      await apiServerDataSet.submit();
      await apiServerDataSet.query();
      return true;
    } else {
      return false;
    }
  };

  /**
   * 打开 ApiServer 弹窗
   * @param isNew 是否为新建,默认为编辑
   * @param importFlag 是否为导入
   */
  function openApiServerModal(isNew = false, importFlag = false) {
    if (isNew && !importFlag) {
      created = apiServerDataSet.create();
    }

    Modal.open({
      key: modalKey.serverKey,
      title: isNew ? $l('hap.new') : $l('hap.edit'),
      drawer: true,
      destroyOnClose: true,
      okText: $l('hap.save'),
      onCancel: handleOnCancelApiServerModal,
      onOk: handleOnOkApiServerModal,
      children: (
        <ApiServerModal isNew={isNew} apiServer={apiServerDataSet} apiInterface={apiInterfaceDataSet} />
      ),
      style: {
        width: 900,
      },
    });
  }

  /**
   * 改变导入的 URL
   * @param url
   */
  function changeImportUrl(url) {
    importUrl = url;
  }

  /**
   *
   * @returns {boolean}
   */
  async function importServer() {
    const url = '/sys/gateway/server/import';

    if (importUrl) {
      const res = await axios.post(url, {
        importUrl,
      });
      if (res.success === false && res.rows.length < 1) {
        Modal.error($l('hap.error', '错误'));
        return true;
      }
      created = apiServerDataSet.create(res.rows[0]);
      res.rows[0].interfaces.forEach((value) => {
        apiInterfaceDataSet.create(value);
      });
      importModal.close();
      openApiServerModal(true, true);
    }

    return false;
  }

  /**
   * 打开 import 弹窗
   */
  function openImportModal() {
    importUrl = null;
    importModal = Modal.open({
      key: modalKey.importKey,
      title: $l('server.import'),
      drawer: true,
      okText: $l('server.import'),
      onOk: importServer,
      children: (
        <ImportModal onChange={changeImportUrl} />
      ),
      style: {
        width: 900,
      },
    });
  }

  const addBtn = (
    <Button
      icon="playlist_add"
      funcType="flat"
      color="blue"
      onClick={() => openApiServerModal(true)}
    >
      {$l('hap.new')}
    </Button>
  );

  const importBtn = (
    <Button
      icon="playlist_add"
      funcType="flat"
      color="blue"
      onClick={() => openImportModal()}
    >
      {$l('server.import')}
    </Button>
  );

  return (
    <Table
      buttons={[addBtn, importBtn, 'delete']}
      dataSet={apiServerDataSet}
      queryFieldsLimit={4}
    >
      <Column name="code" sortable width={225} />
      <Column name="name" width={225} />
      <Column name="domainUrl" />
      <Column name="serviceType" width={100} />
      <Column name="enableFlag" align="center" width={100} />
      <Column
        header={$l('hap.action')}
        align="center"
        width={100}
        lock="right"
        renderer={() => (
          <Tooltip title={$l('hap.edit')}>
            <Button
              funcType="flat"
              icon="mode_edit"
              onClick={() => openApiServerModal()}
            />
          </Tooltip>
        )}
      />
    </Table>
  );
};
