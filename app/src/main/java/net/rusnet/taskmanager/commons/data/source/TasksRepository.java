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

    private void updateCache(@NonNull List<Task> taskList) {
        mTaskListCache = new ArrayList<>(taskList);
        mCacheIsDirty = false;
    }

    @NonNull
    private List<Task> getCachedTasks() {
        return new ArrayList<>(mTaskListCache);
    }

//    @Override
//    public void loadTasks(@Nullable TaskType taskType, boolean isCompleted, @NonNull LoadTasksCallback callback) {
//        new LoadTasksAsyncTask(mTaskDao, taskType, isCompleted, false, null, null, callback).execute();
//    }
//
//    @Override
//    public void loadTasks(@Nullable TaskType taskType, boolean isCompleted, boolean useDateRange, Date startDate, Date endDate, @NonNull LoadTasksCallback callback) {
//        new LoadTasksAsyncTask(mTaskDao, taskType, isCompleted, useDateRange, startDate, endDate, callback).execute();
//    }
//
//    @Override
//    public void createNewTask(@NonNull Task task, @NonNull CreateNewTaskCallback callback) {
//        new CreateNewTaskAsyncTask(mTaskDao, callback).execute(task);
//    }
//
//    @Override
//    public void loadTask(long taskId, @NonNull LoadTaskCallback callback) {
//        new LoadTaskAsyncTask(mTaskDao, callback).execute(taskId);
//    }
//
//    @Override
//    public void updateTask(@NonNull Task task, @NonNull UpdateTaskCallback callback) {
//        new UpdateTaskAsyncTask(mTaskDao, callback).execute(task);
//    }
//
//    @Override
//    public void deleteTasks(@NonNull List<Task> tasks, @NonNull DeleteTasksCallback callback) {
//        new DeleteTasksAsyncTask(mTaskDao, tasks, callback).execute();
//    }
//
//    @Override
//    public void createTasks(@NonNull List<Task> tasks, @NonNull CreateTasksCallback callback) {
//        new CreateTasksAsyncTask(mTaskDao, tasks, callback).execute();
//    }
//
//    private static class LoadTasksAsyncTask extends AsyncTask<Void, Void, List<Task>> {
//        private TaskDao mTaskDao;
//        private TaskType mTaskType;
//        private boolean mIsCompleted;
//        private boolean mUseDateRange;
//        private Date mStartDate;
//        private Date mEndDate;
//        private LoadTasksCallback mCallback;
//
//        LoadTasksAsyncTask(TaskDao taskDao,
//                           TaskType taskType,
//                           boolean isCompleted,
//                           boolean useDateRange,
//                           Date startDate, Date endDate,
//                           LoadTasksCallback callback) {
//            mTaskDao = taskDao;
//            mTaskType = taskType;
//            mIsCompleted = isCompleted;
//            mUseDateRange = useDateRange;
//            mStartDate = startDate;
//            mEndDate = endDate;
//            mCallback = callback;
//        }
//
//        @Override
//        protected List<Task> doInBackground(Void... voids) {
//            return Arrays.asList(mTaskDao.getTasks(
//                    mTaskType.getTypeAsString(),
//                    mIsCompleted,
//                    mUseDateRange,
//                    mStartDate,
//                    mEndDate));
//        }
//
//        @Override
//        protected void onPostExecute(List<Task> tasks) {
//            super.onPostExecute(tasks);
//            mCallback.onTasksLoaded(tasks);
//        }
//    }
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
//    private static class LoadTaskAsyncTask extends AsyncTask<Long, Void, Task> {
//        private TaskDao mTaskDao;
//        private LoadTaskCallback mCallback;
//
//        LoadTaskAsyncTask(TaskDao taskDao, LoadTaskCallback callback) {
//            mTaskDao = taskDao;
//            mCallback = callback;
//        }
//
//        @Override
//        protected Task doInBackground(Long... longs) {
//            return mTaskDao.getById(longs[0]);
//        }
//
//        @Override
//        protected void onPostExecute(Task task) {
//            mCallback.onTaskLoaded(task);
//        }
//
//    }
//
//    private static class UpdateTaskAsyncTask extends AsyncTask<Task, Void, Void> {
//        private TaskDao mTaskDao;
//        private UpdateTaskCallback mCallback;
//
//        UpdateTaskAsyncTask(TaskDao taskDao, UpdateTaskCallback callback) {
//            mTaskDao = taskDao;
//            mCallback = callback;
//        }
//
//        @Override
//        protected Void doInBackground(Task... tasks) {
//            mTaskDao.updateTask(tasks[0]);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            mCallback.onTaskUpdated();
//        }
//    }
//
//    private static class DeleteTasksAsyncTask extends AsyncTask<Void, Void, Void> {
//        private TaskDao mTaskDao;
//        private DeleteTasksCallback mCallback;
//        private List<Task> mTasksToDelete;
//
//        DeleteTasksAsyncTask(TaskDao taskDao, List<Task> tasksToDelete, DeleteTasksCallback callback) {
//            mTaskDao = taskDao;
//            mCallback = callback;
//            mTasksToDelete = tasksToDelete;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            mTaskDao.deleteTasks(mTasksToDelete);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            mCallback.onTasksDeleted();
//        }
//    }
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
