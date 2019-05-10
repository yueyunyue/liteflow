package cn.lite.flow.console.web.utils;

import cn.lite.flow.console.common.consts.Constants;
import cn.lite.flow.console.common.consts.TimeUnit;
import cn.lite.flow.console.common.model.vo.DependencyVo;
import cn.lite.flow.console.model.basic.Flow;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskInstance;
import cn.lite.flow.console.model.basic.TaskVersion;
import cn.lite.flow.executor.model.basic.ExecutorContainer;
import cn.lite.flow.executor.model.basic.ExecutorPlugin;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @description: 实体转化
 * @author: yueyunyue
 * @create: 2019-01-08
 **/
public class ModelUtils {

    /**
     * 任务转化为jsonobj
     * @param task
     * @return
     */
    public static JSONObject getTaskObj(Task task){
        if(task == null){
            return null;
        }
        JSONObject obj = new JSONObject();
        obj.put("id", task.getId());
        obj.put("name", task.getName());
        obj.put("cronExpression", task.getCronExpression());
        obj.put("period", task.getPeriod());
        obj.put("periodName", TimeUnit.findDescByValue(task.getPeriod()));
        obj.put("status", task.getStatus());
        obj.put("version", task.getVersion());
        obj.put("concurrency", task.getIsConcurrency());
        obj.put("pluginId", task.getPluginId());

        String pluginConf = task.getPluginConf();
        if(StringUtils.isNotEmpty(pluginConf)){
            obj.put("pluginConf",JSONObject.parseObject(pluginConf));
        }
        String retryConf = task.getRetryConf();
        if(StringUtils.isNotEmpty(retryConf)){
            obj.put("retryConf",JSONObject.parseObject(retryConf));
        }
        obj.put("userId", task.getUserId());
        obj.put("isRetry", task.getIsRetry());
        obj.put("maxRunTime", task.getMaxRunTime());
        obj.put("alarmEmail", task.getAlarmEmail());
        obj.put("alarmPhone", task.getAlarmPhone());
        obj.put("description", task.getDescription());
        obj.put("createTime", task.getCreateTime());
        obj.put("updateTime", task.getUpdateTime());
        return obj;
    }

    public static JSONArray getTaskArray(List<Task> tasks){
        if(CollectionUtils.isEmpty(tasks)){
            return null;
        }
        JSONArray datas = new JSONArray();
        tasks.stream().map(ModelUtils::getTaskObj).forEach(obj -> datas.add(obj));
        return datas;
    }

    /**
     * 任务流转化
     * @param flow
     * @return
     */
    public static JSONObject getFlowObj(Flow flow){
        if(flow == null){
            return null;
        }
        JSONObject obj = new JSONObject();
        obj.put("id", flow.getId());
        obj.put("name", flow.getName());
        obj.put("description", flow.getDescription());
        obj.put("status", flow.getStatus());
        obj.put("createTime", flow.getCreateTime());
        obj.put("updateTime", flow.getUpdateTime());
        return obj;
    }
    public static JSONArray getFlowArray(List<Flow> flows){
        if(CollectionUtils.isEmpty(flows)){
            return null;
        }
        JSONArray datas = new JSONArray();
        flows.stream().map(ModelUtils::getFlowObj).forEach(obj -> datas.add(obj));
        return datas;
    }


