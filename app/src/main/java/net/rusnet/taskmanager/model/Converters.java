package net.rusnet.taskmanager.model;

import androidx.room.TypeConverter;

public final class Converters {

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

}