package cn.lite.flow.console.common.utils;

import cn.lite.flow.console.common.consts.Constants;
import cn.lite.flow.console.common.consts.TimeUnit;
import cn.lite.flow.console.common.time.TimeCalculatorFactory;
import cn.lite.flow.console.common.time.calculator.DayTimeCalculator;
import cn.lite.flow.console.common.time.calculator.HourTimeCalculator;
import cn.lite.flow.console.common.time.calculator.MinuteTimeCalculator;
import cn.lite.flow.console.common.time.calculator.MonthTimeCalculator;
import cn.lite.flow.console.common.time.calculator.TimeCalculator;
import cn.lite.flow.console.common.time.calculator.WeekTimeCalculator;
import cn.lite.flow.console.common.time.calculator.YearTimeCalculator;
import cn.lite.flow.console.model.basic.Task;
import cn.lite.flow.console.model.basic.TaskDependency;
import cn.lite.flow.console.model.consts.TaskDependencyType;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;

/**
 * @description: 任务版本相关工具
 * @author: yueyunyue
 * @create: 2018-07-15
 **/
public class TaskVersionUtils {

    public static Long versionToLong(String versionNoStr){
        return Long.parseLong(versionNoStr);
    }

    public static String getVersionStr(Long versionNo){
        return String.valueOf(versionNo);
    }

    /**
     * 通过任务版本获取对应的时间
     * @param version
     * @return
     */
    public static Date getDateByVersion(String version){
        TimeUnit[] timeUnits = TimeUnit.values();
        String versionExpression = "";
        for(TimeUnit t : timeUnits){
            if(version.length() == t.getVersionExpression().length()){
                versionExpression = t.getVersionExpression();
                break;
            }
        }
        DateTimeFormatter format = DateTimeFormat.forPattern(versionExpression);
        DateTime dateTime = DateTime.parse(version, format);
        return dateTime.toDate();

    }

    /**
     * 通过版本获取获取版本的时间表达式
     * @param version
     * @return
     */
    public static String getVersionExpression(String version){
        TimeUnit[] timeUnits = TimeUnit.values();
        String versionExpression = null;
        for(TimeUnit t : timeUnits){
            if(version.length() == t.getVersionExpression().length()){
                versionExpression = t.getVersionExpression();
                break;
            }
        }
        return versionExpression;

    }

    /**
     * 通过任务版本获取对应的时间
     * @param version
     * @param timeUnit
     * @return
     */
    public static Date getDateByVersion(String version, TimeUnit timeUnit){

        String versionExpression = timeUnit.getVersionExpression();
        DateTimeFormatter format = DateTimeFormat.forPattern(versionExpression);
        DateTime dateTime = DateTime.parse(version, format);
        return dateTime.toDate();

    }
    /**
     * 通过时间获取任务版本
     * @param date
     * @param timeUnit
     * @return
     */
    public static Long getTaskVersion(Date date, TimeUnit timeUnit){
        String versionExpression = timeUnit.getVersionExpression();
        DateTime dateTime = new DateTime(date);
        String versionNoStr = dateTime.toString(versionExpression);
        return versionToLong(versionNoStr);

    }

    /**
     * 计算任务的时间版本
     * @param task
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<Long> getTaskVersions(Task task, Date startTime, Date endTime){

        String cronExpression = task.getCronExpression();

        String crontab = QuartzUtils.completeCrontab(cronExpression);
        List<Date> runtimes = QuartzUtils.getRunDateTimes(crontab, startTime, endTime);
        if(CollectionUtils.isEmpty(runtimes)){
            return null;
        }
        List<Long> taskVersions = Lists.newArrayList();
        Integer period = task.getPeriod();
        TimeUnit timeUnit = TimeUnit.getType(period);
        runtimes.forEach(time -> {
            Long taskVersion = getTaskVersion(time, timeUnit);
            taskVersions.add(taskVersion);
        });

        return taskVersions;
    }

    /**
     * 计算任务依赖的上游任务的数据版本
     * @param task
     * @param taskVersion
     * @param upstreamTask
     * @param dependency
     * @return
     */
    public static List<Long> getUpstreamTaskVersions(Task task, Long taskVersion, Task upstreamTask, TaskDependency dependency){

        String versionStr = getVersionStr(taskVersion);
        Date taskVersionDate = getDateByVersion(versionStr);
        Date startTime = null;
        Date endTime = null;
        /**
         * 默认
         */
        if(dependency.getType() == TaskDependencyType.DEFAULT.getValue()){
            /**
             * 取两个周期中比较大的
             */
            int maxPeriod = Math.max(task.getPeriod(), upstreamTask.getPeriod());
            TimeUnit timeUnit = TimeUnit.getType(maxPeriod);

            TimeCalculator timeCalculator = TimeCalculatorFactory.getCalculator(timeUnit);
            timeCalculator.setTime(taskVersionDate);
            startTime = timeCalculator.getStartTime();
            endTime = timeCalculator.getEndTime();
        /**
         * 偏移量
         */
        }else if(dependency.getType() == TaskDependencyType.OFFSET.getValue()){

            /**
             * 取两个周期中比较大的
             */
            int maxPeriod = Math.max(task.getPeriod(), upstreamTask.getPeriod());
            TimeUnit timeUnit = TimeUnit.getType(maxPeriod);
            /**
             * 获取偏移量
             */
            String config = dependency.getConfig();
            int offset = Integer.parseInt(config);
            Date offsetDate = null;

            /**
             * 计算offsetDate
             */
            DateTime offsetDateTime = new DateTime(taskVersionDate);
            switch (timeUnit){
                case SECOND:
                    offsetDate = offsetDateTime.plusSeconds(offset).toDate();
                    break;
                case MINUTE:
                    offsetDate = offsetDateTime.plusMinutes(offset).toDate();
                    break;
                case HOUR:
                    offsetDate = offsetDateTime.plusHours(offset).toDate();
                    break;
                case DAY:
                    offsetDate = offsetDateTime.plusDays(offset).toDate();
                    break;
                case WEEK:
                    offsetDate = offsetDateTime.plusWeeks(offset).toDate();
                    break;
                case MONTH:
                    offsetDate = offsetDateTime.plusMonths(offset).toDate();
                    break;
                case YEAR:
                    offsetDate = offsetDateTime.plusYears(offset).toDate();
                    break;
            }
            /**
             * 计算时间区间
             */
            TimeCalculator timeCalculator = TimeCalculatorFactory.getCalculator(timeUnit);
            timeCalculator.setTime(offsetDate);

            startTime = timeCalculator.getStartTime();
            endTime = timeCalculator.getEndTime();
        /**
         * 时间表达式区间
         */
        }else if(dependency.getType() == TaskDependencyType.TIME_RANGE.getValue()){

            String conf = dependency.getConfig();
            if(StringUtils.isBlank(conf)){
                throw new IllegalArgumentException("dependency conf can not be empty :" + dependency.getId());
            }
            JSONObject confObj = JSONObject.parseObject(conf);
            String startTimeExpression = confObj.getString(Constants.START_TIME);
            String endTimeExpression = confObj.getString(Constants.END_TIME);

            startTime = ParamExpressionUtils.expression2Date(startTimeExpression, versionStr);
            endTime = ParamExpressionUtils.expression2Date(endTimeExpression, versionStr);

        }

        return getTaskVersions(upstreamTask, startTime, endTime);

    }





}
