package net.rusnet.taskmanager.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TasksDao {

    @Query("SELECT * FROM task_table WHERE task_id = :taskId")
    Task getById(int taskId);

    @Update
    void updateTask(Task task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("DELETE FROM task_table")
    void deleteAllTasks();

    @Query("SELECT * from task_table")
    Task[] getAllTasks();

    @Query("SELECT COUNT(*) FROM task_table WHERE (type = :taskType OR :taskType IS NULL) AND is_completed = :isCompleted")
    Integer getTasksCount(String taskType, boolean isCompleted);

}
