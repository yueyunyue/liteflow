import {action, observable} from 'mobx';
import {provideSingleton} from "../../../common/utils/IOC";
import {create, offJob, callbackJob, update} from "../service/ExecuteJobService"
import {asyncAction} from "mobx-utils";
import BaseListModel from "../../../common/model/BaseListModel";
import executeJobConfig from "../config/ExecuteJobConfig";
import {notification} from 'antd';
import ResultUtils from "../../../common/utils/ResultUtils";


export class ExecuteJob {
    id: number;
    type: number;
    status: number;
    applicationId: string;
    config: string;
    msg: string;
    executorId: number;
    sourceId: number;
    callbackStatus: number;
    startTime: number;
    endTime: number;
    createTime: number;
    updateTime: number;
}

@provideSingleton(ExecuteJobModel)
export class ExecuteJobModel extends BaseListModel{

    @observable
    executeJobs: Array<ExecuteJob> = [];

    @observable
    loading: boolean = false;

    constructor() {
        super();
        this.path = executeJobConfig.urls.listUrl;
    }

    @action
    queryCallBack(success: boolean, list: Array<any>): void {
        if (success) {
            this.executeJobs = list as Array<ExecuteJob>;
        }
        this.loading = false;
    }

    @action
    query(searchParam) {
        this.loading = true;
        this.queryData(searchParam);
    }

    @asyncAction
    * add(executeJob: ExecuteJob) {
        this.loading = true;
        const result = yield create(executeJob);
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
    * edit(executeJob: ExecuteJob) {
        this.loading = true;
        const result = yield update(executeJob);
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
    * off(id: number) {
        this.loading = true;
        const result = yield offJob(id);
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
    * callback(id: number) {
        this.loading = true;
        const result = yield callbackJob(id);
        if (ResultUtils.isSuccess(result)) {
            notification["success"]({
                message: '成功',
                description: '操作成功',
            });
            this.refresh();
        }
        this.loading = false;
    }
}
