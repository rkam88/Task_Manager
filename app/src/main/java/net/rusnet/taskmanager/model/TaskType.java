package net.rusnet.taskmanager.model;

import androidx.annotation.Nullable;

public enum TaskType {

    ANY(null),
    INBOX("INBOX"),
    ACTIVE("ACTIVE"),
    POSTPONED("POSTPONED");

    private final String mType;

    TaskType(@Nullable String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }
}