    /**
     * 获取任务版本
     * @param taskVersion
     * @return
     */
    public static JSONObject getTaskVersionObj(TaskVersion taskVersion){
        if(taskVersion == null){
            return null;
        }
        JSONObject obj = new JSONObject();
        obj.put("id", taskVersion.getId());
        obj.put("taskId", taskVersion.getTaskId());
        obj.put("versionNo", taskVersion.getVersionNo());
        obj.put("status", taskVersion.getStatus());
        obj.put("finalStatus", taskVersion.getFinalStatus());
        obj.put("retryNum", taskVersion.getRetryNum());
        obj.put("createTime", taskVersion.getCreateTime());
        obj.put("updateTime", taskVersion.getUpdateTime());
        return obj;
    }
    /**
     * 获取任务版本
     * @param taskVersion
     * @return
     */
    public static JSONObject getTaskVersionWithTaskObj(TaskVersion taskVersion, Task task){
        if(taskVersion == null){
            return null;
        }
        JSONObject obj = getTaskVersionObj(taskVersion);
        if(task != null){
            obj.put("taskId", task.getId());
            obj.put("taskName", task.getName());
            obj.put("taskCronExpression", task.getCronExpression());
            obj.put("taskPeriod", task.getPeriod());
            obj.put("taskStatus", task.getStatus());
            obj.put("taskDescription", task.getDescription());
            obj.put("taskCreateTime", task.getCreateTime());
            obj.put("taskUpdateTime", task.getUpdateTime());
            obj.put("taskUserId", task.getUserId());
        }

        return obj;
    }
    /**
     * 获取任务版本
     * @param instance
     * @return
     */
    public static JSONObject getTaskInstanceObj(TaskInstance instance){
        if(instance == null){
            return null;
        }
        JSONObject obj = new JSONObject();
        obj.put("id", instance.getId());
        obj.put("taskId", instance.getTaskId());
        obj.put("taskVersionId", instance.getTaskVersionId());
        obj.put("taskVersionNo", instance.getTaskVersionNo());
        obj.put("logicRunTime", instance.getLogicRunTime());
        obj.put("pluginConf", instance.getPluginConf());
        obj.put("status", instance.getStatus());
        obj.put("runStartTime", instance.getRunStartTime());
        obj.put("runEndTime", instance.getRunEndTime());
        obj.put("msg", instance.getMsg());
        obj.put("executorJobId", instance.getExecutorJobId());
        obj.put("createTime", instance.getCreateTime());
        obj.put("updateTime", instance.getUpdateTime());
        return obj;
    }
    /**
     * 获取插件
     * @param plugin
     * @return
     */
    public static JSONObject getPluginObj(ExecutorPlugin plugin){
        if(plugin == null){
            return null;
        }
        JSONObject obj = new JSONObject();
        obj.put("id", plugin.getId());
        obj.put("name", plugin.getName());
        obj.put("status", plugin.getStatus());
        String fieldConfig = plugin.getFieldConfig();
        obj.put("fieldConfig", fieldConfig);
        obj.put("containerId", plugin.getContainerId());

        String config = plugin.getConfig();
        if(StringUtils.isNotBlank(config)){
            JSONObject confObj = JSONObject.parseObject(config);
            obj.put("config", confObj);
        }
        obj.put("description", plugin.getDescription());
        obj.put("containerId", plugin.getContainerId());
        obj.put("createTime", plugin.getCreateTime());
        obj.put("updateTime", plugin.getUpdateTime());
        return obj;
    }
    /**
     * 获取容器
     * @param container
     * @return
     */
    public static JSONObject getContainerObj(ExecutorContainer container){
        if(container == null){
            return null;
        }
        JSONObject obj = new JSONObject();
        obj.put("id", container.getId());
        obj.put("name", container.getName());
        String fieldConfig = container.getFieldConfig();
        obj.put("fieldConfig", fieldConfig);
        obj.put("className", container.getClassName());
        obj.put("logType", container.getLogType());
        obj.put("status", container.getStatus());
        obj.put("description", container.getDescription());
        obj.put("createTime", container.getCreateTime());
        obj.put("updateTime", container.getUpdateTime());
        return obj;
    }

    /**
     * 校验插件和容器中表单参数中的名称，名称前缀必须是pluginConf
     * @param fields
     */
    public static void checkFieldName(JSONArray fields){
        if(fields == null || fields.size() == 0){
            return;
        }
        for(int i = 0; i < fields.size(); i ++){
            JSONObject fieldItem = fields.getJSONObject(i);
            String name = fieldItem.getString(Constants.PARAM_NAME);
            if(!StringUtils.startsWith(name, Constants.PARAM_PLUGIN_CONF_FIELD_PREFIX)){
                throw new IllegalArgumentException(String.format("参数%s前缀必须是%s", name, Constants.PARAM_PLUGIN_CONF_FIELD_PREFIX));
            }
        }


    }

    /**
     * 获取任务版本依赖
     * @param dependency
     * @return
     */
    public static JSONObject getDependencyVoObj(DependencyVo dependency){
        if(dependency == null){
            return null;
        }
        JSONObject obj = new JSONObject();
        obj.put("versionId", dependency.getId());
        obj.put("upstreamVersionId", dependency.getUpstreamId());
        return obj;
    }
    /**
     * 获取任务版本依赖
     * @param dependencies
     * @return
     */
    public static JSONArray getDependencyVoArray(List<DependencyVo> dependencies){
        if(CollectionUtils.isEmpty(dependencies)){
            return null;
        }
        JSONArray datas = new JSONArray();
        dependencies.stream().forEach(dependencyVo ->  {
            JSONObject obj = getDependencyVoObj(dependencyVo);
            if(obj != null){
                datas.add(obj);
            }
        });

        return datas;
    }

}
