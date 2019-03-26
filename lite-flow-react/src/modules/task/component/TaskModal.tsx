import React, {Component} from 'react'
import {Form, Input, Modal, Select, Row, Col, InputNumber, Radio} from 'antd'
import {Task} from "../model/TaskModel";
import {FormComponentProps} from "antd/lib/form/Form";
import CommonUtils from "../../../common/utils/CommonUtils";
import {DynamicFormUtils} from "../../../common/utils/DynamicFormUtils";
import {DomUtils} from "../../../common/utils/DomUtils";
import EnumUtils from "../../../common/utils/EnumUtils";
import {kernel} from "../../../common/utils/IOC"
import {PluginModel} from "../../plugin/model/PluginModel";

const formItemLayout = {
    labelCol: {
        span: 6
    },
    wrapperCol: {
        span: 14
    }
};

export interface ModalProps extends FormComponentProps {
    task: Task;
    onOk: any;
    onCancel: any;
}

const modalWidth = 1000;

class TaskModal extends Component<ModalProps, { retry, concurrency, pluginId, pluginMap }> {


    constructor(props) {
        super(props);
    }

    componentWillMount() {
        const that = this;
        const taskObj = this.getTaskObj();
        this.setState({
            retry: CommonUtils.getValueFromModel("isRetry", taskObj, 0),
            concurrency: CommonUtils.getValueFromModel("isConcurrency", taskObj, 0),
            pluginId: CommonUtils.getValueFromModel("pluginId", taskObj, 1),
            pluginMap: {}
        });
        /**
         * 获取插件
         * @type {any}
         */
        const pluginModel = kernel.get(PluginModel);
        pluginModel.listAllValid().then(data => {
            if(data && data.length > 0){
                let pluginMap = {};
                for(let plugin of data){
                    pluginMap[plugin.id] = plugin;
                }
                that.setState({
                    pluginMap: pluginMap
                });
                console.log("pluginMap=" + JSON.stringify(pluginMap));
            }
        });
    }

    getTaskObj() {
        let taskObj = this.props.task ? this.props.task : new Task;
        return taskObj;
    }

    /**
     * 获取周期
     * @returns {any[]}
     */
    getPeriodOptions() {
        const periods = EnumUtils.getPeriodOptionArray();
        let options = DomUtils.getSelectOptionsWithNoAll(periods);
        return options;
    }
    /**
     * 重试
     */
    onRetryChange(object) {
        this.setState({
            retry: object.target.value
        });
    }

    /**
     * 获取重试配置
     * @param retryValue
     * @returns {any}
     */
    getRetryDoms(retryValue) {
        let doms = [];
        if (retryValue != 1) {
            return null;
        }
        const taskObj = this.getTaskObj();
        doms.push(<Form.Item label='重试次数' hasFeedback {...formItemLayout} help={"失败后重试次数"}>
            {this.props.form.getFieldDecorator('retryConf.retryNum', {
                initialValue: CommonUtils.getValueFromModel("retryConf.retryNum", taskObj, 1),
                rules: [
                    {
                        required: true,
                        message: '不能为空'
                    }
                ]
            })(<InputNumber min={1} max={5}/>)}
        </Form.Item>)

        doms.push(<Form.Item label='重试周期' hasFeedback {...formItemLayout} help={"失败后，下一次重试与当前时间的间隔"}>
            {this.props.form.getFieldDecorator('retryConf.retryPeriod', {
                initialValue: CommonUtils.getValueFromModel("retryConf.retryPeriod", taskObj, 1),
                rules: [
                    {
                        required: true,
                        message: '不能为空'
                    }
                ]
            })(<InputNumber min={1}/>)}
        </Form.Item>)

        return doms;
    }

    /**
     * 并发
     * @param object
     */
    onConcurrencyChange(object) {
        this.setState({
            concurrency: object.target.value
        });
    }


