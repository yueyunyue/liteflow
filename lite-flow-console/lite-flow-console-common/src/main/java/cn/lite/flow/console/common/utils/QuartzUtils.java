package cn.lite.flow.console.common.utils;

import cn.lite.flow.common.model.consts.CommonConstants;
import cn.lite.flow.console.common.consts.Constants;
import cn.lite.flow.common.model.consts.TimeUnit;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.quartz.CronExpression;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

    /**
     * 使用默认时区
     * @param fromDate
     * @param cronExpression
     * @return
     */
    public static Date getNextFireTimeWithDefaultTimeZone(Date fromDate, String cronExpression) {
        return getNextFireTime(fromDate, cronExpression, TimeZone.getDefault());
    }
    /**
     * 获取表达式下一次运行时间，
     * copy from ooize，https://github.com/Maheshkudu/oozieflow
     * @param fromDate
     * @param cronExpression
     * @param tz
     * @return
     */
    public static Date getNextFireTime(Date fromDate,  String cronExpression, TimeZone tz) {

        try {
            String[] cronArray = cronExpression.split(CommonConstants.BLANK_SPACE);
            Date nextTime = null;

            // Current CronExpression doesn't support operations
            // where both date of months and day of weeks are specified.
            // As a result, we need to split this scenario into two cases
            // and return the earlier time
            if (!cronArray[2].trim().equals(CommonConstants.QUESTION) && !cronArray[4].trim().equals(CommonConstants.QUESTION)) {

                // When any one of day of month or day of week fields is a wildcard
                // we need to replace the wildcard with CommonConstants.QUESTION
                if (cronArray[2].trim().equals(CommonConstants.ASTERISK) || cronArray[4].trim().equals(CommonConstants.ASTERISK)) {
                    if (cronArray[2].trim().equals(CommonConstants.ASTERISK)) {
                        cronArray[2] = CommonConstants.QUESTION;
                    }
                    else {
                        cronArray[4] = CommonConstants.QUESTION;
                    }
                    cronExpression= StringUtils.join(cronArray, CommonConstants.BLANK_SPACE);

                    // The cronExpression class takes second
                    // as the first field where oozie is operating on
                    // minute basis
                    CronExpression expr = new CronExpression(CommonConstants.ZERO + CommonConstants.BLANK_SPACE + cronExpression);
                    expr.setTimeZone(tz);
                    nextTime = expr.getNextValidTimeAfter(fromDate);
                }
                // If both fields are specified by non-wildcards,
                // we need to split it into two expressions
                else {
                    String[] cronArray1 = cronExpression.split(CommonConstants.BLANK_SPACE);
                    String[] cronArray2 = cronExpression.split(CommonConstants.BLANK_SPACE);

                    cronArray1[2] = CommonConstants.QUESTION;
                    cronArray2[4] = CommonConstants.QUESTION;

                    String freq1 = StringUtils.join(cronArray1,  CommonConstants.BLANK_SPACE);
                    String freq2 = StringUtils.join(cronArray2,  CommonConstants.BLANK_SPACE);

                    // The cronExpression class takes second
                    // as the first field where oozie is operating on
                    // minute basis
                    CronExpression expr1 = new CronExpression(CommonConstants.ZERO +  CommonConstants.BLANK_SPACE + freq1);
                    expr1.setTimeZone(tz);
                    CronExpression expr2 = new CronExpression(CommonConstants.ZERO +  CommonConstants.BLANK_SPACE + freq2);
                    expr2.setTimeZone(tz);
                    nextTime = expr1.getNextValidTimeAfter(fromDate);
                    Date nextTime2 = expr2.getNextValidTimeAfter(fromDate);
                    nextTime = nextTime.compareTo(nextTime2) < CommonConstants.ZERO ? nextTime: nextTime2;
                }
            }
            else {
                // The cronExpression class takes second
                // as the first field where oozie is operating on
                // minute basis
                CronExpression expr  = new CronExpression(CommonConstants.ZERO +  CommonConstants.BLANK_SPACE + cronExpression);
                expr.setTimeZone(tz);
                nextTime = expr.getNextValidTimeAfter(fromDate);
            }
            return nextTime;
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage());
        }
    }


}
