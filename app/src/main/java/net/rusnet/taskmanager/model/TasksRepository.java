package net.rusnet.taskmanager.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class TasksRepository implements TaskDataSource {

    private static TasksRepository INSTANCE = null;

    private TaskDao mTaskDao;

    private TasksRepository(Application application) {
        TasksDatabase db = TasksDatabase.getDatabase(application);
        mTaskDao = db.taskDao();
    }

    public static TasksRepository getRepository(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(application);
        }
        return INSTANCE;
    }

    @Override
    public void loadTasks(@Nullable TaskType taskType, boolean isCompleted, @NonNull LoadTasksCallback callback) {
        new LoadTasksAsyncTask(mTaskDao, taskType, isCompleted, callback).execute();
    }

    @Override
    public void loadTasksCount(@Nullable TaskType taskType, boolean isCompleted, @NonNull LoadTasksCountCallback callback) {
        new LoadTasksCountAsyncTask(mTaskDao, taskType, isCompleted, callback).execute();
    }

    @Override
    public void createNewTask(@NonNull Task task, @NonNull CreateNewTaskCallback callback) {
        new CreateNewTaskAsyncTask(mTaskDao, callback).execute(task);
    }

    private static class LoadTasksAsyncTask extends AsyncTask<Void, Void, List<Task>> {
        private TaskDao mTaskDao;
        private TaskType mTaskType;
        private boolean mIsCompleted;
        private LoadTasksCallback mCallback;

        LoadTasksAsyncTask(TaskDao taskDao, TaskType taskType, boolean isCompleted, LoadTasksCallback callback) {
            mTaskDao = taskDao;
            mTaskType = taskType;
            mIsCompleted = isCompleted;
            mCallback = callback;
        }

        @Override
        protected List<Task> doInBackground(Void... voids) {
            return Arrays.asList(mTaskDao.getTasks(mTaskType.getType(), mIsCompleted));
        }

        @Override
        protected void onPostExecute(List<Task> tasks) {
            super.onPostExecute(tasks);
            mCallback.onTasksLoaded(tasks);
        }
    }

    private static class LoadTasksCountAsyncTask extends AsyncTask<Void, Void, Integer> {
        private TaskDao mTaskDao;
        private TaskType mTaskType;
        private boolean mIsCompleted;
        private LoadTasksCountCallback mCallback;

        LoadTasksCountAsyncTask(TaskDao taskDao, TaskType taskType, boolean isCompleted, LoadTasksCountCallback callback) {
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

    private static class CreateNewTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao mTaskDao;
        private CreateNewTaskCallback mCallback;

        CreateNewTaskAsyncTask(TaskDao taskDao, CreateNewTaskCallback callback) {
            mTaskDao = taskDao;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            mTaskDao.insertTask(tasks[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mCallback.onTaskCreated();
        }
    }

}
