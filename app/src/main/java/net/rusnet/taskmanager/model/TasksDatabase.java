package net.rusnet.taskmanager.model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Task.class}, version = 1)
public abstract class TasksDatabase extends RoomDatabase {

    public abstract TasksDao taskDao();

    private static TasksDatabase INSTANCE;

    private static final String NAME = "tasks.db";
    private static final Object sLock = new Object();

    public static TasksDatabase getDatabase(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        TasksDatabase.class,
                        TasksDatabase.NAME)
                        .fallbackToDestructiveMigration() //TODO: remove this and add a migration strategy
                        .addCallback(sInitialDBPopulationCallback) //TODO: replace this for tutorial tasks on first launch
                        .build();
            }
            return INSTANCE;
        }
    }

    private static RoomDatabase.Callback sInitialDBPopulationCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final TasksDao dao;

        PopulateDbAsync(TasksDatabase db) {
            dao = db.taskDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            if (dao.getAllTasks().length < 1) {
                dao.insertTask(new Task("INBOX incomplete 1", TaskType.INBOX.getType(), false));
                dao.insertTask(new Task("ACTIVE incomplete 1", TaskType.ACTIVE.getType(), false));
                dao.insertTask(new Task("ACTIVE incomplete 2", TaskType.ACTIVE.getType(), false));
                for (int i = 1; i <= 100; i++) {
                    dao.insertTask(new Task("POSTPONED incomplete " + i, TaskType.POSTPONED.getType(), false));
                }
                dao.insertTask(new Task("INBOX complete 1", TaskType.INBOX.getType(), true));
                dao.insertTask(new Task("INBOX complete 2", TaskType.INBOX.getType(), true));
                dao.insertTask(new Task("ACTIVE complete 1", TaskType.INBOX.getType(), true));
                dao.insertTask(new Task("POSTPONED complete 1", TaskType.INBOX.getType(), true));
            }
            return null;
        }
    }
}
