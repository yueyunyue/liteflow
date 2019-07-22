import {action, observable} from 'mobx';
import {provideSingleton} from "../../../common/utils/IOC";
import {create, remove, update, listAllAuths} from "../service/RoleService"
import {asyncAction} from "mobx-utils";
import BaseListModel from "../../../common/model/BaseListModel";
import roleConfig from "../config/RoleConfig";
import {notification} from 'antd';
import ResultUtils from "../../../common/utils/ResultUtils";

/**
 * 角色的model
 */
export class Role {
    id: number;
    name: string;
    description ?: string;
    auths ?: Array<RoleAuth>
}

/**
 * 角色权限
 */
export class RoleAuth{
    id: number;
    name: string;
}

@provideSingleton(RoleModel)
export class RoleModel extends BaseListModel{

    @observable
    roles: Array<Role> = [];

    @observable
    loading: boolean = false;

    constructor() {
        super();
        this.path = roleConfig.urls.listUrl;
    }

    @action
    queryCallBack(success: boolean, list: Array<any>): void {
        if (success) {
            this.roles = list as Array<Role>;
        }
        this.loading = false;
    }

    @action
    query(searchParam: any) {
        this.loading = true;
        this.queryData(searchParam);
    }

    /**
     * 获取所有权限
     */
    @asyncAction
    * listAuths():any {
        const result = yield listAllAuths();
        return ResultUtils.getData(result);
    }

    @asyncAction
    * add(role: Role) {
        this.loading = true;
        const result = yield create(role);
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
    * edit(role: Role) {
        this.loading = true;
        const result = yield update(role);
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
}
