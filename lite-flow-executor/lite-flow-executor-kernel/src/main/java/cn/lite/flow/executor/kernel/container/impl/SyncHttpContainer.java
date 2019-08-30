package cn.lite.flow.executor.kernel.container.impl;

import cn.lite.flow.common.model.Tuple;
import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.model.consts.HttpCodeType;
import cn.lite.flow.common.model.consts.HttpMethodType;
import cn.lite.flow.common.utils.ParamExpressionUtils;
import cn.lite.flow.common.utils.ParamUtils;
import cn.lite.flow.executor.common.consts.Constants;
import cn.lite.flow.executor.common.exception.ExecutorRuntimeException;
import cn.lite.flow.executor.common.utils.Props;
import cn.lite.flow.executor.kernel.job.ShellProcessJob;
import cn.lite.flow.executor.kernel.utils.HttpContainerUtils;
import cn.lite.flow.executor.kernel.utils.JobUtils;
import cn.lite.flow.executor.model.basic.ExecutorJob;
import cn.lite.flow.executor.model.kernel.SyncContainer;
import cn.lite.flow.executor.service.utils.ExecutorFileUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
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
            List<Tuple<String, String>> params = HttpContainerUtils.covert2Params(param);
            List<Tuple<String, String>> headers = HttpContainerUtils.covert2Params(header);

            Request.Builder requestBuilder = null;
            /**
             * get请求
             */
            if(StringUtils.equals(method, HttpMethodType.GET.name())){
                if(CollectionUtils.isNotEmpty(params)){
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
                    for (Tuple<String, String> paramTuple : params) {
                        urlBuilder.addQueryParameter(paramTuple.getA(), paramTuple.getB());
                    }
                    HttpUrl httpUrl = urlBuilder.build();
                    requestBuilder = new Request.Builder().get().url(httpUrl);

                }
            /**
             * post请求
             */
            }else if(StringUtils.equals(method, HttpMethodType.POST.name())){
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                if(CollectionUtils.isNotEmpty(params)){
                    for (Tuple<String, String> paramTuple : params) {
                        formBodyBuilder.add(paramTuple.getA(), paramTuple.getB());
                    }
                }
                requestBuilder.post(formBodyBuilder.build()).url(url);
            }
            /**
             * header处理
             */
            if(requestBuilder != null && CollectionUtils.isNotEmpty(headers)){
                for (Tuple<String, String> headerTuple : headers) {
                    requestBuilder.addHeader(headerTuple.getA(), headerTuple.getB());
                }

            }
            if(requestBuilder != null){
                call = client.newCall(requestBuilder.build());
                Response response = call.execute();
                int code = response.code();
                /**
                 * 状态码不等于200的报异常
                 */
                if(!(code == HttpCodeType.CODE_200.getCode())){
                    String errorMsg = "run error,response:" + response.toString();
                    throw new ExecutorRuntimeException(errorMsg);
                }

            }
        } finally {

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
