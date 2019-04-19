import React, {Component} from 'react';
import {Button, Popconfirm,Table, Row, Col, Modal} from 'antd';
import {TaskVersion,TaskVersionModel} from "../model/TaskVersionModel";
import EnumUtils from "../../../common/utils/EnumUtils";
import CommonUtils from "../../../common/utils/CommonUtils";
import ShowInstanceModal from "./ShowInstanceModal";
import ShowLogModal from './ShowLogModal';
import ReactJson from 'react-json-view';

export interface TaskVersionListProps {
    dataSource: Array<TaskVersion>;
    loading: boolean;
    pageConfig: any;
    taskVersionModel: TaskVersionModel;
}

export class TaskVersionList extends Component<TaskVersionListProps, {showLogModal, showInstanceModal, taskVersion}> {

    constructor(props) {
        super(props);
        this.state = {showLogModal:false, showInstanceModal: false, taskVersion: new TaskVersion()}
    }

    /**
     * 实例
     */
    showInstanceModal(taskVersion, e){
        let that = this;
        that.setState({
            taskVersion: taskVersion,
            showInstanceModal: true
        });
    }

    hideInstanceModal(){
        this.setState({
            showInstanceModal: false
        });
    }

    getInstanceModalProps(){
        let that = this;
        return {
            taskVersion: this.state.taskVersion,
            onCancel() {
                that.hideInstanceModal();
            }
        };
    }
    /**
     * 日志
     */
    showLogModal(taskVersion, e){
        let that = this;
        that.setState({
            taskVersion: taskVersion,
            showLogModal: true
        });
    }

    hideLogModal(){
        this.setState({
            showLogModal: false
        });
    }

    getLogModalProps(){
        let that = this;
        return {
            model: this.state.taskVersion,
            type: "taskVersion",
            onCancel() {
                that.hideLogModal();
            }
        };
    }

    //kill
    kill (rowData, e){
        this.props.taskVersionModel.kill(rowData.id);
    }
    //ignore
    ignore (rowData, e){
        this.props.taskVersionModel.ignore(rowData.id);
    }
    //fix
    fix (rowData, e){
        this.props.taskVersionModel.fix(rowData.id);
    }
    //deepFix
    deepFix (rowData, e){
        this.props.taskVersionModel.deepFix(rowData.id);
    }
    //编辑事件
    handleEdit (rowData, e){
        this.setState({taskVersion : rowData})
    }

    /**
     * 显示运行参数
     */
    showPluginParamModal(data, e) {
        let {latestInstance} = data;
        const {pluginConf} = latestInstance;
        let content = <span></span>;
        if(pluginConf){
            let pluginJson = JSON.parse(pluginConf);
            content = <ReactJson src={pluginJson} displayDataTypes={false} style={{wordBreak: "break-all"}}/>;
        }
        Modal.info({
            title: "插件参数",
            width: 600,
            cancelText: "关闭",
            okText: "关闭",
            content:  content
        });
    }

