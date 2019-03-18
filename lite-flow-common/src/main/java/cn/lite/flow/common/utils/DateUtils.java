package cn.lite.flow.common.utils;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;

/**
 *
 * 时间相关的工具类
 */
public class DateUtils {

    public final static String COMMON_DATE_FORMAT = "yyyy-MM-dd";

    public final static String COMMON_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前时间
     */
    public static Date getNow() {
        return DateTime.now().toDate();
    }

    /**
     * 获取今天日期
     *
     * @return
     */
    public static String getTodayDateStr() {
        return formatToDateStr(getNow());
    }

    /**
     * 格式化为日期2019-01-31
     *
     * @param date
     * @return
     */
    public static String formatToDateStr(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(COMMON_DATE_FORMAT);
    }

    /**
     * 格式化为日期2019-01-31 00:00:00
     *
     * @param date
     * @return
     */
    public static String formatToDateTimeStr(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(COMMON_DATE_TIME_FORMAT);
    }
    /**
     * 获取昨天的日期
     *
     * @return
     */
    public static Date getYesterdayDate() {
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(-1);
        return dateTime.toDate();
    }

    /**
     * 获取上个月日期
     * @return
     */
    public static Date getPreMonth(Date date) {
        DateTime dateTime = new DateTime(date);
        dateTime = dateTime.minusMonths(1);
        return dateTime.toDate();
    }

    /**
     * 获取下个月
     * @param date
     * @return
     */
    public static Date getNextMonth(Date date) {
        DateTime dateTime = new DateTime(date);
        dateTime = dateTime.plusMonths(1);
        return dateTime.toDate();
    }
    /**
     * 获取昨天的日期
     *
     * @return
     */
    public static Date getYesterdayDate(Date date) {
        DateTime dateTime = new DateTime(date);
        dateTime = dateTime.plusDays(-1);
        return dateTime.toDate();
    }
    /**
     * 获取明天的日期
     *
     * @return
     */
    public static Date getTomorrowDate() {
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(1);
        return dateTime.toDate();
    }
    /**
     * 获取明天的日期
     *
     * @return
     */
    public static Date getTomorrowDate(Date date) {
        DateTime dateTime = new DateTime(date);
        dateTime = dateTime.plusDays(1);
        return dateTime.toDate();
    }
    /**
     * 获取日期的0点时间
     *
     * @param date
     * @return
     */
    public static String getDateZeroClock(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(COMMON_DATE_TIME_FORMAT);
    }

    /**
     * 获取今天的零点时间
     *
     * @return
     */
    public static String getTodayZeroClock() {
        return getDateZeroClock(new Date());
    }

    /**
     * 获取明天的零点时间
     *
     * @return
     */
    public static String getTomorrowZeroClock() {
        return getDateZeroClock(getTomorrowDate());
    }

    /**
     * 将字符串格式化为Date
     *
     * @param date
     * @return
     */
    public static Date formatToDate(String date) {
        DateTimeFormatter format = DateTimeFormat.forPattern(COMMON_DATE_FORMAT);
        DateTime dateTime = DateTime.parse(date, format);
        return dateTime.toDate();
    }
    /**
     * 将字符串格式化为DateTime
     * @param date
     * @return
     */
    public static Date formatToDateTime(String date) {
        DateTimeFormatter format = DateTimeFormat.forPattern(COMMON_DATE_TIME_FORMAT);
        DateTime dateTime = DateTime.parse(date, format);
        return dateTime.toDate();
    }

    /**
     * 获取时间区间内的日期数
     *
     * @param start
     * @param end
     * @return
     */
    public static List<Date> getBetweenDates(String start, String end) {
        List<Date> dates = Lists.newLinkedList();
        DateTime startDate = new DateTime(formatToDate(start));
        DateTime endDate = new DateTime(formatToDate(end));
        while (startDate.compareTo(endDate) <= 0) {
            dates.add(startDate.toDate());
            startDate = startDate.plusDays(1);
        }
        return dates;
    }

    /**
     * 时间转换成long类型 ->20180101
     * @param date
     * @return
     */
    public static long dateToLong(Date date) {
        DateTime dateTime = new DateTime(date);
        return Long.parseLong(dateTime.toString("yyyyMMdd"));
    }
    /**
     * long类型转换成时间
     * @param date
     * @return
     */
    public static Date longToDate(Long date) {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMdd");
        DateTime dateTime = DateTime.parse(String.valueOf(date), format);
        return dateTime.toDate();
    }

    /**
     * 获取天的开始时间: 2018-01-01 00:00:00
     * @param date
     * @return
     */
    public static Date getStartTimeOfDay(Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime.withTimeAtStartOfDay().toDate();
    }

    /**
     * 获取天的结束时间：2018-01-01 23:59:59
     * @param date
     * @return
     */
    public static Date getEndTimeOfDay(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.millisOfDay().withMaximumValue().toDate();
    }

    /**
     * 获取明天longDate->20180801
     * @return
     */
    public static long getTommorrowLongDate() {
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(1);
        return Long.parseLong(dateTime.toString("yyyyMMdd"));
    }

    /**
     * 增加分钟
     * @param date
     * @return
     */
    public static Date addMinute(Date date, int minute) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMinutes(minute).toDate();
    }
    /**
     * 减少分钟
     * @param date
     * @return
     */
    public static Date minusMinute(Date date, int minute) {
        DateTime dateTime = new DateTime(date);
        return dateTime.minusMinutes(minute).toDate();
    }

    /**
     * 相差分钟数
     * @param start
     * @param end
     * @return
     */
    public static int betweenMinutes(Date start, Date end){
        Interval interval = new Interval(start.getTime(), end.getTime());
        Period period = interval.toPeriod();

        return period.getMinutes();
    }
}