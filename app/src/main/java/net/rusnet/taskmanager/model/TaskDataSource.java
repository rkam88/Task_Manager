package net.rusnet.taskmanager.model;

import androidx.annotation.NonNull;

import java.util.List;

public interface TaskDataSource {

    interface LoadTasksCallback {
        void onTasksLoaded(List<Task> tasks);
    }

    void loadIncompleteTasks(@NonNull final LoadTasksCallback callback, @NonNull final TaskType taskType);

    void loadCompleteTasks(@NonNull final LoadTasksCallback callback);
}
