package net.rusnet.taskmanager.commons.data.source;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.data.source.local.TaskDao;
import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.tasksdisplay.domain.TaskFilter;

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
        mCacheIsDirty = true;
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
        mCacheIsDirty = true;
        return null;
    }

    @Override
    public Void updateTask(@NonNull Task taskToUpdate) {
        mTaskDao.updateTask(taskToUpdate);
        mCacheIsDirty = true;
        return null;
    }

    @Override
    public Task loadTask(long taskId) {
        return mTaskDao.getById(taskId);
    }

    private void updateCache(@NonNull List<Task> taskList) {
        mTaskListCache = new ArrayList<>(taskList);
        mCacheIsDirty = false;
    }

    @NonNull
    private List<Task> getCachedTasks() {
        return new ArrayList<>(mTaskListCache);
    }


//    @Override
//    public void createNewTask(@NonNull Task task, @NonNull CreateNewTaskCallback callback) {
//        new CreateNewTaskAsyncTask(mTaskDao, callback).execute(task);
//    }
//
//
//
//
//    @Override
//    public void createTasks(@NonNull List<Task> tasks, @NonNull CreateTasksCallback callback) {
//        new CreateTasksAsyncTask(mTaskDao, tasks, callback).execute();
//    }
//
//
//
//    private static class CreateNewTaskAsyncTask extends AsyncTask<Task, Void, Long> {
//        private TaskDao mTaskDao;
//        private CreateNewTaskCallback mCallback;
//
//        CreateNewTaskAsyncTask(TaskDao taskDao, CreateNewTaskCallback callback) {
//            mTaskDao = taskDao;
//            mCallback = callback;
//        }
//
//        @Override
//        protected Long doInBackground(Task... tasks) {
//            return mTaskDao.insertTask(tasks[0]);
//        }
//
//        @Override
//        protected void onPostExecute(Long newTaskId) {
//            mCallback.onTaskCreated(newTaskId);
//        }
//    }
//
//
//
//    private static class CreateTasksAsyncTask extends AsyncTask<Void, Void, Void> {
//        private TaskDao mTaskDao;
//        private List<Task> mTasks;
//        private CreateTasksCallback mCallback;
//
//        CreateTasksAsyncTask(TaskDao taskDao, List<Task> tasks, CreateTasksCallback callback) {
//            mTaskDao = taskDao;
//            mTasks = tasks;
//            mCallback = callback;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            mTaskDao.insertTasks(mTasks);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            mCallback.onTasksCreated();
//        }
//    }

}