    getConcurrencyDoms(concurrency, isDomDisable) {
        if (concurrency != 0) {
            return [];
        }
        const taskObj = this.getTaskObj();
        let doms = [];
        const concurrencyArray = EnumUtils.getConcurrencyOptionArray();
        let options = DomUtils.getSelectOptionsWithNoAll(concurrencyArray);
        doms.push(<Form.Item label={"运行策略"} hasFeedback {...formItemLayout}>
            {this.props.form.getFieldDecorator("concurrentStrategy", {
                initialValue: CommonUtils.getStringValueFromModel("concurrentStrategy", taskObj, "1"),
                rules: [
                    {
                        required: true,
                        message: '不能为空'
                    }
                ]
            })(<Select disabled={isDomDisable}>
                {options}
            </Select>)}
        </Form.Item>);

        return doms;
    }

    /**
     * 插件
     * @returns {any}
     */
    onPluginChange(value){
        this.setState({
            pluginId: value
        });
    }

    getPluginDoms(pluginId, isEdit, model){
        const { pluginMap } = this.state;
        const pluginInfo = pluginMap[pluginId];
        if(!pluginInfo){
            return null;
        }
        const formFields = pluginInfo["fieldConfig"];
        let doms = [];
        const isDomEdit = isEdit && model["status"] == EnumUtils.statusOnline;
        if(formFields && formFields.length > 0){
            for(let formField of formFields){
                let dom = DynamicFormUtils.getComponent({
                    property: formField,
                    model: model,
                    isEdit: isDomEdit,
                    formParent: this
                });
                if(dom){
                    doms.push(dom);
                }
            }
        }
        return doms;
    }

