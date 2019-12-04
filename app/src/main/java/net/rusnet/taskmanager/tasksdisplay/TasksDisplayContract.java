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
    }

    interface Presenter {
        void setTasksViewType(@NonNull TaskViewType type);

        void updateAllTaskCount();
    }
}
