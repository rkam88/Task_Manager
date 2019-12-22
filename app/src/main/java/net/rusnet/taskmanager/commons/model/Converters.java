package net.rusnet.taskmanager.commons.model;

import androidx.room.TypeConverter;

import java.util.Date;

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
    public static Long fromDate(Date date) {
        if (date != null) return date.getTime();
        return null;
    }

    @TypeConverter
    public static Date toDate(Long date) {
        if (date != null) return new Date(date);
        return null;
    }

}