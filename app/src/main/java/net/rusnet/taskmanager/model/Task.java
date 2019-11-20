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

    @ColumnInfo(name = "type")
    private int mType;

    @ColumnInfo(name = "is_completed")
    private boolean mIsCompleted;

    public Task(long id, @NonNull String name, int type, boolean isCompleted) {
        mId = id;
        mName = name;
        mType = type;
        mIsCompleted = isCompleted;
    }

    @Ignore
    public Task(@NonNull String name, int type) {
        mName = name;
        mType = type;
        mIsCompleted = false;
    }

    @Ignore
    public Task(@NonNull String name, int type, boolean isCompleted) {
        mName = name;
        mType = type;
        mIsCompleted = isCompleted;
    }

    //getters todo: remove this comment

    public long getId() {
        return mId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public int getType() {
        return mType;
    }

    public boolean isCompleted() {
        return mIsCompleted;
    }
}
