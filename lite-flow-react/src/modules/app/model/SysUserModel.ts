import {action, computed, observable} from 'mobx';
import {inject, provideSingleton} from "../../../common/utils/IOC";
import {asyncAction} from "mobx-utils";
import {Message, SysInfoModel} from "./SysInfoModel";
import {User} from "../../user/model/UserModel";
import {loginSubmit,logoutSubmit} from "../service/AppService";
import ResultUtils from "../../../common/utils/ResultUtils";

export class SysUser {
    name: string;
    userId: number;
    menus: any;
}

@provideSingleton(SysUserModel)
export class SysUserModel {

    @observable
    sysUser: SysUser = null;

    @observable
    loading: boolean = false;

    @inject(SysInfoModel)
    sysInfoModel: SysInfoModel;

    @action
    loginSuccess(vo: SysUser): void {
        this.sysUser = vo;
    }

    @asyncAction
    * login(username: string, password: string, router:any) {
        this.loading = true;
        const result = yield loginSubmit({ username, password});
        this.loading = false;
        if (ResultUtils.isSuccess(result)) {
            this.loginSuccess(ResultUtils.getData(result));
            router.push("/console/dashboard")
        }
    }

    @asyncAction
    * logout(router:any) {
        this.loading = true;
        const result = yield logoutSubmit();
        this.loading = false;
        if (ResultUtils.isSuccess(result)) {
            router.push("/login")
        }
    }
}