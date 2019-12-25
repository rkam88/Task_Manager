package net.rusnet.taskmanager.tasksdisplay.domain.usecase;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.data.source.TaskDataSource;
import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.domain.usecase.DBUseCase;
import net.rusnet.taskmanager.commons.domain.usecase.UseCase;
import net.rusnet.taskmanager.commons.utils.executors.AppExecutor;
import net.rusnet.taskmanager.tasksdisplay.domain.TaskFilter;

import java.util.List;

public class LoadTasks extends DBUseCase<LoadTasks.RequestValues, LoadTasks.Result> {

    public LoadTasks(
            @NonNull AppExecutor.MainThread mainThreadExecutor,
            @NonNull AppExecutor.WorkerThread workerThreadExecutor,
            @NonNull TaskDataSource taskDataSource) {
        super(mainThreadExecutor, workerThreadExecutor, taskDataSource);
    }

    @NonNull
    @Override
    protected Result doInBackground(@NonNull RequestValues requestValues) {
        List<Task> taskList = mTaskDataSource.loadTasks(requestValues.getTaskFilter());
        return new Result(taskList);
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final TaskFilter mTaskFilter;

        public RequestValues(@NonNull TaskFilter taskFilter) {
            mTaskFilter = taskFilter;
        }

        @NonNull
        public TaskFilter getTaskFilter() {
            return mTaskFilter;
        }
    }

    public static final class Result implements UseCase.Result {

        private final List<Task> mTasks;

        public Result(@NonNull List<Task> tasks) {
            mTasks = tasks;
        }

        @NonNull
        public List<Task> getTasks() {
            return mTasks;
        }
    }
}
