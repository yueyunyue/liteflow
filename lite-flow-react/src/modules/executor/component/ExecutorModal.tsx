import React, {Component} from 'react'
import {Form, Input, Modal, Select} from 'antd'
import {Executor} from "../model/ExecutorModel";
import {FormComponentProps} from "antd/lib/form/Form";
import {kernel} from "../../../common/utils/IOC"
import CommonUtils from "../../../common/utils/CommonUtils";

const formItemLayout = {
    labelCol: {
        span: 6
    },
    wrapperCol: {
        span: 14
    }
};

const Option = Select.Option;

export interface ModalProps extends FormComponentProps {
    executor: Executor;
    onOk: any;
    onCancel: any;
}

class ExecutorModal extends Component<ModalProps> {
    render() {

        let isUpdate = false;
        if (this.props.executor && this.props.executor.id) {
            isUpdate = true;
        }
        let executorItem = this.props.executor ? this.props.executor : new Executor;
        let handleOk = (e) => {
            e.preventDefault();
            this.props.form.validateFields((errors) => {
                if (errors) {
                    return
                }
                const data = {
                    ...this.props.form.getFieldsValue(),
                    id: executorItem.id ? executorItem.id : ''
                };
                this.props.onOk(data)
            })
        };
        const modalOpts = {
            title: isUpdate ? '编辑执行引擎' : '添加执行引擎',
            visible: true,
            onOk: handleOk,
            onCancel: this.props.onCancel
        };

        return (<Modal {...modalOpts}>
            <Form layout={'horizontal'}>
                <Form.Item label='名称' hasFeedback {...formItemLayout}>
                    {this.props.form.getFieldDecorator('name', {
                        initialValue: CommonUtils.getStringValueFromModel("name", executorItem, ""),
                        rules: [
                            {
                                required: true,
                                message: '不能为空'
                            }
                        ]
                    })(<Input/>)}
                </Form.Item>
                <Form.Item label='ip' hasFeedback {...formItemLayout}>
                    {this.props.form.getFieldDecorator('ip', {
                        initialValue: CommonUtils.getStringValueFromModel("ip", executorItem, ""),
                        rules: [
                            {
                                required: true,
                                message: '不能为空'
                            }
                        ]
                    })(<Input disabled={isUpdate}/>)}
                </Form.Item>
                <Form.Item label='描述' hasFeedback {...formItemLayout}>
                    {this.props.form.getFieldDecorator('description', {
                        initialValue: CommonUtils.getStringValueFromModel("description", executorItem, ""),
                        rules: [
                            {
                                required: true,
                                message: '不能为空'
                            }
                        ]
                    })(<Input/>)}
                </Form.Item>
            </Form>
        </Modal>);
    }
}

export default Form.create()(ExecutorModal);