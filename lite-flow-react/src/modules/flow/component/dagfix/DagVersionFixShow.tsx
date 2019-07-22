import React, {Component} from 'react';
import {Row, Col, notification, Input, Button, Popconfirm} from 'antd'
import * as dagreD3 from 'dagre-d3/dist/dagre-d3'
import d3 from 'd3';
import './dagFix.less';
import * as ReactDOM from 'react-dom';
import EnumUtils from "../../../../common/utils/EnumUtils";
import CommonUtils from "../../../../common/utils/CommonUtils";
import {Flow, FlowModel} from "../../model/FlowModel";
import {kernel} from "../../../../common/utils/IOC";
import {TaskVersionModel} from "../../../taskVersion/model/TaskVersionModel";
import ResultUtils from "../../../../common/utils/ResultUtils";

/**
 * 获取鼠标位置
 * @returns {{x: any; y: any}}
 */
const getMousePosition = () => {
    let e = window.event;
    let scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
    let scrollY = document.documentElement.scrollTop || document.body.scrollTop;
    let x = e["pageX"] || e["clientX"] + scrollX;
    let y = e["pageY"] || e["clientY"] + scrollY;
    return {'x': x + 10, 'y': y + 10};
}

export interface DagFixProps {
    height: number;                          //宽度
    flow: Flow;                              //任务流
    firstTaskVersionNo: number;              //数据
}

/***
 * 验证任务是否正确
 * @param dagDatas
 * @returns {boolean}
 */
const checkDatas = (dagDatas) => {
    if (!dagDatas || !dagDatas["nodes"]) {
        return false;
    }
    return true;
}

/**
 * 获取点击事件
 * @param isPreventDefault
 * @returns {Event | undefined}
 */
const getEvent = (isPreventDefault) => {
    const event = window.event;
    if (isPreventDefault && isPreventDefault == true) {
        event.preventDefault ? event.preventDefault() : (event.returnValue = false);
    }
    return event;

};

/**
 * 任务流修复
 */
class DagVersionFixShow extends Component<DagFixProps, any> {

    private flowModel: FlowModel;

    private taskVersionModel: TaskVersionModel;

    private dagData: any = {};

    private nodeId: number = 0;

    constructor(pros) {
        super(pros);
        this.state = {
            labelTop: 0,
            labelLeft: 0,
            //node右键菜单
            showMenu: false,
            nodeTop: 0,
            nodeLeft: 0,
            //任务详情
            detailTop: 0,
            detailLeft: 0,
            showDetail: false
        };
    }

    componentWillMount() {
        this.flowModel = kernel.get(FlowModel);
        this.taskVersionModel = kernel.get(TaskVersionModel);
        this.getDataAndRender();
    }

    /**
     * 获取数据并画图
     */
    getDataAndRender() {
        let that = this;
        let id = this.props.flow.id;
        const {flow, firstTaskVersionNo} = this.props;
        this.flowModel.fixViewDag(flow.id, firstTaskVersionNo).then((result) => {
            if (ResultUtils.isSuccess(result)) {
                that.hideAllWindow();
                const resultData = ResultUtils.getData(result);
                that.dagData = resultData;
                that.renderDag(resultData);
            }
        });
    }

    /**
     * 隐藏窗口
     */
    hideAllWindow = () => {
        this.setState({showMenu: false, showDetail: false, showLabelMenu: false})
    }

