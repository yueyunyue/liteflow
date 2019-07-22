import React, {Component} from 'react'
import {Form, Modal, Row, Button, Select, DatePicker, Col} from 'antd'
import {Flow, FlowModel} from "../model/FlowModel";
import DagVersionFixShow from "./dagfix/DagVersionFixShow";
import {kernel} from "../../../common/utils/IOC";
import {FormComponentProps} from "antd/lib/form/Form";
import Moment from "moment/moment";
import ResultUtils from "../../../common/utils/ResultUtils";

const {RangePicker} = DatePicker;

const COMMON_TIME_FORMAT = "YYYY-MM-DD HH:mm:ss";
const Option = Select.Option;

export interface ModalProps extends FormComponentProps {
    flow: Flow;
    onCancel: any;
}

const disabledTime = () => {
    const current = Moment();
    let tomorrow = Moment().add(1,"day");
    return current && current.isAfter(tomorrow);
}
export interface ModalState {
    selectedVersionNo: number;
    versionNos: any;
}

const formItemLayout = {
    labelCol: {
        span: 6
    },
    wrapperCol: {
        span: 14
    },
    style: {
        width: 260
    }
};

class FlowFixModal extends Component<ModalProps, ModalState> {

    private flowModel: FlowModel;

    constructor(props) {
        super(props);
        this.state = {
            selectedVersionNo: null,
            versionNos: []
        };
    }

    componentWillMount() {
        this.flowModel = kernel.get(FlowModel);
        const that = this;
        this.flowModel.fixGetLatestVersionNos(this.props.flow.id).then(result => {
            if (ResultUtils.isSuccess(result)) {
                const versionNos = ResultUtils.getData(result);
                if (versionNos && versionNos.length > 0) {
                    const selectedVersionNo = versionNos[versionNos.length - 1];
                    that.setState({
                        selectedVersionNo: selectedVersionNo,
                        versionNos: versionNos
                    });
                }
            }
        });
    }

    /**
     * 时间选择
     * @param data
     */
    timeRangeChange(data){
        const that = this;
        const startTime = data[0];
        const endTime = data[1];

        const {flow} = this.props;
        this.flowModel.fixGetVersionNos(flow.id, startTime.format(COMMON_TIME_FORMAT), endTime.format(COMMON_TIME_FORMAT)).then(result =>{
            if(ResultUtils.isSuccess(result)){
                const versionNos = ResultUtils.getData(result);
                that.setState({
                    versionNos: versionNos,
                    selectedVersionNo: null
                });
            }
        });

    }

    render() {
        const that = this;
        const {selectedVersionNo, versionNos} = this.state;
        const height = document.body.clientHeight - 110;
        const width = document.body.clientWidth - 60;
        const modalOpts = {
            title: `${this.props.flow.name}-${selectedVersionNo}`,
            visible: true,
            maskClosable: false,
            width: width,
            style: {top: 10, height: height},
            footer: "",
            onCancel: this.props.onCancel
        };


        const fixDagProps = () => {
            return {
                flow: this.props.flow,
                height: height,
                firstTaskVersionNo: selectedVersionNo
            }
        }

        /**
         * select筛选
         */



        /**
         * dag修复component
         * @type {any[]}
         */
        let dagView = [];
        if (selectedVersionNo && selectedVersionNo > 0) {
            dagView.push(<DagVersionFixShow key={"fix-show-" + selectedVersionNo} {...fixDagProps()}></DagVersionFixShow>)
        }

        const versionNoOption = [];
        const versionNoSelected = [];
        if(versionNos){
            for(let versionNo of versionNos){
                if(selectedVersionNo == versionNo){
                    versionNoSelected.push(selectedVersionNo);
                }
                versionNoOption.push(<Option key={versionNo} value={versionNo + ""}>{versionNo}</Option>);
            }
        }

        let handleOk = (e) => {
            e.preventDefault();
            this.props.form.validateFields((errors) => {
                if (errors) {
                    return
                }

                const data = {
                    ...this.props.form.getFieldsValue()
                };
                that.setState({selectedVersionNo: data["selectedVersionNo"]})
            })
        };

        return (
            <Modal {...modalOpts}>
                <Row className={"container-row-block"}>
                    <Col span={12}>
                        <RangePicker
                            showTime={{format: 'HH:mm:ss'}}
                            format="YYYY-MM-DD HH:mm:ss"
                            placeholder={['开始时间', '结束时间']}
                            onOk={this.timeRangeChange.bind(this)}
                        />
                    </Col>
                    <Col span={12}>
                        <Form layout={'inline'} className={"float-right"} onSubmit={handleOk}>
                            <Form.Item label="任务版本" className={"margin-right5"} {...formItemLayout} key={"version-no-select-" + selectedVersionNo}>
                                {this.props.form.getFieldDecorator("selectedVersionNo", {
                                    initialValue: versionNoSelected,
                                    rules: [
                                        {
                                            required: false,
                                            message: '不能为空'
                                        }
                                    ]
                                })(<Select>
                                    {versionNoOption}
                                </Select>)}
                            </Form.Item>
                            <Button type='primary' htmlType='submit' className={"margin-right5"}>查询</Button>
                        </Form>
                    </Col>
                </Row>
                <Row className={"container-row-block"}>
                    {dagView}
                </Row>
            </Modal>
        );
    }
}

export default Form.create()(FlowFixModal);