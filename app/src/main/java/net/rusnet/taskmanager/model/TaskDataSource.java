package net.rusnet.taskmanager.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface TaskDataSource {

    interface LoadTasksCallback {
        void onTasksLoaded(List<Task> tasks);
    }

    interface LoadTasksCountCallback {
        void onTasksCountLoaded(int tasksCount);
    }

    void loadIncompleteTasks(@NonNull final TaskType taskType, @NonNull final LoadTasksCallback callback);

    void loadCompleteTasks(@NonNull final LoadTasksCallback callback);

    void loadTasksCount(@Nullable final TaskType taskType,
                        final boolean isCompleted,
                        @NonNull final LoadTasksCountCallback callback);
}
