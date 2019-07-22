import {action, observable} from 'mobx';
import {provideSingleton} from "../../../common/utils/IOC";
import {create, update, listRoles, listUsers, on, off, listAllUsers} from "../service/UserService"
import {asyncAction} from "mobx-utils";
import BaseListModel from "../../../common/model/BaseListModel";
import userConfig from "../config/UserConfig";
import {Role} from "../../role/model/RoleModel";
import {notification} from 'antd';
import ResultUtils from "../../../common/utils/ResultUtils";

/**
 * 用户搜索实体
 */
export class UserSearchParam {
    userNameLike?: String;
    emailLike?: string;
}

/**
 * 用户实体
 */
export class User {
    id: number;
    mobile: string;
    name: string;
    email: string;
    roles: Array<Role>;
}

@provideSingleton(UserModel)
export class UserModel extends BaseListModel{

    @observable
    users: Array<User> = [];

    @observable
    loading: boolean = false;

    constructor() {
        super();
        this.path = userConfig.urls.listUrl
    }

    @action
    queryCallBack(success: boolean, list: Array<any>): void {
        if (success) {
            this.users = list as Array<User>;
        }
        this.loading = false;
    }

    @action
    query(searchParam: any) {
        this.loading = true;
        this.queryData(searchParam);
    }

    /**
     * 获取所有角色
     */
    @asyncAction
    * listAllRoles(): any{
        const result = yield listRoles();
        return ResultUtils.getData(result);
    }

    @asyncAction
    * add(user: User) {
        this.loading = true;
        const result = yield create(user);
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
    * edit(user: User) {
        this.loading = true;
        const result = yield update(user);
        if (ResultUtils.isSuccess(result)) {
            notification["success"]({
                message: '成功',
                description: '操作成功',
            });
            this.refresh();
        }
        this.loading = false;
    }

    /**
     * 启用用户
     * @param {number} id
     */
    @asyncAction
    * on(id: number) {
        this.loading = true;
        const result = yield on(id);
        if (ResultUtils.isSuccess(result)) {
            notification["success"]({
                message: '成功',
                description: '操作成功',
            });
            this.refresh();
        }
        this.loading = false;
    }

    /**
     * 禁用用户
     * @param {number} id
     */
    @asyncAction
    * off(id: number) {
        this.loading = true;
        const result = yield off(id);
        if (ResultUtils.isSuccess(result)) {
            notification["success"]({
                message: '成功',
                description: '操作成功',
            });
            this.refresh();
        }
        this.loading = false;
    }

    /**
     * 获取所有用户
     */
    @asyncAction
    * listAllUsers(): any{
        const result = yield listAllUsers();
        return ResultUtils.getData(result);
    }
}
