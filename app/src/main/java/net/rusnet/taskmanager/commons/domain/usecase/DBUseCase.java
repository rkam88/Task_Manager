package net.rusnet.taskmanager.commons.domain.usecase;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.data.source.TaskDataSource;
import net.rusnet.taskmanager.commons.utils.executors.AppExecutor;

public abstract class DBUseCase<Q extends UseCase.RequestValues, R extends UseCase.Result> extends UseCase<Q, R> {

    protected TaskDataSource mTaskDataSource;

    public DBUseCase(
            @NonNull AppExecutor.MainThread mainThreadExecutor,
            @NonNull AppExecutor.WorkerThread workerThreadExecutor,
            @NonNull TaskDataSource taskDataSource) {
        super(mainThreadExecutor, workerThreadExecutor);
        mTaskDataSource = taskDataSource;
    }

}
