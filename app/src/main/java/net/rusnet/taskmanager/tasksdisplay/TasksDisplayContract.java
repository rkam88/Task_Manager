package net.rusnet.taskmanager.tasksdisplay;

import androidx.annotation.NonNull;

public interface TasksDisplayContract {

    interface View {
        void updateTasksViewType(@NonNull TaskViewType type);

        void updateTaskCount(@NonNull TaskViewType type, @NonNull String newCount);
    }

    interface Presenter {
        void setTasksViewType(@NonNull TaskViewType type);
    }
}
