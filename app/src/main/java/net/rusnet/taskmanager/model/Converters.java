package net.rusnet.taskmanager.model;

import androidx.room.TypeConverter;

import java.util.Calendar;

public final class Converters {

    private static final int CALENDAR_AS_LONG_NULL_VALUE = -1;

    @TypeConverter
    public static String fromTaskType(TaskType taskType) {
        return taskType.getTypeAsString();
    }

    @TypeConverter
    public static TaskType toTaskType(String taskTypeAsString) {
        return TaskType.getTaskTypeByString(taskTypeAsString);
    }

    @TypeConverter
    public static String fromDateType(DateType dateType) {
        return dateType.getTypeAsString();
    }

    @TypeConverter
    public static DateType toDateType(String dateTypeAsString) {
        return DateType.getDateTypeByString(dateTypeAsString);
    }

    @TypeConverter
    public static String fromDate(Date date) {
        if (date != null) return date.toString();
        return null;
    }

    @TypeConverter
    public static Date toDate(String date) {
        if (date != null) return Date.parseString(date);
        return null;
    }

    @TypeConverter
    public static long fromCalendar(Calendar calendar) {
        if (calendar == null) return CALENDAR_AS_LONG_NULL_VALUE;
        return calendar.getTime().getTime();
    }

    @TypeConverter
    public static Calendar toCalendar(long calendarAsLong) {
        if (calendarAsLong == CALENDAR_AS_LONG_NULL_VALUE) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendarAsLong);
        return calendar;
    }

}