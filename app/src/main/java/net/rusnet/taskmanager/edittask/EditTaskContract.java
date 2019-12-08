package net.rusnet.taskmanager.edittask;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.model.TaskType;

public interface EditTaskContract {

    interface View {
        void onTaskCreated();
    }

    interface Presenter {
        void createNewTask(@NonNull String name, @NonNull TaskType type);
    }
}
