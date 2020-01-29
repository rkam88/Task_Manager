package net.rusnet.taskmanager.edittask.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.rusnet.taskmanager.commons.domain.model.DateType;
import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.domain.model.TaskType;
import net.rusnet.taskmanager.commons.domain.usecase.UpdateTask;
import net.rusnet.taskmanager.commons.domain.usecase.UseCase;
import net.rusnet.taskmanager.edittask.domain.LoadTask;

import java.lang.ref.WeakReference;
import java.util.Date;

public class EditTaskPresenter implements EditTaskContract.Presenter {

    private WeakReference<EditTaskContract.View> mEditTaskViewWeakReference;
    private LoadTask mLoadTask;
    private UpdateTask mUpdateTask;

    public EditTaskPresenter(@NonNull EditTaskContract.View editTaskView,
                             @NonNull LoadTask loadTask,
                             @NonNull UpdateTask updateTask) {
        mEditTaskViewWeakReference = new WeakReference<>(editTaskView);
        mLoadTask = loadTask;
        mUpdateTask = updateTask;
    }

    @Override
    public void createNewTask(@NonNull String name, @NonNull TaskType taskType, @NonNull DateType dateType, @Nullable Date endDate, @Nullable Date reminderDate) {
//        showLoadingScreen(true);
//        mTasksRepository.createNewTask(
//                new Task(name, taskType, dateType, endDate, reminderDate),
//                new TaskDataSource.CreateNewTaskCallback() {
//                    @Override
//                    public void onTaskCreated(long newTaskId) {
//                        EditTaskContract.View view = mEditTaskViewWeakReference.get();
//                        if (view != null) {
//                            view.updateTaskAlarm(newTaskId);
//                            view.onTaskSavingFinished();
//                        }
//                    }
//                });
    }

    @Override
    public void loadTask(long taskId) {
        showLoadingScreen(true);
        mLoadTask.execute(taskId, new UseCase.Callback<Task>() {
            @Override
            public void onResult(@NonNull Task result) {
                EditTaskContract.View view = mEditTaskViewWeakReference.get();
                if (view != null) {
                    view.updateView(result);
                    showLoadingScreen(false);
                }
            }
        });
    }

    @Override
    public void updateTask(@NonNull final Task task) {
        showLoadingScreen(true);
        mUpdateTask.execute(task, new UseCase.Callback<Void>() {
            @Override
            public void onResult(@NonNull Void result) {
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
