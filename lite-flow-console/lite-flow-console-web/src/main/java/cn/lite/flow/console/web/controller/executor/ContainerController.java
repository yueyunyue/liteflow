package cn.lite.flow.console.web.controller.executor;

import cn.lite.flow.common.model.consts.StatusType;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.web.controller.BaseController;
import cn.lite.flow.console.web.utils.ModelUtils;
import cn.lite.flow.executor.client.ExecutorContainerRpcService;
import cn.lite.flow.executor.client.model.ContainerParam;
import cn.lite.flow.executor.model.basic.ExecutorContainer;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ly on 2018/11/16.
 */
@RestController
@RequestMapping("executor/container")
public class ContainerController extends BaseController {

    @Autowired
    private ExecutorContainerRpcService executorContainerRpcService;

    /**
     * 列表接口
     *
     * @param nameLike      按名称模糊查询
     * @param pageNum       当前页码
     * @param pageSize      每页数量
     * @return
     */
    @RequestMapping(value = "list")
    public String list(
            @RequestParam(value = "nameLike", required = false) String nameLike,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {

        ContainerParam param = new ContainerParam();
        if(StringUtils.isNotEmpty(nameLike)){
            param.setNameLike(nameLike);
        }
        param.setPageNum(pageNum);
        param.setPageSize(pageSize);
        List<ExecutorContainer> containers = executorContainerRpcService.list(param);

        int total = 0;
        JSONArray datas = new JSONArray();
        if (CollectionUtils.isNotEmpty(containers)) {
            total = executorContainerRpcService.count(param);
            List<Long> userIds = containers.stream()
                    .map(ExecutorContainer::getUserId)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Long, String> userInfo = getUserName(userIds);

            containers.forEach(container -> {
                JSONObject obj = ModelUtils.getContainerObj(container);
                setUserInfo(obj, container.getUserId(), userInfo);
                datas.add(obj);
            });

        }
        return ResponseUtils.list(total, datas);
    }

    /**
     * 编辑
     *
     * @param id                id
     * @param name              名称
     * @param fieldConfig       插件需要实现的参数`
     * @param envFieldConfig    环境插件参数配置
     * @return
     */
    @RequestMapping(value = "addOrUpdate")
    public String addOrUpdate(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "className") String className,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "fieldConfig", required = false) String fieldConfig,
            @RequestParam(value = "envFieldConfig", required = false) String envFieldConfig
    ) {
        ExecutorContainer container = new ExecutorContainer();
        container.setId(id);
        container.setName(name);
        container.setClassName(className);

        if(StringUtils.isNotEmpty(fieldConfig)){
            try {
                JSONArray fields = JSONArray.parseArray(fieldConfig);


            }catch (Throwable e){
                return ResponseUtils.error("插件参数格式不对," + e.getMessage());
            }
        }else{
            fieldConfig = "";
        }

        container.setFieldConfig(fieldConfig);
        container.setDescription(description);
        if (id == null) {
            container.setUserId(getUser().getId());
            executorContainerRpcService.add(container);
        } else {
            executorContainerRpcService.update(container);
        }
        return ResponseUtils.success("操作成功");
    }

    /**
     * 上线
     *
     * @param id        容器id
     * @return
     */
    @RequestMapping(value = "online")
    public String online(@RequestParam(value = "id") Long id) {
        ExecutorContainer container = new ExecutorContainer();
        container.setId(id);
        container.setStatus(StatusType.ON.getValue());
        executorContainerRpcService.update(container);
        return ResponseUtils.success("发布成功");
    }

    /**
     * 下线
     *
     * @param id        容器id
     * @return
     */
    @RequestMapping(value = "offline")
    public String offline(@RequestParam(value = "id") Long id) {
        ExecutorContainer container = new ExecutorContainer();
        container.setId(id);
        container.setStatus(StatusType.OFF.getValue());
        executorContainerRpcService.update(container);
        return ResponseUtils.success("下线成功");
    }
}
