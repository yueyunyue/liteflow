package cn.lite.flow.console.common.utils.portal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by ly on 2018/11/23.
 */
@Data
@ToString
@AllArgsConstructor
public class CallableInfo {

    private static final Logger LOG = LoggerFactory.getLogger(CallableInfo.class);

    private String name;        //名称

    private Future<?> future;   //返回future

    private long waitMills;     //等待返回结果时长

    /**
     * 获取返回结果
     *
     * @return
     */
    public Object getObject() {
        try {
            return future.get(waitMills, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOG.error("get object error!name:{}", name, e);
        }
        return null;
    }

}
