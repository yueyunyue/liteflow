package cn.lite.flow.executor.test.normal;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.common.utils.IpUtils;
import cn.lite.flow.common.utils.CommonFileUtils;
import cn.lite.flow.executor.common.utils.ProcessorUtils;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.Map;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2019-01-10
 **/
public class NormalTest {

    @Test
    public void testUrlParse(){
        String url = CommonConstants.ATTACHMENT_PREFIX  + CommonConstants.URL_QUESTION + "id=123&name=112321";
//        String url = "https://www.baidu.com/s?wd=protocol&rsv_spt=1&rsv_iqid=0x9d4b46510000c731&issp=1&f=3&rsv_bp=1&rsv_idx=2&ie=utf-8&rqlang=cn&tn=baiduhome_pg&rsv_enter=1&inputT=335&rsv_t=532fJ%2FHuQHep7QULmyFHvQo6B4BzXlLBF3tbtlH5%2Fb2Eso2kGOqWeZTmAmxaAjlEIuyQ&oq=protocol%2520buffer&rsv_pq=e1f38f62000146b2&rsv_sug3=38&rsv_sug1=37&rsv_sug7=100&rsv_sug2=0&prefixsug=protocol&rsp=0&rsv_sug4=1986";

        Map<String, String> paramMap = CommonFileUtils.getAttachmentParamMap(url);
        System.out.println(url);
        System.out.println(JSONObject.toJSONString(paramMap));
        System.out.println(CommonFileUtils.getProtocolAndDomain(url));

    }


    @Test
    public void testIsAssignableFrom(){
        System.out.println("object-string:" + Object.class.isAssignableFrom(String.class));
        System.out.println("string-object:" + String.class.isAssignableFrom(Object.class));

    }
    @Test
    public void testGetIp(){
        System.out.println("Ip:" + IpUtils.getIp());

    }
    @Test
    public void testProcessId(){

        String applicationId = ProcessorUtils.generateApplicationId(1L, 2L);
        System.out.println(applicationId);
        System.out.println(ProcessorUtils.getProcessId(applicationId));
    }

}
