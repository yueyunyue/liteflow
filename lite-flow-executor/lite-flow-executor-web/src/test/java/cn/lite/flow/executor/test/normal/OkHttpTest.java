package cn.lite.flow.executor.test.normal;

import okhttp3.HttpUrl;
import org.junit.Test;

/**
 * @description:
 * @author: yueyunyue
 * @create: 2019-08-30
 **/
public class OkHttpTest {

    @Test
    public void testGetUrl(){
        String url = "http://www.baidu.com?c=c";
        HttpUrl.Builder urlBuilder =  HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("a", "a");
        urlBuilder.addQueryParameter("b", "a");
        System.out.println(urlBuilder.build().toString());
    }

}
