import {action, observable} from 'mobx';
import {provideSingleton} from "../../../common/utils/IOC";
import {create, offExecutor, onExecutor, update} from "../service/ExecutorService"
import {asyncAction} from "mobx-utils";
import BaseListModel from "../../../common/model/BaseListModel";
import executorConfig from "../config/ExecutorConfig";
import {notification} from 'antd';
import ResultUtils from "../../../common/utils/ResultUtils";

export class ExecutorSearchParam {
    id?: number;
    nameLike?: String;
}
export class Executor {
    id: number;
    ip: string;
    name: string;
    description ?: string;
    createTime: number;
    updateTime: number;
}

@provideSingleton(ExecutorModel)
export class ExecutorModel extends BaseListModel{

    @observable
    executors: Array<Executor> = [];

    @observable
    loading: boolean = false;

    constructor() {
        super();
        this.path = executorConfig.urls.listUrl;
    }

    @action
    queryCallBack(success: boolean, list: Array<any>): void {
        if (success) {
            this.executors = list as Array<Executor>;
        }
        this.loading = false;
    }

    @action
    query(searchParam) {
        this.loading = true;
        this.queryData(searchParam);
    }

    @asyncAction
    * add(executor: Executor) {
        this.loading = true;
        const result = yield create(executor);
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
    * edit(executor: Executor) {
        this.loading = true;
        const result = yield update(executor);
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
        const result = yield offExecutor(id);
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
        const result = yield onExecutor(id);
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
