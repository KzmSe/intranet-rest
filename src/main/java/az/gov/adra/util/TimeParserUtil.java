package az.gov.adra.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeParserUtil {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm");

    public static LocalDateTime parseStringToLocalDateTime(String time) {
        return LocalDateTime.parse(time);
    }

}
