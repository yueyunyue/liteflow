package cn.lite.flow.console.web.controller.common;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.console.common.consts.Constants;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.web.annotation.AuthCheckIgnore;
import cn.lite.flow.console.web.controller.BaseController;
import cn.lite.flow.console.web.utils.ModelUtils;
import cn.lite.flow.executor.client.ExecutorContainerRpcService;
import cn.lite.flow.executor.client.ExecutorPluginRpcService;
import cn.lite.flow.executor.model.basic.ExecutorContainer;
import cn.lite.flow.executor.model.basic.ExecutorPlugin;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by ly on 2018/12/14.
 */
@RestController("executorCommonController")
@RequestMapping("executor/common")
@AuthCheckIgnore
public class ExecutorCommonController extends BaseController {

    @Autowired
    private ExecutorPluginRpcService executorPluginRpcService;

    @Autowired
    private ExecutorContainerRpcService executorContainerRpcService;

    /**
     * 全部有效的插件
     * @return
     */
    @RequestMapping(value = "getAllValidPlugin")
    public String getAllValidPlugin() {
        JSONArray datas = new JSONArray();
        List<ExecutorPlugin> plugins = executorPluginRpcService.listAllValid();
        if (CollectionUtils.isNotEmpty(plugins)) {

            plugins.stream().forEach(plugin -> {
                String pluginConfig = plugin.getConfig();
                JSONObject pluginConfObj = null;
                if(StringUtils.isNotBlank(pluginConfig)){
                    pluginConfObj = JSONObject.parseObject(pluginConfig);
                }
                JSONObject pluginObj = ModelUtils.getPluginObj(plugin);
                //所属容器
                ExecutorContainer executorContainer = executorContainerRpcService.getById(plugin.getContainerId());
                /**
                 * 参数合并,将容器以及插件的参数进行合并
                 */
                JSONArray combineFieldConfigs = new JSONArray();
                if(StringUtils.isNotEmpty(plugin.getFieldConfig())){
                    JSONArray fieldArray = JSONArray.parseArray(plugin.getFieldConfig());
                    combineFieldConfigs.addAll(fieldArray);
                }
                if (executorContainer != null) {
                    JSONObject containerObj = new JSONObject();
                    containerObj.put(CommonConstants.PARAM_ID, executorContainer.getId());
                    containerObj.put(CommonConstants.PARAM_NAME, executorContainer.getName());
                    pluginObj.put(CommonConstants.PARAM_CONTAINER, containerObj);

                    String containerFieldConfig = executorContainer.getFieldConfig();
                    if(StringUtils.isNotEmpty(containerFieldConfig)){
                        JSONArray containerFieldArray = JSONArray.parseArray(containerFieldConfig);
                        JSONArray containerFields = new JSONArray();
                        /**
                         * 插件中已经配置过得数据,表单中不在需要添加
                         */
                        if(containerFieldArray != null && containerFieldArray.size() > 0){
                            for(int i = 0; i < containerFieldArray.size(); i ++){
                                JSONObject containerField = containerFieldArray.getJSONObject(i);
                                String name = containerField.getString(CommonConstants.PARAM_NAME);
                                if(pluginConfObj == null || pluginConfObj.getString(name) == null){
                                    containerFields.add(containerField);
                                }
                            }
                        }
                        combineFieldConfigs.addAll(containerFields);
                    }
                }

                if(CollectionUtils.isNotEmpty(combineFieldConfigs)){
                    for(int i = 0; i < combineFieldConfigs.size(); i ++){
                        JSONObject pluginField = combineFieldConfigs.getJSONObject(i);
                        /**
                         * 名称设置为pluginConf，保证前端自动封装数据
                         */
                        String name = pluginField.getString(CommonConstants.PARAM_NAME);
                        pluginField.put(CommonConstants.PARAM_NAME, Constants.PARAM_PLUGIN_CONF_FIELD_PREFIX + name);

                    }
                    /**
                     * 校验参数合法性
                     */
                    ModelUtils.checkFieldName(combineFieldConfigs);
                }
                pluginObj.put(CommonConstants.PARAM_FIELD_CONFIG, combineFieldConfigs);
                datas.add(pluginObj);
            });
        }

        return ResponseUtils.success(datas);
    }

    /**
     * 全部有效的容器
     * @return
     */
    @RequestMapping(value = "getAllValidContainer")
    public String getAllValidContainer() {
        List<ExecutorContainer> executorContainers = executorContainerRpcService.listAllValid();
        JSONArray datas = new JSONArray();
        if (CollectionUtils.isNotEmpty(executorContainers)) {
            executorContainers.forEach(o -> {
                JSONObject obj = new JSONObject();
                obj.put(CommonConstants.PARAM_ID, o.getId());
                obj.put(CommonConstants.PARAM_NAME, o.getName());
                String fieldConfig = o.getFieldConfig();
                if(StringUtils.isNotBlank(fieldConfig)){
                    obj.put(CommonConstants.PARAM_FIELD_CONFIG, JSONArray.parseArray(fieldConfig));
                }
                datas.add(obj);
            });
        }
        return ResponseUtils.success(datas);
    }
}
