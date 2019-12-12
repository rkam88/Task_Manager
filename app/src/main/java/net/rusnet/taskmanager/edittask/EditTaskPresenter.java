package net.rusnet.taskmanager.edittask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.rusnet.taskmanager.model.Date;
import net.rusnet.taskmanager.model.DateType;
import net.rusnet.taskmanager.model.Task;
import net.rusnet.taskmanager.model.TaskDataSource;
import net.rusnet.taskmanager.model.TaskType;

import java.lang.ref.WeakReference;

public class EditTaskPresenter implements EditTaskContract.Presenter {

    private WeakReference<EditTaskContract.View> mEditTaskViewWeakReference;
    private TaskDataSource mTasksRepository;

    public EditTaskPresenter(@NonNull EditTaskContract.View editTaskView,
                             @NonNull TaskDataSource tasksRepository) {
        mEditTaskViewWeakReference = new WeakReference<>(editTaskView);
        mTasksRepository = tasksRepository;
    }

    @Override
    public void createNewTask(@NonNull String name, @NonNull TaskType type, @NonNull DateType dateType, @Nullable Date endDate) {
        mTasksRepository.createNewTask(
                new Task(name, type.getType(), dateType, endDate),
                new TaskDataSource.CreateNewTaskCallback() {
                    @Override
                    public void onTaskCreated() {
                        EditTaskContract.View view = mEditTaskViewWeakReference.get();
                        if (view != null) {
                            view.onTaskSavingFinished();
                        }
                    }
                });
    }

    @Override
    public void loadTask(long taskId) {
        mTasksRepository.loadTask(taskId, new TaskDataSource.LoadTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                EditTaskContract.View view = mEditTaskViewWeakReference.get();
                if (view != null) {
                    view.updateView(task);
                }
            }
        });
    }

    @Override
    public void updateTask(@NonNull Task task) {
        mTasksRepository.updateTask(task, new TaskDataSource.UpdateTaskCallback() {
            @Override
            public void onTaskUpdated() {
                EditTaskContract.View view = mEditTaskViewWeakReference.get();
                if (view != null) {
                    view.onTaskSavingFinished();
                }
            }
        });
    }
}
