package com.tfjt.pay.external.unionpay.utils;

import com.google.common.collect.Lists;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.enums.DatePatternEnum;
import com.tfjt.tfcommon.core.util.DateTimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日期工具类
 *
 * @author effine
 * @Date 2022/9/29 16:22
 * @email iballad#163.com
 */
@Slf4j
public final class DateUtil extends DateUtils {

    public static final DateTimeFormatter FMT_DEFAULT = ofPattern(DatePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern());

    public static final String YYYY_MM_DD_T_HH_MM_SS_SSSXXX = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    /**
     * 格式化日期(如2022-9-29 00:00:00)
     *
     * @param date 待处理的日期
     * @return 处理完成的日期字符串
     */
    public static String format(Date date) {
        return FMT_DEFAULT.format(DateUtil.date2LocalDateTime(date));
    }

    /**
     * 指定格式对时间进行格式化
     *
     * @param date    待处理的时间
     * @param pattern 格式枚举
     * @return 处理完成的时间字符串
     */
    public static String format(Date date, String pattern) {
        DateTimeFormatter dateTimeFormatter = ofPattern(pattern);
        return dateTimeFormatter.format(DateUtil.date2LocalDateTime(date));
    }

    /**
     * 指定格式对时间进行格式化
     *
     * @param date        待处理的时间
     * @param patternEnum 格式枚举
     * @return 处理完成的时间字符串
     */
    public static String format(Date date, DatePatternEnum patternEnum) {
        DateTimeFormatter dateTimeFormatter = ofPattern(patternEnum.getPattern());
        return dateTimeFormatter.format(DateUtil.date2LocalDateTime(date));
    }

    /**
     * 获取当天零点
     *
     * @param date 日期
     * @return 处理后的日期
     */
    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = date2LocalDateTime(date);
        return localDateTime2Date(getStartOfDay(localDateTime));
    }

    /**
     * 获取当天零点
     *
     * @param localDateTime 日期
     * @return 处理后的时间
     */
    public static LocalDateTime getStartOfDay(LocalDateTime localDateTime) {
        return localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * 获取指定日期当天的结束时间
     *
     * @param date 指定日期
     * @return 处理完成的时间
     */
    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = date2LocalDateTime(date);
        return localDateTime2Date(getEndOfDay(localDateTime));
    }

    /**
     * 获取指定日期当天的结束时间
     *
     * @param localDateTime 指定日期
     * @return 处理完成的时间
     */
    public static LocalDateTime getEndOfDay(LocalDateTime localDateTime) {
        return getEndOfDay(localDateTime, DateTimeUnit.SECONDS);
    }

    /**
     * 获取指定日期当天的结束时间
     *
     * @param localDateTime 指定日期
     * @param dateTimeUnit  日期单位
     * @return 处理完成的时间
     */
    public static LocalDateTime getEndOfDay(LocalDateTime localDateTime, DateTimeUnit dateTimeUnit) {
        LocalDateTime nextDayStart = localDateTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime result;
        switch (dateTimeUnit) {
            case HOURS:
                result = nextDayStart.plusHours(-1);
                break;
            case MINUTES:
                result = nextDayStart.plusMinutes(-1);
                break;
            case SECONDS:
                result = nextDayStart.plusSeconds(-1);
                break;
            case MILLISECONDS:
                result = nextDayStart.plusNanos(-1000L * 1000);
                break;
            default:
                throw new RuntimeException(dateTimeUnit + " not supported");
        }
        return result;
    }

    /**
     * LocalDateTime转换为Date
     *
     * @param localDateTime 日期
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static LocalDateTime date2LocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static DateTimeFormatter ofPattern(String pattern) {
        return new DateTimeFormatterBuilder()
                .appendPattern(pattern)
                .parseDefaulting(ChronoField.YEAR_OF_ERA, 1970)
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                .toFormatter();
    }

    /**
     * 获取本周开始时间(2022-10-3 00:00:00)
     *
     * @param date 待处理的时间
     * @return 处理完成的时间
     */
    public static Date getStartOfWeek(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            dayOfWeek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayOfWeek);
        return getStartOfDay(cal.getTime());
    }

    /**
     * 获取本周结束时间(2022-10-2 59:59:59)
     *
     * @param date 待处理的时间
     * @return 处理完成的时间
     */
    public static Date getEndDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartOfWeek(date));
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getEndOfDay(weekEndSta);
    }

    /**
     * 获取指定时间所在周的所有日期字符串列表(日期元素格式"2022-10-03")
     *
     * @param date 指定日期
     * @return 当周日期字符串列表
     */
    public static List<String> getAllWeekDate(Date date) {
        Date startOfWeek = getStartOfWeek(date);
        List<Date> list = Lists.newArrayList(startOfWeek);
        for (int i = NumberConstant.ONE; i < NumberConstant.SEVEN; i++) {
            list.add(DateUtil.addDays(startOfWeek, i));
        }
        return list.stream().map(DateUtil::format).collect(Collectors.toList());
    }

    /**
     * 比较当前时间
     *
     * @return 是否
     * @Param startDate 开始时间
     * @Param endDate 结束时间
     */
    public static boolean timeComparison(Date startDate, Date endDate) {
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

        String date = sdf2.format(nowDate);
        try {
            if (startDate == null) {
                startDate = sdf.parse(date + " 00:00:00");
            }
            if (endDate == null) {
                endDate = sdf.parse(date + " 04:00:00");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (nowDate.compareTo(startDate) <= 0) {
            return false;
        }
        return nowDate.compareTo(endDate) > 0;
    }

    /**
     * 计算日期相差天数
     *
     * @return 是否
     * @Param startDate 开始时间
     * @Param endDate 结束时间
     */
    public static long differDay(Date startDate, Date endDate) {
        DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        long result = 0;
        try {
            if (startDate == null) {
                startDate = dft.parse(dft.format(new Date()));
            }
            Long starTime = startDate.getTime();
            Long endTime = endDate.getTime();
            Long num = endTime - starTime;//时间戳相差的毫秒数
            result = num / 24 / 60 / 60 / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Date dealDateFormat(String oldDateStr) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            Date date = df.parse(oldDateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取RFC3339格式时间
     *
     * @return
     */
    public static String getNowByRFC3339() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSXXX);
        return dateTime.atOffset(ZoneOffset.ofHours(8)).format(formatter);
    }

}
