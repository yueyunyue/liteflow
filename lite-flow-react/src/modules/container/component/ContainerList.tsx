import React, {Component} from 'react';
import {Button, Popconfirm,Table} from 'antd';
import {Container,ContainerModel} from "../model/ContainerModel";
import ContainerModal from "./ContainerModal";
import EnumUtils from "../../../common/utils/EnumUtils";

export interface ContainerListProps {
    dataSource: Array<Container>;
    loading: boolean;
    pageConfig: any;
    containerModel: ContainerModel;
}

export class ContainerList extends Component<ContainerListProps, {showModal, container}> {

    constructor(props) {
        super(props);
        this.state = {showModal: false, container: new Container()}
    }

    showEditModal(){
        let that = this;
        that.setState({
            showModal: true
        });
    }

    hideEditModal(){
        this.setState({
            showModal: false
        });
    }

    getModalProps(){
        let that = this;
        return {
            container: this.state.container,
            onOk: function (container: Container) {
                that.props.containerModel.edit(container);
                that.hideEditModal();
            },
            onCancel() {
                that.hideEditModal();
            }
        };
    }
    //上线事件
    handleOn (rowData, e){
        this.props.containerModel.on(rowData.id);
    }
    //下线事件
    handleOff (rowData, e){
        this.props.containerModel.off(rowData.id);
    }
    //编辑事件
    handleEdit (rowData, e){
        this.setState({container : rowData})
        this.showEditModal();
    }

    render(){
        let columns = [
        {
            title: 'id',
            dataIndex: 'id',
            key: 'id'
        },{
            title: '名称',
            dataIndex: 'name',
            key: 'name'
        },{
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (status, record, index) => {
                const statusStr = EnumUtils.getStatusName(status);
                return statusStr;
            }
        },{
            title: '日志类型',
            dataIndex: 'logType',
            key: 'logType',
            render: (logType, record, index) => {
                const logTypeStr = EnumUtils.getLogTypeMapName(logType);
                return logTypeStr;
            }
        },{
            title: '描述',
            dataIndex: 'description',
            key: 'description'
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
            render:(text, record, index) => {
                const {status} = record;
                let onOrOffBtn = null;
                if(status == EnumUtils.statusOnline ){
                    onOrOffBtn = (<Popconfirm title='确定下线？' onConfirm={e => this.handleOff(record , e)}>
                        <Button type='ghost' size={'small'} >
                            下线
                        </Button>
                    </Popconfirm>);
                }else{
                    onOrOffBtn = (<Popconfirm title='确定上线？' onConfirm={e => this.handleOn(record , e)}>
                        <Button type='ghost'  size={'small'} >
                            上线
                        </Button>
                    </Popconfirm>);
                }

                return <div>
                    <Button size={'small'}  type='ghost' className={"margin-right5"} onClick={e => this.handleEdit(record , e)}>
                        修改
                    </Button>
                    {onOrOffBtn}
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
                {this.state.showModal ? <ContainerModal {...this.getModalProps()}/> : ''}
            </div>
        );

    }

}

