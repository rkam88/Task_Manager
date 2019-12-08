package net.rusnet.taskmanager.edittask;

import androidx.annotation.NonNull;

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
    public void createNewTask(@NonNull String name, @NonNull TaskType type) {
        mTasksRepository.createNewTask(
                new Task(name, type.getType()),
                new TaskDataSource.CreateNewTaskCallback() {
                    @Override
                    public void onTaskCreated() {
                        EditTaskContract.View view = mEditTaskViewWeakReference.get();
                        if (view != null) {
                            view.onTaskCreated();
                        }
                    }
                });
    }
}