    /**
     * 渲染dag图
     */
    renderDag = (dagData) => {
        let that = this;
        if (!checkDatas(dagData)) {
            return;
        }
        let nodes = dagData["nodes"];
        let links = dagData["links"];
        // 获取dom节点
        const svgDOMNode = ReactDOM.findDOMNode(this.refs.dag);
        const svg = d3.select(svgDOMNode);

        svg.selectAll("*").remove();

        let inner = svg.append("g");
        const renderDag = new dagreD3.render();

        //创建graph对象
        let conf = {
            labelpos: "l",
            nodesep: 70,
            ranksep: 50,
            marginx: 20,
            marginy: 20
        };
        let g = new dagreD3.graphlib.Graph()
            .setGraph(conf);
        //添加节点
        for (let node of nodes) {
            g.setNode(node.id, {
                label: node.taskName,
                width: 266,
                class: "dag-fix-node-" + node.finalStatus
            });

        }

        // 添加节点关系
        if (links && links.length > 0) {
            for (let link of links) {
                g.setEdge(link.upstreamVersionId, link.versionId, {
                    label: "",
                    labelStyle: 'width: 25px;height: 25px;text-align: center;line-height: 25px;'
                });

            }
        }


        //每个node添加radus
        g.nodes().forEach(function (v) {
            let node = g.node(v);
            //每个节点
            node.rx = node.ry = 5;
        });


        // render画布
        renderDag(inner, g);

        // 放大缩小、拖拽
        let zoom = d3.behavior.zoom().on("zoom", function () {
            inner.attr("transform", "translate(" + d3.event.translate + ")" +
                "scale(" + d3.event.scale + ")");
        });
        svg.call(zoom);


        /**
         * 右键处理
         */
        svg.selectAll("g.node").on("contextmenu", function (id) {
            const event = getEvent(true);
            let btnCode = event["button"];
            if (btnCode != 2) {
                return;
            }
            let position = getMousePosition();

            that.nodeId = id;    //设置节点id

            that.setState({
                showDetail: false,//隐藏详情
                showMenu: true,
                nodeTop: position["y"],
                nodeLeft: position["x"]
            });

        });
        /**
         * 鼠标移动到node节点时
         */
        svg.selectAll("g.node").on("mouseenter", function (id) {
            const position = getMousePosition();

            that.nodeId = id;    //设置节点id

            that.setState({
                showDetail: true,
                detailTop: position["y"],
                detailLeft: position["x"]
            });

        });
        /**
         * 鼠标移出
         */
        svg.selectAll("g.node").on("mouseleave", function (id) {
            that.setState({
                showDetail: false
            });
        });
        svg.on("click", function () {
            let e = window.event;
            let btnCode = e["button"];
            if (btnCode == 0) {
                that.hideAllWindow();
            }
        });

        //画布居中
        let svgWidth = svg.attr("width") ? svg.attr("width") : svg.style("width");
        let psIndex = svgWidth.indexOf("px");
        if (psIndex >= 0) {
            svgWidth = svgWidth.substring(0, psIndex);
        }
        const initialScale = 0.8;
        zoom
            .translate([(parseInt(svgWidth) - g.graph().width * initialScale) / 2, 20])
            .scale(initialScale)
            .event(svg);
        svg.attr('height', g.graph().height * initialScale + 40);
    }

    /**
     * 通过任务id获取任务数据
     * @param id
     * @returns {any}
     */
    getTaskVersionById = (id) => {
        let versions = this.dagData["nodes"];
        let current = null;
        if (versions) {
            for (let version of versions) {
                if (version.id == id) {
                    current = version;
                    break;
                }
            }
        }
        return current;
    }


    /**
     * 提交任务流
     * @returns {any}
     */
    fixFlow() {
        this.hideAllWindow();
        const that = this;
        const {flow, firstTaskVersionNo} = this.props;
        this.flowModel.fixFlow(flow.id, firstTaskVersionNo)
            .then((result) => {
                if (ResultUtils.isSuccess(result)) {
                    notification["success"]({
                        message: '成功',
                        description: '操作成功',
                    });
                    that.getDataAndRender();
                }
            });

    }

    /**
     * kill掉任务版本
     * @returns {any}
     */
    killVersion() {
        this.hideAllWindow();
        const versionId = this.nodeId;
        const that = this;
        this.taskVersionModel.fixById(versionId).then(result => {
            if (ResultUtils.isSuccess(result)) {
                notification["success"]({
                    message: '成功',
                    description: '操作成功',
                });
                that.getDataAndRender();
            }
        });
    }

    /**
     * kill掉任务版本
     * @returns {any}
     */
    fixVersion() {
        this.hideAllWindow();
        const versionId = this.nodeId;
        const that = this;
        this.taskVersionModel.fixById(versionId).then(result => {
            if (ResultUtils.isSuccess(result)) {
                notification["success"]({
                    message: '成功',
                    description: '操作成功',
                });
                that.getDataAndRender();
            }
        });
    }

    /**
     * kill掉任务版本
     * @returns {any}
     */
    fixVersionFromNode() {
        this.hideAllWindow();
        const versionId = this.nodeId;
        const {flow, firstTaskVersionNo} = this.props;
        const that = this;
        this.flowModel.fixFromNode(flow.id, firstTaskVersionNo, versionId).then(result => {
            if (ResultUtils.isSuccess(result)) {
                notification["success"]({
                    message: '成功',
                    description: '操作成功',
                });
                that.getDataAndRender();
            }
        });
    }

