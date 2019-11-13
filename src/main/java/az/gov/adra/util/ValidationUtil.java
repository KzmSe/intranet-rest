package az.gov.adra.util;

import java.time.LocalTime;

public class ValidationUtil {

    private static final LocalTime STARTTIME_BEGIN = LocalTime.parse("09:00");
    private static final LocalTime STARTTIME_END = LocalTime.parse("17:30");
    private static final LocalTime ENDTIME_BEGIN = LocalTime.parse("09:30");
    private static final LocalTime ENDTIME_END = LocalTime.parse("18:00");
    private static final LocalTime START_OF_LUNCH = LocalTime.parse("13:00");
    private static final LocalTime END_OF_LUNCH = LocalTime.parse("14:00");

    public static boolean isNullOrEmpty(String... fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNull(Object... fields) {
        for (Object field : fields) {
            if (field == null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStartTimeValid(LocalTime startTime) {
        boolean result = true;

        if (startTime.isBefore(STARTTIME_BEGIN) || startTime.isAfter(STARTTIME_END)) {
            result = false;
        }

        if (startTime.isAfter(START_OF_LUNCH) && startTime.isBefore(END_OF_LUNCH)) {
            result = false;
        }

        return result;
    }

    public static boolean isEndTimeValid(LocalTime endTime) {
        boolean result = true;

        if (endTime.isBefore(ENDTIME_BEGIN) || endTime.isAfter(ENDTIME_END)) {
            result = false;
        }

        return result;
    }

}