package com.open.ai.eros.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class DateUtils {
    public final static String FORMAT_YYYY_MM_DD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
    public final static String HHMMSS = "HH:mm:ss";
    public final static String FORMATYYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public final static String FORMATYYYYMMDDHH = "yyyyMMddHH";
    public final static String FORMATYYYYMMDD = "yyyyMMdd";
    public final static String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public final static String FORMAT_YYYY_MM = "yyyy-MM";
    public final static String FORMAT_YYYY_MM_DD_HHMM = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_YYYY_MM_DDHH = "yyyy-MM-dd_HH";
    public final static String FORMAT_YYYY = "yyyy";
    public final static String FORMAT_YYYY_MM_DD_HH = "yyyy-MM-dd HH";


    public static void main(String[] args) {


        long timestampMillis = System.currentTimeMillis(); // 替换为您的毫秒时间戳

        Instant instant = Instant.ofEpochSecond(timestampMillis / 1000);
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();

        System.out.println(localDateTime);
        System.out.println("LocalDateTime: " + localDateTime);
        System.out.println(timestampMillis+" "+convertLocalDateTimeToTimestamp(localDateTime));

        System.out.println(DateUtils.getDescDay(10));
        System.out.println(DateUtils.getExpireTimeDesc(System.currentTimeMillis(),System.currentTimeMillis()+100000));
    }

    /**
     * 将小时转化为 描述
     *
     * @param restSec 小时
     * @return
     */
    public static String getDescDay(long restSec) {
        //计算期限
        StringBuilder bdr = new StringBuilder();
        if (restSec >= 24) {
            long restDay = restSec / 24;
            int dressUpPermanentTime = 10000;
            if (restDay >= dressUpPermanentTime) {
                // 永久
                return "永久";
            }
            bdr.append(restDay);
            bdr.append("天");
            restSec %= 24;
        }
        if (restSec >= 1) {
            bdr.append(restSec);
            bdr.append("小时");
        }
        return bdr.toString();
    }



    /**
     * 获取过期时间的描述
     *
     * @param now 时间戳
     * @param expireTime 时间戳
     * @return
     */
    public static String getExpireTimeDesc(long now, long expireTime) {
        //已过期
        if (expireTime < now) {
            return "已过期";
        }
        //计算期限
        long restSec = (expireTime - now) / 1000L;
        StringBuilder bdr = new StringBuilder();
        if (restSec >= 86400) {
            long restDay = restSec / 86400;
            int dressUpPermanentTime = 100000;
            if (restDay >= dressUpPermanentTime) {
                // 永久
                return "永久";
            }
            bdr.append(restDay);
            bdr.append("天");
            restSec %= 86400;
        }
        if (restSec >= 3600) {
            bdr.append(restSec / 3600);
            bdr.append("小时");
            restSec %= 3600;
        }
        if (restSec >= 60) {
            bdr.append(restSec / 60);
            bdr.append("分钟");
            restSec %= 60;
        }

        if (bdr.length() == 0) {
            bdr.append("1分钟");
        }
        bdr.append("后过期");
        return bdr.toString();
    }




    /**
     * 小时转化为毫秒
     *
     * @param hour
     * @return
     */
    public static Long hourToTimeStamp(Long hour) {
        return hour * 1000 * 60 * 60;
    }


    /**
     * 将时间戳转化为 LocalDateTime
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime convertTimestampToLocalDateTime(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp / 1000);
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        return instant.atZone(zoneId).toLocalDateTime();
    }



    /**
     * LocalDateTime 转换为毫秒级时间戳
     *
     * @param time
     * @return
     */
    public static Long convertLocalDateTimeToTimestamp(LocalDateTime time) {
        if (time == null) {
            return 0L;
        }
        // 获取当前时区或指定时区
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");

        // 将 LocalDateTime 转换为 Instant
        Instant instant = time.atZone(zoneId).toInstant();
        // 获取毫秒级时间戳
        return instant.toEpochMilli();
    }


    /**
     * 为时间添加秒数偏移量
     *
     * @param date    时间
     * @param seconds 偏移量，小于 0 表示往过去偏移，大于 0 表示往未来偏移
     * @return 计算后的新时间
     */
    public static Date plusSeconds(Date date, int seconds) {
        Calendar calendar = toCalendar(date);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    /**
     * 获取当前时间到凌晨0的时间，单位秒
     *
     * @return
     */
    public static long getSecondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.MIDNIGHT);
        Duration duration = Duration.between(now, midnight);
        return duration.getSeconds();
    }


    /**
     * 为时间添加天数偏移量
     *
     * @param date 时间
     * @param days 偏移量，小于 0 表示往过去偏移，大于 0 表示往未来偏移
     * @return 计算后的新时间
     */
    public static Date plusDays(Date date, int days) {
        Calendar calendar = toCalendar(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    public static String plusDaysThenFormat(Date date, int days, String format) {
        return formatDate(plusDays(date, days), format);
    }

    /**
     * 获取一天的开始
     *
     * @param date 时间
     * @return 一天的零点
     */
    public static Date startOfDay(Date date) {
        Calendar calendar = toCalendar(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        return calendar.getTime();
    }

    /**
     * 获取一天的结束
     *
     * @param date 时间
     * @return 一天的结束
     */
    public static Date endOfDay(Date date) {
        Calendar calendar = toCalendar(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                23, 59, 59);
        return calendar.getTime();
    }


    public static Date millisToDate(long millis) {
        return new Date(millis);
    }

    public static String formatDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static String formatTimeStamp(long timeStamp, String format) {
        try {
            if (timeStamp > 0) {
                Date date = millisToDate(timeStamp);
                return formatDate(date, format);
            }
        } catch (Exception e) {
            log.error("formatTimeStamp error`timestamp={}", timeStamp, e);
            return "";
        }
        return "";
    }

    public static Date dateOf(String millis, String format) {
        try {
            if (!StringUtils.isEmpty(millis)) {
                SimpleDateFormat sdfTime = new SimpleDateFormat(format);
                return sdfTime.parse(millis);
            }
        } catch (Exception e) {
            log.error("dateOf error`millis={}`format={}", millis, format, e);
            return null;
        }
        return null;
    }


    public static Long formatStrToMillis(String millis) {
        try {
            if (!StringUtils.isEmpty(millis)) {
                SimpleDateFormat sdfTime = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HHMMSS);
                Date date = sdfTime.parse(millis);
                return date.getTime();
            }
        } catch (Exception e) {
            log.error("formatStrToMillis error`millis={}`e={}", millis, e);
            return null;
        }
        return null;
    }


    /**
     * 根据   时间戳  将时间转化为   X天X小时X分钟X秒
     *
     * @param time 时间戳  单位 毫秒
     * @return
     */
    public static String getLiveTime(int time) {
        String result = "";
        time = (time / 1000); //  化为 秒

        int day = time / (24 * 60 * 60);// 天
        if (day > 0) {
            time = time - day * 24 * 60 * 60;
            result += day + "天";
        }
        int hour = time / (60 * 60); // 小时
        if (hour > 0) {
            time = time - hour * 60 * 60;
            result += hour + "小时";
        }
        int fengZhong = time / (60);// 分钟
        if (fengZhong > 0) {
            time = time - fengZhong * 60;
            result += fengZhong + "分钟";
        }
        int miao = time; // 秒
        if (miao > 0) {
            result += miao + "秒";
        }
        return result;
    }

    public static String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD);
        return sdf.format(cal.getTime());
    }

    public static String getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        int lastDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD);
        return sdf.format(cal.getTime());
    }


    /**
     * 获取日期范围内的所有日期，
     * 例如startDate:2020-11-24, endDate:2020-11-25, 返回["2020-11-24", "2020-11-25"]
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Set<String> getBetweenDates(Date startDate, Date endDate, String pattern) {
        Set<String> dates = new LinkedHashSet<>();
        if (startDate == null && endDate == null) {
            return Collections.EMPTY_SET;
        }

        if (startDate == null) {
            startDate = new Date();
        }

        if (endDate == null) {
            endDate = new Date();
        }

        Date tmpstartDate = startDate;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        while (tmpstartDate.before(endDate)) {
            dates.add(sdf.format(tmpstartDate.getTime()));
            tmpstartDate = getDayAfter(tmpstartDate, 1);
        }

        dates.add(sdf.format(endDate.getTime()));
        return dates;
    }

    public static Date getDayAfter(Date date, Integer day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(5, calendar.get(5) + day);
        return calendar.getTime();
    }

    public static Date getDayBefore(Date date, Integer day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 减去天数来得到之前的日期
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return calendar.getTime();
    }


    /**
     * 判断时间HH:mm:ss 是否在时间范围内
     *
     * @param current
     * @param begin
     * @param end
     * @return
     */
    public static boolean compareTime(String current, String begin, String end) throws ParseException {
        boolean result = false;
        //将时间字符串转化成时间
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

        //转换成时间格式
        Date beginTime = df.parse(begin);
        Date endTime = df.parse(end);
        //取出当前时间的时分秒编码再解码
        Date currentTime = df.parse(current);
        //通过日历形式开始比较
        Calendar beginTimeCld = toCalendar(beginTime);

        Calendar endTimeCld = toCalendar(endTime);

        Calendar currentCld = toCalendar(currentTime);

        //当前时间晚于开始时间，早于结束时间则表明在指定的时间段内
        if (currentCld.after(beginTimeCld) && currentCld.before(endTimeCld)) {
            result = true;
        }
        return result;
    }

    public static String covertTime(String blockTime) {
        try {
            if (StringUtils.isEmpty(blockTime)) {
                return "";
            }
            Long time = Long.parseLong(blockTime);
            if (time <= 0) {
                return "永久封禁";
            }
            if (time < 3600) {
                //目前系统最低封禁时间单位为秒  如果time小于1小时 就默认单位是天
                return time + "天";
            }
            //小时
            int hours = (int) (time / 60 / 60);
            int day = -1;
            if (hours >= 24) {
                day = hours / 24;
                hours = hours - day * 24;
            }
            if (hours == 0) {
                return day + "天";
            }
            if (day > 0) {
                return day + "天" + hours + "小时";
            }
            return hours + "小时";
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 根据出生日期计算年龄
     *
     * @param birthday 格式：yyyy-MM-dd
     * @return
     */
    public static Integer getAgeByBirthday(Long birthday) {
        Integer age = null;
        try {
            if (Objects.isNull(birthday)) {
                return age;
            }
            Calendar now = toCalendar(new Date());
            Calendar birth = toCalendar(millisToDate(birthday));
            if (birth.after(now)) {
                return age;
            }
            int year = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            if (now.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
                year -= 1;
            }
            age = year;
        } catch (Exception e) {
            log.error("getAgeByBirthday error`birthday={}`e={}", birthday, e);
        }
        return age;
    }

    /**
     * 获取索引
     */
    public static String[] getIndex(Date firstDate, Date secondDate) {
        final Calendar startDate = Calendar.getInstance(), endDate = Calendar.getInstance();
        if (firstDate.before(secondDate)) {
            startDate.setTime(firstDate);
            endDate.setTime(secondDate);
        } else {
            startDate.setTime(secondDate);
            endDate.setTime(firstDate);
        }
        List<String> months = new ArrayList<>();
        while (monthLessOrEqual(startDate, endDate)) {
            months.add(String.format("%04d-%02d", startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH) + 1));
            startDate.add(Calendar.MONTH, 1);
        }
        return months.toArray(new String[0]);
    }

    /**
     * 比较两个日期的年份+月份，第一个日期是否小于或等于第二个日期
     */
    private static boolean monthLessOrEqual(Calendar first, Calendar second) {
        final int firstYear = first.get(Calendar.YEAR), secondYear = second.get(Calendar.YEAR),
                firstMonth = first.get(Calendar.MONTH), secondMonth = second.get(Calendar.MONTH);
        return firstYear == secondYear ? firstMonth <= secondMonth : firstYear < secondYear;
    }


    private static Calendar toCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }


    public static long convertToTimestamp(String time) {
        // 获取当前日期时间
        LocalDateTime now = LocalDateTime.now();

        // 解析输入的时间
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        // 设置输入的时间
        LocalDateTime targetTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);

        // 如果目标时间在当前时间之前，则表示是第二天的时间
        if (targetTime.isBefore(now)) {
            targetTime = targetTime.plusDays(1);
        }
        // 转换为时间戳并返回
        return targetTime.toEpochSecond(ZoneOffset.ofHours(8)) * 1000;
    }


    public static String getTodayDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return today.format(formatter);
    }
}
