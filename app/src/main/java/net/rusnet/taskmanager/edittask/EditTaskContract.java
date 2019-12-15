package net.rusnet.taskmanager.edittask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.rusnet.taskmanager.model.Date;
import net.rusnet.taskmanager.model.DateType;
import net.rusnet.taskmanager.model.Task;
import net.rusnet.taskmanager.model.TaskType;

import java.util.Calendar;

public interface EditTaskContract {

    interface View {
        void onTaskSavingFinished();

        void updateView(Task task);

        void updateTaskAlarm(long taskId);

        void showLoadingScreen();

        void hideLoadingScreen();
    }

    interface Presenter {
        void createNewTask(@NonNull String name,
                           @NonNull TaskType taskType,
                           @NonNull DateType dateType,
                           @Nullable Date endDate,
                           @Nullable Calendar reminderDate);

        void loadTask(long taskId);

        void updateTask(@NonNull Task task);
    }
}
