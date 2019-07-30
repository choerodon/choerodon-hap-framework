import React from 'react';
import {Button, DatePicker, Form, Modal, Password, Table, TextField, Tooltip} from 'choerodon-ui/pro';
import {$l} from '@choerodon/boot';
import moment from 'moment';

const {Column} = Table;
const modalKey = {
    passwordKey: Modal.key(),
    assignKey: Modal.key(),
};

export default ({passwordDS, userDS}) => {
    /**
     * 返回最大时间.
     * @param record 当前记录
     * @returns {moment.Moment} 设置的时间
     */
    function maxStartDate(record) {
        if (record) {
            const {endActiveDate} = record.data;
            if (endActiveDate) {
                return moment(endActiveDate);
            }
        }
    }

    /**
     * 返回最小时间.
     * @param record 当前记录
     * @returns {moment.Moment} 设置的时间
     */
    function minEndDate(record) {
        if (record) {
            const {startActiveDate} = record.data;
            if (startActiveDate) {
                return moment(startActiveDate);
            }
        }
    }

    async function handlePasswordModalOk() {
        if (await passwordDS.current.validate()) {
            const result = await passwordDS.submit();
            if (result && result.success) {
                return true;
            }
        }
        return false;
    }

    function openPasswordModal(user) {
        passwordDS.reset();
        passwordDS.create();
        passwordDS.current.set('userId', user.get('userId'));
        passwordDS.current.set('userName', user.get('userName'));
        Modal.open({
            key: modalKey.passwordKey,
            title: $l('user.updatepassword'),
            drawer: true,
            destroyOnClose: true,
            children: (
                <Form dataSet={passwordDS}>
                    <TextField name="userName" disabled/>
                    <Password name="password"/>
                    <Password name="passwordAgain"/>
                </Form>
            ),
            onOk: handlePasswordModalOk,
        });
    }

    return (
        <Table dataSet={userDS} buttons={['add', 'save', 'delete', 'export']} queryFieldsLimit={5}>
            <Column name="userName" editor sortable/>
            <Column name="employee" editor/>
            <Column name="employeeName"/>
            <Column name="email" editor/>
            <Column name="phone" editor/>
            <Column name="status" editor/>
            <Column name="startActiveDate" editor={record => (<DatePicker max={maxStartDate(record)}/>)}/>
            <Column name="endActiveDate" editor={record => (<DatePicker min={minEndDate(record)}/>)}/>
            <Column name="description" editor/>
            <Column
                header={$l('user.updatepassword')}
                lock="right"
                align="center"
                minWidth={75}
                renderer={({record}) => {
                    const userId = record.get('userId');
                    if (userId) {
                        return (
                            <Tooltip title={$l('user.updatepassword')}>
                                <Button
                                    funcType="flat"
                                    icon="password"
                                    color="blue"
                                    onClick={() => openPasswordModal(record)}
                                />
                            </Tooltip>
                        );
                    }
                }}
            />
        </Table>
    );
};
