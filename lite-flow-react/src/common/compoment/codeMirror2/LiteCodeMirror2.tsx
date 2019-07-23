import React, {Component} from 'react'
import {Select, Icon, Modal, Upload, Row, Input, notification} from 'antd'
import {UnControlled as CodeMirror} from 'react-codemirror2';
import 'codemirror/lib/codemirror.css';

import 'codemirror/theme/material.css';
import 'codemirror/mode/cmake/cmake';
import 'codemirror/mode/xml/xml';
import 'codemirror/mode/groovy/groovy';
import 'codemirror/mode/javascript/javascript';
import 'codemirror/mode/clike/clike';
import 'codemirror/keymap/sublime';
import 'codemirror/theme/monokai.css';
import 'codemirror/theme/dracula.css';

export interface LiteCodeMirrorProps {
    theme ?: boolean;
    keyMap?: string;
    mode ?: string;
    config? : any;
    onChange ?: any;
    value ?: any;
}

class LiteLiteCodeMirror2 extends Component<LiteCodeMirrorProps, {}> {
    /**
     * 是否初始化
     * @type {boolean}
     */
    private initialized:boolean = false;

    constructor(pros){
        super(pros);

    }

    componentWillMount(){
        console.log("init code mirror");
    }


    render() {
        const that = this;
        const {value, theme, keyMap, mode, config} = this.props;

        let configOption = {};
        if(config && config.options){
            configOption = config.options;
            config["options"] = null;
        }
        const defaultOptions = {
            theme: theme ? theme : 'dracula',
            keyMap: keyMap ? keyMap : 'sublime',
            mode: mode ? mode : 'groovy',
        }
        /**
         * 属性复制
         */
        Object.assign(configOption, defaultOptions);
        let codeMirror = null;

        /**
         * 由于onchange后会触发render，所以每次重新设置value会导致光标总是移动到末尾处
         * 所以通过标志位来处理，初始化完成后，不再设置value属性
         */
        if(!this.initialized){
            this.initialized = true;
            codeMirror = (<CodeMirror
                {...config}
                value= {value ? value : ""}
                options={configOption}
                onChange={(editor, data, codeValue) => {
                    that.props.onChange(codeValue);
                }}
            />)
        }else{
            codeMirror = (<CodeMirror
                {...config}
                options={configOption}
                onChange={(editor, data, codeValue) => {
                    that.props.onChange(codeValue);
                }}
            />)
        }
        return (
            <Row>
                {codeMirror}
            </Row>
        );

    }
}

export default LiteLiteCodeMirror2;