package net.rusnet.taskmanager.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table")
public class Task {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    private long mId;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @NonNull
    @ColumnInfo(name = "type")
    private String mType;

    @ColumnInfo(name = "is_completed")
    private boolean mIsCompleted;

    public Task(long id, @NonNull String name, @NonNull String type, boolean isCompleted) {
        mId = id;
        mName = name;
        mType = type;
        mIsCompleted = isCompleted;
    }

    @Ignore
    public Task(@NonNull String name, @NonNull String type) {
        mName = name;
        mType = type;
        mIsCompleted = false;
    }

    @Ignore
    public Task(@NonNull String name, @NonNull String type, boolean isCompleted) {
        mName = name;
        mType = type;
        mIsCompleted = isCompleted;
    }

    public long getId() {
        return mId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getType() {
        return mType;
    }

    public boolean isCompleted() {
        return mIsCompleted;
    }

    public void setName(@NonNull String name) {
        mName = name;
    }

    public void setType(@NonNull String type) {
        mType = type;
    }

    public void setCompleted(boolean completed) {
        mIsCompleted = completed;
    }
}
