import React, {Component} from 'react'
import {Form, Input, Modal, Select, Row, Col} from 'antd'
import {Plugin} from "../model/PluginModel";
import {ContainerModel} from "../../container/model/ContainerModel"
import {FormComponentProps} from "antd/lib/form/Form";
import {kernel} from "../../../common/utils/IOC"
import CommonUtils from "../../../common/utils/CommonUtils";
import {DynamicFormUtils} from "../../../common/utils/DynamicFormUtils";
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

const FIELD_CONFIG_MAP = "fieldConfigMap";

const FIELD_CONFIG = "fieldConfig";

const CONFIG = "config";

const CONFIG_PREFIX = CONFIG + ".";

const NAME = "name";

const LABEL = "label";

export interface ModalProps extends FormComponentProps{
    plugin: Plugin;
    onOk: any;
    onCancel: any;
}

class PluginModal extends Component<ModalProps, {allContainers, selectedContainerId, selectedFieldNames}> {

    constructor(props){
        super(props);
        this.state = {
            allContainers: [],
            selectedContainerId: null,
            selectedFieldNames: []
        }
    }
    componentWillMount(){
        const that = this;
        const containerModel = kernel.get(ContainerModel);
        containerModel.listAllContainers().then(data => {
            if (data && data.length > 1) {
                for(let container of data){
                    let containerFieldConfigs = container.fieldConfig;
                    if(containerFieldConfigs){
                        let formFieldMap = {};
                        for(let containerField of containerFieldConfigs ){
                            const name = containerField[NAME];
                            containerField[NAME] = CONFIG_PREFIX + name;
                            formFieldMap[name] = containerField;
                            container[FIELD_CONFIG_MAP] = formFieldMap;
                        }
                    }
                }
                console.log(data);
                that.setState({
                    allContainers: data
                })
            }
        });

        const plugin = this.props.plugin;
        if(plugin){
            const selectedNames = [];
            if(plugin.config){
                for(let configKey in plugin.config){
                    selectedNames.push(CONFIG_PREFIX + configKey);
                }
            }
            const containerId = plugin.containerId;
            that.setState({
                selectedFieldNames: selectedNames,
                selectedContainerId: containerId
            });
        }
    }
    /**
     * 容器变更
     * @returns {any}
     */
    onContainerChange(value){
        this.setState({
            selectedContainerId: value,
            selectedFieldNames: []
        });
    }
    /**
     * 容器字段变更
     * @returns {any}
     */
    onContainerFieldsChange(value){
        this.setState({
            selectedFieldNames: value
        });
    }
    render() {

        let isUpdate = false;
        if(this.props.plugin && this.props.plugin.id){
            isUpdate = true;
        }
        let pluginObj = this.props.plugin ? this.props.plugin : new Plugin();
        let handleOk = (e) => {
            e.preventDefault();
            this.props.form.validateFields((errors) => {
                if (errors) {
                    return
                }
                const data = {
                    ...this.props.form.getFieldsValue(),
                    id: pluginObj.id ? pluginObj.id : ''
                };
                let config = data[CONFIG];
                if(config){
                    data[CONFIG] = JSON.stringify(config);
                }else{
                    data[CONFIG] = "";
                }

                this.props.onOk(data)
            })
        };
        const modalOpts = {
                title: isUpdate ? '编辑插件' : '添加插件',
                visible: true,
                maskClosable: false,
                width: modalWidth,
                onOk: handleOk,
                onCancel: this.props.onCancel
            };

        /**
         * 初始化option
         */
        const {selectedFieldNames, allContainers, selectedContainerId} = this.state;

        let containerOptions = [];
        if (allContainers) {
            for(let container of allContainers){
                containerOptions.push(<option key={container.id + ""  }>{container.name}</option>);
            }
        }


        /**
         * containerFieldOption
         */
        let containerFieldOptions = [];
        let pluginAddedFieldConfig = [];

        if (allContainers) {

            for(let container of allContainers){
                if(container.id == selectedContainerId){
                    const containerFieldConfigArray = container[FIELD_CONFIG];
                    if(containerFieldConfigArray && containerFieldConfigArray.length > 0){
                        for(let fcConfig of containerFieldConfigArray){
                            const name = fcConfig[NAME];
                            const label = fcConfig[LABEL];

                            let fieldExist = false;
                            if(selectedFieldNames){
                                for(let fieldName of selectedFieldNames){
                                    if(fieldName == name){
                                        fieldExist = true;
                                        break;
                                    }
                                }
                            }
                            if(fieldExist){
                                pluginAddedFieldConfig.push(fcConfig);
                            }
                            containerFieldOptions.push(<option key={name}>{label}</option>);
                        }
                    }
                    break;
                }
            }
        }
        let fieldSelectedDoms = [];
        fieldSelectedDoms.push(<Form.Item label='容器参数' hasFeedback {...formItemLayout}>
            {this.props.form.getFieldDecorator("containerFields-" + selectedContainerId, {
                initialValue: selectedFieldNames,
                rules: [
                    {
                        required: false,
                        message: '不能为空'
                    }
                ]
            })(<Select
                allowClear={true}
                onChange={this.onContainerFieldsChange.bind(this)}
                mode="multiple">
                {containerFieldOptions}
            </Select>)}
        </Form.Item>);

        let dynamicDoms = [];
        const isDomEdit = isUpdate && pluginObj["status"] == EnumUtils.statusOnline;
        if(pluginAddedFieldConfig){
            for(let formField of pluginAddedFieldConfig){
                let dom = DynamicFormUtils.getComponent({
                    property: formField,
                    model: pluginObj,
                    isEdit: isDomEdit,
                    formParent: this,
                    layout: {
                        labelCol: {
                            span: 4
                        },
                        wrapperCol: {
                            span: 20
                        }
                    }
                });
                if(dom){
                    dynamicDoms.push(dom);
                }
            }
        }


        return (<Modal {...modalOpts}>
            <Form layout={'horizontal'} >
                <Row >
                    <Col span={12}>
                        <Form.Item label='名称：' hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator('name', {
                                initialValue: CommonUtils.getStringValueFromModel("name", pluginObj, ""),
                                rules: [
                                    {
                                        required: true,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Input/>)}
                        </Form.Item>
                        <Form.Item label='容器' hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator('containerId', {
                                initialValue: CommonUtils.getStringValueFromModel("containerId", pluginObj, ""),
                                rules: [
                                    {
                                        required: true,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Select onChange={this.onContainerChange.bind(this)}>
                                {containerOptions}
                            </Select>)}
                        </Form.Item>

                        {fieldSelectedDoms}

                        {dynamicDoms}

                        <Form.Item label='描述' hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator('description', {
                                initialValue: CommonUtils.getStringValueFromModel("description", pluginObj, ""),
                                rules: [
                                    {
                                        required: false,
                                        message: '不能为空'
                                    }
                                ]
                            })(<Input.TextArea rows={6}/>)}
                        </Form.Item>
                    </Col>
                    <Col span={12}>
                        <Form.Item label='参数' hasFeedback {...formItemLayout}>
                            {this.props.form.getFieldDecorator('fieldConfig', {
                                initialValue: CommonUtils.getStringValueFromModel("fieldConfig", pluginObj, ""),
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

export default Form.create()(PluginModal);