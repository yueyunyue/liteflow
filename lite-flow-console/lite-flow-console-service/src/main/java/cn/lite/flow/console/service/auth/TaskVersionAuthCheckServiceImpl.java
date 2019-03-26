package cn.lite.flow.console.service.auth;

import cn.lite.flow.console.common.enums.AuthCheckTypeEnum;
import cn.lite.flow.console.common.enums.OperateTypeEnum;
import cn.lite.flow.console.model.basic.TaskVersion;
import cn.lite.flow.console.service.TaskVersionService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ly on 2019/1/29.
 */
@Service("taskVersionAuthCheckServiceImpl")
public class TaskVersionAuthCheckServiceImpl implements AuthCheckService {

    @Autowired
    private TaskVersionService taskVersionService;

    @Qualifier("taskAuthCheckServiceImpl")
    @Autowired
    private AuthCheckService taskAuthCheckExecutor;

    @Override
    public AuthCheckTypeEnum supportCheckType() {
        return AuthCheckTypeEnum.AUTH_CHECK_TASK_VERSION;
    }

    @Override
    public boolean checkUserAuth(Long userId, Long targetId, OperateTypeEnum operateTypeEnum) {
        TaskVersion taskVersion = taskVersionService.getById(targetId);
        if (taskVersion != null) {
            return taskAuthCheckExecutor.checkUserAuth(userId, taskVersion.getTaskId(), operateTypeEnum);
        }
        return false;
    }

    @Override
    public boolean checkUserGroupAuth(List<Long> userGroupIds, Long targetId, OperateTypeEnum operateTypeEnum) {
        if(CollectionUtils.isEmpty(userGroupIds)){
            return false;
        }
        TaskVersion taskVersion = taskVersionService.getById(targetId);
        if (taskVersion != null) {
            return taskAuthCheckExecutor.checkUserGroupAuth(userGroupIds, taskVersion.getTaskId(), operateTypeEnum);
        }

        return false;
    }
}
