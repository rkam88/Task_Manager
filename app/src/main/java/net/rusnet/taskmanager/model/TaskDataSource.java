package net.rusnet.taskmanager.model;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.tasksdisplay.TaskViewType;

import java.util.List;

public interface TaskDataSource {

    interface LoadTasksCallback {
        void onTasksLoaded(List<Task> tasks);
    }

    void loadTasks(@NonNull LoadTasksCallback callback, @NonNull TaskViewType type);
}
