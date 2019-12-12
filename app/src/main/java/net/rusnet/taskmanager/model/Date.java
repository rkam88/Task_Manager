package net.rusnet.taskmanager.model;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class Date {

    private static final int CALENDAR_MONTH_OFFSET = 1;
    private static final String DOT = ".";
    private static final String DELIMITER = "\\.";

    private static final int REQUIRED_LENGTH = 3;
    private static final int YEAR_POSITION = 0;
    private static final int MONTH_POSITION = 1;
    private static final int DAY_POSITION = 2;
    private static final int MIN_DAY = 1;
    private static final int MAX_DAY = 31;
    private static final int MIN_MONTH = Calendar.JANUARY + CALENDAR_MONTH_OFFSET;
    private static final int MAX_MONTH = Calendar.DECEMBER + CALENDAR_MONTH_OFFSET;
    private static final int MIN_YEAR = 1970;

    public static final Date START_DATE = new Date(false, MIN_YEAR, MIN_MONTH, MIN_DAY);

    private int mYear;
    private int mMonth;
    private int mDay;

    public Date(boolean usesCalendarFormat, int year, int month, int day) {
        if (usesCalendarFormat) {
            mYear = year;
            mMonth = month + CALENDAR_MONTH_OFFSET;
            mDay = day;
        } else {
            mYear = year;
            mMonth = month;
            mDay = day;
        }
    }

    public Date(Calendar calendar) {
        this(true,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    @NonNull
    @Override
    public String toString() {
        return mYear + DOT + mMonth + DOT + mDay;
    }

    @NonNull
    public Calendar toCalendar() {
        Calendar c = Calendar.getInstance();
        c.set(mYear, mMonth - CALENDAR_MONTH_OFFSET, mDay);
        return c;
    }

    @NonNull
    public static Date parseString(@NonNull String dateAsString) {
        String[] s = dateAsString.split(DELIMITER);

        if (s.length != REQUIRED_LENGTH) throw new IllegalArgumentException(dateAsString);

        int year = Integer.parseInt(s[YEAR_POSITION]);
        int month = Integer.parseInt(s[MONTH_POSITION]);
        int day = Integer.parseInt(s[DAY_POSITION]);

        if (month < MIN_MONTH ||
                month > MAX_MONTH ||
                day < MIN_DAY ||
                day > MAX_DAY ||
                year < MIN_YEAR)
            throw new IllegalArgumentException(dateAsString);

        return new Date(false, year, month, day);

    }

    public static Date today() {
        return new Date(Calendar.getInstance());
    }

    public static Date aWeekFromToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        return new Date(calendar);
    }
}
