import React, {Component} from 'react';
import {observer} from "mobx-react";
import {inject} from '../../../common/utils/IOC';
import {ExecuteJobModel, ExecuteJob} from "../model/ExecuteJobModel";
import Search, {ExecuteJobSearchProps} from "../component/ExecuteJobSearch";
import {Row} from 'antd'
import {ExecuteJobList, ExecuteJobListProps} from "../component/ExecuteJobList";

@observer
export default class ExecuteJobView extends Component<any, any> {

    @inject(ExecuteJobModel)
    private executeJobModel: ExecuteJobModel;

    constructor(props) {
        super(props);
    }

    componentWillMount(): void {
        if(this.props.param){
            this.executeJobModel.query(this.props.param);
        }else{
            this.executeJobModel.query({});
        }

    }

    /**
     * 搜索参数
     * @returns {{user: any; onSearch: ((searchVo: ExecuteJobSearchParam) => any); onAdd: ((executeJob: ExecuteJob) => any)}}
     */
    getSearchProps(){
        let that = this;
        return {
            executeJobModel: that.executeJobModel
        };
    };

    getListProps(): ExecuteJobListProps {
        let that = this;
        return {
            dataSource: this.executeJobModel.executeJobs,
            loading: this.executeJobModel.loading,
            pageConfig:this.executeJobModel.pageConfig,
            executeJobModel: that.executeJobModel
        };
    };

    getOperProps(){
        let that = this;
        return {
            executeJobModel: that.executeJobModel
        };
    };

    render() {
        let hideSearch = false;
        if(this.props.param){
            hideSearch = true;
        }


        return (
            <Row>
                {
                    hideSearch ? "" : <Row className={"container-row-block"}>
                        <Search {...this.getSearchProps()}/>
                    </Row>
                }
                <Row className={"container-row-block"}>
                    <Row>
                        <ExecuteJobList {...this.getListProps()}/>
                    </Row>
                </Row>
            </Row>
        )
    }

}
