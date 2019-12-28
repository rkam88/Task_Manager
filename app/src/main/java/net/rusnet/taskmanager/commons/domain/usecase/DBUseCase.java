package net.rusnet.taskmanager.commons.domain.usecase;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.data.source.TaskDataSource;

public abstract class DBUseCase<Q, R> extends UseCase<Q, R> {

    protected TaskDataSource mTaskDataSource;

    public DBUseCase(@NonNull UseCaseExecutor useCaseExecutor,
                     @NonNull TaskDataSource taskDataSource) {
        super(useCaseExecutor);
        mTaskDataSource = taskDataSource;
    }

}
