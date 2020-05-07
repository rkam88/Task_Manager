package net.rusnet.taskmanager_old.commons.domain.usecase;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager_old.commons.data.source.TaskDataSource;
import net.rusnet.taskmanager_old.commons.domain.model.Task;
import net.rusnet.taskmanager_old.tasksdisplay.domain.TaskFilter;

import java.util.List;

public class LoadTasks extends DBUseCase<TaskFilter, List<Task>> {

    public LoadTasks(@NonNull UseCaseExecutor useCaseExecutor,
                     @NonNull TaskDataSource taskDataSource) {
        super(useCaseExecutor, taskDataSource);
    }

    @NonNull
    @Override
    protected List<Task> doInBackground(@NonNull TaskFilter requestValues) {
        return mTaskDataSource.loadTasks(requestValues);
    }

}
