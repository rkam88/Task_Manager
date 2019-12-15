package net.rusnet.taskmanager.commons.model;

import androidx.annotation.NonNull;

public enum TaskType {

    ANY("ANY"),
    INBOX("INBOX"),
    ACTIVE("ACTIVE"),
    POSTPONED("POSTPONED");

    private final String mType;

    TaskType(@NonNull String type) {
        mType = type;
    }

    @NonNull
    public String getTypeAsString() {
        return mType;
    }

    @NonNull
    static TaskType getTaskTypeByString(@NonNull String type) {
        if (type.equals(INBOX.getTypeAsString())) return INBOX;
        if (type.equals(ACTIVE.getTypeAsString())) return ACTIVE;
        if (type.equals(POSTPONED.getTypeAsString())) return POSTPONED;
        throw new IllegalArgumentException();
    }
}
