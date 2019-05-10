import {action, observable} from 'mobx';
import {provideSingleton} from "../../../common/utils/IOC";
import {create, offContainer, onContainer, update, listAllValidContainer} from "../service/ContainerService"
import {asyncAction} from "mobx-utils";
import BaseListModel from "../../../common/model/BaseListModel";
import containerConfig from "../config/ContainerConfig";
import {notification} from 'antd';
import {listAllValidPlugin} from "../../plugin/service/PluginService";

export class Container {
    id: number;
    name: string;
    fieldConfig ?: string;
    description ?: string;
    envFieldConfig ?: string;
    status: number;
    logType: number;
    createTime: number;
    updateTime: number;
}

@provideSingleton(ContainerModel)
export class ContainerModel extends BaseListModel{

    @observable
    containers: Array<Container> = [];

    @observable
    loading: boolean = false;

    constructor() {
        super();
        this.path = containerConfig.urls.listUrl;
    }

    @action
    queryCallBack(success: boolean, list: Array<any>): void {
        if (success) {
            this.containers = list as Array<Container>;
        }
        this.loading = false;
    }

    @action
    query(searchParam) {
        this.loading = true;
        this.queryData(searchParam);
    }

    @asyncAction
    * add(container: Container) {
        this.loading = true;
        const result = yield create(container);
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
    * edit(container: Container) {
        this.loading = true;
        const result = yield update(container);
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
        const result = yield offContainer(id);
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
        const result = yield onContainer(id);
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
    * listAllValid() : any{
        this.loading = true;
        const result = yield listAllValidPlugin();
        return result.data;
    }

    @asyncAction
    * listAllContainers() : any{
        const result = yield listAllValidContainer();
        return result.data;
    }
}
