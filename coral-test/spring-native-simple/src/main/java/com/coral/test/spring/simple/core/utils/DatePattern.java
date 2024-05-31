package com.coral.test.spring.simple.core.utils;

import java.time.ZoneOffset;

/**
 * @author huss
 * @version 1.0
 * @className DatePattern
 * @description 日期格式
 * @date 2021/3/29 10:51
 */
public class DatePattern {

    /** 日期格式(yyyy-MM-dd) */
    public static final String YYYY_MM_DD_EN = "yyyy-MM-dd";

    /** 日期格式(HH:mm:ss) */
    public static final String HH_MM_SS_EN = "HH:mm:ss";

    /** 日期格式(MM.dd) */
    public static final String MM_DD_EN = "MM.dd";
    /** HH:mm */
    public static final String HH_MM = "HH:mm";

    /** 日期格式(yyyyMMdd) */
    public static final String YYYYMMDD_EN = "yyyyMMdd";

    /** 日期格式(yyMMdd) */
    public static final String YYMMDD_EN = "yyMMdd";

    /** 日期格式(MMdd) */
    public static final String MMDD_EN = "MMdd";

    /** 日期格式(yyyy-MM) */
    public static final String YYYY_MM_EN = "yyyy-MM";

    /** 日期格式(yyyyMM) */
    public static final String YYYYMM_EN = "yyyyMM";

    /** 日期格式(yyyy-MM-dd HH:mm:ss) */
    public static final String YYYY_MM_DD_HH_MM_SS_EN = "yyyy-MM-dd HH:mm:ss";

    /** 日期格式(yyyy-MM-dd HH:mm:ss.SSS) */
    public static final String YYYY_MM_DD_HH_MM_SS_SSS_EN = "yyyy-MM-dd HH:mm:ss.SSS";

    /** 日期格式(yyyyMMddHHmmss) */
    public static final String YYYYMMDDHHMMSS_EN = "yyyyMMddHHmmss";

    /** 日期格式(yyyy年MM月dd日) */
    public static final String YYYY_MM_DD_CN = "yyyy年MM月dd日";

    /** 日期格式(yyyy年MM月dd日HH时mm分ss秒) */
    public static final String YYYY_MM_DD_HH_MM_SS_CN = "yyyy年MM月dd日HH时mm分ss秒";

    /** 日期格式(yyyy年MM月dd日HH时mm分) */
    public static final String YYYY_MM_DD_HH_MM_CN = "yyyy年MM月dd日HH时mm分";

    /** 日期格式(yyyy-MM-dd HH:mm) */
    public static final String YYYY_MM_DD_HH_MM_EN = "yyyy-MM-dd HH:mm";
    /** 日期格式(MM/dd/yy) **/
    public static final String MM_DD_YY_SLASH = "MM/dd/yy";

    /** 日期格式(yyyy/MM/dd) **/
    public static final String YYYY_MM_DD_SLASH = "yyyy/MM/dd";

    /**
     * 一天开始时间
     */
    public static final String TIME_START_STR = " 00:00:00";

    /**
     * 一天结束时间
     */
    public static final String TIME_END_STR = " 23:59:59";

    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_MONTH = "yyyy-MM";

    public static final String PATTERN_TIME = "HH:mm:ss";

    public static final ZoneOffset DEFAULT_ZONE_OFF_SET = ZoneOffset.of("+8");

}
