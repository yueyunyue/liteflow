package cn.lite.flow.console.web.controller.executor;

import cn.lite.flow.common.model.consts.StatusType;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.web.controller.BaseController;
import cn.lite.flow.executor.client.ExecutorServerRpcService;
import cn.lite.flow.executor.client.model.ServerParam;
import cn.lite.flow.executor.model.basic.ExecutorServer;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ly on 2018/12/14.
 */
@RestController("consoleServerController")
@RequestMapping("executor/server")
public class ServerController extends BaseController {

    @Resource
    private ExecutorServerRpcService executorServerRpcService;

    /**
     * 列表
     *
     * @param nameLike      按照名称模糊查询
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

        ServerParam param = new ServerParam();
        param.setNameLike(nameLike);
        param.setPageNum(pageNum);
        param.setPageSize(pageSize);
        List<ExecutorServer> servers = executorServerRpcService.list(param);

        int total = 0;
        JSONArray datas = new JSONArray();
        if (CollectionUtils.isNotEmpty(servers)) {
            total = executorServerRpcService.count(param);

            List<Long> userIds = servers.stream()
                    .map(ExecutorServer::getUserId)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Long, String> userInfo = getUserName(userIds);

            servers.forEach(server -> {
                JSONObject obj = new JSONObject();
                obj.put("id", server.getId());
                obj.put("name", server.getName());
                obj.put("ip", server.getIp());
                obj.put("description", server.getDescription());
                obj.put("status", server.getStatus());
                setUserInfo(obj, server.getUserId(), userInfo);
                obj.put("createTime", server.getCreateTime());
                obj.put("updateTime", server.getUpdateTime());
                datas.add(obj);
            });
        }
        return ResponseUtils.list(total, datas);
    }

    /**
     * 编辑
     *
     * @param id        id
     * @param name      名称
     * @param ip        ip
     * @param description 说明
     * @return
     */
    @RequestMapping(value = "addOrUpdate")
    public String addOrUpdate(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "ip") String ip,
            @RequestParam(value = "description") String description
    ) {
        ExecutorServer executor = new ExecutorServer();
        executor.setId(id);
        executor.setName(name);
        executor.setIp(ip);
        executor.setUserId(getUser().getId());
        executor.setDescription(description);
        if (id == null) {
            executorServerRpcService.add(executor);
        } else {
            executorServerRpcService.update(executor);
        }
        return ResponseUtils.success("操作成功");
    }

    /**
     * 上线操作
     *
     * @param id        id
     * @return
     */
    @RequestMapping(value = "online")
    public String online(@RequestParam(value = "id") Long id) {
        ExecutorServer executor = new ExecutorServer();
        executor.setId(id);
        executor.setStatus(StatusType.ON.getValue());
        executorServerRpcService.update(executor);
        return ResponseUtils.success("操作成功");
    }

    /**
     * 下线操作
     *
     * @param id    id
     * @return
     */
    @RequestMapping(value = "offline")
    public String offline(@RequestParam(value = "id") Long id) {
        ExecutorServer executor = new ExecutorServer();
        executor.setId(id);
        executor.setStatus(StatusType.OFF.getValue());
        executorServerRpcService.update(executor);
        return ResponseUtils.success("操作成功");
    }
}
