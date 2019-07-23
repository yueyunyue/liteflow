import React, {Component} from 'react'
import {Select, Icon, Modal, Upload, Row, Input, notification} from 'antd'
import {requestPost, requestGet} from "../../../common/utils/Request";
import ResultUtils from "../../../common/utils/ResultUtils";


export interface RemoteSelectProps {
    url: string;
    showAllOption ?: boolean;
    allOptionValue ?: string;
    optionKeyName? : string;
    optionValueName? : string;
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

const MODE = "mode";

/**
 * 获取默认值
 * @param isMulitple
 * @returns {any}
 */
const getDefaultValue = (isMulitple) => {
    if(isMulitple && isMulitple == true){
        return [];
    }
    return "";
}
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
        const {value, config, optionKeyName, optionValueName, showAllOption, allOptionValue} = this.props;
        const {options} = this.state;
        const keyName = optionKeyName ? optionKeyName: OPTION_KEY_NAME;
        const valueName = optionValueName ? optionValueName: OPTION_VALUE_NAME;

        let configOption = {};
        let selectOptions= [];
        /**
         * 获取当前mode，用来判断是否为多选
         * @type {null}
         */
        let mode = null;
        if(config){
            mode = config[MODE];
        }
        let isMulitple = false;
        if(mode && mode == MODE_MULTIPLE){
            isMulitple = true;
        }
        if(options){
            if(showAllOption && !isMulitple){
                const allValueStr = allOptionValue ? allOptionValue + "" : "";
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

        return (
            <Row>
                <Select
                    {...configOption}
                    value= {value ? value : getDefaultValue(isMulitple)}
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