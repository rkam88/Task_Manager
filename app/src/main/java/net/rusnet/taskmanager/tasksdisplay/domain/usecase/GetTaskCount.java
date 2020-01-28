package net.rusnet.taskmanager.tasksdisplay.domain.usecase;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.data.source.TaskDataSource;
import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.domain.usecase.DBUseCase;
import net.rusnet.taskmanager.commons.domain.usecase.UseCaseExecutor;
import net.rusnet.taskmanager.tasksdisplay.domain.TaskFilter;

import java.util.List;

public class GetTaskCount extends DBUseCase<TaskFilter, Integer> {

    public GetTaskCount(@NonNull UseCaseExecutor useCaseExecutor, @NonNull TaskDataSource taskDataSource) {
        super(useCaseExecutor, taskDataSource);
    }

    @NonNull
    @Override
    protected Integer doInBackground(@NonNull TaskFilter requestValues) {
        List<Task> taskList = mTaskDataSource.loadTasks(requestValues);
        return taskList.size();
    }
}
