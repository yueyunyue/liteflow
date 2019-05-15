package cn.lite.flow.executor.kernel.container.impl;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.utils.JSONUtils;
import cn.lite.flow.common.utils.ParamExpressionUtils;
import cn.lite.flow.common.utils.ParamUtils;
import cn.lite.flow.executor.common.utils.Props;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.service.utils.ExecutorFileUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @description: sql容器
 * @author: yueyunyue
 * @create: 2019-05-15
 **/
public class SQLOnYarnContainer extends SparkOnYarnContainer {

    public SQLOnYarnContainer(ExecutorJob executorJob) throws Throwable{
        super(executorJob);
        String config = executorJob.getConfig();
        Props props = new Props(config);
        /**
         * 可以直接写sql
         */
        String sqlContent = props.getString(CommonConstants.PARAM_SQL);
        if(StringUtils.isBlank(sqlContent)){
            String filePath = props.getString(CommonConstants.PARAM_FILE);
            sqlContent = ExecutorFileUtils.getFileContent(filePath);
        }

        String param = props.getString(CommonConstants.PARAM);
        logger.info("job:{} original sql:{}", executorJob.getId(), param, sqlContent);

        Map<String, String> jobParamMap = props.getParamMap();
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.putAll(jobParamMap);
        /**
         * 参数不为空，替换关键字
         */
        if(StringUtils.isNotBlank(param)){
            paramMap.putAll(ParamUtils.param2Map(param));
        }
        sqlContent = ParamExpressionUtils.handleParam(sqlContent, paramMap);
        logger.info("job:{} handle shell param:{} sql:{}", executorJob.getId(), paramMap.toString(), sqlContent);
        props.put(CommonConstants.PARAM_SQL, sqlContent);
        //配置重新放回config
        config = JSONUtils.toJSONStringWithoutCircleDetect(props.getParamMap());
        executorJob.setConfig(config);
    }
}
