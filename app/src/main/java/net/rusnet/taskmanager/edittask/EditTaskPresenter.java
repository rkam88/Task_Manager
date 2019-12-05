package net.rusnet.taskmanager.edittask;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.model.Task;
import net.rusnet.taskmanager.model.TaskDataSource;
import net.rusnet.taskmanager.model.TaskType;

import java.lang.ref.WeakReference;

public class EditTaskPresenter implements EditTaskContract.Presenter {

    private WeakReference<EditTaskActivity> mEditTaskActivityWeakReference;
    private TaskDataSource mTasksRepository;

    public EditTaskPresenter(@NonNull EditTaskActivity editTaskActivity,
                                 @NonNull TaskDataSource tasksRepository) {
        mEditTaskActivityWeakReference = new WeakReference<>(editTaskActivity);
        mTasksRepository = tasksRepository;
    }

    @Override
    public void createNewTask(@NonNull String name, @NonNull TaskType type) {
        mTasksRepository.createNewTask(
                new Task(name, type.getType()),
                new TaskDataSource.CreateNewTaskCallback() {
                    @Override
                    public void onTaskCreated() {
                        EditTaskActivity view = mEditTaskActivityWeakReference.get();
                        if (view != null) {
                            view.onTaskCreated();
                        }
                    }
                });
    }
}
