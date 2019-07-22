import {action, observable} from 'mobx';
import {provideSingleton} from "../../../common/utils/IOC";
import {fixVersion, ignoreVersion, killVersion, fixRangeVersion, deepFixVersion} from "../service/TaskVersionService"
import {asyncAction} from "mobx-utils";
import BaseListModel from "../../../common/model/BaseListModel";
import TaskVersionConfig from "../config/TaskVersionConfig";
import {notification} from 'antd';
import ResultUtils from "../../../common/utils/ResultUtils";

export class TaskVersion {
    id: number;
    versionNo: number;
    taskId: number;
    taskName: String;
    status: number;
    finalStatus: number;
    retryNum: number;
    msg ?: string;
    createTime ?: number;
    updateTime ?: number;
}

@provideSingleton(TaskVersionModel)
export class TaskVersionModel extends BaseListModel{

    @observable
    taskVersions: Array<TaskVersion> = [];

    @observable
    loading: boolean = false;

    constructor() {
        super();
        this.path = TaskVersionConfig.urls.listUrl;
    }

    @action
    queryCallBack(success: boolean, list: Array<any>): void {
        if (success) {
            this.taskVersions = list as Array<TaskVersion>;
        }
        this.loading = false;
    }

    @action
    query(searchParam) {
        this.loading = true;
        this.queryData(searchParam);
    }

    @asyncAction
    * fix(id: number) {
        this.loading = true;
        const result = yield fixVersion(id);
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
    * fixById(id: number): any {
        const result = yield fixVersion(id);
        return result;
    }

    @asyncAction
    * deepFix(id: number) {
        this.loading = true;
        const result = yield deepFixVersion(id);
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
    * ignore(id: number) {
        this.loading = true;
        const result = yield ignoreVersion(id);
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
    * ignoreById(id: number): any {
        const result = yield ignoreVersion(id);
        return result;
    }

    @asyncAction
    * kill(id: number) {
        this.loading = true;
        const result = yield killVersion(id);
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
    * killById(id: number): any {
        const result = yield killVersion(id);
        return result;
    }

    @asyncAction
    * fixRange(data) {
        this.loading = true;
        const result = yield fixRangeVersion(data);
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
