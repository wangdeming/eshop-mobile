package cn.ibdsr.web.core.util;

import cn.ibdsr.core.util.DateUtil;
import cn.ibdsr.core.util.ToolUtil;

import java.util.Calendar;
import java.util.Date;

import static cn.ibdsr.core.util.DateUtil.formatDate;

/**
 * @Description 类功能和用法的说明
 * @Version V1.0
 * @CreateDate 2019/3/13 14:18
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/3/13      wangzhipeng            类说明
 */
public class DateUtils {
    /**
     * 获取yyyy/M/d H:m格式
     *
     * @return
     */
    public static String currentDateTime() {
        return formatDate(new Date(), "yyyy/M/d H:m");
    }

    /**
     * 获取yyyy/M/d格式
     *
     * @return
     */
    public static String currentDate() {
        return formatDate(new Date(), "yyyy/M/d");
    }

    /**
     * 获取yyyy-M-d格式
     */
    public static String birthdayDate(Date date) {
        if (ToolUtil.isNotEmpty(date)) {
            return formatDate(date, "yyyy-MM-dd");
        }else{
            return "请填写";
        }
    }

    /**
     * 获取yyyy-MM-dd HH:mm格式
     *
     * @param dateTime 转换时间
     * @return
     */
    public static String convertDateFormat(Date dateTime) {
        return null != dateTime ? DateUtil.format(dateTime, "yyyy-MM-dd HH:mm") : null;
    }

    /**
     * 获取未来第days天的时间
     *
     * @param days 天数
     * @return
     */
    public static Date getFetureTime(Date time, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + days);
        Date dateTime = calendar.getTime();
        return dateTime;
    }

    /**
     * 获取订单失效的剩余时间
     *
     * @param second 秒数
     * @return
     */
    public static Date getFailTime(Date time, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND)+ second);
        Date dateTime = calendar.getTime();
        return dateTime;
    }

    /**
     * 获取两个时间点相距的分秒
     *
     * @param startTime
     * @param endTime
     * @return
     */
     public static String getDateTimeDiff(Date startTime, Date endTime) {
         long nd = 1000 * 24 * 60 * 60;      // 一天的毫秒数
         long nh = 1000 * 60 * 60;           // 一小时的毫秒数
         long nm = 1000 * 60;                // 一分钟的毫秒数
         long ns = 1000;                     // 一秒钟的毫秒数

         long diff = endTime.getTime() - startTime.getTime();
         String min = diff % nd % nh / nm +"";           // 计算差多少分钟
         String sec = diff % nd % nh % nm / ns + "";

         // 输出结果
         StringBuilder sb = new StringBuilder();
         return sb.append(min).append("分").append(sec).append("秒").toString();
     }
    /**
     * 获取两个时间点相距的天时分
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static String getDateDiff(Date startTime, Date endTime) {
        long nd = 1000 * 24 * 60 * 60;      // 一天的毫秒数
        long nh = 1000 * 60 * 60;           // 一小时的毫秒数
        long nm = 1000 * 60;                // 一分钟的毫秒数

        // 获得两个时间的毫秒时间差异
        long diff = endTime.getTime() - startTime.getTime();
        if (diff < 0) {
            return "0天0小时0分钟";
        }

        String day = diff / nd +"";                     // 计算差多少天
        String hour = diff % nd / nh +"";               // 计算差多少小时
        String min = diff % nd % nh / nm +"";           // 计算差多少分钟

        // 输出结果
        StringBuilder sb = new StringBuilder();
        return sb.append(day).append("天").append(hour).append("小时").append(min).append("分钟").toString();

    }
}
