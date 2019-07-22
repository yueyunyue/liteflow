import React, {Component} from 'react';
import {observer} from "mobx-react";
import {Row, Col, Button, Form, Input} from 'antd'
import {FormComponentProps} from "antd/lib/form/Form";
import RemoteSelect from "../../../common/compoment/remoteSelect/RemoteSelect";
import ResultUtils from "../../../common/utils/ResultUtils";

const formItemLayout = {
    wrapperCol: {
        span: 24
    }
};
export interface TestViewProps extends FormComponentProps {
    onOk: any;
    onCancel: any;
}
const REMOTE_SELECT_PROPS = {
    url: "/test/remote/option",
    showAll: true,
    mode : "multiple"
}

@observer
 class TestView extends Component<TestViewProps> {

    constructor(props) {
        super(props);
    }

    render() {
        let handleOk = (e) => {
            e.preventDefault();
            this.props.form.validateFields((errors) => {
                if (errors) {
                    return
                }
                const data = {
                    ...this.props.form.getFieldsValue()
                };
                console.dir(data);
            })
        };

        return (
            <Row>
                 <Row className={"container-row-block"}>
                 </Row>
                <Row className={"container-row-block"}>
                    <Form layout={'vertical'} onSubmit={handleOk}>

                        <Row>
                            <Col span={11}>
                                <Form.Item label='数据' {...formItemLayout} >
                                    {this.props.form.getFieldDecorator('name', {
                                        initialValue: "2",
                                        rules: [
                                            {
                                                required: true,
                                                message: '不能为空'
                                            }
                                        ]
                                    })(<RemoteSelect {...REMOTE_SELECT_PROPS}/>)}
                                </Form.Item>
                            </Col>
                        </Row>
                        <Row style={{textAlign: "center"}}>
                            <Button type='primary' htmlType='submit' className={"margin-right5"}>运行</Button>
                            <Button htmlType='reset' className={"margin-right5"}>重置</Button>
                        </Row>
                    </Form>
                </Row>
            </Row>
        )
    }

}
export default Form.create()(TestView);