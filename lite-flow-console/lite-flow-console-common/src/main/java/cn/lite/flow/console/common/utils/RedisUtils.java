package cn.lite.flow.console.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by ly on 2018/10/31.
 */
@Component
public class RedisUtils {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置元素
     *
     * @param key       key
     * @param value     值
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置元素以及过期时间
     *
     * @param key       key
     * @param value     value
     * @param timeout   过期时间
     * @param timeUnit  时间单位
     */
    public void set(String key, String value, long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 获取元素的值
     *
     * @param key
     * @return
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        Boolean result = stringRedisTemplate.hasKey(key);
        return result != null && result;
    }

    /**
     * 删除元素的值
     *
     * @param key
     * @return
     */
    public boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * 设置key的过期时间
     *
     * @param key           key
     * @param timeout       过期时间
     * @param timeUnit      时间单位
     * @return
     */
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        Boolean result = stringRedisTemplate.expire(key, timeout, timeUnit);
        return result != null && result;
    }
}
