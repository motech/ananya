package org.motechproject.ananya.utils;


import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

public class DateUtils {

    public static String DATE_FORMAT = "dd-MM-yyyy";
    public static String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static String TIME_FORMAT = "HH:mm:ss";

    public static DateTime parseDate(String date) {
        return StringUtils.isNotEmpty(date) ? DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(date) : null;
    }

    public static String formatDate(DateTime dateTime) {
        return dateTime.toString(DATE_FORMAT);
    }

    public static DateTime parseDateTime(String dateTime) {
        return StringUtils.isNotEmpty(dateTime) ? DateTimeFormat.forPattern(DATE_TIME_FORMAT).parseDateTime(dateTime): null;
    }

    public static String formatDateTime(DateTime dateTime) {
        return dateTime.toString(DATE_TIME_FORMAT);
    }

    public static LocalTime parseLocalTime(String time) {
        return StringUtils.isNotEmpty(time) ? LocalTime.parse(time, DateTimeFormat.forPattern(TIME_FORMAT)) :null;
    }

    public static LocalDate parseLocalDate(String date) {
        return StringUtils.isNotEmpty(date) ? LocalDate.parse(date, DateTimeFormat.forPattern(DATE_FORMAT)) :null;
    }
}
