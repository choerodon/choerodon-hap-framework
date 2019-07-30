import React, { PureComponent } from 'react';
import { ContentPro as Content } from '@choerodon/boot';
import { DataSet } from 'choerodon-ui/pro';
import EmailConfigModal from './view/EmailConfigModal';
import EmailConfigEditModal from './view/EmailConfigEditModal';
import EmailConfigDS from './stores/EmailConfigDS';
import EmailAccountDS from './stores/EmailAccountDS';
import PropertyListsDS from './stores/EmailPropertyDS';
import EmailWhiteList from './stores/EmailWhiteListDS';

export default class Index extends PureComponent {
  emailAccountDS = new DataSet(EmailAccountDS);

  propertyListsDS = new DataSet(PropertyListsDS);

  emailWhiteList = new DataSet(EmailWhiteList);

  headDS = new DataSet({
    ...EmailConfigDS,
    children: {
      emailAccounts: this.emailAccountDS,
      propertyLists: this.propertyListsDS,
      whiteLists: this.emailWhiteList,
    },
  });

  render() {
    return (
      <Content>
        <EmailConfigModal dataset={this.headDS} />
        <EmailConfigEditModal
          emailAccountDS={this.emailAccountDS}
          propertyListsDS={this.propertyListsDS}
          emailWhiteList={this.emailWhiteList}
        />
      </Content>
    );
  }
};
