package net.rusnet.taskmanager.tasksdisplay.presentation;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.domain.usecase.UseCase;
import net.rusnet.taskmanager.tasksdisplay.domain.TaskFilter;
import net.rusnet.taskmanager.tasksdisplay.domain.usecase.GetTaskCount;
import net.rusnet.taskmanager.tasksdisplay.domain.usecase.LoadTasks;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TasksDisplayPresenter implements TasksDisplayContract.Presenter {

    private static final String COUNT_99_PLUS = "99+";

    private TaskViewType mTaskViewType;
    private WeakReference<TasksDisplayContract.View> mTasksDisplayViewWeakReference;
    private LoadTasks mLoadTasks;
    private GetTaskCount mGetTaskCount;

    public TasksDisplayPresenter(@NonNull TasksDisplayContract.View tasksDisplayView,
                                 @NonNull LoadTasks loadTasks,
                                 @NonNull GetTaskCount getTaskCount) {
        mTasksDisplayViewWeakReference = new WeakReference<>(tasksDisplayView);
        mLoadTasks = loadTasks;
        mGetTaskCount = getTaskCount;
    }

    @Override
    public void setTasksViewType(@NonNull TaskViewType taskViewType) {
        showLoadingScreen(true);
        mTaskViewType = taskViewType;

        TasksDisplayContract.View view = mTasksDisplayViewWeakReference.get();
        if (view != null) {
            view.updateTasksViewType(taskViewType);
        }

        loadTasks(taskViewType);
    }

    @Override
    public void updateAllTaskCount() {
        for (final TaskViewType taskViewType : TaskViewType.values()) {
            TaskFilter taskFilter = new TaskFilter(taskViewType);
            mGetTaskCount.execute(taskFilter, new UseCase.Callback<Integer>() {
                @Override
                public void onResult(@NonNull Integer result) {
                    updateTasksCountInView(taskViewType, result);
                }
            });
        }
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

        mLoadTasks.execute(filter, new UseCase.Callback<List<Task>>() {
            @Override
            public void onResult(@NonNull List<Task> result) {
                updateView(result);
            }
        });

        showLoadingScreen(false);
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
