package net.rusnet.taskmanager_old.edittask.domain;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager_old.commons.data.source.TaskDataSource;
import net.rusnet.taskmanager_old.commons.domain.model.Task;
import net.rusnet.taskmanager_old.commons.domain.usecase.DBUseCase;
import net.rusnet.taskmanager_old.commons.domain.usecase.UseCaseExecutor;

public class CreateTask extends DBUseCase<Task, Long> {

    public CreateTask(@NonNull UseCaseExecutor useCaseExecutor, @NonNull TaskDataSource taskDataSource) {
        super(useCaseExecutor, taskDataSource);
    }

    @NonNull
    @Override
    protected Long doInBackground(@NonNull Task requestValues) {
        return mTaskDataSource.createNewTask(requestValues);
    }
}
