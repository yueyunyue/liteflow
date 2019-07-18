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

export interface LiteCodeMirrorProps {
    theme ?: boolean;
    keyMap?: string;
    mode ?: string;
    config? : any;
    onChange ?: any;
    value ?: any;
}

class LiteLiteCodeMirror2 extends Component<LiteCodeMirrorProps, {}> {

    constructor(pros){
        super(pros);

    }

    componentWillMount(){
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
            theme: theme ? theme : 'monokai',
            keyMap: keyMap ? keyMap : 'sublime',
            mode: mode ? mode : 'groovy',
        }
        /**
         * 属性复制
         */
        Object.assign(configOption, defaultOptions);

        return (
            <Row>
                <CodeMirror
                    {...config}
                    value= {value ? value : ""}
                    options={configOption}
                    onChange={(editor, data, value) => {
                        that.props.onChange(value);
                    }}
                />
            </Row>
        );

    }
}

export default LiteLiteCodeMirror2;