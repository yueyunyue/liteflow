package cn.lite.flow.console.common.utils;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.console.common.consts.Constants;
import cn.lite.flow.common.model.consts.TimeUnit;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.quartz.CronExpression;

import java.util.Date;
import java.util.List;

/**
 * @description: quartz相关工具
 * @author: yueyunyue
 * @create: 2018-07-15
 **/
public class QuartzUtils {

    /**
     * 判断crontab是否是个有效的表达式
     */
    public static boolean isCrontabValid(String crontab) {
        return CronExpression.isValidExpression(crontab);
    }
    /**
     * 判断crontab是否是个有效的表达式
     */
    public static boolean isCrontabPeriodValid(String crontab, TimeUnit unit) {
        return CronExpression.isValidExpression(crontab);
    }

    /**
     * 获取完整的quartz表达式，因为任务的表达式精确度只到分钟，所以秒默认为0
     * @param cron
     * @return
     */
    public static String completeCrontab(String cron){
        return CommonConstants.ZERO + " " + cron;
    }

    /**
     * 获取时间区间内，满足crontab表达式的时间
     * @param crontab
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<Date> getRunDateTimes(String crontab, Date startTime, Date endTime){

        Preconditions.checkArgument(startTime != null, "startTime is null");
        Preconditions.checkArgument(endTime != null, "endTime is null");

        List<Date> dateTimes = Lists.newArrayList();
        try {
            CronExpression cronExpression = new CronExpression(crontab);
            /**
             * 由于开始时间可能与第一次触发时间相同而导致拿不到第一个时间
             * 所以，起始时间较少1ms
             */
            DateTime startDateTime = new DateTime(startTime).minusMillis(1);
            Date runtime = startDateTime.toDate();
            do{
                runtime = cronExpression.getNextValidTimeAfter(runtime);
                if(runtime.before(endTime)){
                    dateTimes.add(runtime);
                }

            }while (runtime.before(endTime));

        } catch (Exception e) {
            throw new IllegalArgumentException(crontab + " is invalid");
        }
        return dateTimes;

    }


}
