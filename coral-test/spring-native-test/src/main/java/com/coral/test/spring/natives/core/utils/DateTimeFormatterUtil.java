package com.coral.test.spring.natives.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 时间转换
 *
 * @author huss
 * @date 2023/12/28 17:25
 * @packageName com.coral.test.spring.graphql.util
 * @className DateTimeFormatterUtil
 */
public class DateTimeFormatterUtil {
    private static final List<String> FORMAT_LIST = Arrays.asList(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "EEE MMM dd HH:mm:ss zzz yyyy",
            "yyyy-MM-dd",
            "HH:mm:ss"
    );

    private static final SimpleDateFormat DEF_DATE_FORMAT = new SimpleDateFormat(FORMAT_LIST.get(0));
    private static final DateTimeFormatter DEF_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_LIST.get(0));
    private static final DateTimeFormatter DEF_DATE_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_LIST.get(6));
    private static final DateTimeFormatter DEF_TIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_LIST.get(7));

    public static String format(LocalDateTime date) {
        if (Objects.isNull(date)) {
            return "";
        }
        return DEF_DATE_TIME_FORMATTER.format(date);
    }

    public static String format(LocalDate date) {
        return format(LocalDateTime.of(date, LocalTime.MIN));
    }

    public static String format(Date date) {
        if (Objects.isNull(date)) {
            return "";
        }
        return DEF_DATE_FORMAT.format(date);
    }

    public static String formatDate(LocalDate date) {
        if (Objects.isNull(date)) {
            return "";
        }
        return DEF_DATE_FORMATTER.format(date);
    }

    public static String formatTime(LocalTime time) {
        if (Objects.isNull(time)) {
            return "";
        }
        return DEF_TIME_FORMATTER.format(time);
    }


    /**
     * 解析时间 带日期的
     *
     * @param source 时间字符串
     * @return Instant
     * @throws DateTimeException
     */
    public static Optional<Instant> parseDateTime(String source) throws DateTimeException {
        String value = source.trim();
        if (StringUtils.isBlank(value)) {
            return Optional.empty();
        }
        Instant instant = null;

        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_LIST.get(0));
            if (source.matches("^\\d{4}-\\d{1,2}$")) {
                source += "-01 00:00:00";
            } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
                source += " 00:00:00";
            } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}$")) {
                source += ":00:00";
            } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
                source += ":00";
            } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
                // pass
            } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}.*T.*\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
                dateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_LIST.get(1));
            } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}.*T.*\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}.*Z.*$")) {
                dateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_LIST.get(2));
            } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}$")) {
                dateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_LIST.get(3));
            } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}.*T.*\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}$")) {
                dateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_LIST.get(4));
            } else {
                dateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_LIST.get(5), Locale.US);
            }
            instant = parseDate(source, dateTimeFormatter);
        } catch (Exception e) {
            throw new DateTimeException("Invalid Date value '" + source + "'");
        }
        return Optional.of(instant);
    }


    /**
     * 格式化日期
     *
     * @param dateStr   String 字符型日期
     * @param formatter formatter格式
     * @return Date 日期
     */
    private static Instant parseDate(String dateStr, DateTimeFormatter formatter) {
        return LocalDateTime.from(formatter.parse(dateStr)).atZone(ZoneId.systemDefault()).toInstant();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(parseDateTime("2023-12"));
        System.out.println(parseDateTime("2023-12-28"));
        System.out.println(parseDateTime("2023-12-28 17:41"));
        System.out.println(parseDateTime("2023-12-28 17:41:30"));
        System.out.println(parseDateTime("2023-12-28T17:41:30"));
        System.out.println(parseDateTime("2023-12-28T17:41:30.999Z"));
        System.out.println(parseDateTime("2023-12-28T17:41:30.999"));
        System.out.println(parseDateTime(new Date().toString()));
    }
}
