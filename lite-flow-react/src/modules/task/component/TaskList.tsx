import React, {Component} from 'react';
import {Button, Popconfirm, Table, Row, Col} from 'antd';
import {Task, TaskModel} from "../model/TaskModel";
import TaskModal from "./TaskModal";
import EnumUtils from "../../../common/utils/EnumUtils";
import CommonUtils from "../../../common/utils/CommonUtils";
import CommonAuthModal from "../../auth/view/CommonAuthModal";
import ShowTaskVersionModal from "./ShowTaskVersionModal";
import TaskRelationModal from "./TaskRelationModal";

export interface TaskListProps {
    dataSource: Array<Task>;
    loading: boolean;
    pageConfig: any;
    taskModel: TaskModel;
}

export class TaskList extends Component<TaskListProps, { showModal, showVersionModal, showRelationModal, showAuthModal, task }> {

    constructor(props) {
        super(props);
        this.state = {
            showModal: false,
            showAuthModal: false,
            showVersionModal: false,
            showRelationModal: false,
            task: new Task()
        }
    }

    /**
     * 编辑
     */
    showEditModal() {
        let that = this;
        that.setState({
            showModal: true
        });
    }

    hideEditModal() {
        this.setState({
            showModal: false
        });
    }

    getModalProps() {
        let that = this;
        return {
            task: this.state.task,
            onOk: function (task: Task) {
                that.props.taskModel.edit(task);
                that.hideEditModal();
            },
            onCancel() {
                that.hideEditModal();
            }
        };
    }
    /**
     * 版本
     */
    showVersionModal(task, e) {
        let that = this;
        that.setState({
            task: task,
            showVersionModal: true
        });
    }
    hideVersionModal() {
        this.setState({
            showVersionModal: false
        });
    }

    getVersionModalProps() {
        let that = this;
        return {
            task: this.state.task,
            onCancel() {
                that.hideVersionModal();
            }
        };
    }

    /**
     * 展示上下游
     * @param rowData
     * @param e
     */
    showRelationModal(task, e) {
        let that = this;
        that.setState({
            task: task,
            showRelationModal: true
        });
    }

    hideRelationModal() {
        this.setState({
            showRelationModal: false
        });
    }

    getRelationModalProps() {
        let that = this;
        return {
            task: this.state.task,
            upstream: [],
            downstream: [],
            onCancel() {
                that.hideRelationModal();
            }
        };
    }

    //删除事件
    handleDelete(rowData, e) {
        this.props.taskModel.delete(rowData.id);
    }
    //上线
    handleOn(rowData, e) {
        this.props.taskModel.on(rowData.id);
    }
    //下线
    handleOff(rowData, e) {
        this.props.taskModel.off(rowData.id);
    }
    //上线
    run(rowData, e) {
        this.props.taskModel.run(rowData.id);
    }
    /**
     * 权限
     */
    getAuthModalProps(){
        let that = this;
        return {
            model: this.state.task,
            targetType: 1,
            onCancel() {
                that.hideAuthModal();
            }
        };
    }
    showAuthModal(task, e){
        let that = this;
        that.setState({
            showAuthModal: true,
            task: task
        });
    }

    hideAuthModal(){
        this.setState({
            showAuthModal: false
        });
    }
    //编辑事件
    handleEdit(rowData, e) {
        this.setState({task: rowData})
        this.showEditModal();
    }

