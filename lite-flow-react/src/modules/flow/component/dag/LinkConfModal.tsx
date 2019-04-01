import React, {Component} from 'react'
import {Form, Input, InputNumber, Modal, Select, Row} from 'antd'
import {FormComponentProps} from "antd/lib/form/Form";
import CommonUtils from "../../../../common/utils/CommonUtils";
import EnumUtils from "../../../../common/utils/EnumUtils";
import {DomUtils} from "../../../../common/utils/DomUtils";

const formItemLayout = {
    labelCol: {
        span: 6
    },
    wrapperCol: {
        span: 14
    }
};

export interface ModalProps extends FormComponentProps{
    link : any;
    onOk: any;
    onCancel: any;
}
const CONFIG = "config";
const TYPE = "type";

class LinkConfModal extends Component<ModalProps, {type}> {


    constructor(props) {
        super(props);
        let type = EnumUtils.taskDependencyTypeOffset;
        if(props.link){
            type = props.link[TYPE];
        }
        this.state = {type: type}
    }

    typeChange(value){
        this.setState({
            type: value
        });
    }


    render() {
        let linkObj = this.props.link ? this.props.link : {};
        let handleOk = (e) => {
            e.preventDefault();
            this.props.form.validateFields((errors) => {
                if (errors) {
                    return
                }
                const {type} = this.state;
                const data = this.props.form.getFieldsValue();
                this.props.onOk(type, data[CONFIG]);
            })
        };
        const modalOpts = {
            title: '配置关联',
            visible: true,
            onOk: handleOk,
            onCancel: this.props.onCancel
        };


        const {type} = this.state;
        const domMap = {};

        const {link} = this.props;
        const linkConfig = link[CONFIG];
        let config = null;
        if(type == EnumUtils.taskDependencyTypeOffset && linkConfig){
            try{
                config = JSON.parse(linkConfig);
            }catch (e) {
                console.log(e);
            }
        }
        let offset = 0;
        if(config && !isNaN(config)){
            offset = config;
        }

        /**
         * 设置偏移量
         * @type {any}
         */
        domMap[EnumUtils.taskDependencyTypeOffset] = (
            <Row>
                <Form.Item label='偏移量' hasFeedback {...formItemLayout}>
                    {this.props.form.getFieldDecorator('config', {
                        initialValue: offset + "",
                        rules: [
                            {
                                required: true,
                                message: '不能为空'
                            }
                        ]
                    })(<InputNumber max={-1}/>)}
                </Form.Item>
            </Row>
        );
        /**
         * 设置区间
         * @type {any}
         */
        domMap[EnumUtils.taskDependencyTypeTimeRange] = (
            <Row>
                <Form.Item label='开始时间' hasFeedback {...formItemLayout}>
                    {this.props.form.getFieldDecorator('startTime', {
                        initialValue: CommonUtils.getStringValueFromModel("startTime", config, "${time: yyyy-MM-dd,-1d}"),
                        rules: [
                            {
                                required: true,
                                message: '不能为空'
                            }
                        ]
                    })(<Input/>)}
                </Form.Item>
                <Form.Item label='结束时间' hasFeedback {...formItemLayout}>
                    {this.props.form.getFieldDecorator('endTime', {
                        initialValue: CommonUtils.getStringValueFromModel("endTime", config, "${time: yyyy-MM-dd,-1d}"),
                        rules: [
                            {
                                required: true,
                                message: '不能为空'
                            }
                        ]
                    })(<Input/>)}
                </Form.Item>
            </Row>
        );

        const types = EnumUtils.getTaskDependencyTypeOptionArray();
        let options = DomUtils.getSelectOptionsWithoutAll(types);


        return (<Modal {...modalOpts}>
            <Form layout={'horizontal'} >
                <Form.Item label='类型' hasFeedback {...formItemLayout}>
                    {this.props.form.getFieldDecorator('type', {
                        initialValue: CommonUtils.getStringValueFromModel("type", linkObj, "1"),
                        rules: [
                            {
                                required: true,
                                message: '不能为空'
                            }
                        ]
                    })(<Select onChange={this.typeChange.bind(this)}>
                        {options}
                    </Select>)}
                </Form.Item>



                {domMap[type]}
            </Form>
        </Modal>);
    }
}

export default Form.create()(LinkConfModal);