package net.rusnet.taskmanager.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class TasksRepository implements TaskDataSource {

    private static TasksRepository INSTANCE = null;

    private TasksDao mTasksDao;
    private List<Task> mAllTasksList;

    private TasksRepository(Application application) {
        TasksDatabase db = TasksDatabase.getDatabase(application);
        mTasksDao = db.taskDao();
    }

    public static TasksRepository getRepository(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(application);
        }
        return INSTANCE;
    }

    @Override
    public void loadTasks(@Nullable TaskType taskType, boolean isCompleted, @NonNull LoadTasksCallback callback) {
        new loadTasksAsyncTask(mTasksDao, taskType, isCompleted, callback).execute();
    }

    @Override
    public void loadTasksCount(@Nullable TaskType taskType, boolean isCompleted, @NonNull LoadTasksCountCallback callback) {
        new loadTasksCount(mTasksDao, taskType, isCompleted, callback).execute();
    }

    private static class loadTasksAsyncTask extends AsyncTask<Void, Void, List<Task>> {
        private TasksDao mTasksDao;
        private TaskType mTaskType;
        private boolean mIsCompleted;
        private LoadTasksCallback mCallback;

        public loadTasksAsyncTask(TasksDao tasksDao, TaskType taskType, boolean isCompleted, LoadTasksCallback callback) {
            mTasksDao = tasksDao;
            mTaskType = taskType;
            mIsCompleted = isCompleted;
            mCallback = callback;
        }

        @Override
        protected List<Task> doInBackground(Void... voids) {
            return Arrays.asList(mTasksDao.getTasks(mTaskType.getType(), mIsCompleted));
        }

        @Override
        protected void onPostExecute(List<Task> tasks) {
            super.onPostExecute(tasks);
            mCallback.onTasksLoaded(tasks);
        }
    }

    private static class loadTasksCount extends AsyncTask<Void, Void, Integer> {
        private TasksDao mTaskDao;
        private TaskType mTaskType;
        private boolean mIsCompleted;
        private LoadTasksCountCallback mCallback;

        public loadTasksCount(TasksDao taskDao, TaskType taskType, boolean isCompleted, LoadTasksCountCallback callback) {
            mTaskDao = taskDao;
            mTaskType = taskType;
            mIsCompleted = isCompleted;
            mCallback = callback;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return mTaskDao.getTasksCount(
                    mTaskType.getType(),
                    mIsCompleted
            );
        }

        @Override
        protected void onPostExecute(Integer tasksCount) {
            mCallback.onTasksCountLoaded(tasksCount);
        }
    }

}
