import React, {Component} from 'react'
import {Form, Input, Modal, Select, Row, Col} from 'antd'
import {Container} from "../model/ContainerModel";
import {FormComponentProps} from "antd/lib/form/Form";
import {kernel} from "../../../common/utils/IOC"
import CommonUtils from "../../../common/utils/CommonUtils";
import {DomUtils} from "../../../common/utils/DomUtils";
import EnumUtils from "../../../common/utils/EnumUtils";

const formItemLayout = {
    labelCol: {
        span: 4
    },
    wrapperCol: {
        span: 20
    }
};

const modalWidth = 800;
export interface ModalProps extends FormComponentProps{
    container: Container;
    onOk: any;
    onCancel: any;
}

class ContainerModal extends Component<ModalProps> {
    render() {

        let isUpdate = false;
        if(this.props.container && this.props.container.id){
            isUpdate = true;
        }
        let containerItem = this.props.container ? this.props.container : new Container;
        let handleOk = (e) => {
            e.preventDefault();
            this.props.form.validateFields((errors) => {
                if (errors) {
                    return
                }
                const data = {
                    ...this.props.form.getFieldsValue(),
                    id: containerItem.id ? containerItem.id : ''
                };
                this.props.onOk(data)
            })
        };
        const modalOpts = {
                title: isUpdate ? '编辑容器' : '添加容器',
                visible: true,
                maskClosable: false,
                width: modalWidth,
                onOk: handleOk,
                onCancel: this.props.onCancel
            };
        const logTypes = EnumUtils.getLogTypeOptionArray();
        let logTypeOptions = DomUtils.getSelectOptionsWithoutAll(logTypes);

        return (<Modal {...modalOpts}>
            <Form layout={'horizontal'} >
                <Row >
                    <Col span={12}>
                        <Form.Item label='名称' hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator('name', {
                                initialValue: CommonUtils.getStringValueFromModel("name", containerItem, ""),
                                rules: [
                                    {
                                        required: true,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Input/>)}
                        </Form.Item>
                        <Form.Item label='类名' hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator('className', {
                                initialValue: CommonUtils.getStringValueFromModel("className", containerItem, ""),
                                rules: [
                                    {
                                        required: true,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Input/>)}
                        </Form.Item>
                        <Form.Item label="日志类型" hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator("logType", {
                                initialValue: CommonUtils.getStringValueFromModel("logType", containerItem, "1"),
                                rules: [
                                    {
                                        required: true,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Select>
                                {logTypeOptions}
                            </Select>)}
                        </Form.Item>
                        <Form.Item label='描述' hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator('description', {
                                initialValue: CommonUtils.getStringValueFromModel("description", containerItem, ""),
                                rules: [
                                    {
                                        required: true,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Input.TextArea rows={5}/>)}
                        </Form.Item>
                    </Col>
                    <Col span={12}>
                        <Form.Item label='参数' hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator('fieldConfig', {
                                initialValue: CommonUtils.getStringValueFromModel("fieldConfig", containerItem, ""),
                                rules: [
                                    {
                                        required: false,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Input.TextArea rows={20}/>)}
                        </Form.Item>
                    </Col>
                </Row>


            </Form>
        </Modal>);
    }
}

export default Form.create()(ContainerModal);