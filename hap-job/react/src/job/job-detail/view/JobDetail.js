import React, {PureComponent} from 'react';
import {observer} from 'mobx-react';
import {action, observable, runInAction} from 'mobx';
import {Button, Dropdown, Icon, Menu, Modal, Table} from 'choerodon-ui/pro';
import {$l, axiosPro as axios} from '@choerodon/boot';
import JobModal from './JobModal';

const {Column} = Table;
const {Item} = Menu;
const key = Modal.key();

const TITLE_MAP = {
    corn_view: $l('job.jobdetail'),
    simple_view: $l('job.jobdetail'),
    corn_new: $l('job.newcronjob'),
    simple_new: $l('job.newsimplejob'),
};
const CLASS_NAME_MAP = {
    green: 'job-green-common',
    red: 'job-red-common',
    yellow: 'job-yellow-common',
    ERROR: 'job-red-common',
    NORMAL: 'job-green-common',
    COMPLETE: 'job-black-common',
    PAUSED: 'job-yellow-common',
};

@observer
export default class JobDetail extends PureComponent {
    created;

    jobModal;

    @observable btnColor;

    constructor(props) {
        super(props);

        this.state = {
            visible: false,
        };

        runInAction(() => {
            this.btnColor = 'green';
        });
        axios.get('/job/scheduler/info')
            .then(action((res) => {
                if (res.success) {
                    if (res.rows[0].inStandbyMode) {
                        this.btnColor = 'yellow';
                    }
                }
            }));
    }

    handleToggleDropdown = () => {
        const {visible} = this.state;
        this.setState({visible});
    };

    handleMenuClick = () => {
        this.setState({visible: false});
    };

    /**
     * 关闭弹窗
     */
    handleJobModalOnClose = async () => {
        const {jobInfo} = this.props;
        if (this.created) {
            jobInfo.remove(this.created);
            this.created = null;
        }
        await jobInfo.query();
    };

    handleJobModalOnOk = async () => {
        const {jobInfo} = this.props;
        if (await jobInfo.current.validate()) {
            const response = await jobInfo.submit();
            return (response && response.success);
        }
        return false;
    };

    handleCloseModal = () => {
        this.jobModal.close();
    };

    renderCloseButton() {
        return (<Button onClick={this.handleCloseModal} color="blue">{$l('hap.close')}</Button>);
    }

    /**
     * 打开新建或编辑任务的弹窗
     */
    openJobModel(jobType, operation) {
        const titleType = `${jobType}_${operation}`;
        const {jobInfo, jobData} = this.props;

        if (operation === 'new') {
            this.created = jobInfo.create();
            if (jobType === 'simple') {
                jobInfo.current.set('triggerType', 'SIMPLE');
            }
            if (jobType === 'cron') {
                jobInfo.current.set('triggerType', 'CRON');
            }

            jobData.create({
                name: 'job_internal_email_template',
                value: '',
            });
            jobData.create({
                name: 'job_internal_emailAddress',
                value: '',
            });
            jobData.create({
                name: 'job_internal_notification',
                value: 'false',
            });
        }

        this.jobModal = Modal.open({
            key,
            title: TITLE_MAP[titleType],
            drawer: true,
            okText: $l('hap.save'),
            footer: operation === 'view' ? this.renderCloseButton() : undefined,
            style: {width: 900},
            destroyOnClose: true,
            afterClose: this.handleJobModalOnClose,
            onOk: this.handleJobModalOnOk,
            children: (
                <JobModal operation={operation} job={jobType} jobInfo={jobInfo} jobData={jobData}/>
            ),
        });
    }

    /**
     * 调度器的启动和暂停
     */
    async handleStandbySeched() {
        await axios.get('/job/scheduler/standby')
            .then(action((res) => {
                if (res.success) {
                    this.btnColor = 'yellow';
                } else {
                    Modal.error($l(res.message));
                }
            }));
    }

    async handleStartSeched() {
        await axios.get('/job/scheduler/start')
            .then(action((res) => {
                if (res.success) {
                    this.btnColor = 'green';
                } else {
                    Modal.error($l(res.message));
                }
            }));
    }

    /**
     * 继续暂停的任务
     * @returns {Promise<void>}
     */
    async handleResumeJob() {
        const {selected} = this.props.jobInfo;
        if (selected.length > 0) {
            selected.forEach((value) => {
                if (value.get('runningState') !== 'NORMAL') {
                    value.set('runningState', 'NORMAL');
                }
            });
            this.props.jobInfo.submit(true);
        } else {
            Modal.warning($l('hap.tip.selectrows'));
        }
    }

