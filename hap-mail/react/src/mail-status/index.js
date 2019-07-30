import React, { PureComponent } from 'react';
import { withRouter } from 'react-router-dom';
import { Button, DataSet, Modal, Table, Tooltip } from 'choerodon-ui/pro';
import { $l, axiosPro as axios, ContentPro as Content } from '@choerodon/boot';
import MailStatusDataSet from './stores/MailStatusDataSet';
import MailReceiverDataSet from './stores/MailReceiverDataSet';
import MailContentModal from './view/MailContentModal';
import MailReceiverModal from './view/MailReceiverModal';

const { Column } = Table;
const modalKey = Modal.key();

function statusRender({ record, text }) {
  if (record.get('sendFlag') === 'F') {
    return <span style={{ color: '#c9302c' }}>{text}</span>;
  } else {
    return <span>{text}</span>;
  }
}

export default class Index extends PureComponent {
  created;

  mailReceiverDS = new DataSet(MailReceiverDataSet);

  mailStatusDS = new DataSet({
    ...MailStatusDataSet,
    children: {
      mailReceivers: this.mailReceiverDS,
    },
  });

  openContentModal = async () => {
    const messageId = this.mailStatusDS.current.get('messageId');
    const temp = await axios.post(`/sys/message/messageContent?messageId=${messageId}`, {
      messageId,
    });
    Modal.open({
      modalKey,
      title: $l('message.content'),
      drawer: true,
      destroyOnClose: true,
      children: (
        <MailContentModal>
          {temp}
        </MailContentModal>
      ),
      okCancel: false,
      okText: $l('hap.close'),
      style: {
        width: 700,
      },
    });
  };

  openErrorModal = async () => {
    const messageId = this.mailStatusDS.current.get('messageId');
    const temp = await axios.post(`/sys/message/error_mess?messageId=${messageId}`, {
      messageId,
    });
    Modal.open({
      modalKey,
      title: $l('hap.error'),
      drawer: true,
      destroyOnClose: true,
      children: (
        <MailContentModal>
          {temp}
        </MailContentModal>
      ),
      okCancel: false,
      okText: $l('hap.close'),
      style: {
        width: 700,
      },
    });
  };

  openReceiverModal(record) {
    Modal.open({
      modalKey,
      title: $l('message.receivers'),
      drawer: true,
      destroyOnClose: true,
      children: (
        <MailReceiverModal dataSet={this.mailReceiverDS} />
      ),
      okCancel: false,
      okText: $l('hap.close'),
      style: {
        width: 700,
      },
    });
  }

  render() {
    return (
      <Content>
        <Table
          dataSet={this.mailStatusDS}
          queryFieldsLimit={3}
        >
          <Column name="messageFrom" sortable />
          <Column name="subject" sortable />
          <Column name="creationDate" sortable />
          <Column name="lastUpdateDate" sortable />
          <Column name="sendFlag" sortable renderer={statusRender} />
          <Column
            name="content"
            renderer={({ record, text, name }) => (
              <Tooltip title={$l('hap.view')}>
                <Button funcType="flat" color="blue" icon="work_log" onClick={() => this.openContentModal()} />
              </Tooltip>
            )}
          />
          <Column
            header={$l('message.receivers')}
            renderer={({ record, text, name }) => (
              <Tooltip title={$l('hap.view')}>
                <Button funcType="flat" color="blue" icon="work_log" onClick={() => this.openReceiverModal(record)} />
              </Tooltip>
            )}
          />
          <Column
            header={$l('hap.error')}
            renderer={({ record, text, name }) => {
              if (record.get('sendFlag') === 'F') {
                return (
                  <Tooltip title={$l('hap.view')}>
                    <Button funcType="flat" color="blue" icon="work_log" onClick={() => this.openErrorModal()} />
                  </Tooltip>
                );
              }
            }
            }
          />
        </Table>
      </Content>
    );
  }
}
