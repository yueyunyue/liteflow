import React, {Component} from 'react';
import {Button, Popconfirm, Table, Row, Col} from 'antd';
import {DailyInit, DailyInitModel} from "../model/DailyInitModel";
import CommonUtils from "../../../common/utils/CommonUtils";
import EnumUtils from "../../../common/utils/EnumUtils";

export interface DailyInitListProps {
    dataSource: Array<DailyInit>;
    loading: boolean;
    pageConfig: any;
    dailyInitModel: DailyInitModel;
}

export class DailyInitList extends Component<DailyInitListProps, { showModal, dailyInit }> {

    constructor(props) {
        super(props);
        this.state = {showModal: false, dailyInit: new DailyInit()}
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
                title: '任务名',
                dataIndex: 'taskName',
                key: 'taskName'
            }, {
                title: '日期',
                dataIndex: 'day',
                key: 'day'
            }, {
                title: '状态',
                dataIndex: 'status',
                key: 'status',
                render: (status, record, index) => {
                    const statusStr = EnumUtils.getDailyInitStatusName(status);
                    return statusStr;
                }
            }, {
                title: '信息',
                dataIndex: 'msg',
                render: (msg, record, index) => {
                    return <div style={{width: 200, wordBreak: "break-all", wordWrap: "break-word"}} ><p>{msg}</p></div>
                }
            }
            , {
                title: '时间',
                dataIndex: 'infos',
                key: 'infos',
                render: (infos, record, index) => {
                    const {createTime, updateTime} = record;
                    return <Row className={"list-content-row"}>
                        <Row><Col className={"list-content-col-title"} span={12}>创建时间:</Col><Col className={"list-content-col-content"} span={12}>{CommonUtils.dateFormat(createTime)}</Col></Row>
                        <Row><Col className={"list-content-col-title"} span={12}>更新时间:</Col><Col className={"list-content-col-content"} span={12}>{CommonUtils.dateFormat(updateTime)}</Col></Row>
                    </Row>;
                }
            }
        ];
        return (
            <div>
                <Table dataSource={this.props.dataSource}
                       columns={columns}
                       rowKey="id"
                       loading={this.props.loading}
                       pagination={this.props.pageConfig}/>
            </div>
        );

    }

}

