import {request, requestPost, requestGet} from "../../../common/utils/Request";
import qs from "qs";
import TaskConfig from "../config/TaskConfig";

export async function create(params) {
    return requestPost(TaskConfig.urls.addUrl, params);
}

export async function update(params) {
    return requestPost(TaskConfig.urls.updateUrl, params);
}

export async function remove(params) {
    return requestPost(TaskConfig.urls.deleteUrl, {id: params});
}

export async function getTaskRelation(taskId) {
    return requestGet(TaskConfig.urls.getRelationUrl, {id: taskId});
}
export async function getTaskRelatedFlow(taskId) {
    return requestGet(TaskConfig.urls.getRelatedFlowUrl, {id: taskId});
}

export async function getAllAuthTask() {
    return requestGet(TaskConfig.urls.getAllAuthTaskUrl, {});
}

export async function onTask(id) {
    return requestPost(TaskConfig.urls.onUrl, {id: id});
}

export async function offTask(id) {
    return requestPost(TaskConfig.urls.offUrl, {id: id});
}
export async function runTask(id) {
    return requestPost(TaskConfig.urls.runTaskUrl, {id: id});
}