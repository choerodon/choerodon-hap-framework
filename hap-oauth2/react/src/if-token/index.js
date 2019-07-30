import React, { PureComponent } from 'react';
import { DataSet, Table, TextField, Button, Tooltip, Modal } from 'choerodon-ui/pro';
import { ContentPro as Content, axiosPro as axios, $l } from '@choerodon/boot';
import TokenDataSet from './stores/TokenDataSet';

const { Column } = Table;
const modalKeys = Modal.key();

function editorRenderer(record) {
  return record.status === 'add' ? <TextField /> : null;
}

export default class Index extends PureComponent {
  tokenDS = new DataSet(TokenDataSet);

  revokeAccess() {
    Modal.confirm({
      modalKeys,
      title: 'Confirm',
      children: (
        <div>
          <p>{$l('tokenlogs.cancelauthorization')}</p>
        </div>
      ),
    }).then((button) => {
      const token = this.tokenDS.current.get('token');
      axios.post('/sys/token/logs/remove', {
        token,
      }).then((res) => {
        this.tokenDS.query();
      });
    });
  }

  tokenStatus({ record, text }) {
    if (record.get('tokenStatus') === 'valid') {
      return <span style={{ color: 'green' }}>{text}</span>;
    } else if (record.get('tokenStatus') === 'invalid') {
      return <span style={{ color: '#5d6d7c' }}>{text}</span>;
    }
  }

  render() {
    return (
      <Content>
        <Table
          dataSet={this.tokenDS}
          queryFieldsLimit={4}
        >
          <Column name="userId" align="left" sortable />
          <Column name="clientId" sortable />
          <Column
            sortable
            name="token"
            align="right"
            style={{ direction: 'rtl' }}
            renderer={({ record, text, name }) => (
              <span title={text}>{text}</span>
            )}
          />
          <Column name="tokenAccessTime" sortable />
          <Column name="tokenExpiresTime" sortable />
          <Column name="tokenAccessType" sortable />
          <Column
            name="tokenStatus"
            renderer={this.tokenStatus}
          />
          <Column
            header={$l('hap.action')}
            renderer={({ record, text, name }) => {
              if (record.get('tokenStatus') === 'valid') {
                return (
                  <Tooltip title={$l('tokenlogs.tokenaccessrevoke')}>
                    <Button funcType="flat" icon="delete" onClick={() => this.revokeAccess()} />
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
