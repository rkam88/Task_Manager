package net.rusnet.taskmanager.commons.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Calendar;

@Entity(tableName = "task_table")
public class Task implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    private long mId;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @NonNull
    @ColumnInfo(name = "task_type")
    private TaskType mTaskType;

    @NonNull
    @ColumnInfo(name = "date_type")
    private DateType mDateType;

    @Nullable
    @ColumnInfo(name = "end_date")
    private Date mEndDate;

    @ColumnInfo(name = "is_completed")
    private boolean mIsCompleted;

    @Nullable
    @ColumnInfo(name = "reminder_date")
    private Calendar mReminderDate;

    public Task(long id, @NonNull String name, @NonNull TaskType taskType, @NonNull DateType dateType, @Nullable Date endDate, boolean isCompleted, @Nullable Calendar reminderDate) {
        mId = id;
        mName = name;
        mTaskType = taskType;
        mDateType = dateType;
        mEndDate = endDate;
        mIsCompleted = isCompleted;
        mReminderDate = reminderDate;
    }

    @Ignore
    public Task(@NonNull String name, @NonNull TaskType taskType, @NonNull DateType dateType, @Nullable Date endDate, @Nullable Calendar reminderDate) {
        mName = name;
        mTaskType = taskType;
        mDateType = dateType;
        mEndDate = endDate;
        mIsCompleted = false;
        mReminderDate = reminderDate;
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
    public TaskType getTaskType() {
        return mTaskType;
    }

    public void setTaskType(@NonNull TaskType taskType) {
        mTaskType = taskType;
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

    @Nullable
    public Calendar getReminderDate() {
        return mReminderDate;
    }

    public void setReminderDate(@Nullable Calendar reminderDate) {
        mReminderDate = reminderDate;
    }
}
