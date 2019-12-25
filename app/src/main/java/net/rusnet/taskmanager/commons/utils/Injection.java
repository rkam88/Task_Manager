package net.rusnet.taskmanager.commons.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.data.source.TasksRepository;
import net.rusnet.taskmanager.commons.data.source.local.TasksDatabase;
import net.rusnet.taskmanager.commons.utils.executors.DiskIOThreadExecutor;
import net.rusnet.taskmanager.commons.utils.executors.MainThreadExecutor;
import net.rusnet.taskmanager.tasksdisplay.domain.usecase.LoadTasks;

public class Injection {

    public static TasksRepository provideTasksRepository(@NonNull Context context) {
        TasksDatabase database = TasksDatabase.getDatabase(context);
        return TasksRepository.getRepository(database.taskDao());
    }

    public static LoadTasks provideLoadTasksUseCase(@NonNull Context context) {
        return new LoadTasks(
                MainThreadExecutor.getInstance(),
                DiskIOThreadExecutor.getInstance(),
                provideTasksRepository(context));
    }


}
