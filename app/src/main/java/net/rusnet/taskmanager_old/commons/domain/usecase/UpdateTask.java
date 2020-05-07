package net.rusnet.taskmanager_old.commons.domain.usecase;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager_old.commons.data.source.TaskDataSource;
import net.rusnet.taskmanager_old.commons.domain.model.Task;

public class UpdateTask extends DBUseCase<Task, Void> {

    public UpdateTask(@NonNull UseCaseExecutor useCaseExecutor, @NonNull TaskDataSource taskDataSource) {
        super(useCaseExecutor, taskDataSource);
    }

    @NonNull
    @Override
    protected Void doInBackground(@NonNull Task requestValues) {
        mTaskDataSource.updateTask(requestValues);
        return null;
    }
}
