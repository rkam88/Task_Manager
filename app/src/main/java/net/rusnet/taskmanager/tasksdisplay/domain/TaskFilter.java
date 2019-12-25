package net.rusnet.taskmanager.tasksdisplay.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.domain.model.TaskEndDate;
import net.rusnet.taskmanager.commons.domain.model.TaskType;
import net.rusnet.taskmanager.tasksdisplay.presentation.TaskViewType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class TaskFilter {

    private final TaskType mTaskType;
    private final Date mEndDate;
    private final boolean mIsCompleted;

    public TaskFilter(@NonNull TaskType taskType,
                      @Nullable Date endDate,
                      boolean isCompleted) {
        mTaskType = taskType;
        mEndDate = (endDate != null) ? new TaskEndDate(endDate) : null;
        mIsCompleted = isCompleted;
    }

    public TaskFilter(@NonNull TaskViewType taskViewType) {
        this(TaskViewType.getTaskType(taskViewType),
                TaskViewType.getEndDate(taskViewType),
                TaskViewType.getCompletedStatus(taskViewType));
    }

    @NonNull
    public TaskType getTaskType() {
        return mTaskType;
    }

    @Nullable
    public Date getEndDate() {
        return mEndDate;
    }

    public boolean isCompleted() {
        return mIsCompleted;
    }

    @NonNull
    public List<Task> filter(@NonNull List<Task> tasks) {
        List<Task> filteredTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (taskTypeMatches(task)
                    && (taskEndDateMatches(task))
                    && taskCompletedStatusMatches(task)) {
                filteredTasks.add(task);
            }
        }

        return filteredTasks;
    }

    private boolean taskTypeMatches(@NonNull Task task) {
        return (mTaskType == TaskType.ANY || task.getTaskType() == mTaskType);
    }

    private boolean taskEndDateMatches(@NonNull Task task) {
        Date taskEndDate = task.getEndDate();
        return (mEndDate == null ||
                (taskEndDate != null && (taskEndDate.getTime() <= mEndDate.getTime())));
    }

    private boolean taskCompletedStatusMatches(@NonNull Task task) {
        return (task.isCompleted() == mIsCompleted);
    }

}
