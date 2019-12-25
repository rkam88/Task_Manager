package net.rusnet.taskmanager.commons.data.source;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.tasksdisplay.domain.TaskFilter;

import java.util.List;

public interface TaskDataSource {

    List<Task> loadTasks(@NonNull TaskFilter taskFilter);

//    void loadTasks(
//            @Nullable final TaskType taskType,
//            final boolean isCompleted);
//
//    void loadTasks(
//            @Nullable final TaskType taskType,
//            final boolean isCompleted,
//            final boolean useDateRange,
//            final Date startDate,
//            final Date endDate);
//
//    void loadTasksCount(
//            @Nullable final TaskType taskType,
//            final boolean isCompleted);
//
//    void loadTasksCount(
//            @Nullable final TaskType taskType,
//            final boolean isCompleted,
//            final boolean useDateRange,
//            final Date startDate,
//            final Date endDate);
//
//    void createNewTask(
//            @NonNull final Task task);
//
//    void loadTask(
//            final long taskId);
//
//    void updateTask(
//            @NonNull final Task task);
//
//    void deleteTasks(
//            @NonNull final List<Task> tasks);
//
//    void createTasks(
//            @NonNull final List<Task> tasks);

}
