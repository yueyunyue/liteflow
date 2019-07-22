import {action, observable} from 'mobx';
import {provideSingleton} from "../../../common/utils/IOC";
import {offDependency, onDependency} from "../service/TaskInstanceService"
import {asyncAction} from "mobx-utils";
import BaseListModel from "../../../common/model/BaseListModel";
import TaskInstanceConfig from "../config/TaskInstanceConfig";
import {notification} from "antd";
import ResultUtils from "../../../common/utils/ResultUtils";

export class Dependency {
    id: number;
    upstreamTaskId: number;
    upstreamTaskName: string;
    upstreamtaskVersion: any;
    status: number;
    createTime: number;
    updateTime: number;
}

@provideSingleton(InstanceDependencyModel)
export class InstanceDependencyModel extends BaseListModel{

    @observable
    dependencies: Array<Dependency> = [];

    @observable
    loading: boolean = false;

    constructor() {
        super();
        this.path = TaskInstanceConfig.urls.listDependencyUrl;
    }

    @action
    queryCallBack(success: boolean, list: Array<any>): void {
        if (success) {
            this.dependencies = list as Array<Dependency>;
        }
        this.loading = false;
    }

    @action
    query(searchParam: any) {
        this.queryData(searchParam);
    }

    @asyncAction
    * on(id: number) {
        this.loading = true;
        const result = yield onDependency(id);
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
        const result = yield offDependency(id);
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
