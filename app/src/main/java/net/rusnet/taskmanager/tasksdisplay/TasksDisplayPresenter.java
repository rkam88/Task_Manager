package net.rusnet.taskmanager.tasksdisplay;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.model.Task;
import net.rusnet.taskmanager.model.TaskDataSource;
import net.rusnet.taskmanager.model.TaskType;
import net.rusnet.taskmanager.model.TasksRepository;

import java.lang.ref.WeakReference;
import java.util.List;

public class TasksDisplayPresenter implements TasksDisplayContract.Presenter {

    private WeakReference<TasksDisplayActivity> mTasksDisplayActivityWeakReference;
    private TasksRepository mTasksRepository;
    private TaskViewType mTaskViewType;

    public TasksDisplayPresenter(@NonNull TasksDisplayActivity tasksDisplayActivity,
                                 @NonNull TasksRepository tasksRepository) {
        mTasksDisplayActivityWeakReference = new WeakReference<>(tasksDisplayActivity);
        mTasksRepository = tasksRepository;
    }

    @Override
    public void setTasksViewType(@NonNull TaskViewType taskViewType) {
        //todo: show loading screen
        mTaskViewType = taskViewType;

        TasksDisplayActivity view = mTasksDisplayActivityWeakReference.get();
        if (view != null) {
            view.updateTasksViewType(taskViewType);
        }

        loadTasks(taskViewType);
    }

    private void loadTasks(@NonNull TaskViewType taskViewType) {
        switch (taskViewType) {
            case COMPLETED:
                mTasksRepository.loadCompleteTasks(new TaskDataSource.LoadTasksCallback() {
                    @Override
                    public void onTasksLoaded(List<Task> tasks) {
                        updateView(tasks);
                    }
                });
                break;
            case INBOX:
            case ACTIVE:
            case POSTPONED:
                TaskType taskType = getTaskType(taskViewType);
                mTasksRepository.loadIncompleteTasks(new TaskDataSource.LoadTasksCallback() {
                    @Override
                    public void onTasksLoaded(List<Task> tasks) {
                        updateView(tasks);
                    }
                }, taskType);
                break;
        }
    }

    private TaskType getTaskType(@NonNull TaskViewType taskViewType) {
        switch (taskViewType) {
            case ACTIVE:
                return TaskType.ACTIVE;
            case POSTPONED:
                return TaskType.POSTPONED;
            case INBOX:
            default:
                return TaskType.INBOX;
        }
    }

    private void updateView(@NonNull List<Task> tasks) {
        TasksDisplayActivity view = mTasksDisplayActivityWeakReference.get();
        if (view != null) {
            //todo: hide loading screen
            //todo: update view with data result (recycler contents and task counts in nav bar) or show "no tasks" screen
            view.updateTaskList(tasks);
        }
    }
}
