package com.coral.test.spring.natives.core.utils;


import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.LinkedList;
import java.util.List;

/**
 * @author huss
 * @version 1.0
 * @className DateTimeUtil
 * @description 时间转换
 * @date 2021/3/29 10:50
 */
public class DateTimeUtil {
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern(DatePattern.PATTERN_DATETIME);
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(DatePattern.PATTERN_DATE);
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern(DatePattern.PATTERN_TIME);

    /**
     * 日期时间格式化
     *
     * @param temporal
     *            时间
     * @return 格式化后的时间
     */
    public static String  formatDateTime(TemporalAccessor temporal) {
        return DATETIME_FORMAT.format(temporal);
    }

    /**
     * 日期时间格式化
     *
     * @param temporal
     *            时间
     * @return 格式化后的时间
     */
    public static String formatDate(TemporalAccessor temporal) {
        return DATE_FORMAT.format(temporal);
    }

    /**
     * 时间格式化
     *
     * @param temporal
     *            时间
     * @return 格式化后的时间
     */
    public static String formatTime(TemporalAccessor temporal) {
        return TIME_FORMAT.format(temporal);
    }

    /**
     * 日期格式化
     *
     * @param temporal
     *            时间
     * @param pattern
     *            表达式
     * @return 格式化后的时间
     */
    public static String format(TemporalAccessor temporal, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(temporal);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr
     *            时间字符串
     * @param pattern
     *            表达式
     * @return 时间
     */
    public static TemporalAccessor parse(String dateStr, String pattern) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
        return format.parse(dateStr);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr
     *            时间字符串
     * @param formatter
     *            DateTimeFormatter
     * @return 时间
     */
    public static TemporalAccessor parse(String dateStr, DateTimeFormatter formatter) {
        return formatter.parse(dateStr);
    }

    /**
     * 时间转 Instant
     *
     * @param dateTime
     *            时间
     * @return Instant
     */
    public static Instant toInstant(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * Instant 转 时间
     *
     * @param instant
     *            Instant
     * @return Instant
     */
    public static LocalDateTime toDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * excel日期戳转日期 日期戳将1900-01-01那一天记为日期戳1
     *
     * @param excel
     * @return
     */
    public static LocalDate excel2LocalDate(double excel) {
        int days = (int)excel;
        return LocalDate.of(1900, 1, 1).plusDays(days - 2);
    }

    /**
     * excel日期戳转日期 日期戳将1900-01-01那一天记为日期戳1
     *
     * @param excel
     * @return
     */
    public static LocalTime excel2LocalTime(double excel) {
        long second = Math.round(excel % 1 * 24 * 60 * 60);
        return LocalTime.of(0, 0, 0).plusSeconds(second);
    }

    /**
     * excel日期小数转为日期
     *
     * @param excel
     * @return a
     * @description excel日期小数转为日期
     * @author huss
     * @date 2020/5/28 0:0
     */
    public static LocalDateTime excel2LocalDateTime(double excel) {
        return LocalDateTime.of(excel2LocalDate(excel), excel2LocalTime(excel));
    }

    /**
     * 将时间转换为excel中的日期小数值表示
     *
     * @param localDateTime
     * @return
     */
    public static double dateTime2Excel(LocalDateTime localDateTime) {
        long start = LocalDate.of(1900, 1, 1).toEpochDay();
        LocalDate localDate = localDateTime.toLocalDate();
        long days = localDate.toEpochDay() - start + 2;
        LocalTime localTime = localDateTime.toLocalTime();
        double timeOfDay = localTime.toSecondOfDay() * 1.00 / 24 / 60 / 60;
        return Double.valueOf(days + timeOfDay);
    }

    /**
     * 开始日期和结束日期之间所有的日期列表
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<String> inDays(LocalDate startDate, LocalDate endDate, String pattern) {

        pattern = StringUtils.isEmpty(pattern) ? DatePattern.YYYY_MM_DD_EN : pattern;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);

        long days = endDate.toEpochDay() - startDate.toEpochDay();
        List<String> dates = new LinkedList<>();
        for (int i = 0; i < days + 1; i++) {
            dates.add(dateTimeFormatter.format(startDate.plusDays(i)));
        }
        return dates;
    }

    /**
     * 开始日期和结束日期之间所有的日期列表
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<LocalDate> inDays(LocalDate startDate, LocalDate endDate) {
        long days = endDate.toEpochDay() - startDate.toEpochDay();
        List<LocalDate> dates = new LinkedList<>();
        for (int i = 0; i < days + 1; i++) {
            dates.add(startDate.plusDays(i));
        }
        return dates;
    }
}
