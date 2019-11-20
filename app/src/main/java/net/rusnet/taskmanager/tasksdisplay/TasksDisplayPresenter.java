package net.rusnet.taskmanager.tasksdisplay;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.model.Task;
import net.rusnet.taskmanager.model.TaskDataSource;
import net.rusnet.taskmanager.model.TasksRepository;

import java.lang.ref.WeakReference;
import java.util.List;

public class TasksDisplayPresenter implements TasksDisplayContract.Presenter {

    private WeakReference<TasksDisplayActivity> mTasksDisplayActivityWeakReference;
    private TasksRepository mTasksRepository;
    private TaskViewType mTaskViewType;

    public TasksDisplayPresenter(@NonNull TasksDisplayActivity tasksDisplayActivity,
                                 @NonNull TasksRepository tasksRepository,
                                 @NonNull TaskViewType taskViewType) {
        mTasksDisplayActivityWeakReference = new WeakReference<>(tasksDisplayActivity);
        mTasksRepository = tasksRepository;

        setTasksViewType(taskViewType);
    }

    @Override
    public void setTasksViewType(@NonNull TaskViewType type) {
        //todo: show loading screen
        mTaskViewType = type;

        TasksDisplayActivity view = mTasksDisplayActivityWeakReference.get();
        if (view != null) {
            view.updateTasksViewType(type);
        }

        mTasksRepository.loadTasks(new TaskDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                TasksDisplayActivity view = mTasksDisplayActivityWeakReference.get();
                if (view != null) {
                    //todo: hide loading screen
                    //todo: update view with data result (recycler contents and task counts in nav bar) or show "no tasks" screen
                    view.updateTaskList(tasks);
                }
            }
        }, mTaskViewType);


    }
}
