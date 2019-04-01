import React, {Component} from 'react';
import {Form, Input, Modal, Tabs, Radio, InputNumber, Upload, Button, Icon, Select} from 'antd'
import CommonUtils from './CommonUtils'
import {Kernel} from "inversify";

const defaultLabelCol = 6;
const defaultWrapperCol = 14;

export class CommonDomUtils {

   getSelectOptions(params, hasAll: boolean=false){
        let options = [];
        if(!params){
            return null;
        }
        if(hasAll){
            options.push(<Select.Option key={"option-all"} value={""}>全部</Select.Option>);
        }
        for(let param of params){
            options.push(<Select.Option key={"" + param.id} value={"" + param.id}>{param.name}</Select.Option>);
        }
        return options;
    }

    getSelectOptionsWithoutAll(params){
       return this.getSelectOptions(params, false);
    }

    getSelectOptionsWithAll(params){
       return this.getSelectOptions(params, true);
    }



}

export const DomUtils = new CommonDomUtils();
