import {action, observable} from 'mobx';
import {provideSingleton} from "../../../common/utils/IOC";
// import {create, offJob, callbackJob, update} from "../service/DailyInitService"
import {asyncAction} from "mobx-utils";
import BaseListModel from "../../../common/model/BaseListModel";
import dailyInitConfig from "../config/DailyInitConfig";
import {notification} from 'antd';


export class DailyInit {
    id: number;
    type: number;
    status: number;
    applicationId: string;
    config: string;
    msg: string;
    executorId: number;
    sourceId: number;
    callbackStatus: number;
    startTime: number;
    endTime: number;
    createTime: number;
    updateTime: number;
}

@provideSingleton(DailyInitModel)
export class DailyInitModel extends BaseListModel{

    @observable
    dailyInits: Array<DailyInit> = [];

    @observable
    loading: boolean = false;

    constructor() {
        super();
        this.path = dailyInitConfig.urls.listUrl;
    }

    @action
    queryCallBack(success: boolean, list: Array<any>): void {
        if (success) {
            this.dailyInits = list as Array<DailyInit>;
        }
        this.loading = false;
    }

    @action
    query(searchParam) {
        this.loading = true;
        this.queryData(searchParam);
    }

    // @asyncAction
    // * add(dailyInit: DailyInit) {
    //     this.loading = true;
    //     const result = yield create(dailyInit);
    //     if (result.status == 0) {
    //         notification["success"]({
    //             message: '成功',
    //             description: '操作成功',
    //         });
    //
    //         this.refresh();
    //     }
    //     this.loading = false;
    // }
    //
    // @asyncAction
    // * edit(dailyInit: DailyInit) {
    //     this.loading = true;
    //     const result = yield update(dailyInit);
    //     if (result.status == 0) {
    //         notification["success"]({
    //             message: '成功',
    //             description: '操作成功',
    //         });
    //         this.refresh();
    //     }
    //     this.loading = false;
    // }
    //
    // @asyncAction
    // * off(id: number) {
    //     this.loading = true;
    //     const result = yield offJob(id);
    //     if (result.status == 0) {
    //         notification["success"]({
    //             message: '成功',
    //             description: '操作成功',
    //         });
    //         this.refresh();
    //     }
    //     this.loading = false;
    // }
    //
    // @asyncAction
    // * callback(id: number) {
    //     this.loading = true;
    //     const result = yield callbackJob(id);
    //     if (result.status == 0) {
    //         notification["success"]({
    //             message: '成功',
    //             description: '操作成功',
    //         });
    //         this.refresh();
    //     }
    //     this.loading = false;
    // }
}
