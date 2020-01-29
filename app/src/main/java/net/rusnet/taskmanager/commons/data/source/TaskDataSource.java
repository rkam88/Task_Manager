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

//
//    void createNewTask(
//            @NonNull final Task task);
//
//    void loadTask(
//            final long taskId);
//
//
//    void createTasks(
//            @NonNull final List<Task> tasks);

}