    /**
     * 暂停选择的任务
     */
    handlePauseJob() {
        const {selected} = this.props.jobInfo;
        if (selected.length > 0) {
            selected.forEach((value) => {
                if (value.get('runningState') !== 'PAUSED') {
                    value.set('runningState', 'PAUSED');
                }
            });
            this.props.jobInfo.submit(true);
        } else {
            Modal.warning($l('hap.tip.selectrows'));
        }
    }

    handleDeleteJob() {
        const {selected} = this.props.jobInfo;
        if (selected.length > 0) {
            this.props.jobInfo.delete(selected);
        } else {
            Modal.warning($l('hap.tip.selectrows'));
        }
    }

    schedulerMenu = (
        <Menu onClick={this.handleMenuClick}>
            <Item>
                <a onClick={() => this.handleStartSeched()} className={CLASS_NAME_MAP.green}><Icon
                    type="play_arrow"/>{$l('hap.resume')}</a>
            </Item>
            <Item>
                <a onClick={() => this.handleStandbySeched()} className={CLASS_NAME_MAP.yellow}><Icon
                    type="pause"/>{$l('hap.pause')}</a>
            </Item>
        </Menu>
    );

    addJobMenu = (
        <Menu onClick={this.handleMenuClick}>
            <Item>
                <a onClick={() => this.openJobModel('simple', 'new')}>{$l('job.newsimplejob')}</a>
            </Item>
            <Item>
                <a onClick={() => this.openJobModel('cron', 'new')}>{$l('job.newcronjob')}</a>
            </Item>
        </Menu>
    );

    newJobDropDown = (
        <Dropdown overlay={this.addJobMenu}>
            <Button funcType="flat" color="blue" icon="add">{$l('job.newjob')}<Icon type="arrow_drop_down"
                                                                                    onClick={this.handleToggleDropdown}/></Button>
        </Dropdown>
    );

    jobMenu = (
        <Menu onClick={this.handleMenuClick}>
            <Item>
                <a onClick={() => this.handleResumeJob()} className={CLASS_NAME_MAP.green}><Icon
                    type="play_arrow"/>{$l('hap.resume')}</a>
            </Item>
            <Item>
                <a onClick={() => this.handlePauseJob()} className={CLASS_NAME_MAP.yellow}><Icon
                    type="pause"/>{$l('hap.pause')}</a>
            </Item>
            <Item>
                <a onClick={() => this.handleDeleteJob()} className={CLASS_NAME_MAP.red}><Icon
                    type="delete"/>{$l('hap.delete')}</a>
            </Item>
        </Menu>
    );

    jobDropDown = (
        <Dropdown overlay={this.jobMenu}>
            <Button
                funcType="flat"
                color="blue"
                icon="operation_subtask"
            >
                {$l('hap.action')}
                <Icon
                    type="arrow_drop_down"
                    onClick={this.handleToggleDropdown}
                />
            </Button>
        </Dropdown>
    );

    /**
     * 渲染内容
     */
    render() {
        const schedulerDropDown = (
            <Dropdown overlay={this.schedulerMenu}>
                <Button
                    funcType="flat"
                    className={CLASS_NAME_MAP[this.btnColor]}
                    icon="settings_applications"
                >
                    {$l('job.scheduler')}
                    <Icon type="arrow_drop_down" onClick={this.handleToggleDropdown}/>
                </Button>
            </Dropdown>
        );
        return (
            <Table buttons={[schedulerDropDown, this.newJobDropDown, this.jobDropDown]} dataSet={this.props.jobInfo}
                   queryFieldsLimit={2}>
                <Column
                    width={225}
                    header={$l('jobdetaildto.jobname')}
                    renderer={
                        ({record}) => (
                            <Button
                                funcType="flat"
                                onClick={() => this.openJobModel(record.get('triggerType').toLowerCase(), 'view')}
                                className={CLASS_NAME_MAP[record.get('runningState')]}
                                style={{width: 190}}
                            >
                                {record.get('jobName')}
                            </Button>
                        )
                    }
                />
                <Column name="runningState" width={100}/>
                <Column name="jobGroup" width={125}/>
                <Column name="jobClassName" minWidth={225}/>
                <Column name="description" minWidth={225}/>
                <Column name="previousFireTime" minWidth={150}/>
                <Column name="nextFireTime" minWidth={150}/>
            </Table>
        );
    }
}
