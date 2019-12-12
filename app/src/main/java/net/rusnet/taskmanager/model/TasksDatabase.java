package net.rusnet.taskmanager.model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Task.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class TasksDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    private static TasksDatabase INSTANCE;

    private static final String NAME = "tasks.db";
    private static final Object LOCK = new Object();

    public static TasksDatabase getDatabase(Context context) {
        synchronized (LOCK) {
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

        private final TaskDao dao;

        PopulateDbAsync(TasksDatabase db) {
            dao = db.taskDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            if (dao.getAllTasks().length < 1) {
                dao.insertTask(new Task("INBOX incomplete 1", TaskType.INBOX, DateType.NO_DATE, null));
                dao.insertTask(new Task("ACTIVE 1", TaskType.ACTIVE, DateType.NO_DATE, null));
                dao.insertTask(new Task("ACTIVE 2", TaskType.ACTIVE, DateType.FIXED, Date.today()));
                dao.insertTask(new Task("ACTIVE 3", TaskType.ACTIVE, DateType.FIXED, Date.today()));
                dao.insertTask(new Task("ACTIVE 4", TaskType.ACTIVE, DateType.DEADLINE, Date.aWeekFromToday()));
                for (int i = 1; i <= 100; i++) {
                    dao.insertTask(new Task("POSTPONED incomplete " + i, TaskType.POSTPONED, DateType.NO_DATE, null));
                }
            }
            return null;
        }
    }
}