    render(){
        let columns = [
        {
            title: 'id',
            dataIndex: 'id',
            key: 'id'
        },{
            title: 'taskId',
            dataIndex: 'taskId',
            key: 'taskId'
        },{
            title: '名称',
            dataIndex: 'taskName',
            key: 'taskName'
        },{
            title: '任务版本',
            dataIndex: 'versionNo',
            key: 'versionNo'
        }, {
                title: '信息',
                dataIndex: 'info',
                key: 'info',
                render: (info, record, index) => {
                    const {retryNum, status, finalStatus, msg} = record;
                    return  <Row className={"list-content-row"}>
                        <Row><Col className={"list-content-col-title"} span={12}>状态:</Col><Col className={"list-content-col-content"} span={12}>{EnumUtils.getTaskVersionStatusName(status)}</Col></Row>
                        <Row><Col className={"list-content-col-title"} span={12}>最终状态:</Col><Col className={"list-content-col-content"} span={12}>{EnumUtils.getTaskVersionFinalStatusName(finalStatus)}</Col></Row>
                        <Row><Col className={"list-content-col-title"} span={12}>重试次数:</Col><Col className={"list-content-col-content"} span={12}>{retryNum}</Col></Row>
                        <Row><Col className={"list-content-col-title"} span={12}>msg:</Col><Col className={"list-content-col-content"} span={12}>{msg}</Col></Row>
                    </Row>;
                }
        }, {
                title: '相关时间',
                dataIndex: 'times',
                key: 'times',
                render: (times, record, index) => {
                    let {createTime, updateTime, latestInstance} = record;
                    if(!latestInstance){
                        latestInstance = {};
                    }
                    const {logicRunTime, runStartTime, runEndTime} = latestInstance;

                    return  <Row className={"list-content-row"}>
                        <Row><Col className={"list-content-col-title"} span={12}>逻辑开始时间:</Col><Col className={"list-content-col-content"} span={12}>{CommonUtils.dateFormat(logicRunTime)}</Col></Row>
                        <Row><Col className={"list-content-col-title"} span={12}>运行开始时间:</Col><Col className={"list-content-col-content"} span={12}>{CommonUtils.dateFormat(runStartTime)}</Col></Row>
                        <Row><Col className={"list-content-col-title"} span={12}>运行结束时间:</Col><Col className={"list-content-col-content"} span={12}>{CommonUtils.dateFormat(runEndTime)}</Col></Row>
                        <Row><Col className={"list-content-col-title"} span={12}>运行耗时:</Col><Col className={"list-content-col-content"} span={12}>{CommonUtils.getTimeConsumer(runStartTime, runEndTime)}</Col></Row>
                        <Row><Col className={"list-content-col-title"} span={12}>创建时间:</Col><Col className={"list-content-col-content"} span={12}>{CommonUtils.dateFormat(createTime)}</Col></Row>
                        <Row><Col className={"list-content-col-title"} span={12}>更新时间:</Col><Col className={"list-content-col-content"} span={12}>{CommonUtils.dateFormat(updateTime)}</Col></Row>
                    </Row>;
                }
        }, {
            title: '操作',
            dataIndex: 'id',
            key: 'operate',
            render:(text, record, index) => {

                const {status, finalStatus} = record;

                let showKilled = false;
                if(finalStatus == EnumUtils.taskVersionFinalStatusUndefined){
                    showKilled = true;
                }
                let showIgnore = false;
                let showFix = false;
                if(finalStatus == EnumUtils.taskVersionFinalStatusKilled || finalStatus == EnumUtils.taskVersionFinalStatusFail){
                    showIgnore = true;
                    showFix = true;
                }
                if(finalStatus == EnumUtils.taskVersionFinalStatusSuccess){
                    showFix = true;
                }

                return <div>
                    <p>
                        <Button type='ghost' size={"small"} className={"margin-right5"} onClick={e => this.showInstanceModal(record , e)}>
                            实例
                        </Button>
                        <Button type='ghost' size={"small"} className={"margin-right5"} onClick={e => this.showLogModal(record , e)}>
                            日志
                        </Button>
                        {
                            showIgnore ?
                            <Popconfirm title='确定忽略吗？' onConfirm={e => this.ignore(record , e)}>
                                <Button type='ghost' size={"small"} className={"margin-right5"} >
                                    忽略
                                </Button>
                            </Popconfirm> : ""
                        }
                        {
                            showFix ?
                            <Popconfirm title='确定修复吗？' onConfirm={e => this.fix(record , e)}>
                                <Button type='ghost' size={"small"} className={"margin-right5"} >
                                    修复
                                </Button>
                            </Popconfirm>  : ""
                        }

                        {
                            showKilled ?
                        <Popconfirm title='确定kill吗？' onConfirm={e => this.kill(record , e)}>
                            <Button type='ghost' size={"small"} className={"margin-right5"} >
                                KILL
                            </Button>
                        </Popconfirm> : ""}


                    </p>
                    <p>
                        {
                            showFix ?
                                <Popconfirm title='确定深度修复吗？' onConfirm={e => this.deepFix(record , e)}>
                                    <Button type='ghost' size={"small"} className={"margin-right5"} >
                                        深度修复
                                    </Button>
                                </Popconfirm>  : ""
                        }
                        <Button type='ghost' size={"small"} className={"margin-right5"} onClick={e => this.showPluginParamModal(record, e)}>
                            运行参数
                        </Button>
                    </p>
                </div>;
            }
        }];
        return (
            <div>
                <Table dataSource={this.props.dataSource}
                       columns={columns}
                       rowKey="id"
                       loading={this.props.loading}
                       pagination={this.props.pageConfig}/>
                {this.state.showInstanceModal ? <ShowInstanceModal {...this.getInstanceModalProps()}/> : ''}
                {this.state.showLogModal ? <ShowLogModal {...this.getLogModalProps()}/> : ''}
            </div>
        );

    }

}

