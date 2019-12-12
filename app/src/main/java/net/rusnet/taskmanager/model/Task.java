package net.rusnet.taskmanager.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "task_table")
@TypeConverters({Converters.class})
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

    @NonNull
    @ColumnInfo(name = "date_type")
    private DateType mDateType;

    @Nullable
    @ColumnInfo(name = "end_date")
    private Date mEndDate;

    @ColumnInfo(name = "is_completed")
    private boolean mIsCompleted;

    public Task(long id, @NonNull String name, @NonNull String type, @NonNull DateType dateType, @Nullable Date endDate, boolean isCompleted) {
        mId = id;
        mName = name;
        mType = type;
        mDateType = dateType;
        mEndDate = endDate;
        mIsCompleted = isCompleted;
    }

    @Ignore
    public Task(@NonNull String name, @NonNull String type, @NonNull DateType dateType, @Nullable Date endDate) {
        mName = name;
        mType = type;
        mDateType = dateType;
        mEndDate = endDate;
        mIsCompleted = false;
    }

    public long getId() {
        return mId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public void setName(@NonNull String name) {
        mName = name;
    }

    @NonNull
    public String getType() {
        return mType;
    }

    public void setType(@NonNull String type) {
        mType = type;
    }

    @NonNull
    public DateType getDateType() {
        return mDateType;
    }

    public void setDateType(@NonNull DateType dateType) {
        mDateType = dateType;
    }

    @Nullable
    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(@Nullable Date endDate) {
        mEndDate = endDate;
    }

    public boolean isCompleted() {
        return mIsCompleted;
    }

    public void setCompleted(boolean completed) {
        mIsCompleted = completed;
    }

}
