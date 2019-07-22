import {action, observable} from 'mobx';
import {provideSingleton} from "../../../common/utils/IOC";
import {create, offPlugin, onPlugin, update, listAllValidPlugin} from "../service/PluginService"
import {asyncAction} from "mobx-utils";
import BaseListModel from "../../../common/model/BaseListModel";
import pluginConfig from "../config/PluginConfig";
import {notification} from 'antd';
import ResultUtils from "../../../common/utils/ResultUtils";


export class Plugin {
    id: number;
    name: string;
    status: number;
    containerId: number;
    fieldConfig : string;
    config : any;
    description ?: string;
    createTime: number;
    updateTime: number;
    container ?: any;
    user ?: any;
}

@provideSingleton(PluginModel)
export class PluginModel extends BaseListModel{

    @observable
    plugins: Array<Plugin> = [];

    @observable
    loading: boolean = false;

    constructor() {
        super();
        this.path = pluginConfig.urls.listUrl;
    }

    @action
    queryCallBack(success: boolean, list: Array<any>): void {
        if (success) {
            this.plugins = list as Array<Plugin>;
        }
        this.loading = false;
    }

    @action
    query(searchParam) {
        this.loading = true;
        this.queryData(searchParam);
    }

    @asyncAction
    * add(plugin: Plugin) {
        this.loading = true;
        const result = yield create(plugin);
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
    * edit(plugin: Plugin) {
        this.loading = true;
        const result = yield update(plugin);
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
        const result = yield offPlugin(id);
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
        const result = yield onPlugin(id);
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
    * listAllValid() : any{
        this.loading = true;
        const result = yield listAllValidPlugin();
        return ResultUtils.getData(result);
    }
}
