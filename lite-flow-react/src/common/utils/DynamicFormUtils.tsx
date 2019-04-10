import React, {Component} from 'react';
import {Form, Input, Modal, Select, Tabs, Radio, InputNumber, Upload, Button, Icon} from 'antd'
import CommonUtils from './CommonUtils'

const defaultLabelCol = 6;
const defaultWrapperCol = 14;

export class CommonDynamicFormUtils {
    /**
     * 通过参数获取表单
     * @param param
     * @returns {any}
     */
    getComponent(param){

        const {property, formParent, isEdit, model, layout} = param;
        let dom = null;
        let disabled = false;

        if(isEdit && (property.editable != null && property.editable == false)){
            disabled = true;
        }

        let formLayout = layout;
        if(formLayout){
            if(!formLayout.labelCol && !formLayout.wrapperCol) {
                formLayout["labelCol"] = {span: defaultLabelCol};
                formLayout["wrapperCol"] = {span: defaultWrapperCol};
            }
        }else{
            formLayout = {};
            formLayout["labelCol"] = {span : defaultLabelCol};
            formLayout["wrapperCol"] = {span : defaultWrapperCol} ;
        }

        formLayout["label"] = property.label;

        if(property.help){
            formLayout["help"] = property.help;
        }
        const componentConfig = property.componentConfig ? property.componentConfig : {};

        switch (property.type) {
            case "Input":
                dom = (<Form.Item {...formLayout} >
                    {formParent.props.form.getFieldDecorator(property.name, {
                        initialValue: CommonUtils.getValueFromModel(property.name, model, property.defaultValue),
                        rules: [
                            {
                                required: property.required,
                                message: property.message ? property.message : ''
                            }
                        ]
                    })(<Input {...componentConfig} disabled={disabled}/>)}
                </Form.Item>)
                break;
            case "InputNumber":
                dom = (<Form.Item {...formLayout} >
                    {formParent.props.form.getFieldDecorator(property.name, {
                        initialValue: CommonUtils.getValueFromModel(property.name, model, property.defaultValue),
                        rules: [
                            {
                                required: property.required,
                                message: property.message ? property.message : ''
                            }
                        ]
                    })(<InputNumber {...componentConfig} disabled={disabled}/>)}
                </Form.Item>)
                break;
            case "Select":
                let options = [];
                if (property.children && property.children.length > 0) {
                    for (let item in property.children) {
                        options.push(<Select.Option key={property.children[item].id}
                                                    value={property.children[item].id + ""}>{property.children[item].name}</Select.Option>);
                    }
                }
                dom = (
                    <Form.Item {...formLayout}>
                        {formParent.props.form.getFieldDecorator(property.name, {
                            initialValue: CommonUtils.getStringValueFromModel(property.name, model, property.defaultValue),
                            rules: [
                                {
                                    required: property.required,
                                    message: property.message ? property.message : ''
                                }
                            ]
                        })(<Select {...componentConfig} disabled={disabled}>
                            {options}
                        </Select>)}
                    </Form.Item>);
                break;
            case "TextArea":
                dom = (<Form.Item {...formLayout} >
                    {formParent.props.form.getFieldDecorator(property.name, {
                        initialValue: CommonUtils.getValueFromModel(property.name, model, property.defaultValue),
                        rules: [
                            {
                                required: property.required,
                                message: property.message ? property.message : ''
                            }
                        ]
                    })(<Input.TextArea {...componentConfig} disabled={disabled} />)}
                </Form.Item>)
                break;
        }
        return dom;
    }

    /**
     * 批量获取表单
     * @param params
     * @returns {any[]}
     */
    getComponents(params) {
        let doms = [];
        for (let param of params) {
            let dom = this.getComponent(param);
            if(dom){
                doms.push(dom);
            }
        }
        return doms;
    }

}

export const DynamicFormUtils = new CommonDynamicFormUtils();