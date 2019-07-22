import React, {Component} from 'react'
import {Select, Icon, Modal, Upload, Row, Input, notification} from 'antd'
import {requestGetNoShowMsg} from "../../utils/Request";
import fetch from "isomorphic-fetch";
import {array, instanceOf} from "prop-types";
const { TextArea } = Input;
import ResultUtils from "../../../common/utils/ResultUtils";

export interface FileUploadProps {
    multiply : boolean;
    disable ?: boolean;
    url: string;
    showTextContentUrl : string;
    downloadUrl : string;
    onChange ?: any;
    value ?: any;
}
export interface FileUploadState {
    fileList: any;
    showText: boolean;
    clickedFile: any;
    textContent ?: string;
    value ?: any;
}

async function getFileContent(requestUrl, url) {
    return requestGetNoShowMsg(requestUrl, {"url": url});
}

/**
 * 获取文件名
 */
const liteAttactment = "liteAttachment://";
const getFileName = (file) => {
    if(!file){
        return "";
    }
    if(file.startsWith(liteAttactment)){
        try{
            const urlArray = file.split("?");
            const param = urlArray[1];
            const paramArray = param.split("&");
            for(let paramItem of paramArray){
                if(paramItem.startsWith("name=")){
                    return paramItem.split("=")[1];
                }
            }
        }catch (e) {
            return "error";
        }
    }else{
        return file.split("/").pop();
    }
    return "";
}

/**
 * 验证file是不是文本类型
 * @param url
 * @returns {boolean}
 */
const TEXT_FILE_SUFFIX = [".sql", ".txt", ".json"];
const checkIsTextFile = (url) => {
    if(url){
        for(let fileSuffix of TEXT_FILE_SUFFIX){
            if(url.endsWith(fileSuffix)){
                return true;
            }
        }
    }
    return false;
}

const getFileObjs = (fileUrls) => {
    if(!fileUrls){
        return null;
    }
    let result = [];
    let fileArray = fileUrls.split(",");
    for(let fileUrl of fileArray){
        let fileName = getFileName(fileUrl);
        let fileObj = {
            uid: fileUrl,
            status: "done",
            name: fileName,
            url: fileUrl ,
        };
        result.push(fileObj);
    }
    return result;
}

class LiteFileUpload extends Component<FileUploadProps, FileUploadState> {

    constructor(pros){
        super(pros);

    }

    componentWillMount(){
        this.setState( {showText: false, textContent:"",clickedFile: null, fileList: []});
        const {value} = this.props;
        if(value && value.length > 0){
            this.updateFiles(getFileObjs(value));
        }
    }

    /**
     * 更新文件
     * @param fileList
     */
    updateFiles(fileList){
        this.setState({fileList: fileList});
        if(fileList && fileList.length > 0){
            const urlArray = fileList.map(file => {
                return file["url"];
            })
            this.props.onChange(urlArray.join(","));
        }else{
            this.props.onChange(null);
        }
    }

    handleModalCancel = () => this.setState({showText: false})

    /**
     * 预览文本
     * @param file
     */
    handlePreview = (file) => {
        if(!file){
            return;
        }
        const that = this;
        const {downloadUrl, showTextContentUrl} = this.props;
        let showText = false;
        const url = file["url"];
        if(file && checkIsTextFile(url)){
            showText = true;
            getFileContent(showTextContentUrl, url).then(result => {
                if(ResultUtils.isSuccess(result)){
                    that.setState({
                        clickedFile: file,
                        showText: true,
                        textContent: result["data"]
                    });
                }
            });

        }
        /**
         * 非文本直接返回
         */
        if(!showText){
            window.location.href = downloadUrl + "?url=" + url;
        }
    }

    handleChange = (data) => {
        let {file} = data;
        const that = this;
        let {fileList} = that.state;
        const uid = file.uid;
        if(file.status == "removed"){
            fileList = fileList.filter((img) => {
                return img.uid != uid;
            });
            that.updateFiles(fileList);
        }
        console.dir(data)
    }

    beforeUpload = (file) => {
        const that = this;
        const {url} = this.props;
        const formData = new FormData();
        formData.append("file", file);
        fetch(url, {
            method: 'POST',
            body: formData
        }).then(response => {
            response.json().then(result => {
                if(ResultUtils.isSuccess(result)){
                    let fileUrls = ResultUtils.getData(result);
                    if(fileUrls instanceof Array && fileUrls.length > 0){
                        that.updateFiles(getFileObjs(fileUrls.join(",")));
                        return;
                    }
                    if(fileUrls instanceof String){
                        that.updateFiles(getFileObjs(fileUrls));
                        return;
                    }

                }else {
                    notification["error"]({
                        message: '上传异常',
                        description: result["data"]
                    });
                }
            });
        })
        return false;
    }

    render() {
        const {fileList, showText, textContent} = this.state;
        const {multiply, disable} = this.props;
        /**
         * 单个上传的，如果已经添加需要
         * @type {boolean}
         */
        let showAdd = true;
        if(!multiply && fileList && fileList.length > 0){
            showAdd = false;
        }

        return (
            <Row>
                <Upload
                    disabled={disable ? disable : false}
                    listType="text"
                    multiple={multiply ? multiply : false}
                    fileList={fileList}
                    onPreview={this.handlePreview}
                    beforeUpload={this.beforeUpload}
                    onChange={this.handleChange}>
                    {showAdd ? <a href={"javascript:void(0)"}>添加</a> : ""}
                </Upload>
                <Modal title={"内容"} visible={showText} footer={null} onCancel={this.handleModalCancel} maskClosable={false}>
                   <TextArea rows={20} value={showText && textContent ? textContent : ""}>
                   </TextArea>
                </Modal>
            </Row>
        );
    }
}

export default LiteFileUpload;