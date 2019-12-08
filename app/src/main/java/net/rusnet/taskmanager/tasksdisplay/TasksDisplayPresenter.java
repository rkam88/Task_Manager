package net.rusnet.taskmanager.tasksdisplay;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.model.Task;
import net.rusnet.taskmanager.model.TaskDataSource;
import net.rusnet.taskmanager.model.TaskType;

import java.lang.ref.WeakReference;
import java.util.List;

public class TasksDisplayPresenter implements TasksDisplayContract.Presenter {

    private static final String COUNT_99_PLUS = "99+";

    private WeakReference<TasksDisplayContract.View> mTasksDisplayViewWeakReference;
    private TaskDataSource mTasksRepository;
    private TaskViewType mTaskViewType;

    public TasksDisplayPresenter(@NonNull TasksDisplayContract.View tasksDisplayView,
                                 @NonNull TaskDataSource tasksRepository) {
        mTasksDisplayViewWeakReference = new WeakReference<>(tasksDisplayView);
        mTasksRepository = tasksRepository;
    }

    @Override
    public void setTasksViewType(@NonNull TaskViewType taskViewType) {
        //todo: show loading screen
        mTaskViewType = taskViewType;

        TasksDisplayContract.View view = mTasksDisplayViewWeakReference.get();
        if (view != null) {
            view.updateTasksViewType(taskViewType);
        }

        loadTasks(taskViewType);
    }

    @Override
    public void updateAllTaskCount() {
        mTasksRepository.loadTasksCount(
                TaskType.INBOX,
                false,
                new TaskDataSource.LoadTasksCountCallback() {
                    @Override
                    public void onTasksCountLoaded(int tasksCount) {
                        updateTasksCountInView(TaskViewType.INBOX, tasksCount);
                    }
                });
        mTasksRepository.loadTasksCount(
                TaskType.ACTIVE,
                false,
                new TaskDataSource.LoadTasksCountCallback() {
                    @Override
                    public void onTasksCountLoaded(int tasksCount) {
                        updateTasksCountInView(TaskViewType.ACTIVE, tasksCount);
                    }
                });
        mTasksRepository.loadTasksCount(
                TaskType.POSTPONED,
                false,
                new TaskDataSource.LoadTasksCountCallback() {
                    @Override
                    public void onTasksCountLoaded(int tasksCount) {
                        updateTasksCountInView(TaskViewType.POSTPONED, tasksCount);
                    }
                });
        mTasksRepository.loadTasksCount(
                TaskType.ANY,
                true,
                new TaskDataSource.LoadTasksCountCallback() {
                    @Override
                    public void onTasksCountLoaded(int tasksCount) {
                        updateTasksCountInView(TaskViewType.COMPLETED, tasksCount);
                    }
                });
    }

    private void loadTasks(@NonNull TaskViewType taskViewType) {
        mTasksRepository.loadTasks(
                getTaskType(taskViewType),
                (taskViewType.equals(TaskViewType.COMPLETED)),
                new TaskDataSource.LoadTasksCallback() {
                    @Override
                    public void onTasksLoaded(List<Task> tasks) {
                        updateView(tasks);
                    }
                }
        );
    }

    private TaskType getTaskType(@NonNull TaskViewType taskViewType) {
        switch (taskViewType) {
            case ACTIVE:
                return TaskType.ACTIVE;
            case POSTPONED:
                return TaskType.POSTPONED;
            case INBOX:
                return TaskType.INBOX;
            default:
                return TaskType.ANY;
        }
    }

    private void updateView(@NonNull List<Task> tasks) {
        TasksDisplayContract.View view = mTasksDisplayViewWeakReference.get();
        if (view != null) {
            //todo: hide loading screen
            //todo: add an if statement to update recycler or show "no tasks" screen
            view.updateTaskList(tasks);
        }
    }

    private void updateTasksCountInView(@NonNull TaskViewType taskViewType, int count) {
        TasksDisplayContract.View view = mTasksDisplayViewWeakReference.get();
        if (view != null) {
            if (count < 100) {
                view.updateTaskCount(taskViewType, String.valueOf(count));
            } else {
                view.updateTaskCount(taskViewType, COUNT_99_PLUS);
            }
        }
    }
}
