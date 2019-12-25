package net.rusnet.taskmanager.commons.data.source.local;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import net.rusnet.taskmanager.commons.domain.model.DateType;
import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.domain.model.TaskType;

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
                        .fallbackToDestructiveMigration()
                        .addCallback(sInitialDBPopulationCallback) //todo: remove this
                        .build();
            }
            return INSTANCE;
        }
    }

    //TODO: replace this with Tutorial tasks
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
                dao.insertTask(new Task("Not completed Inbox example task 1", TaskType.INBOX, DateType.NO_DATE, null, null));
                dao.insertTask(new Task("Not completed Inbox example task 2", TaskType.INBOX, DateType.NO_DATE, null, null));
                dao.insertTask(new Task("Active example task", TaskType.ACTIVE, DateType.NO_DATE, null, null));
                dao.insertTask(new Task("Postponed example task", TaskType.POSTPONED, DateType.NO_DATE, null, null));
            }
            return null;
        }
    }
}
