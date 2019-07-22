import React, {Component} from 'react'
import {Select, Icon, Modal, Upload, Row, Input, notification} from 'antd'
import {requestPost, requestGet} from "../../../common/utils/Request";
import ResultUtils from "../../../common/utils/ResultUtils";


export interface RemoteSelectProps {
    url: string;
    showAll ?: boolean;
    allValue ?: string;
    optionKeyName? : string;
    optionValueName? : string;
    mode? : any;
    config? : any;
    onChange ?: any;
    value ?: any;
}
export interface RemoteSelectState {
    /**
     * key：
     * value:
     */
    options: Array<any>;
}
const OPTION_KEY_NAME = "key";

const OPTION_VALUE_NAME = "value";

const MODE_MULTIPLE = "multiple";

/**
 * 通过url自动获取并生成 options
 *
 */
class RemoteSelect extends Component<RemoteSelectProps, RemoteSelectState> {

    constructor(pros){
        super(pros);
        this.state = {options : []}

    }

    componentWillMount(){
        const that = this;
        const {url} = this.props;
        requestGet(url, {}).then(result => {
            const data = ResultUtils.getData(result);
            if(data){
                that.setState({
                    options: data
                })
            }
        })
    }


    render() {
        const that = this;
        const {value, mode,config, optionKeyName, optionValueName, showAll, allValue} = this.props;
        const {options} = this.state;
        const keyName = optionKeyName ? optionKeyName: OPTION_KEY_NAME;
        const valueName = optionValueName ? optionValueName: OPTION_VALUE_NAME;

        let configOption = {};
        let selectOptions= [];
        if(options){
            if(showAll){
                const allValueStr = allValue ? allValue + "" : "";
                selectOptions.push(<Select.Option key={"option-all"} value={allValueStr}>全部</Select.Option>);
            }
            for(let option of options){
                const key = option[keyName];
                const value = option[valueName];
                selectOptions.push(<Select.Option key={"option-" + key} value={value + ""}>{key}</Select.Option>);

            }
        }

        /**
         * 属性复制
         */
        Object.assign(configOption, config);
        if(mode){
            configOption["mode"] = mode;
        }

        return (
            <Row>
                <Select
                    {...configOption}
                    value= {value ? value : ""}
                    onChange={(value) => {
                        that.props.onChange(value);
                    }}
                >
                    {selectOptions}
                </Select>
            </Row>
        );

    }
}

export default RemoteSelect;