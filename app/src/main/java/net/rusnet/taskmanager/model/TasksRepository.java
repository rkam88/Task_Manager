package net.rusnet.taskmanager.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

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
    public void loadIncompleteTasks(@NonNull final LoadTasksCallback callback, @NonNull final TaskType requestedTaskType) {
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
        protected void onPostExecute(List<Task> tasks) {
            super.onPostExecute(tasks);
            mCallback.onTasksLoaded(tasks);
        }

        @Override
        protected List<Task> doInBackground(Void... voids) {
            return Arrays.asList(mTasksDao.getAllTasks());
        }
    }
}
