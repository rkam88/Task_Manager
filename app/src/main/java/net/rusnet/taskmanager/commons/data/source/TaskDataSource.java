package net.rusnet.taskmanager.commons.data.source;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.domain.model.TaskType;

import java.util.Date;
import java.util.List;

public interface TaskDataSource {

    interface LoadTasksCallback {
        void onTasksLoaded(List<Task> tasks);
    }

    interface LoadTasksCountCallback {
        void onTasksCountLoaded(int tasksCount);
    }

    interface CreateNewTaskCallback {
        void onTaskCreated(long newTaskId);
    }

    interface LoadTaskCallback {
        void onTaskLoaded(Task task);
    }

    interface UpdateTaskCallback {
        void onTaskUpdated();
    }

    interface DeleteTasksCallback {
        void onTasksDeleted();
    }

    interface CreateTasksCallback {
        void onTasksCreated();
    }

    void loadTasks(
            @Nullable final TaskType taskType,
            final boolean isCompleted,
            @NonNull final LoadTasksCallback callback);

    void loadTasks(
            @Nullable final TaskType taskType,
            final boolean isCompleted,
            final boolean useDateRange,
            final Date startDate,
            final Date endDate,
            @NonNull final LoadTasksCallback callback);

    void loadTasksCount(
            @Nullable final TaskType taskType,
            final boolean isCompleted,
            @NonNull final LoadTasksCountCallback callback);

    void loadTasksCount(
            @Nullable final TaskType taskType,
            final boolean isCompleted,
            final boolean useDateRange,
            final Date startDate,
            final Date endDate,
            @NonNull final LoadTasksCountCallback callback);

    void createNewTask(
            @NonNull final Task task,
            @NonNull final CreateNewTaskCallback callback);

    void loadTask(
            final long taskId,
            @NonNull final LoadTaskCallback callback);

    void updateTask(
            @NonNull final Task task,
            @NonNull final UpdateTaskCallback callback);

    void deleteTasks(
            @NonNull final List<Task> tasks,
            @NonNull final DeleteTasksCallback callback);

    void createTasks(
            @NonNull final List<Task> tasks,
            @NonNull final CreateTasksCallback callback);

}
