package cn.lite.flow.console.web.annotation;

import cn.lite.flow.console.common.enums.AuthCheckTypeEnum;
import cn.lite.flow.console.common.enums.OperateTypeEnum;

import java.lang.annotation.*;

/**
 * Created by ly on 2018/11/5.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface AuthCheck {

    /**
     * 类型
     *
     * @return
     */
    AuthCheckTypeEnum checkType() default AuthCheckTypeEnum.AUTH_CHECK_TASK;

    /**
     * 参数
     *
     * @return
     */
    String paramName() default "id";

    /**
     * 操作类型
     *
     * @return
     */
    OperateTypeEnum operateType() default OperateTypeEnum.OPERATE_TYPE_EDIT;
}
