package cn.lite.flow.console.web.controller.auth;

import cn.lite.flow.console.common.enums.AuthCheckTypeEnum;
import cn.lite.flow.console.common.enums.OperateTypeEnum;
import cn.lite.flow.console.common.enums.SourceTypeEnum;
import cn.lite.flow.console.common.enums.TargetTypeEnum;
import cn.lite.flow.console.common.exception.ConsoleRuntimeException;
import cn.lite.flow.console.common.model.vo.SessionUser;
import cn.lite.flow.console.common.utils.ResponseUtils;
import cn.lite.flow.console.model.basic.UserGroupAuthMid;
import cn.lite.flow.console.model.query.UserGroupAuthMidQM;
import cn.lite.flow.console.service.UserGroupAuthMidService;
import cn.lite.flow.console.service.auth.AuthCheckManager;
import cn.lite.flow.console.web.annotation.AuthCheckIgnore;
import cn.lite.flow.console.web.controller.BaseController;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by ly on 2018/12/27.
 */
@RestController("authController")
@RequestMapping("console/auth")
public class AuthController extends BaseController {

    @Autowired
    private UserGroupAuthMidService userGroupAuthMidService;

    @Autowired
    private AuthCheckManager authCheckManager;

    /**
     * 任务 or 任务组权限列表
     *
     * @param targetId          目标id
     * @param targetType        目标类型
     * @param sourceType        源类型
     * @param pageNum           当前页码
     * @param pageSize          每页数量
     * @return
     */
    @RequestMapping(value = "list")
    @AuthCheckIgnore
    public String list(
            @RequestParam(value = "targetId") Long targetId,
            @RequestParam(value = "targetType") Integer targetType,
            @RequestParam(value = "sourceType", required = false) Integer sourceType,
            @RequestParam(value = "pageNum",required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {
        UserGroupAuthMidQM userGroupAuthMidQM = new UserGroupAuthMidQM();
        userGroupAuthMidQM.setTargetType(targetType);
        userGroupAuthMidQM.setTargetId(targetId);
        userGroupAuthMidQM.setSourceType(sourceType);
        userGroupAuthMidQM.setPage(pageNum, pageSize);
        userGroupAuthMidQM.addOrderDesc(UserGroupAuthMidQM.COL_UPDATE_TIME);

        List<UserGroupAuthMid> userGroupAuthMidList = userGroupAuthMidService.list(userGroupAuthMidQM);

        int total = 0;
        JSONArray datas = new JSONArray();
        if (CollectionUtils.isNotEmpty(userGroupAuthMidList)) {
            total = userGroupAuthMidService.count(userGroupAuthMidQM);
            List<Long> userIds = Lists.newArrayList();
            List<Long> groupIds = Lists.newArrayList();
            List<Long> createUserIds = Lists.newArrayListWithCapacity(userGroupAuthMidList.size());
            userGroupAuthMidList.forEach(o -> {
                if (o.getSourceType().equals(SourceTypeEnum.SOURCE_TYPE_USER.getCode())) {
                    userIds.add(o.getSourceId());
                } else if (o.getSourceType().equals(SourceTypeEnum.SOURCE_TYPE_USER_GROUP.getCode())) {
                    groupIds.add(o.getSourceId());
                }
                createUserIds.add(o.getUserId());
            });

            Map<Long, String> userInfo = getUserName(userIds);
            Map<Long, String> userGroupInfo = getGroupName(groupIds);

            Map<Long, String> createUserInfo = getUserName(createUserIds);

            userGroupAuthMidList.forEach(o -> {
                JSONObject obj = new JSONObject();
                obj.put("id", o.getId());
                obj.put("sourceId", o.getSourceId());

                if (SourceTypeEnum.SOURCE_TYPE_USER.getCode().equals(o.getSourceType())) {
                    obj.put("sourceName", userInfo.get(o.getSourceId()));
                } else if (SourceTypeEnum.SOURCE_TYPE_USER_GROUP.getCode().equals(o.getSourceType())) {
                    obj.put("sourceName", userGroupInfo.get(o.getSourceId()));
                }
                obj.put("sourceType", o.getSourceType());
                obj.put("hasEditAuth", o.getHasEditAuth());
                obj.put("hasExecuteAuth", o.getHasExecuteAuth());
                setUserInfo(obj, o.getUserId(), createUserInfo);
                obj.put("createTime", o.getCreateTime());
                obj.put("updateTime", o.getUpdateTime());
                datas.add(obj);
            });
        }
        return ResponseUtils.list(total, datas);
    }

    /**
     * 添加  or  更新权限
     *
     * @param id
     * @param targetId          目标id
     * @param targetType        目标类型
     * @param sourceId          源id
     * @param sourceType        源类型
     * @param hasExecuteAuth    是否有执行权限
     * @param hasEditAuth       是否有编辑权限
     * @return
     */
    @RequestMapping(value = "addOrUpdate")
    @AuthCheckIgnore
    public String addOrUpdate(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "targetId") Long targetId,
            @RequestParam(value = "targetType") Integer targetType,
            @RequestParam(value = "sourceId") Long sourceId,
            @RequestParam(value = "sourceType") Integer sourceType,
            @RequestParam(value = "hasExecuteAuth") Integer hasExecuteAuth,
            @RequestParam(value = "hasEditAuth") Integer hasEditAuth
    ) {
        SessionUser user = getUser();
        if (!user.getIsSuper()) {
            AuthCheckTypeEnum checkTypeEnum = null;
            if (TargetTypeEnum.TARGET_TYPE_TASK.getCode().equals(targetType)) {
                checkTypeEnum = AuthCheckTypeEnum.AUTH_CHECK_TASK;
            } else {
                checkTypeEnum = AuthCheckTypeEnum.AUTH_CHECK_FLOW;
            }

            if (!authCheckManager.checkAuth(user.getId(), user.getGroupIds(), targetId, checkTypeEnum, OperateTypeEnum.OPERATE_TYPE_EDIT)) {
                throw new ConsoleRuntimeException("该用户没有该目标类型的编辑权限");
            }
        }

        UserGroupAuthMid userGroupAuthMid = new UserGroupAuthMid();
        userGroupAuthMid.setId(id);
        userGroupAuthMid.setSourceId(sourceId);
        userGroupAuthMid.setSourceType(sourceType);
        userGroupAuthMid.setTargetId(targetId);
        userGroupAuthMid.setTargetType(targetType);
        userGroupAuthMid.setHasEditAuth(hasEditAuth);
        userGroupAuthMid.setHasExecuteAuth(hasExecuteAuth);
        userGroupAuthMid.setUserId(user.getId());

        try {
            if (id == null) {
                userGroupAuthMidService.add(userGroupAuthMid);
            } else {
                userGroupAuthMidService.update(userGroupAuthMid);
            }
        } catch (DuplicateKeyException e) {
            throw new ConsoleRuntimeException("该权限已经存在");
        }
        return ResponseUtils.success("操作成功");
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "delete")
    public String delete(@RequestParam(value = "id") Long id) {
        SessionUser user = getUser();
        if (!user.getIsSuper()) {
            UserGroupAuthMid userGroupAuthMid = userGroupAuthMidService.getById(id);
            if (userGroupAuthMid == null) {
                throw new ConsoleRuntimeException("该记录不存在");
            }
            AuthCheckTypeEnum checkTypeEnum = null;
            if (TargetTypeEnum.TARGET_TYPE_TASK.getCode().equals(userGroupAuthMid.getTargetType())) {
                checkTypeEnum = AuthCheckTypeEnum.AUTH_CHECK_TASK;
            } else {
                checkTypeEnum = AuthCheckTypeEnum.AUTH_CHECK_FLOW;
            }

            if (!authCheckManager.checkAuth(user.getId(), user.getGroupIds(), userGroupAuthMid.getTargetId(), checkTypeEnum, OperateTypeEnum.OPERATE_TYPE_EDIT)) {
                throw new ConsoleRuntimeException("该用户没有该目标类型的编辑权限");
            }
        }

        userGroupAuthMidService.deleteById(id);
        return ResponseUtils.success("操作成功");
    }
}
