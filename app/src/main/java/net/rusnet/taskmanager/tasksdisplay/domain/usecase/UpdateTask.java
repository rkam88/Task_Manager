package net.rusnet.taskmanager.tasksdisplay.domain.usecase;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.data.source.TaskDataSource;
import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.domain.usecase.DBUseCase;
import net.rusnet.taskmanager.commons.domain.usecase.UseCaseExecutor;

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
