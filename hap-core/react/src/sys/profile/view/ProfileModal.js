import React, { Component } from 'react';
import { Form, Lov, Select, Table, TextField } from 'choerodon-ui/pro';
import { $l } from '@choerodon/boot';
import '../index.scss';

const { Column } = Table;

export default ({ profileDS, profileValueDS, isNew }) => (

  <div className="core-sys-profile-modal">
    <Form labelWidth={60}>
      <TextField name="profileName" label={$l('profile.profilename')} dataSet={profileDS} disabled={!isNew} required />
      <TextField name="description" dataSet={profileDS} label={$l('profile.description')} required />
    </Form>
    <div className="wrap">
      <div className="label">{$l('profile.configurationitem')}</div>
      <div className="table">
        <Table dataSet={profileValueDS} buttons={['add', 'delete']}>
          <Column name="levelId" editor={<Select />} />
          <Column
            name="levelObj"
            renderer={({ record }) => {
              const levelId = record.get('levelId');
              const levelName = record.get('levelName');
              if (levelId === '10') {
                return <div>GLOBAL</div>;
              } else if (levelId === '20' || levelId === '30') {
                return levelName;
              }
            }
            }
            editor={(record) => {
              const levelId = record.get('levelId');
              if (levelId === '20' || levelId === '30') {
                return (
                  <Lov />
                );
              } else {
                return null;
              }
            }
            }
          />
          <Column name="profileValue" editor={<TextField />} />
        </Table>
      </div>
    </div>
  </div>
);
