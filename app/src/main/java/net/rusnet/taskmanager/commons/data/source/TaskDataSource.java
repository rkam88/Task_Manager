package net.rusnet.taskmanager.commons.data.source;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.tasksdisplay.domain.TaskFilter;

import java.util.List;

public interface TaskDataSource {

    List<Task> loadTasks(@NonNull TaskFilter taskFilter);

    Void deleteTasks(@NonNull List<Task> tasksToDelete);

    Void updateTask(@NonNull Task taskToUpdate);

    Task loadTask(long taskId);

    long createNewTask(@NonNull Task task);

    Void createTasks(@NonNull List<Task> tasks);

}
