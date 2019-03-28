import {request, requestPost, requestGet} from "../../../common/utils/Request";
import qs from "qs";
import dailyInitConfig from "../config/DailyInitConfig";

export async function queryList(params) {
    return request(`${dailyInitConfig.urls.listUrl}?${qs.stringify(params)}`,{});
}

// export async function create(params) {
//     return requestPost(dailyInitConfig.urls.addUrl, params);
// }
//
// export async function update(params) {
//     return requestPost(dailyInitConfig.urls.updateUrl, params);
// }
//
// export async function callbackJob(id) {
//     return requestPost(dailyInitConfig.urls.callbackUrl, {id: id});
// }
//
// export async function offJob(id) {
//     return requestPost(dailyInitConfig.urls.offUrl, {id: id});
// }