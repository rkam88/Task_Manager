package net.rusnet.taskmanager.tasksdisplay;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.rusnet.taskmanager.R;
import net.rusnet.taskmanager.model.TaskType;

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

}
