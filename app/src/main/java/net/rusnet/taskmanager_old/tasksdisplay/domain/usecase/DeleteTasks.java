package net.rusnet.taskmanager_old.tasksdisplay.domain.usecase;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager_old.commons.data.source.TaskDataSource;
import net.rusnet.taskmanager_old.commons.domain.model.Task;
import net.rusnet.taskmanager_old.commons.domain.usecase.DBUseCase;
import net.rusnet.taskmanager_old.commons.domain.usecase.UseCaseExecutor;

import java.util.List;

public class DeleteTasks extends DBUseCase<List<Task>, Void> {

    public DeleteTasks(@NonNull UseCaseExecutor useCaseExecutor, @NonNull TaskDataSource taskDataSource) {
        super(useCaseExecutor, taskDataSource);
    }

    @NonNull
    @Override
    protected Void doInBackground(@NonNull List<Task> requestValues) {
        mTaskDataSource.deleteTasks(requestValues);
        return null;
    }
}