    render() {

        let isUpdate = false;
        if (this.props.task && this.props.task.id) {
            isUpdate = true;
        }
        let taskObj = this.getTaskObj();
        let handleOk = (e) => {
            e.preventDefault();
            this.props.form.validateFields((errors) => {
                if (errors) {
                    return
                }
                const data = {
                    ...this.props.form.getFieldsValue(),
                    id: taskObj.id ? taskObj.id : ''
                };
                let pluginConf = data["pluginConf"];
                if(pluginConf){
                    data["pluginConf"] = JSON.stringify(pluginConf);
                }else{
                    data["pluginConf"] = "";
                }
                let retryConf = data["retryConf"];
                if(retryConf){
                    data["retryConf"] = JSON.stringify(retryConf);
                }else{
                    data["retryConf"] = "";
                }
                this.props.onOk(data)
            })
        };
        const taskModalOpts = {
            title: isUpdate ? '编辑任务' : '添加任务',
            visible: true,
            maskClosable: false,
            width: modalWidth,
            style: {top: 10},
            onOk: handleOk,
            onCancel: this.props.onCancel
        };
        const {retry, concurrency, pluginId} = this.state;


        /**
         * 是否可以编辑
         * @type {boolean}
         */
        let isEditable = true;
        if(isUpdate && taskObj){
           const status = taskObj["status"];
           if(status == EnumUtils.statusOnline){
               isEditable = false;
           }
        }
        const isDomDisable = !isEditable;
        /**
         * 周期
         */
        let periodOptions = this.getPeriodOptions();
        /**
         * 重试
         * @type {any[]}
         */
        let retryDoms = this.getRetryDoms(retry);
        /**
         * 并发
         * @type {any[]}
         */
        let concurrentDoms = this.getConcurrencyDoms(concurrency, isDomDisable);

        /**
         * 插件
         * @type {any[]}
         */
        const {pluginMap} = this.state;
        let plugins = [];
        if(pluginMap){
            for(let pluginId in pluginMap){
                plugins.push(pluginMap[pluginId]);
            }
        }
        let pluginOptions = DomUtils.getSelectOptionsWithNoAll(plugins);
        let pluginDoms = this.getPluginDoms(pluginId, isUpdate, taskObj);

        return (<Modal {...taskModalOpts}>
            <Form layout={'horizontal'}>
                <Row>
                    <Col span={12}>
                        <Form.Item label='名称' hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator('name', {
                                initialValue: CommonUtils.getStringValueFromModel("name", taskObj, ""),
                                rules: [
                                    {
                                        required: true,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Input disabled={isUpdate}/>)}
                        </Form.Item>
                        <Form.Item label="周期" hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator("period", {
                                initialValue: CommonUtils.getStringValueFromModel("period", taskObj, "4"),
                                rules: [
                                    {
                                        required: true,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Select disabled={isDomDisable}>
                                {periodOptions}
                            </Select>)}
                        </Form.Item>
                        <Form.Item label='cron表达式' hasFeedback {...formItemLayout} help={'cron表达式,例如"0 1 * * ?",每天1点'}>
                            {this.props.form.getFieldDecorator('cronExpression', {
                                initialValue: CommonUtils.getStringValueFromModel("cronExpression", taskObj, "0 1 * * ?"),
                                rules: [
                                    {
                                        required: false,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Input disabled={isDomDisable}/>
                            )}
                        </Form.Item>
                        <Form.Item label='是否并发' hasFeedback {...formItemLayout} help={"任务是否可以并行运行"}>
                            {this.props.form.getFieldDecorator('concurrency', {
                                initialValue: CommonUtils.getValueFromModel("concurrency", taskObj, 0),
                                rules: [
                                    {
                                        required: true,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Radio.Group onChange={this.onConcurrencyChange.bind(this)} disabled={isDomDisable}>
                                <Radio value={0}>否</Radio>
                                <Radio value={1}>是</Radio>
                            </Radio.Group>)}
                        </Form.Item>
                        {concurrentDoms}

                        <Form.Item label='失败重试' hasFeedback {...formItemLayout} help={"任务失败后是否重试"}>
                            {this.props.form.getFieldDecorator('isRetry', {
                                initialValue: CommonUtils.getValueFromModel("isRetry", taskObj, 0),
                                rules: [
                                    {
                                        required: true,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Radio.Group onChange={this.onRetryChange.bind(this)}>
                                <Radio value={0}>否</Radio>
                                <Radio value={1}>是</Radio>
                            </Radio.Group>)}
                        </Form.Item>
                        {retryDoms}

                        <Form.Item label='最长运行时间' hasFeedback {...formItemLayout} help={"单位：分,-1代表没有限制"}>
                            {this.props.form.getFieldDecorator('maxRunTime', {
                                initialValue: CommonUtils.getStringValueFromModel("maxRunTime", taskObj, -1),
                                rules: [
                                    {
                                        required: false
                                    }
                                ]
                            })(<InputNumber min={-1}/>)}
                        </Form.Item>
                        <Form.Item label='报警邮箱' hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator('alarmEmail', {
                                initialValue: CommonUtils.getStringValueFromModel("alarmEmail", taskObj, ""),
                                rules: [
                                    {
                                        required: false,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Input/>)}
                        </Form.Item>
                        <Form.Item label='报警电话' hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator('alarmPhone', {
                                initialValue: CommonUtils.getStringValueFromModel("alarmPhone", taskObj, ""),
                                rules: [
                                    {
                                        required: false,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Input/>)}
                        </Form.Item>
                        <Form.Item label='描述' hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator('description', {
                                initialValue: CommonUtils.getStringValueFromModel("description", taskObj, ""),
                                rules: [
                                    {
                                        required: false,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Input.TextArea/>)}
                        </Form.Item>
                    </Col>
                    <Col span={12}>
                        <Form.Item label={"插件"} hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator("pluginId", {
                                initialValue: CommonUtils.getStringValueFromModel("pluginId", taskObj, 1),
                                rules: [
                                    {
                                        required: true,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Select onChange={this.onPluginChange.bind(this)} disabled={isDomDisable}>
                                {pluginOptions}
                            </Select>)}
                        </Form.Item>

                        {pluginDoms}
                    </Col>
                </Row>
            </Form>
        </Modal>);
    }
}

export default Form.create()(TaskModal);