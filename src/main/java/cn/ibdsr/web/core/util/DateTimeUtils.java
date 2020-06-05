package cn.ibdsr.web.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * Created by xujincai on 2017/6/24.
 */
public class DateTimeUtils {

    private static final String defaultZoneId = ZoneId.systemDefault().toString();

    /**
     * Date -> LocalDateTime
     *
     * @param date 时间
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        Objects.requireNonNull(date, "Date不能为空");
        return date.toInstant().atZone(ZoneId.of(defaultZoneId)).toLocalDateTime();
    }

    /**
     * LocalDate -> Date
     *
     * @param localDate 时间
     * @return Date
     */
    public static Date toDate(LocalDate localDate) {
        Objects.requireNonNull(localDate, "LocalDate不能为空");
        return Date.from(localDate.atStartOfDay(ZoneId.of(defaultZoneId)).toInstant());
    }

    /**
     * String -> LocalDate
     *
     * @param dateTimeString 时间字符串
     * @param pattern        格式
     * @return LocalDate
     */
    public static LocalDate parseLocalDate(String dateTimeString, String pattern) {
        Objects.requireNonNull(dateTimeString, "dateTimeString不能为空");
        Objects.requireNonNull(pattern, "pattern不能为空");
        return LocalDate.parse(dateTimeString, DateTimeFormatter.ofPattern(pattern));
    }

}
