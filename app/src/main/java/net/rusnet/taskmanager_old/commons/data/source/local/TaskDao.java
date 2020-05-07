package net.rusnet.taskmanager_old.commons.data.source.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import net.rusnet.taskmanager_old.commons.domain.model.Task;

import java.util.Date;
import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task_table WHERE task_id = :taskId")
    Task getById(long taskId);

    @Update
    void updateTask(Task task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTask(Task task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTasks(List<Task> tasks);

    @Delete
    void deleteTask(Task task);

    @Delete
    void deleteTasks(List<Task> tasks);

    @Query("DELETE FROM task_table")
    void deleteAllTasks();

    @Query("SELECT * from task_table")
    Task[] getAllTasks();

    @Query("SELECT * " +
            "FROM task_table " +
            "WHERE (task_type = :taskType OR :taskType = 'ANY') " +
            "AND is_completed = :isCompleted " +
            "AND ((:useDateRange = 0) OR (end_date BETWEEN :dateRangeStart AND :dateRangeEnd))" +
            "ORDER BY end_date IS NULL, end_date, task_id ASC")
    Task[] getTasks(String taskType, boolean isCompleted, boolean useDateRange, Date dateRangeStart, Date dateRangeEnd);

    @Query("SELECT COUNT(*) " +
            "FROM task_table " +
            "WHERE (task_type = :taskType OR :taskType = 'ANY') " +
            "AND is_completed = :isCompleted " +
            "AND ((:useDateRange = 0) OR (end_date BETWEEN :dateRangeStart AND :dateRangeEnd))")
    Integer getTasksCount(String taskType, boolean isCompleted, boolean useDateRange, Date dateRangeStart, Date dateRangeEnd);

}
