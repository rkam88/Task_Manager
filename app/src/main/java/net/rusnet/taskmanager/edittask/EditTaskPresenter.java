package net.rusnet.taskmanager.edittask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.rusnet.taskmanager.commons.model.DateType;
import net.rusnet.taskmanager.commons.model.Task;
import net.rusnet.taskmanager.commons.model.TaskDataSource;
import net.rusnet.taskmanager.commons.model.TaskType;

import java.lang.ref.WeakReference;
import java.util.Date;

public class EditTaskPresenter implements EditTaskContract.Presenter {

    private WeakReference<EditTaskContract.View> mEditTaskViewWeakReference;
    private TaskDataSource mTasksRepository;

    public EditTaskPresenter(@NonNull EditTaskContract.View editTaskView,
                             @NonNull TaskDataSource tasksRepository) {
        mEditTaskViewWeakReference = new WeakReference<>(editTaskView);
        mTasksRepository = tasksRepository;
    }

    @Override
    public void createNewTask(@NonNull String name, @NonNull TaskType taskType, @NonNull DateType dateType, @Nullable Date endDate, @Nullable Date reminderDate) {
        showLoadingScreen(true);
        mTasksRepository.createNewTask(
                new Task(name, taskType, dateType, endDate, reminderDate),
                new TaskDataSource.CreateNewTaskCallback() {
                    @Override
                    public void onTaskCreated(long newTaskId) {
                        EditTaskContract.View view = mEditTaskViewWeakReference.get();
                        if (view != null) {
                            view.updateTaskAlarm(newTaskId);
                            view.onTaskSavingFinished();
                        }
                    }
                });
    }

    @Override
    public void loadTask(long taskId) {
        showLoadingScreen(true);
        mTasksRepository.loadTask(taskId, new TaskDataSource.LoadTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                EditTaskContract.View view = mEditTaskViewWeakReference.get();
                if (view != null) {
                    view.updateView(task);
                    showLoadingScreen(false);
                }
            }
        });
    }

    @Override
    public void updateTask(@NonNull final Task task) {
        showLoadingScreen(true);
        mTasksRepository.updateTask(task, new TaskDataSource.UpdateTaskCallback() {
            @Override
            public void onTaskUpdated() {
                EditTaskContract.View view = mEditTaskViewWeakReference.get();
                if (view != null) {
                    view.updateTaskAlarm(task.getId());
                    view.onTaskSavingFinished();
                }
            }
        });
    }

    private void showLoadingScreen(boolean showLoadingScreen) {
        EditTaskContract.View view = mEditTaskViewWeakReference.get();
        if (view != null) {
            if (showLoadingScreen) view.showLoadingScreen();
            else view.hideLoadingScreen();
        }
    }

}
