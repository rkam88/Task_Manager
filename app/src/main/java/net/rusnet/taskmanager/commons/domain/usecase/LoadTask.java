package net.rusnet.taskmanager.commons.domain.usecase;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.data.source.TaskDataSource;
import net.rusnet.taskmanager.commons.domain.model.Task;

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
