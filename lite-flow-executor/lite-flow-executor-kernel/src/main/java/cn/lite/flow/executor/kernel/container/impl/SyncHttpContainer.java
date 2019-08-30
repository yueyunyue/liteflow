package cn.lite.flow.executor.kernel.container.impl;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.model.consts.HttpCodeType;
import cn.lite.flow.common.model.consts.HttpMethodType;
import cn.lite.flow.executor.common.exception.ExecutorRuntimeException;
import cn.lite.flow.executor.common.utils.ExecutorLoggerFactory;
import cn.lite.flow.executor.common.utils.Props;
import cn.lite.flow.common.utils.OkHttpUtils;
import cn.lite.flow.executor.kernel.utils.JobUtils;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.kernel.SyncContainer;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @description: 同步http
 * @author: yueyunyue
 * @create: 2019-08-30
 **/
public class SyncHttpContainer extends SyncContainer {

    private Call call;

    private final static Logger LOG = LoggerFactory.getLogger(SyncHttpContainer.class);

    public SyncHttpContainer(ExecutorJob executorJob) {
        super(executorJob);
    }

    @Override
    public void runInternal() throws Exception {
        Logger logger = JobUtils.getLogger(executorJob);
        try {
            String config = executorJob.getConfig();
            Props props = new Props(config);

            long readTimeOut = props.getLong(CommonConstants.HTTP_READ_TIMEOUT, CommonConstants.DEFAULT_HTTP_READ_TIMEOUT);

            OkHttpClient client = new OkHttpClient()
                    .newBuilder()
                    .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                    .build();
            /**
             * 生成脚本文件
             */
            String url = props.getString(CommonConstants.HTTP_URL);
            String method = props.getString(CommonConstants.HTTP_METHOD);
            String param = props.getString(CommonConstants.HTTP_PARAM);
            String header = props.getString(CommonConstants.HTTP_HEADER);
            Map<String, String> paramMap = OkHttpUtils.covert2Params(param);
            Map<String, String> headerMap = OkHttpUtils.covert2Params(header);

            Request.Builder requestBuilder = null;
            /**
             * get请求
             */
            if(StringUtils.equals(method, HttpMethodType.GET.name())){
                    requestBuilder = OkHttpUtils.getGetBuilder(url, headerMap, paramMap);
            /**
             * post请求
             */
            }else if(StringUtils.equals(method, HttpMethodType.POST.name())){
                requestBuilder = OkHttpUtils.getPostBuilder(url, headerMap, paramMap);
            }
            if(requestBuilder == null){
                String errorMsg = "request param error";
                throw new ExecutorRuntimeException(errorMsg);
            }
            call = client.newCall(requestBuilder.build());
            Response response = call.execute();

            logger.info(response.body().string());

            int code = response.code();
            /**
             * 状态码不等于200的报异常
             */
            if(code != HttpCodeType.CODE_200.getCode()){
                String errorMsg = "run error,response:" + response.toString();
                throw new ExecutorRuntimeException(errorMsg);
            }
        } finally {
            ExecutorLoggerFactory.stop(executorJob.getId());
        }
    }

    @Override
    public void kill() {
        try {
            if(call != null){
                if(call.isExecuted() || call.isCanceled()){
                    return;
                }
                call.cancel();
            }
            LOG.info("kill remote shell container executorJobId:{} get applicationId:{}", executorJob.getId(), executorJob.getApplicationId());
        } catch (Throwable e) {
            LOG.error("kill remote shell container, jobId:" + executorJob.getId(), e);
            throw new ExecutorRuntimeException(e.getMessage());
        }
    }

}
