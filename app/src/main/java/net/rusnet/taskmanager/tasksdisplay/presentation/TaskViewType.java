package net.rusnet.taskmanager.tasksdisplay.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.rusnet.taskmanager.R;
import net.rusnet.taskmanager.commons.domain.model.TaskEndDate;
import net.rusnet.taskmanager.commons.domain.model.TaskType;

import java.util.Date;

public enum TaskViewType {

    INBOX(R.string.inbox),
    TODAY(R.string.today),
    THIS_WEEK(R.string.this_week),
    ACTIVE_ALL(R.string.all),
    POSTPONED(R.string.postponed),
    COMPLETED(R.string.completed);

    @StringRes
    private final int mTitle;

    TaskViewType(@StringRes int title) {
        mTitle = title;
    }

    public int getTitle() {
        return mTitle;
    }

    @NonNull
    public static TaskType getTaskType(@NonNull TaskViewType taskViewType) {
        switch (taskViewType) {
            case INBOX:
                return TaskType.INBOX;
            case TODAY:
            case THIS_WEEK:
            case ACTIVE_ALL:
                return TaskType.ACTIVE;
            case POSTPONED:
                return TaskType.POSTPONED;
            case COMPLETED:
                return TaskType.ANY;
        }
        throw new IllegalArgumentException(taskViewType.toString());
    }

    public static boolean getCompletedStatus(@NonNull TaskViewType taskViewType) {
        switch (taskViewType) {
            case INBOX:
            case TODAY:
            case THIS_WEEK:
            case ACTIVE_ALL:
            case POSTPONED:
                return false;
            case COMPLETED:
                return true;
        }
        throw new IllegalArgumentException(taskViewType.toString());
    }

    @Nullable
    public static Date getEndDate(@NonNull TaskViewType taskViewType) {
        switch (taskViewType) {
            case TODAY:
                return TaskEndDate.getDateForToday();
            case THIS_WEEK:
                return TaskEndDate.getDateInAWeek();
            case INBOX:
            case ACTIVE_ALL:
            case POSTPONED:
            case COMPLETED:
                return null;
        }
        throw new IllegalArgumentException(taskViewType.toString());
    }


}
