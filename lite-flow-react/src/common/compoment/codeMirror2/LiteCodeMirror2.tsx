import React, {Component} from 'react'
import {Select, Icon, Modal, Upload, Row, Input, notification} from 'antd'
import {requestGetNoShowMsg} from "../../utils/Request";
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
    keyMap: string;
    mode : string;
    config? : any;
    onChange ?: any;
    value ?: any;
}
export interface LiteCodeMirrorState {
    fileList: any;
    showText: boolean;
    clickedFile: any;
    textContent ?: string;
    value ?: any;
}

async function getFileContent(requestUrl, url) {
    return requestGetNoShowMsg(requestUrl, {"url": url});
}

class LiteLiteCodeMirror2 extends Component<LiteCodeMirrorProps, LiteCodeMirrorState> {

    constructor(pros){
        super(pros);

    }

    componentWillMount(){
        this.setState( {showText: false, textContent:"",clickedFile: null, fileList: []});
        const {value} = this.props;
        if(value && value.length > 0){

        }
    }


    render() {
        const that = this;
        const {fileList, showText, textContent} = this.state;
        const {value, theme, keyMap, mode} = this.props;

        const options = {
            theme: theme ? theme : 'monokai',
            keyMap: keyMap ? keyMap : 'sublime',
            mode: mode ? mode : 'groovy',
        }

        return (
            <Row>
                <CodeMirror
                    value= {value ? value : ""}
                    options={options}
                    onChange={(editor, data, value) => {
                        that.props.onChange(value);
                    }}
                />
            </Row>
        );
    }
}

export default LiteLiteCodeMirror2;