import {action, observable} from 'mobx';
import {provideSingleton} from "../../../common/utils/IOC";
import {asyncAction} from "mobx-utils";
import BaseListModel from "../../../common/model/BaseListModel";
import FlowConfig from "../config/FlowConfig";
import {notification} from 'antd';
import {
    create,
    remove,
    update,
    onFlow,
    offFlow,
    addOrUpdateFlowLinks,
    viewFlowDag,
    fixGetHeadTaskVersionNos,
    fixGetLatestVersionNos,
    fixViewFlowDag,
    fixFlowByVersion,
    fixFlowFromNode
} from "../service/FlowService"
import ResultUtils from "../../../common/utils/ResultUtils";


export class Flow {
    id: number;
    name: string;
    description ?: string;
}

@provideSingleton(FlowModel)
export class FlowModel extends BaseListModel{

    @observable
    flows: Array<Flow> = [];

    @observable
    loading: boolean = false;

    constructor() {
        super();
        this.path = FlowConfig.urls.listUrl;
    }

    @action
    queryCallBack(success: boolean, list: Array<any>): void {
        if (success) {
            this.flows = list as Array<Flow>;
        }
        this.loading = false;
    }

    @action
    query(searchParam) {
        this.loading = true;
        this.queryData(searchParam);
    }

    @asyncAction
    * add(flow: Flow) {
        this.loading = true;
        const result = yield create(flow);
        if (ResultUtils.isSuccess(result)) {
            notification["success"]({
                message: '成功',
                description: '操作成功',
            });

            this.refresh();
        }
        this.loading = false;
    }

    @asyncAction
    * edit(flow: Flow) {
        this.loading = true;
        const result = yield update(flow);
        if (ResultUtils.isSuccess(result)) {
            notification["success"]({
                message: '成功',
                description: '操作成功',
            });
            this.refresh();
        }
        this.loading = false;
    }

    @asyncAction
    * delete(id: number) {
        this.loading = true;
        const result = yield remove(id);
        if (ResultUtils.isSuccess(result)) {
            notification["success"]({
                message: '成功',
                description: '操作成功',
            });
            this.refresh();
        }
        this.loading = false;
    }
    @asyncAction
    * on(id: number) {
        this.loading = true;
        const result = yield onFlow(id);
        if (ResultUtils.isSuccess(result)) {
            notification["success"]({
                message: '成功',
                description: ResultUtils.getData(result)
            });
            this.refresh();
        }
        this.loading = false;
    }
    @asyncAction
    * off(id: number) {
        this.loading = true;
        const result = yield offFlow(id);
        if (ResultUtils.isSuccess(result)) {
            notification["success"]({
                message: '成功',
                description: ResultUtils.getData(result)
            });
            this.refresh();
        }
        this.loading = false;
    }

    @asyncAction
    * viewDag(id):any {
        const result = yield viewFlowDag(id);
        return result;
    }

    /**
     * 更新任务流的节点
     * @param id
     * @param links
     * @returns {any}
     */
    @asyncAction
    * addOrUpdateLinks(id, links):any {
        const result = yield addOrUpdateFlowLinks(id, links);
        return result;
    }

    /**
     * 任务流修复相关
     */
    @asyncAction
    * fixGetVersionNos(id, startTime, endTime):any {
        const result = yield fixGetHeadTaskVersionNos(id, startTime, endTime);
        return result;
    }
    @asyncAction
    * fixGetLatestVersionNos(id):any {
        const result = yield fixGetLatestVersionNos(id);
        return result;
    }
    @asyncAction
    * fixViewDag(id, versionNo):any {
        const result = yield fixViewFlowDag(id, versionNo);
        return result;
    }
    @asyncAction
    * fixFlow(id, versionNo):any {
        const result = yield fixFlowByVersion(id, versionNo);
        return result;
    }
    @asyncAction
    * fixFromNode(id, versionNo, versionId):any {
        const result = yield fixFlowFromNode(id, versionNo, versionId);
        return result;
    }

}
