package net.rusnet.taskmanager.tasksdisplay.presentation;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.domain.usecase.UseCase;
import net.rusnet.taskmanager.tasksdisplay.domain.TaskFilter;
import net.rusnet.taskmanager.tasksdisplay.domain.usecase.LoadTasks;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TasksDisplayPresenter implements TasksDisplayContract.Presenter {

    private static final String COUNT_99_PLUS = "99+";

    private WeakReference<TasksDisplayContract.View> mTasksDisplayViewWeakReference;
    private LoadTasks mLoadTasks;
    private TaskViewType mTaskViewType;

    public TasksDisplayPresenter(@NonNull TasksDisplayContract.View tasksDisplayView,
                                 @NonNull LoadTasks loadTasks) {
        mTasksDisplayViewWeakReference = new WeakReference<>(tasksDisplayView);
        mLoadTasks = loadTasks;
    }

    @Override
    public void setTasksViewType(@NonNull TaskViewType taskViewType) {
        //showLoadingScreen(true);
        mTaskViewType = taskViewType;

        TasksDisplayContract.View view = mTasksDisplayViewWeakReference.get();
        if (view != null) {
            view.updateTasksViewType(taskViewType);
        }

        loadTasks(taskViewType);
    }

    @Override
    public void updateAllTaskCount() {
//        mTasksRepository.loadTasksCount(
//                TaskType.INBOX,
//                false,
//                new TaskDataSource.LoadTasksCountCallback() {
//                    @Override
//                    public void onTasksCountLoaded(int tasksCount) {
//                        updateTasksCountInView(TaskViewType.INBOX, tasksCount);
//                    }
//                });
//        mTasksRepository.loadTasksCount(
//                TaskType.ACTIVE,
//                false,
//                true,
//                new Date(0),
//                new Date(System.currentTimeMillis()),
//                new TaskDataSource.LoadTasksCountCallback() {
//                    @Override
//                    public void onTasksCountLoaded(int tasksCount) {
//                        updateTasksCountInView(TaskViewType.TODAY, tasksCount);
//                    }
//                });
//        mTasksRepository.loadTasksCount(
//                TaskType.ACTIVE,
//                false,
//                true,
//                new Date(0),
//                DateUtil.getDateInAWeek(),
//                new TaskDataSource.LoadTasksCountCallback() {
//                    @Override
//                    public void onTasksCountLoaded(int tasksCount) {
//                        updateTasksCountInView(TaskViewType.THIS_WEEK, tasksCount);
//                    }
//                });
//        mTasksRepository.loadTasksCount(
//                TaskType.ACTIVE,
//                false,
//                new TaskDataSource.LoadTasksCountCallback() {
//                    @Override
//                    public void onTasksCountLoaded(int tasksCount) {
//                        updateTasksCountInView(TaskViewType.ACTIVE_ALL, tasksCount);
//                    }
//                });
//        mTasksRepository.loadTasksCount(
//                TaskType.POSTPONED,
//                false,
//                new TaskDataSource.LoadTasksCountCallback() {
//                    @Override
//                    public void onTasksCountLoaded(int tasksCount) {
//                        updateTasksCountInView(TaskViewType.POSTPONED, tasksCount);
//                    }
//                });
//        mTasksRepository.loadTasksCount(
//                TaskType.ANY,
//                true,
//                new TaskDataSource.LoadTasksCountCallback() {
//                    @Override
//                    public void onTasksCountLoaded(int tasksCount) {
//                        updateTasksCountInView(TaskViewType.COMPLETED, tasksCount);
//                    }
//                });
    }

    @Override
    public void markTaskAsCompleted(@NonNull Task task) {
//        showLoadingScreen(true);
//        removeTaskAlarms(task);
//        task.setCompleted(true);
//        mTasksRepository.updateTask(
//                task,
//                new TaskDataSource.UpdateTaskCallback() {
//                    @Override
//                    public void onTaskUpdated() {
//                        showLoadingScreen(false);
//                        updateAllTaskCount();
//                        loadTasks(mTaskViewType);
//                    }
//                });
    }

    @Override
    public void deleteTasks(@NonNull List<Task> tasks) {
//        showLoadingScreen(true);
//        removeTaskAlarms(tasks);
//        mTasksRepository.deleteTasks(tasks, new TaskDataSource.DeleteTasksCallback() {
//            @Override
//            public void onTasksDeleted() {
//                showLoadingScreen(false);
//                updateAllTaskCount();
//                loadTasks(mTaskViewType);
//            }
//        });
    }

    @Override
    public void createFirstLaunchTasks(@NonNull final List<Task> tasks) {
//        showLoadingScreen(true);
//        mTasksRepository.createTasks(tasks, new TaskDataSource.CreateTasksCallback() {
//            @Override
//            public void onTasksCreated() {
//                mTasksRepository.loadTasks(TaskType.ANY, false, new TaskDataSource.LoadTasksCallback() {
//                    @Override
//                    public void onTasksLoaded(List<Task> tasks) {
//                        for (Task task : tasks) {
//                            if (task.getReminderDate() != null) {
//                                TasksDisplayContract.View view = mTasksDisplayViewWeakReference.get();
//                                if (view != null) {
//                                    view.updateTaskAlarm(task.getId());
//                                }
//                            }
//                        }
//                    }
//                });
//
//                loadTasks(mTaskViewType);
//                updateAllTaskCount();
//            }
//        });
    }

    private void loadTasks(@NonNull TaskViewType taskViewType) {
        TaskFilter filter = new TaskFilter(taskViewType);
        LoadTasks.RequestValues requestValues = new LoadTasks.RequestValues(filter);

        mLoadTasks.execute(requestValues, new UseCase.Callback<LoadTasks.Result>() {
            @Override
            public void onResult(@NonNull LoadTasks.Result result) {
                List<Task> taskList = result.getTasks();
                updateView(taskList);
            }
        });


//        boolean useDateRange = false;
//        Date startDate = new Date(0);
//        Date endDate = null;
//        if (taskViewType == TaskViewType.TODAY) {
//            useDateRange = true;
//            endDate = new Date(System.currentTimeMillis());
//        } else if (taskViewType == TaskViewType.THIS_WEEK) {
//            useDateRange = true;
//            endDate = DateUtil.getDateInAWeek();
//        }
//        mTasksRepository.loadTasks(
//                TaskViewType.getTaskType(taskViewType),
//                (taskViewType.equals(TaskViewType.COMPLETED)),
//                useDateRange,
//                startDate,
//                endDate,
//                new TaskDataSource.LoadTasksCallback() {
//                    @Override
//                    public void onTasksLoaded(List<Task> tasks) {
//                        showLoadingScreen(false);
//                        updateView(tasks);
//                    }
//                }
//        );
    }

    private void updateView(@NonNull List<Task> tasks) {
        TasksDisplayContract.View view = mTasksDisplayViewWeakReference.get();
        if (view != null) {
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

    private void removeTaskAlarms(Task task) {
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        removeTaskAlarms(tasks);
    }

    private void removeTaskAlarms(List<Task> tasks) {
        TasksDisplayContract.View view = mTasksDisplayViewWeakReference.get();
        if (view != null) {
            for (Task task : tasks) {
                view.removeTaskAlarms(task.getId());
            }
        }
    }

    private void showLoadingScreen(boolean showLoadingScreen) {
        TasksDisplayContract.View view = mTasksDisplayViewWeakReference.get();
        if (view != null) {
            if (showLoadingScreen) view.showLoadingScreen();
            else view.hideLoadingScreen();
        }

    }


}
