package net.rusnet.taskmanager.model;

import androidx.annotation.NonNull;

public enum TaskType {

    INBOX("INBOX"),
    ACTIVE("ACTIVE"),
    POSTPONED("POSTPONED");

    private final String mType;

    TaskType(@NonNull String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }
}
