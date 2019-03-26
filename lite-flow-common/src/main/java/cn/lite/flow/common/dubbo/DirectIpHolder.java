package cn.lite.flow.common.dubbo;

/**
 * Created by ly on 2018/12/17.
 */
public class DirectIpHolder {

    private static ThreadLocal<String> holder = new ThreadLocal<>();

    /**
     * 获取ip
     *
     * @return
     */
    public static String getIp() {
        return holder.get();
    }

    /**
     * 设置ip
     *
     * @param ip
     */
    public static void setIp(String ip) {
        holder.set(ip);
    }

    /**
     * 删除
     *
     */
    public static void remove() {
        holder.remove();
    }

}
