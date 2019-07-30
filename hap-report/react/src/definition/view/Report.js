import React, { PureComponent } from 'react';
import { withRouter } from 'react-router-dom';
import { Button, Modal, Table, Tooltip } from 'choerodon-ui/pro';
import { $l, openTab } from '@choerodon/boot';
import ParameterModal from './ParameterModal.js';
import '../index.scss';

const { Column } = Table;
const parameterModalKey = Modal.key();
@withRouter
export default class Report extends PureComponent {
  constructor(props) {
    super(props);
    this.reportDS = this.props.reportDS;
    this.parameterDS = this.props.parameterDS;
  }

  /**
   * 打开报表参数编辑窗口.
   * @param report 当前行记录
   */
  openParameterModal(report) {
    const { reportId, reportCode, sourceType } = report.data;
    this.parameterDS.queryParameter = {
      code: 'REPORT',
      targetId: reportId,
    };
    this.parameterDS.getField('code').set('defaultValue', 'REPORT');
    this.parameterDS.getField('targetId').set('defaultValue', reportId);
    // 如果是内置数据源或jdbc直连数据源 加载tableFieldName数据源
    if (sourceType === 'buildin' || sourceType === 'jdbc') {
      const lookupUrl = `/sys/report/getReportFileParams?reportCode=${reportCode}`;
      this.parameterDS.getField('tableFieldName').set('lookupUrl', lookupUrl);
      this.parameterDS.getField('tableFieldName').set('textField', 'meaning');
      this.parameterDS.getField('tableFieldName').set('valueField', 'value');
    }
    Modal.open({
      parameterModalKey,
      title: $l('hap.edit'),
      drawer: true,
      okText: $l('hap.close'),
      okCancel: false,
      children: (
        <ParameterModal parameterDS={this.parameterDS} reportData={report.data} />
      ),
      style: {
        width: 1000,
      },
    });
    this.parameterDS.query();
  }

  /**
   * 打开报表预览窗口.
   * @param report 当前行记录
   */
  openPreviewModal(report) {
    this.props.history.push(`/hap-report/preview/${report.get('reportCode')}`);
  }

  /**
   * 打开报表设计窗口.
   * @param report 当前行记录
   */
  openDesignModal(report) {
    const title = $l('report.design');
    openTab('', title, `/ureport/designer?_u=database:${report.get('fileName')}`);
  }

  /**
   * 复制url以用于资源管理挂载报表.
   * @param reportUrl 报表url
   */
  copyText(reportUrl) {
    let transfer = document.getElementById('J_CopyTransfer');
    if (!transfer) {
      transfer = document.createElement('textarea');
      transfer.id = 'J_CopyTransfer';
      transfer.style.position = 'absolute';
      transfer.style.left = '-9999px';
      transfer.style.top = '-9999px';
      document.body.appendChild(transfer);
    }
    transfer.value = reportUrl;
    transfer.focus();
    transfer.select();
    document.execCommand('Copy', false, null);
  }

  render() {
    return (
      <Table
        buttons={['add', 'save', 'delete']}
        dataSet={this.reportDS}
        queryFieldsLimit={2}
      >
        <Column name="reportCode" editor sortable />
        <Column name="reportFile" editor />
        <Column name="name" editor />
        <Column name="description" editor />
        <Column name="defaultQuery" editor width={120} />
        <Column
          header={$l('report.url')}
          width={280}
          help={$l('report.copyurl')}
          renderer={({ record, text, name }) => {
            if (record.get('reportId')) {
              const code = record.get('reportCode');
              const reportUrl = `hap-report/preview/${code}`;
              return (
                <Tooltip title={$l('report.copytext')}>
                  <span
                    className="report-url"
                    onClick={() => this.copyText(reportUrl)}
                  >
                    {reportUrl}
                  </span>
                </Tooltip>
              );
            }
            return null;
          }}
        />
        <Column
          header={$l('report.parameter')}
          align="center"
          lock="right"
          width={120}
          renderer={({ record, text, name }) => {
            if (record.get('reportId')) {
              return (
                <Tooltip title={$l('hap.edit')}>
                  <Button
                    funcType="flat"
                    icon="mode_edit"
                    onClick={() => this.openParameterModal(record)}
                  />
                </Tooltip>
              );
            }
            return null;
          }}
        />
        <Column
          header={$l('report.design')}
          align="center"
          lock="right"
          width={120}
          renderer={({ record, text, name }) => {
            if (record.get('reportId')) {
              return (
                <Tooltip title={$l('report.design')}>
                  <Button
                    funcType="flat"
                    icon="mode_edit"
                    onClick={() => this.openDesignModal(record)}
                  />
                </Tooltip>
              );
            }
            return null;
          }}
        />
        <Column
          header={$l('report.preview')}
          align="center"
          lock="right"
          width={100}
          renderer={({ record, text, name }) => {
            if (record.get('reportId')) {
              return (
                <Tooltip title={$l('report.preview')}>
                  <Button
                    funcType="flat"
                    icon="mode_edit"
                    onClick={() => this.openPreviewModal(record)}
                  />
                </Tooltip>
              );
            }
            return null;
          }}
        />
      </Table>
    );
  }
}
