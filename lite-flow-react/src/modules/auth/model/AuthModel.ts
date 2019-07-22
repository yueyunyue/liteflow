import {action, observable} from 'mobx';
import {provideSingleton} from "../../../common/utils/IOC";
import {create, update, deleteAuth} from "../service/AuthService"
import {asyncAction} from "mobx-utils";
import BaseListModel from "../../../common/model/BaseListModel";
import authConfig from "../config/AuthConfig";
import {notification} from 'antd';
import ResultUtils from "../../../common/utils/ResultUtils";

export class Auth {
    id: number;
    sourceId: number;
    sourceType: number;
    targetId: number;
    targetType: number;
    hasEditAuth: number;
    hasExecuteAuth: number;
    user: any;
    sourceName: any;
    createTime: number;
    updateTime: number;
}

@provideSingleton(AuthModel)
export class AuthModel extends BaseListModel{

    @observable
    auths: Array<Auth> = [];

    @observable
    loading: boolean = false;

    constructor() {
        super();
        this.path = authConfig.urls.listUrl;
    }

    @action
    queryCallBack(success: boolean, list: Array<any>): void {
        if (success) {
            this.auths = list as Array<Auth>;
        }
        this.loading = false;
    }

    @action
    query(searchParam: any) {
        this.loading = true;
        this.queryData(searchParam);
    }

    @asyncAction
    * add(auth: Auth) {
        this.loading = true;
        const result = yield create(auth);
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
    * edit(auth: Auth) {
        this.loading = true;
        const result = yield update(auth);
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
        const result = yield deleteAuth(id);
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
