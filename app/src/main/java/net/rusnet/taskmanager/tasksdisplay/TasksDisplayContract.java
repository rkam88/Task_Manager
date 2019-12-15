package net.rusnet.taskmanager.tasksdisplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.rusnet.taskmanager.model.Task;

import java.util.List;

public interface TasksDisplayContract {

    interface View {
        void updateTasksViewType(@NonNull TaskViewType type);

        void updateTaskList(@Nullable List<Task> taskList);

        void updateTaskCount(@NonNull TaskViewType type, @NonNull String newCount);

        void removeTaskAlarms(long taskId);
    }

    interface Presenter {
        void setTasksViewType(@NonNull TaskViewType type);

        void updateAllTaskCount();

        void markTaskAsCompleted(@NonNull Task task);

        void deleteTasks(@NonNull List<Task> tasks);
    }
}
