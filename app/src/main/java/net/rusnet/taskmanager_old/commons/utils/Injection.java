package net.rusnet.taskmanager_old.commons.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager_old.commons.data.source.TasksRepository;
import net.rusnet.taskmanager_old.commons.data.source.local.TasksDatabase;
import net.rusnet.taskmanager_old.commons.domain.usecase.LoadTask;
import net.rusnet.taskmanager_old.commons.domain.usecase.LoadTasks;
import net.rusnet.taskmanager_old.commons.domain.usecase.UpdateTask;
import net.rusnet.taskmanager_old.commons.domain.usecase.UseCaseExecutor;
import net.rusnet.taskmanager_old.commons.utils.executors.DiskIOThreadExecutor;
import net.rusnet.taskmanager_old.commons.utils.executors.MainThreadExecutor;
import net.rusnet.taskmanager_old.edittask.domain.CreateTask;
import net.rusnet.taskmanager_old.tasksdisplay.domain.usecase.CreateTasks;
import net.rusnet.taskmanager_old.tasksdisplay.domain.usecase.DeleteTasks;
import net.rusnet.taskmanager_old.tasksdisplay.domain.usecase.GetTaskCount;

public class Injection {

    private static MainThreadExecutor MAIN_THREAD_EXECUTOR_INSTANCE;
    private static DiskIOThreadExecutor DISK_IO_THREAD_EXECUTOR_INSTANCE;
    private static UseCaseExecutor USE_CASE_EXECUTOR_INSTANCE;
    private static TasksRepository TASKS_REPOSITORY_INSTANCE;
    private static LoadTasks LOAD_TASKS_INSTANCE;
    private static GetTaskCount GET_TASK_COUNT_INSTANCE;
    private static DeleteTasks DELETE_TASKS_INSTANCE;
    private static UpdateTask UPDATE_TASK_INSTANCE;
    private static LoadTask LOAD_TASK_INSTANCE;
    private static CreateTask CREATE_TASK_INSTANCE;
    private static CreateTasks CREATE_TASKS_INSTANCE;

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

    public static DeleteTasks provideDeleteTasksUseCase(@NonNull Context context) {
        if (DELETE_TASKS_INSTANCE == null) {
            DELETE_TASKS_INSTANCE = new DeleteTasks(
                    provideUseCaseExecutor(),
                    provideTasksRepository(context)
            );
        }
        return DELETE_TASKS_INSTANCE;
    }

    public static UpdateTask provideUpdateTaskUseCase(@NonNull Context context) {
        if (UPDATE_TASK_INSTANCE == null) {
            UPDATE_TASK_INSTANCE = new UpdateTask(
                    provideUseCaseExecutor(),
                    provideTasksRepository(context)
            );
        }
        return UPDATE_TASK_INSTANCE;
    }

    public static LoadTask provideLoadTaskUseCase(@NonNull Context context) {
        if (LOAD_TASK_INSTANCE == null) {
            LOAD_TASK_INSTANCE = new LoadTask(
                    provideUseCaseExecutor(),
                    provideTasksRepository(context)
            );
        }
        return LOAD_TASK_INSTANCE;
    }

    public static CreateTask provideCreateTaskUseCase(@NonNull Context context) {
        if (CREATE_TASK_INSTANCE == null) {
            CREATE_TASK_INSTANCE = new CreateTask(
                    provideUseCaseExecutor(),
                    provideTasksRepository(context)
            );
        }
        return CREATE_TASK_INSTANCE;
    }

    public static CreateTasks provideCreateTasksUseCase(@NonNull Context context) {
        if (CREATE_TASKS_INSTANCE == null) {
            CREATE_TASKS_INSTANCE = new CreateTasks(
                    provideUseCaseExecutor(),
                    provideTasksRepository(context)
            );
        }
        return CREATE_TASKS_INSTANCE;
    }
}
