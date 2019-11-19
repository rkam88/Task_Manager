package net.rusnet.taskmanager.tasksdisplay;

import androidx.annotation.StringRes;

import net.rusnet.taskmanager.R;

public enum TaskViewType {

    INBOX(R.string.inbox, 0),
    ACTIVE(R.string.active, 1),
    POSTPONED(R.string.postponed, 2),
    COMPLETED(R.string.completed, 3);
//		+Today
//		+This week

    @StringRes
    private final int mTitle;
    private final int mType;

    TaskViewType(@StringRes int title, int type) {
        mTitle = title;
        mType = type;
    }

    public int getTitle() {
        return mTitle;
    }

    public int getType() {
        return mType;
    }
}
