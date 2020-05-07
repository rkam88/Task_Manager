package net.rusnet.taskmanager_old.commons.data.source;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager_old.commons.data.source.local.TaskDao;
import net.rusnet.taskmanager_old.commons.domain.model.Task;
import net.rusnet.taskmanager_old.tasksdisplay.domain.TaskFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TasksRepository implements TaskDataSource {

    private TaskDao mTaskDao;
    private List<Task> mTaskListCache;
    private boolean mCacheIsDirty;

    public TasksRepository(@NonNull TaskDao taskDao) {
        mTaskDao = taskDao;
        mTaskListCache = new ArrayList<>();
        invalidateCache();
    }

    @Override
    public List<Task> loadTasks(@NonNull TaskFilter taskFilter) {
        List<Task> taskList;
        if (mCacheIsDirty) {
            taskList = Arrays.asList(mTaskDao.getAllTasks());
            updateCache(taskList);
        } else {
            taskList = getCachedTasks();
        }
        return taskFilter.filter(taskList);
    }

    @Override
    public Void deleteTasks(@NonNull List<Task> tasksToDelete) {
        mTaskDao.deleteTasks(tasksToDelete);
        invalidateCache();
        return null;
    }

    @Override
    public Void updateTask(@NonNull Task taskToUpdate) {
        mTaskDao.updateTask(taskToUpdate);
        invalidateCache();
        return null;
    }

    @Override
    public Task loadTask(long taskId) {
        return mTaskDao.getById(taskId);
    }

    @Override
    public long createNewTask(@NonNull Task task) {
        long newTaskId = mTaskDao.insertTask(task);
        invalidateCache();
        return newTaskId;
    }

    @Override
    public Void createTasks(@NonNull List<Task> tasks) {
        mTaskDao.insertTasks(tasks);
        invalidateCache();
        return null;
    }

    private void updateCache(@NonNull List<Task> taskList) {
        mTaskListCache = new ArrayList<>(taskList);
        mCacheIsDirty = false;
    }

    private void invalidateCache() {
        mCacheIsDirty = true;
    }

    @NonNull
    private List<Task> getCachedTasks() {
        return new ArrayList<>(mTaskListCache);
    }

}
