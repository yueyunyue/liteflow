import React, {Component} from 'react'
import {Form, Input, Modal} from 'antd'
import {FormComponentProps} from "antd/lib/form/Form";
import CommonUtils from "../../../../common/utils/CommonUtils";
import EnumUtils from "../../../../common/utils/EnumUtils";

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

class LinkConfModal extends Component<ModalProps, any> {

    render() {

        let handleOk = (e) => {
            e.preventDefault();
            this.props.form.validateFields((errors) => {
                if (errors) {
                    return
                }
                const data = this.props.form.getFieldsValue();
                this.props.onOk(EnumUtils.taskDependencyTypeTimeRange, data);
            })
        };
        const modalOpts = {
                title: '配置关联',
                visible: true,
                onOk: handleOk,
                onCancel: this.props.onCancel
            };
        const {link} = this.props;
        const linkConfig = link["config"];
        let config = null;
        if(linkConfig){
            try{
                config = JSON.parse(linkConfig);
            }catch (e) {
                console.log(e);
            }
        }

        return (<Modal {...modalOpts}>
            <Form layout={'horizontal'} >
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
            </Form>
        </Modal>);
    }
}

export default Form.create()(LinkConfModal);