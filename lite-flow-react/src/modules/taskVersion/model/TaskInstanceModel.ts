import {action, observable} from 'mobx';
import {provideSingleton} from "../../../common/utils/IOC";
import {logVersion, logInstance} from "../service/TaskInstanceService"
import {asyncAction} from "mobx-utils";
import BaseListModel from "../../../common/model/BaseListModel";
import TaskInstanceConfig from "../config/TaskInstanceConfig";
import ResultUtils from "../../../common/utils/ResultUtils";

export class TaskInstance {
    id: number;
    taskId: number;
    taskVersionId: number;
    taskVersionNo: number;
    status: number;
    pluginConf: string;
    executorJodId: number;
    logicRunTime: number;
    runStartTime ?: number;
    runEndTime ?: number;
    createTime ?: number;
    updateTime ?: number;
}

@provideSingleton(TaskInstanceModel)
export class TaskInstanceModel extends BaseListModel{

    @observable
    taskInstances: Array<TaskInstance> = [];

    @observable
    loading: boolean = false;

    constructor() {
        super();
        this.path = TaskInstanceConfig.urls.listUrl;
    }

    @action
    queryCallBack(success: boolean, list: Array<any>): void {
        if (success) {
            this.taskInstances = list as Array<TaskInstance>;
        }
        this.loading = false;
    }

    @action
    query(searchParam: any) {
        this.queryData(searchParam);
    }

    @asyncAction
    * logVersion(id: number): any {
        const result = yield logVersion(id);
        return ResultUtils.getData(result);
    }

    @asyncAction
    * log(id: number): any {
        const result = yield logInstance(id);
        return ResultUtils.getData(result);
    }
}