    render() {
        let columns = [
            {
                title: 'id',
                dataIndex: 'id',
                key: 'id'
            }, {
                title: '名称',
                dataIndex: 'name',
                key: 'name',
                render:(name, record) => {
                    return <div key={"taskName"} style={{width: 100}} className={"wd-break"}>{name}</div>
                }
            }, {
                title: '状态',
                dataIndex: 'status',
                key: 'status',
                render: (status, record, index) => {
                    const statusStr = EnumUtils.getStatusName(status);
                    return <b className={'task-status-' + status}>{statusStr}</b>;
                }
            }, {
                title: '任务配置',
                dataIndex: 'config',
                key: 'config',
                render: (config, record, index) => {
                    const {cronExpression, period} = record;
                    const periodStr = EnumUtils.getPeriodName(period);
                    return <Row className={"list-content-row"}>
                        <Row><Col className={"list-content-col-title"} span={12}>周期:</Col><Col className={"list-content-col-content"} span={12}>{periodStr}</Col></Row>
                        <Row><Col className={"list-content-col-title"} span={12}>表达式:</Col><Col className={"list-content-col-content"} span={12}>{cronExpression}</Col></Row>
                    </Row>;
                }
            }, {
                title: '时间',
                dataIndex: 'times',
                key: 'times',
                render: (times, record, index) => {
                    const {createTime, updateTime} = record;
                    return  <Row className={"list-content-row"}>
                        <Row><Col className={"list-content-col-title"} span={12}>创建时间:</Col><Col className={"list-content-col-content"} span={12}>{CommonUtils.dateFormat(createTime)}</Col></Row>
                        <Row><Col className={"list-content-col-title"} span={12}>更新时间:</Col><Col className={"list-content-col-content"} span={12}>{CommonUtils.dateFormat(updateTime)}</Col></Row>
                    </Row>;
                }
            }, {
                title: '创建人',
                dataIndex: 'user',
                key: 'user',
                render: (user, record, index) => {
                    if(user){
                        return user.name;
                    }
                    return "";
                }
            }, {
                title: '操作',
                dataIndex: 'id',
                key: 'operateInstance',
                render: (text, record, index) => {

                    const {status} = record;
                    let runBtn = [];
                    let onlineOrOfflineBtn = ( <Popconfirm title='确定下线吗？' onConfirm={e => this.handleOff(record, e)}>
                        <Button  size={'small'}  type='ghost'>
                            下线
                        </Button>
                    </Popconfirm>);

                    if(status != EnumUtils.statusOnline){
                        onlineOrOfflineBtn = (<Popconfirm title='确定上线吗？' onConfirm={e => this.handleOn(record, e)}>
                            <Button  size={'small'} type='ghost'>
                                上线
                            </Button>
                        </Popconfirm>);

                    }
                    runBtn.push(<Popconfirm title='确定运行吗？'  onConfirm={e => this.run(record, e)}>
                        <Button className={"margin-right5"} size={'small'} type='ghost'>
                            运行
                        </Button>
                    </Popconfirm>);

                    return <div className={"list-btn-container"}>
                        <p>
                            <Button type='ghost' className={"margin-right5"}  size={'small'}  onClick={e => this.handleEdit(record, e)}>
                                修改
                            </Button>
                            <Button type='ghost' className={"margin-right5"}  size={'small'}  onClick={e => this.showRelationModal(record, e)}>
                                血缘
                            </Button>
                            {onlineOrOfflineBtn}
                        </p>
                        <p>
                            <Button type='ghost' className={"margin-right5"}  size={'small'}  onClick={e => this.showVersionModal(record, e)}>
                                记录
                            </Button>
                            {runBtn}
                            <Button type='ghost' className={"margin-right5"}  size={'small'}  onClick={e => this.showAuthModal(record, e)}>
                                权限
                            </Button>
                            {/*<Button type='ghost' className={"margin-right5"}  size={'small'}  onClick={e => this.handleEdit(record, e)}>*/}
                                {/*复制*/}
                            {/*</Button>*/}
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
                {this.state.showModal ? <TaskModal {...this.getModalProps()}/> : ''}
                {this.state.showRelationModal ? <TaskRelationModal {...this.getRelationModalProps()}/> : ''}
                {this.state.showAuthModal ? <CommonAuthModal {...this.getAuthModalProps()}/> : ''}
                {this.state.showVersionModal ? <ShowTaskVersionModal {...this.getVersionModalProps()}/> : ''}
            </div>
        );

    }

}

