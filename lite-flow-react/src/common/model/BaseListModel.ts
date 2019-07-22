import {action, computed, observable} from 'mobx';
import {asyncAction} from "mobx-utils";
import {requestGet} from "../utils/Request";
import {provideInstance} from "../utils/IOC";
import ResultUtils from "../../common/utils/ResultUtils";

class Page {
    pageSize: number = 10;
    pageNum: number = 1;
    total: number = 0;
}

@provideInstance(BaseListModel)
class BaseListModel {

    page: Page = observable.object(new Page);

    searchParam: any = {};

    path: string;

    private async queryList(params: any) {
        let param = params ? params : {};
        return requestGet(this.path, param);
    }

    /**
     * 刷新页面
     * @param searchParam
     */
    refresh() {
        this.queryInternal(this.searchParam, false);
    }

    queryData(searchParam?: any) {
        this.queryInternal(searchParam? searchParam:{});
    }

    @asyncAction
    private * queryInternal(searchParam: any,resetPage:boolean=true,saveParam:boolean=true) {
        if(resetPage){
            this.page.pageNum = 1;
        }
        if(saveParam){
            this.searchParam = searchParam;
        }
        const result = yield this.queryList({...searchParam, ...this.page});
        if (ResultUtils.isSuccess(result)) {
            this.page.total = result.total;
            const resultData = ResultUtils.getData(result);
            this.queryCallBack(true, resultData as Array<any>);
            return;
        }

        this.queryCallBack(false, []);
    }

    /**
     * 列表数据加载后，回调函数，由子类实现
     * @param {boolean} success
     * @param {Array<any>} list
     */
    queryCallBack(success: boolean, list: Array<any>): void {
    }

    @action
    onPageChange(pageNum: number, pageSize: number) {
        this.page.pageNum = pageNum;
        this.page.pageSize = pageSize;
        this.queryInternal(this.searchParam, false);
    }

    /**
     *  获取页面的初始值
     * @returns {{current: number; pageSize: number; showQuickJumper: boolean; showSizeChanger: boolean; pageSizeOptions: [string , string , string , string]; total: number; onChange: ((page, pageSize) => any); onShowSizeChange: ((currentPage, pageSize) => any); showTotal: ((total) => string)}}
     */
    @computed
    get pageConfig() {
        return {
            current: this.page.pageNum,
            pageSize: this.page.pageSize,
            showQuickJumper: false,
            showSizeChanger: true,
            pageSizeOptions: [
                '10',
                '20',
                '50',
                '100'
            ],
            total: this.page.total,

            onChange: (page, pageSize) => {
                this.onPageChange(page, pageSize);
            },
            onShowSizeChange: (currentPage, pageSize) => {
                this.onPageChange(currentPage, pageSize);
            },
            showTotal: (total) => {
                return `共 ${total} 条`;
            }
        }
    }
}
export default BaseListModel
