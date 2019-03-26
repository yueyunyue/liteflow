import {action, observable} from 'mobx';
import {provideSingleton} from "../../../common/utils/IOC";
import {asyncAction} from "mobx-utils";
import BaseListModel from "../../../common/model/BaseListModel";
import TaskConfig from "../config/TaskConfig";
import {
    create,
    remove,
    update,
    onTask,
    offTask,
    getTaskRelation,
    getTaskRelatedFlow,
    runTask,
    getAllAuthTask} from "../service/TaskService"
import {notification} from 'antd';


export class Task {
    id: number;
    name: string;
    cronExpression: string;
    period: number;
    status: number;
    version: number;
    isConcurrency: number;
    executeStrategy: number;
    pluginId: number;
    pluginConf: string;
    isRetry: number;
    retryConf: string;
    maxRunTime: number;
    createTime: number;
    updateTime: number;
    user ?: any;
    description ?: string;
}

@provideSingleton(TaskModel)
export class TaskModel extends BaseListModel{

    @observable
    tasks: Array<Task> = [];

    @observable
    loading: boolean = false;

    constructor() {
        super();
        this.path = TaskConfig.urls.listUrl;
    }

    @action
    queryCallBack(success: boolean, list: Array<any>): void {
        if (success) {
            this.tasks = list as Array<Task>;
        }
        this.loading = false;
    }

    @action
    query(searchParam) {
        this.loading = true;
        this.queryData(searchParam);
    }

    @asyncAction
    * add(task: Task) {
        this.loading = true;
        const result = yield create(task);
        if (result.status == 0) {
            notification["success"]({
                message: '成功',
                description: '操作成功',
            });

            this.refresh();
        }
        this.loading = false;
    }

    @asyncAction
    * edit(task: Task) {
        this.loading = true;
        const result = yield update(task);
        if (result.status == 0) {
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
        if (result.status == 0) {
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
        const result = yield onTask(id);
        if (result.status == 0) {
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
        const result = yield offTask(id);
        if (result.status == 0) {
            notification["success"]({
                message: '成功',
                description: '操作成功',
            });
            this.refresh();
        }
        this.loading = false;
    }

    /**
     * 展示任务的上下游
     * @param taskId
     * @returns {any}
     */
    @asyncAction
    * getRelation(taskId): any {
        const result = yield getTaskRelation(taskId);
        return result.data;
    }
    /**
     * 展示任务的上下游
     * @param taskId
     * @returns {any}
     */
    @asyncAction
    * getRelatedFlow(taskId): any {
        const result = yield getTaskRelatedFlow(taskId);
        return result.data;
    }

    /**
     * 获取所有有权限的任务
     * @returns {any}
     */
    @asyncAction
    * getAllAuth(): any {
        const result = yield getAllAuthTask();
        return result.data;
    }

    /**
     * 运行任务
     * @returns {any}
     */
    @asyncAction
    * run(id): any {
        const result = yield runTask(id);
        if (result.status == 0) {
            notification["success"]({
                message: '成功',
                description: '操作成功',
            });
        }
        return result;
    }



}
