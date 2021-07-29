package com.robot.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * 时间处理工具类。
 *
 * @Author BaoXu Zhang
 * @Date 2021/7/29
 */
public class DateUtils {

    /**
     * 时间转毫秒值。
     *
     * @param time 时间
     * @param format 自定义时间格式，例如yyyy-MM-dd HH:mm
     * @return 毫秒值
     */
    public static Long timeToMilli(String time, String format) {
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(format))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    /**
     * 毫秒值转时间。
     *
     * @param milliseconds 时间毫秒值
     * @param format 自定义时间格式，例如yyyy-MM-dd HH:mm
     * @return 时间
     */
    public static String milliTotTime(long milliseconds, String format) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 获取指定时间在那一天的初始时间。
     *
     * @param time 指定时间
     * @param pattern 时间格式，例如 yyyy-MM-dd HH:mm:ss
     * @return 初始时间
     */
    public static String getFirstTimeOfDay(String time, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(time, dateTimeFormatter);
        return dateTimeFormatter.format(localDateTime.withHour(0).withMinute(0).withSecond(0));
    }

    /**
     * 获取两个时间的时间差，单位：分钟。
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param format 自定义时间格式，例如yyyy-MM-dd HH:mm
     * @return 分钟时间差
     */
    public static Long getMinutesDiff(String beginTime, String endTime, String format) {
        LocalDateTime begin = LocalDateTime.parse(beginTime, DateTimeFormatter.ofPattern(format));
        LocalDateTime end = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern(format));
        return Math.abs(Duration.between(begin, end).toMinutes());
    }

    /**
     * 获取指定时间前后，偏移指定天的时间。
     *
     * @param currentTime 指定时间
     * @param num         指定几天
     * @param format      时间格式，例如yyyy-MM-dd HH:mm:ss
     * @return 偏移后的时间
     */
    public static String getOffsetHoursTime(String currentTime, Integer num, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime time = LocalDateTime.parse(currentTime, dateTimeFormatter);
        return time.plusDays(num).format(dateTimeFormatter);
    }

    public static void main(String[] args) {

        // 获取指定时间在这个月的第几天
        int day = MonthDay.from(LocalDateTime.parse("2020-01-25 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).getDayOfMonth();

        // 获取指定时间是第几个月
        int month = YearMonth.from(LocalDateTime.parse("2020-12-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).getMonthValue();

        // 获取指定时间是星期几
        int week = DayOfWeek.from(LocalDateTime.parse("2021-03-29 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).getValue();

        Long startTime = timeToMilli("2021-07-29 10:00:00", "yyyy-MM-dd HH:mm:ss");

        System.out.println(startTime);

    }

}
