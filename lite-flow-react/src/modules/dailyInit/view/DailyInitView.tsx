import React, {Component} from 'react';
import {observer} from "mobx-react";
import {inject} from '../../../common/utils/IOC';
import {DailyInitModel} from "../model/DailyInitModel";
import Search, {DailyInitSearchProps} from "../component/DailyInitSearch";
import {Row} from 'antd'
import {DailyInitList, DailyInitListProps} from "../component/DailyInitList";

@observer
export default class DailyInitView extends Component<any, any> {

    @inject(DailyInitModel)
    private dailyInitModel: DailyInitModel;

    constructor(props) {
        super(props);
    }
    componentWillMount(): void {
        this.dailyInitModel.query({});
    }

    /**
     * 搜索参数
     * @returns {{user: any; onSearch: ((searchVo: DailyInitSearchParam) => any); onAdd: ((dailyInit: DailyInit) => any)}}
     */
    getSearchProps(){
        let that = this;
        return {
            dailyInitModel: that.dailyInitModel
        };
    };

    getListProps(): DailyInitListProps {
        let that = this;
        return {
            dataSource: this.dailyInitModel.dailyInits,
            loading: this.dailyInitModel.loading,
            pageConfig:this.dailyInitModel.pageConfig,
            dailyInitModel: that.dailyInitModel
        };
    };

    getOperProps(){
        let that = this;
        return {
            dailyInitModel: that.dailyInitModel
        };
    };

    render() {
        let hideSearch = false;
        if(this.props.param){
            hideSearch = true;
        }


        return (
            <Row>
                 <Row className={"container-row-block"}>
                    <Search {...this.getSearchProps()}/>
                </Row>
                <Row className={"container-row-block"}>
                    <Row>
                        <DailyInitList {...this.getListProps()}/>
                    </Row>
                </Row>
            </Row>
        )
    }

}
