package net.rusnet.taskmanager.tasksdisplay;

import androidx.annotation.StringRes;

import net.rusnet.taskmanager.R;

public enum TaskViewType {

    INBOX(R.string.inbox),
    ACTIVE(R.string.active),
    POSTPONED(R.string.postponed),
    COMPLETED(R.string.completed);
//todo: add Today
//todo:	add This week

    @StringRes
    private final int mTitle;

    TaskViewType(@StringRes int title) {
        mTitle = title;
    }

    public int getTitle() {
        return mTitle;
    }
}
