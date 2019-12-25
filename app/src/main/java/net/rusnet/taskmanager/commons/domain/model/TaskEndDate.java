package net.rusnet.taskmanager.commons.domain.model;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskEndDate extends Date {

    private static final long DATE_IN_A_WEEK = 604800000;
    private static final int DATE_LAST_HOUR = 23;
    private static final int DATE_LAST_MINUTE = 59;
    private static final int DATE_LAST_SECOND = 59;
    private static final int DATE_LAST_MILLISECOND = 999;

    public TaskEndDate(long dateAsLong) {
        super(getDateAsLongAtEndOfDay(dateAsLong));
    }

    public TaskEndDate(@NonNull Date date) {
        super(getDateAsLongAtEndOfDay(date.getTime()));
    }

    public TaskEndDate(int year, int month, int dayOfMonth) {
        super(getDateAsLongAtEndOfDay(year, month, dayOfMonth));
    }

    @NonNull
    public String getDateAsString() {
        return new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(getTime());
    }

    private static long getDateAsLongAtEndOfDay(long dateAsLong) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateAsLong);
        calendar.set(Calendar.HOUR_OF_DAY, DATE_LAST_HOUR);
        calendar.set(Calendar.MINUTE, DATE_LAST_MINUTE);
        calendar.set(Calendar.SECOND, DATE_LAST_SECOND);
        calendar.set(Calendar.MILLISECOND, DATE_LAST_MILLISECOND);
        return calendar.getTimeInMillis();
    }

    private static long getDateAsLongAtEndOfDay(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        return getDateAsLongAtEndOfDay(calendar.getTimeInMillis());
    }

    @NonNull
    public static Date getDateForToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, DATE_LAST_HOUR);
        calendar.set(Calendar.MINUTE, DATE_LAST_MINUTE);
        calendar.set(Calendar.SECOND, DATE_LAST_SECOND);
        calendar.set(Calendar.MILLISECOND, DATE_LAST_MILLISECOND);
        return new Date(calendar.getTimeInMillis());
    }

    @NonNull
    public static Date getDateInAWeek() {
        return new Date(getDateForToday().getTime() + DATE_IN_A_WEEK);
    }

}
