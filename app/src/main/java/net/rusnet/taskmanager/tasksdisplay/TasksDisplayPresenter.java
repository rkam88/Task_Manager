package net.rusnet.taskmanager.tasksdisplay;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.model.Date;
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
                true,
                Date.START_DATE,
                Date.today(),
                new TaskDataSource.LoadTasksCountCallback() {
                    @Override
                    public void onTasksCountLoaded(int tasksCount) {
                        updateTasksCountInView(TaskViewType.TODAY, tasksCount);
                    }
                });
        mTasksRepository.loadTasksCount(
                TaskType.ACTIVE,
                false,
                true,
                Date.START_DATE,
                Date.aWeekFromToday(),
                new TaskDataSource.LoadTasksCountCallback() {
                    @Override
                    public void onTasksCountLoaded(int tasksCount) {
                        updateTasksCountInView(TaskViewType.THIS_WEEK, tasksCount);
                    }
                });
        mTasksRepository.loadTasksCount(
                TaskType.ACTIVE,
                false,
                new TaskDataSource.LoadTasksCountCallback() {
                    @Override
                    public void onTasksCountLoaded(int tasksCount) {
                        updateTasksCountInView(TaskViewType.ACTIVE_ALL, tasksCount);
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

    @Override
    public void markTaskAsCompleted(@NonNull Task task) {
        task.setCompleted(true);
        mTasksRepository.updateTask(
                task,
                new TaskDataSource.UpdateTaskCallback() {
                    @Override
                    public void onTaskUpdated() {
                        updateAllTaskCount();
                        loadTasks(mTaskViewType);
                    }
                });
    }

    @Override
    public void deleteTasks(@NonNull List<Task> tasks) {
        mTasksRepository.deleteTasks(tasks, new TaskDataSource.DeleteTasksCallback() {
            @Override
            public void onTasksDeleted() {
                updateAllTaskCount();
                loadTasks(mTaskViewType);
            }
        });
    }

    private void loadTasks(@NonNull TaskViewType taskViewType) {
        boolean useDateRange = false;
        Date startDate = Date.START_DATE;
        Date endDate = null;
        if (taskViewType == TaskViewType.TODAY) {
            useDateRange = true;
            endDate = Date.today();
        } else if (taskViewType == TaskViewType.THIS_WEEK) {
            useDateRange = true;
            endDate = Date.aWeekFromToday();
        }
        mTasksRepository.loadTasks(
                TaskViewType.getTaskType(taskViewType),
                (taskViewType.equals(TaskViewType.COMPLETED)),
                useDateRange,
                startDate,
                endDate,
                new TaskDataSource.LoadTasksCallback() {
                    @Override
                    public void onTasksLoaded(List<Task> tasks) {
                        updateView(tasks);
                    }
                }
        );
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
            String countAsString = count < 100 ? String.valueOf(count) : COUNT_99_PLUS;
            view.updateTaskCount(taskViewType, countAsString);
        }
    }
}
