package net.rusnet.taskmanager.edittask.domain;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.data.source.TaskDataSource;
import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.domain.usecase.DBUseCase;
import net.rusnet.taskmanager.commons.domain.usecase.UseCaseExecutor;

public class LoadTask extends DBUseCase<Long, Task> {

    public LoadTask(@NonNull UseCaseExecutor useCaseExecutor, @NonNull TaskDataSource taskDataSource) {
        super(useCaseExecutor, taskDataSource);
    }

    @NonNull
    @Override
    protected Task doInBackground(@NonNull Long requestValues) {
        return mTaskDataSource.loadTask(requestValues);
    }
}
