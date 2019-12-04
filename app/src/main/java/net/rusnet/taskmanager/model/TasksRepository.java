package net.rusnet.taskmanager.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
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
    public void loadIncompleteTasks(@NonNull final TaskType requestedTaskType, @NonNull final LoadTasksCallback callback) {
        loadAllTasksFromDB(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                List<Task> tasksToShow = new ArrayList<>();
                for (Task task : tasks) {
                    if (task.getType().equals(requestedTaskType.getType()) && !task.isCompleted())
                        tasksToShow.add(task);
                }
                callback.onTasksLoaded(tasksToShow);
            }
        });

        new loadTasksAsyncTask(mTasksDao, new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {


            }
        }).execute();
    }

    @Override
    public void loadCompleteTasks(@NonNull final LoadTasksCallback callback) {
        loadAllTasksFromDB(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                List<Task> tasksToShow = new ArrayList<>();
                for (Task task : tasks) {
                    if (task.isCompleted()) tasksToShow.add(task);
                }
                callback.onTasksLoaded(tasksToShow);
            }
        });
    }

    @Override
    public void loadTasksCount(@Nullable TaskType taskType, boolean isCompleted, @NonNull LoadTasksCountCallback callback) {
        new loadTasksCount(mTasksDao, taskType, isCompleted, callback).execute();
    }

    private void loadAllTasksFromDB(@NonNull final LoadTasksCallback callback) {
        new loadTasksAsyncTask(mTasksDao, new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                mAllTasksList = new ArrayList<>(tasks);
                callback.onTasksLoaded(tasks);
            }
        }).execute();
    }

    private static class loadTasksAsyncTask extends AsyncTask<Void, Void, List<Task>> {
        private TasksDao mTasksDao;
        private LoadTasksCallback mCallback;

        public loadTasksAsyncTask(TasksDao tasksDao, LoadTasksCallback callback) {
            mTasksDao = tasksDao;
            mCallback = callback;
        }

        @Override
        protected List<Task> doInBackground(Void... voids) {
            return Arrays.asList(mTasksDao.getAllTasks());
        }

        @Override
        protected void onPostExecute(List<Task> tasks) {
            super.onPostExecute(tasks);
            mCallback.onTasksLoaded(tasks);
        }
    }

    private static class loadTasksCount extends AsyncTask<Void, Void, Integer> {
        private TasksDao mTaskDao;
        private LoadTasksCountCallback mCallback;
        private TaskType mTaskType;
        private boolean mIsCompleted;

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