    /**
     * 忽略任务版本
     * @returns {any}
     */
    ignoreVersion() {
        this.hideAllWindow();
        const versionId = this.nodeId;
        const that = this;
        this.taskVersionModel.ignoreById(versionId).then(result => {
            if (ResultUtils.isSuccess(result)) {
                notification["success"]({
                    message: '成功',
                    description: '操作成功',
                });
                that.getDataAndRender();
            }
        });
    }

    render() {
        const currentVersion = this.getTaskVersionById(this.nodeId);

        const killBtn = (<div key={"kill-versionBtn"}>
            <Popconfirm title={"确定KIll？"} key={"killVersionPop"}
                        onConfirm={this.killVersion.bind(this)}>
                <Button key={"killVersionBtn"}>KILL</Button>
            </Popconfirm>
        </div>);

        const ignoreBtn = (<div key={"ignore-versionBtn"}>
            <Popconfirm title={"确定忽略？"} key={"ignoreVersionPop"}
                        onConfirm={this.ignoreVersion.bind(this)}>
                <Button key={"successVersionBtn"}>忽略</Button>
            </Popconfirm>
        </div>);
        /**
         * 右键按钮判断
         * @type {any[]}
         */
        let rightMenuBtns = [];
        if (currentVersion) {
            const finalStatus = currentVersion.finalStatus;
            if (finalStatus == EnumUtils.taskVersionFinalStatusUndefined) {
                rightMenuBtns.push(killBtn);
            }
            if (finalStatus != EnumUtils.taskVersionFinalStatusSuccess) {
                rightMenuBtns.push(ignoreBtn);
            }
        }

        //按钮添加
        let topBtns = [];
        topBtns.push(<div>
            <Popconfirm title={"确定修复任务流？"} onConfirm={this.fixFlow.bind(this)}>
                <Button size={"large"} type={"primary"} icon={"tool"}>修复任务流</Button>
            </Popconfirm>
        </div>);

        return (
            <div className={"dag-fix-container"} key={"dagContainer"}
                 style={{width: "100%", height: this.props.height, margin: "0 auto"}}>
                <svg ref={"dag"} style={{width: "100%", height: "100%"}}></svg>
                <div className={"top-menu-container"}>
                    {topBtns}
                </div>
                : ""

                {this.state.showMenu ?
                    <div className={"menu-container"} key={"menu2Container"}
                         style={{top: this.state.nodeTop, left: this.state.nodeLeft}}>
                        <div>
                            <Popconfirm title={"是否修复？"} key={"fixVersionPop"}
                                        onConfirm={this.fixVersion.bind(this)}>
                                <Button key={"fixVersionBtn"}>修复当前节点</Button>
                            </Popconfirm>
                        </div>
                        <div>
                            <Popconfirm title={"是否从该节点修复任务流？"} key={"fixFlowPop"}
                                        onConfirm={this.fixVersionFromNode.bind(this)}>
                                <Button key={"fixFlowBtn"}>从该节点修复</Button>
                            </Popconfirm>
                        </div>
                        {rightMenuBtns}
                    </div> : ""}
                {this.state.showDetail ?
                    <div className={"detail-container"}
                         style={{top: this.state.detailTop, left: this.state.detailLeft}}>
                        <p><strong>id:&nbsp;</strong>{currentVersion.id}</p>
                        <p><strong>任务id:&nbsp;</strong>{currentVersion.taskId}</p>
                        <p><strong>任务名称:&nbsp;</strong>{currentVersion.taskName}</p>
                        <p><strong>任务版本:&nbsp;</strong>{currentVersion.versionNo}</p>
                        <p><strong>状态:&nbsp;</strong>{EnumUtils.getTaskVersionStatusName(currentVersion.status)}</p>
                        <p><strong>时间粒度:&nbsp;</strong>{EnumUtils.getPeriodName(currentVersion.taskPeriod)}</p>
                        <p><strong>时间规则:&nbsp;</strong>{currentVersion.taskCronExpression}</p>
                        <p><strong>描述:&nbsp;</strong>{currentVersion.taskDescription}</p>
                        <p><strong>信息:&nbsp;</strong>{currentVersion.msg}</p>
                        <p><strong>创建时间:&nbsp;</strong>{CommonUtils.dateFormat(currentVersion.createTime)}</p>
                        <p><strong>更新时间:&nbsp;</strong>{CommonUtils.dateFormat(currentVersion.updateTime)}</p>
                    </div> : ""}
            </div>
        )
    }

}


export default DagVersionFixShow;