package cn.lite.flow.common.utils;

import org.junit.Assert;
import org.junit.Test;
import org.joda.time.DateTimeUtils;
import java.util.ArrayList;
import java.util.Date;

import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class DateUtilsTest {

    @Test
    public void testGetTodayDateStr() {
        DateTimeUtils.setCurrentMillisFixed(1557915604000L);
        Assert.assertEquals("2019-05-15",
                DateUtils.getTodayDateStr());
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void testFormatToDateStr() {
        Date date = new Date(1557874800000L);
        Assert.assertEquals("2019-05-15",
                DateUtils.formatToDateStr(date));
    }

    @Test
    public void testFormatToDateTimeStr() {
        Date date = new Date(1557874800000L);
        Assert.assertEquals("2019-05-15 00:00:00",
                DateUtils.formatToDateTimeStr(date));
    }

    @Test
    public void testGetYesterdayDate1() {
        DateTimeUtils.setCurrentMillisFixed(1557878400000L);
        Assert.assertEquals(new Date(1557792000000L),
                DateUtils.getYesterdayDate());
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void testGetPreMonth() {
        Assert.assertEquals(new Date(1555804800000L),
                DateUtils.getPreMonth(new Date(1558396800000L)));
    }

    @Test
    public void testGetNextMonth() {
        Assert.assertEquals(new Date(1558396800000L),
                DateUtils.getNextMonth(new Date(1555804800000L)));
    }

    @Test
    public void testGetYesterdayDate2() {
        Assert.assertEquals(new Date(1558224000000L),
                DateUtils.getYesterdayDate(new Date(1558310400000L)));
    }

    @Test
    public void testGetTomorrowDate() {
        Assert.assertEquals(new Date(1558310400000L),
                DateUtils.getTomorrowDate(new Date(1558224000000L)));
    }

    @PrepareForTest({Date.class, DateUtils.class})
    @Test
    public void testGetTodayZeroClock() throws Exception {
        Date current = new Date(1557915604000L);
        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(current);

        Assert.assertEquals("2019-05-15 11:20:04",
                DateUtils.getTodayZeroClock());
    }

    @Test
    public void testGetTomorrowZeroClock() {
        DateTimeUtils.setCurrentMillisFixed(1557878400000L);
        Assert.assertEquals("2019-05-16 01:00:00",
                DateUtils.getTomorrowZeroClock());
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void testFormatToDateTime() {
        Assert.assertEquals(new Date(1557923014000L),
                DateUtils.formatToDateTime("2019-05-15 13:23:34"));
    }

    @Test
    public void testGetBetweenDates() {
        ArrayList<Date> dates = new ArrayList<>();
        dates.add(new Date(1557615600000L));
        dates.add(new Date(1557702000000L));
        dates.add(new Date(1557788400000L));
        Assert.assertEquals(dates, DateUtils.getBetweenDates(
                "2019-05-12", "2019-05-14"));
    }

    @Test
    public void testDateToLong() {
        Assert.assertEquals(20190515,
                DateUtils.dateToLong(new Date(1557923014000L)));
    }

    @Test
    public void testLongToDate() {
        Assert.assertEquals(new Date(1557874800000L),
                DateUtils.longToDate(20190515L));
    }

    @Test
    public void testGetStartTimeOfDay() {
        Assert.assertEquals(new Date(1557874800000L),
                DateUtils.getStartTimeOfDay(new Date(1557923014000L)));
    }

    @Test
    public void testGetEndTimeOfDay() {
        Assert.assertEquals(new Date(1557961199999L),
                DateUtils.getEndTimeOfDay(new Date(1557923014000L)));
    }

    @Test
    public void testGetTommorrowLongDate() {
        DateTimeUtils.setCurrentMillisFixed(1557878400000L);
        Assert.assertEquals(20190516L,
                DateUtils.getTommorrowLongDate());
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void testAddMinute() {
        Assert.assertEquals(new Date(1557916200000L),
                DateUtils.addMinute(new Date(1557914400000L), 30));
    }

    @Test
    public void testMinusMinute() {
        Assert.assertEquals(new Date(1557914400000L),
                DateUtils.minusMinute(new Date(1557916200000L), 30));
    }

    @Test
    public void testBetweenMinutes() {
        Assert.assertEquals(4320, DateUtils.betweenMinutes(
                new Date(1557961199000L), new Date(1558220399000L)));
    }
}
