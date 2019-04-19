import React, {Component} from 'react';
import {Button, Popconfirm, Table, Row, Col, Modal} from 'antd';
import {TaskInstance, TaskInstanceModel} from "../model/TaskInstanceModel";
import EnumUtils from "../../../common/utils/EnumUtils";
import CommonUtils from "../../../common/utils/CommonUtils";
import ShowLogModal from './ShowLogModal';
import InstanceDependencyModal from "./InstanceDependencyModal";
import ReactJson from 'react-json-view';

export interface TaskInstanceListProps {
    dataSource: Array<TaskInstance>;
    loading: boolean;
    pageConfig: any;
    taskInstanceModel: TaskInstanceModel;
}

export class TaskInstanceList extends Component<TaskInstanceListProps, { showDependencyModal, showLogModal, taskInstance }> {

    constructor(props) {
        super(props);
        this.state = {showDependencyModal: false, showLogModal: false, taskInstance: new TaskInstance()}
    }

    /**
     * 日志
     */
    showLogModal(taskInstance, e){
        let that = this;
        that.setState({
            taskInstance: taskInstance,
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
            model: this.state.taskInstance,
            type: "taskInstance",
            onCancel() {
                that.hideLogModal();
            }
        };
    }

    /**
     * 显示运行参数
     */
    showPluginParamModal(data, e) {
        let content = <span></span>;
        const {pluginConf} = data;
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
    /**
     * 日志
     */
    showDependencyModal(taskInstance, e){
        let that = this;
        that.setState({
            taskInstance: taskInstance,
            showDependencyModal: true
        });
    }

    hideDependencyModal(){
        this.setState({
            showDependencyModal: false
        });
    }

    getDependencyModalProps(){
        let that = this;
        return {
            instance: this.state.taskInstance,
            onCancel() {
                that.hideDependencyModal();
            }
        };
    }

    render() {
        let columns = [
            {
                title: 'id',
                dataIndex: 'id',
                key: 'id'
            }, {
                title: '任务id',
                dataIndex: 'taskId',
                key: 'taskId'
            }, {
                title: '任务版本id',
                dataIndex: 'taskVersionId',
                key: 'taskVersionId'
            }, {
                title: '任务版本',
                dataIndex: 'taskVersionNo',
                key: 'taskVersionNo'
            }, {
                title: '执行引擎id',
                dataIndex: 'executorJobId',
                key: 'executorJobId'
            }, {
                title: '状态',
                dataIndex: 'status',
                key: 'status',
                render: (status, record, index) => {
                    return  EnumUtils.getTaskVersionStatusName(status);
                }
            }, {
                title: '相关时间',
                dataIndex: 'times',
                key: 'times',
                render: (auths, record, index) => {
                    const {createTime, updateTime, logicRunTime, runStartTime, runEndTime} = record;
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
                key: 'operateInstance',
                render: (text, record, index) => {
                    return <div>
                        <Button type='ghost' size={"small"} className={"margin-right5"} onClick={e => this.showDependencyModal(record, e)}>
                            依赖
                        </Button>
                        <Button type='ghost' size={"small"} className={"margin-right5"} onClick={e => this.showLogModal(record, e)}>
                            日志
                        </Button>
                        <Button type='ghost' size={"small"} className={"margin-right5"} onClick={e => this.showPluginParamModal(record, e)}>
                            运行参数
                        </Button>
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
                {this.state.showLogModal ? <ShowLogModal {...this.getLogModalProps()}/> : ''}
                {this.state.showDependencyModal ? <InstanceDependencyModal {...this.getDependencyModalProps()}/> : ''}
            </div>
        );

    }

}

