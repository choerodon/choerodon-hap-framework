import React from 'react';
import { $l } from '@choerodon/boot';
import { Button, Modal, Table, Tooltip } from 'choerodon-ui/pro';
import CodeRuleModal from './CodeRuleModal';

const { Column } = Table;
const modalKey = Modal.key();

export default ({ headerDS, lineDS }) => {
  let isCancel;
  let created;

  /**
   * 确定编码规则编辑修改
   * 数据校验成功时保存
   */
  async function handleOnOkCodeRuleModal() {
    isCancel = false;
    if (await headerDS.current.validate()) {
      await headerDS.submit();
    } else {
      return false;
    }
  }

  function handleOnCancelCodeRuleModal() {
    isCancel = true;
  }

  /**
   * 关闭编码规则弹窗.
   *
   */
  function handleOnCloseCodeRuleModal() {
    if (isCancel) {
      // 新建时取消，移除dataSet记录
      if (created) {
        headerDS.remove(created);
      } else {
        // 修改时取消 重置当前记录数据
        headerDS.current.reset();
        lineDS.reset();
      }
    }
    // 重置新建记录标记
    created = null;
  }

  /**
   * 打开编码规则弹窗.
   * @param headerId 头Id
   * @param enableFlag 是否启用
   */
  function openCodeRuleModal(headerId, enableFlag) {
    if (!headerId) {
      created = headerDS.create();
    }
    // 如果是编辑状态 编码不可编辑
    const isEditDisabled = !!headerId;
    // 如果为启用状态 只可以进行查看 不能编辑数据
    const isEnableDisabled = (isEditDisabled) && (enableFlag === 'Y');
    // 如果为启用状态 编码规则行，不可被选中
    lineDS.selection = isEnableDisabled ? false : 'multiple';
    Modal.open({
      key: modalKey,
      title: headerId ? $l('hap.edit') : $l('hap.add'),
      drawer: true,
      destroyOnClose: true,
      okCancel: !isEnableDisabled,
      okText: !isEnableDisabled ? $l('hap.save') : $l('hap.close'),
      onOk: !isEnableDisabled ? handleOnOkCodeRuleModal : handleOnCancelCodeRuleModal,
      onCancel: handleOnCancelCodeRuleModal,
      afterClose: handleOnCloseCodeRuleModal,
      children: (
        <CodeRuleModal headerDS={headerDS} lineDS={lineDS} isEditDisabled={isEditDisabled} isEnableDisabled={isEnableDisabled} />
      ),
      style: {
        width: 1100,
      },
    });
  }

  const addBtn = (
    <Button
      icon="playlist_add"
      funcType="flat"
      color="blue"
      onClick={() => openCodeRuleModal(null, null)}
    >
      {$l('hap.add')}
    </Button>
  );

  /**
   * 渲染表格内容.
   */
  return (
    <Table buttons={[addBtn, 'save', 'delete']} dataSet={headerDS} queryFieldsLimit={2}>
      <Column name="ruleCode" />
      <Column name="ruleName" editor />
      <Column name="description" editor />
      <Column name="enableFlag" editor align="center" width={120} />
      <Column
        header={$l('hap.action')}
        align="center"
        width={120}
        renderer={({ record, text, name }) => {
          const title = record.get('enableFlag') === 'Y' ? $l('hap.view') : $l('hap.edit');
          const icon = record.get('enableFlag') === 'Y' ? 'visibility' : 'mode_edit';
          return (
            <Tooltip
              title={title}
            >
              <Button
                funcType="flat"
                icon={icon}
                onClick={() => openCodeRuleModal(record.get('headerId'), record.get('enableFlag'))}
              />
            </Tooltip>
          );
        }}
      />
    </Table>
  );
};
