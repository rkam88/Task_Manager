package net.rusnet.taskmanager.commons.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.data.source.TasksRepository;
import net.rusnet.taskmanager.commons.data.source.local.TasksDatabase;
import net.rusnet.taskmanager.commons.domain.usecase.UseCaseExecutor;
import net.rusnet.taskmanager.commons.utils.executors.DiskIOThreadExecutor;
import net.rusnet.taskmanager.commons.utils.executors.MainThreadExecutor;
import net.rusnet.taskmanager.tasksdisplay.domain.usecase.GetTaskCount;
import net.rusnet.taskmanager.tasksdisplay.domain.usecase.LoadTasks;

public class Injection {

    private static MainThreadExecutor MAIN_THREAD_EXECUTOR_INSTANCE;
    private static DiskIOThreadExecutor DISK_IO_THREAD_EXECUTOR_INSTANCE;
    private static UseCaseExecutor USE_CASE_EXECUTOR_INSTANCE;
    private static TasksRepository TASKS_REPOSITORY_INSTANCE;
    private static LoadTasks LOAD_TASKS_INSTANCE;
    private static GetTaskCount GET_TASK_COUNT_INSTANCE;

    public static MainThreadExecutor provideMainThreadExecutor() {
        if (MAIN_THREAD_EXECUTOR_INSTANCE == null) {
            MAIN_THREAD_EXECUTOR_INSTANCE = new MainThreadExecutor();
        }
        return MAIN_THREAD_EXECUTOR_INSTANCE;
    }

    public static DiskIOThreadExecutor provideDiskIOThreadExecutor() {
        if (DISK_IO_THREAD_EXECUTOR_INSTANCE == null) {
            DISK_IO_THREAD_EXECUTOR_INSTANCE = new DiskIOThreadExecutor();
        }
        return DISK_IO_THREAD_EXECUTOR_INSTANCE;
    }

    public static UseCaseExecutor provideUseCaseExecutor() {
        if (USE_CASE_EXECUTOR_INSTANCE == null) {
            USE_CASE_EXECUTOR_INSTANCE = new UseCaseExecutor(
                    provideMainThreadExecutor(),
                    provideDiskIOThreadExecutor());
        }
        return USE_CASE_EXECUTOR_INSTANCE;
    }

    public static TasksRepository provideTasksRepository(@NonNull Context context) {
        if (TASKS_REPOSITORY_INSTANCE == null) {
            TasksDatabase database = TasksDatabase.getDatabase(context);
            TASKS_REPOSITORY_INSTANCE = new TasksRepository(database.taskDao());
        }
        return TASKS_REPOSITORY_INSTANCE;
    }

    public static LoadTasks provideLoadTasksUseCase(@NonNull Context context) {
        if (LOAD_TASKS_INSTANCE == null) {
            LOAD_TASKS_INSTANCE = new LoadTasks(
                    provideUseCaseExecutor(),
                    provideTasksRepository(context));
        }
        return LOAD_TASKS_INSTANCE;
    }

    public static GetTaskCount provideGetTaskCountUseCase(@NonNull Context context) {
        if (GET_TASK_COUNT_INSTANCE == null) {
            GET_TASK_COUNT_INSTANCE = new GetTaskCount(
                    provideUseCaseExecutor(),
                    provideTasksRepository(context)
            );
        }
        return GET_TASK_COUNT_INSTANCE;
    }
}
