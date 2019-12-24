package net.rusnet.taskmanager.commons.data.source.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import net.rusnet.taskmanager.commons.domain.model.Task;

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
                        .build();
            }
            return INSTANCE;
        }
    }
}
