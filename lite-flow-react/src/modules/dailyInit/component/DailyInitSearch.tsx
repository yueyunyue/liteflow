import React, {Component} from 'react'
import {Button, Form, Input, Popconfirm, Row} from 'antd'
import { DailyInitModel} from "../model/DailyInitModel";
import {FormComponentProps} from "antd/lib/form/Form";

export interface DailyInitSearchProps extends FormComponentProps {
    dailyInitModel: DailyInitModel
}

class DailyInitSearch extends Component<DailyInitSearchProps, {}> {

    constructor(props) {
        super(props);
        this.state = {showModal: false}
    }


    render() {
        let handleOk = (e) => {
            e.preventDefault();
            this.props.form.validateFields((errors) => {
                if (errors) {
                    return
                }

                let data = this.props.form.getFieldsValue();
                this.props.dailyInitModel.query(data);
            })
        };

        return (<Row>
            <Form layout={'inline'} onSubmit={handleOk} className={"float-right"}>
                <Form.Item label='日期' className={"margin-right5"}>
                        <span>
                        {this.props.form.getFieldDecorator('day', {
                            initialValue: '',
                        })(
                            <Input/>
                        )}
                        </span>
                </Form.Item>
                <Form.Item label='任务Id' className={"margin-right5"}>
                    <span>
                        {this.props.form.getFieldDecorator('taskId', {
                            initialValue: '',
                        })(
                            <Input/>
                        )}
                        </span>
                </Form.Item>
                <Form.Item label='id' className={"margin-right5"}>
                        <span>
                        {this.props.form.getFieldDecorator('id', {
                            initialValue: '',
                        })(
                            <Input/>
                        )}
                        </span>
                </Form.Item>
                <Button type='primary' htmlType='submit' className={"margin-right5"}>查询</Button>
            </Form>
        </Row>);
    }
}

export default Form.create()(DailyInitSearch);
