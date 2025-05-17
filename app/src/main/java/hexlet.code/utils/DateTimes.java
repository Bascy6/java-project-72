package hexlet.code.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimes {

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }

}
