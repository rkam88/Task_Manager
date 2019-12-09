package net.rusnet.taskmanager.edittask;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.model.Task;
import net.rusnet.taskmanager.model.TaskType;

public interface EditTaskContract {

    interface View {
        void onTaskSavingFinished();

        void updateView(Task task);
    }

    interface Presenter {
        void createNewTask(@NonNull String name, @NonNull TaskType type);

        void loadTask(long taskId);

        void updateTask(@NonNull Task task);
    }
}
