package cn.lite.flow.console.service.auth;

import cn.lite.flow.console.common.enums.AuthCheckTypeEnum;
import cn.lite.flow.console.common.enums.OperateTypeEnum;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by ly on 2018/11/9.
 */
@Service
public class AuthCheckManager extends InstantiationAwareBeanPostProcessorAdapter {

    private static Logger LOG = LoggerFactory.getLogger(AuthCheckManager.class);

    private final static Map<AuthCheckTypeEnum, AuthCheckService> executors = Maps.newHashMap();

    /**
     * 校验权限
     * @param userId            用户id
     * @param groupIds          用户所属用户组id
     * @param targetId          目标id
     * @param authCheckTypeEnum 权限校验类型
     * @param operateTypeEnum   操作类型
     * @return
     */
    public boolean checkAuth(Long userId, List<Long> groupIds, Long targetId,
                             AuthCheckTypeEnum authCheckTypeEnum, OperateTypeEnum operateTypeEnum) {
        AuthCheckService authCheckService = executors.get(authCheckTypeEnum);
        Preconditions.checkArgument(authCheckService != null, "targetTypeEnum不合法");

        return authCheckService.checkUserAuth(userId, targetId, operateTypeEnum)
                || authCheckService.checkUserGroupAuth(groupIds, targetId, operateTypeEnum);
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AuthCheckService) {
            AuthCheckTypeEnum checkTypeEnum = ((AuthCheckService) bean).supportCheckType();
            AuthCheckService exe = executors.get(checkTypeEnum);
            if (exe != null) {
                LOG.warn("authCheck:{} already exists:{}, replace by:{}",
                        checkTypeEnum.getMsg(), exe.getClass(), bean.getClass());
            }
            executors.put(checkTypeEnum, (AuthCheckService) bean);
        }
        return bean;
    }

}
