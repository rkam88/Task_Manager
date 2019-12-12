package net.rusnet.taskmanager.model;

import androidx.annotation.NonNull;

public enum DateType {

    NO_DATE("NO_DATE"),
    FIXED("FIXED"),
    DEADLINE("DEADLINE");

    private final String mType;

    DateType(@NonNull String type) {
        mType = type;
    }

    @NonNull
    public String getTypeAsString() {
        return mType;
    }

    @NonNull
    static DateType getDateTypeByString(@NonNull String type) {
        if (type.equals(NO_DATE.getTypeAsString())) return NO_DATE;
        if (type.equals(FIXED.getTypeAsString())) return FIXED;
        if (type.equals(DEADLINE.getTypeAsString())) return DEADLINE;
        throw new IllegalArgumentException();
    }

}
