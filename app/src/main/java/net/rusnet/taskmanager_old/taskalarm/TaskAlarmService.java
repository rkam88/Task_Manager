package net.rusnet.taskmanager_old.taskalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import net.rusnet.taskmanager_old.R;
import net.rusnet.taskmanager_old.commons.domain.model.Task;
import net.rusnet.taskmanager_old.commons.domain.model.TaskType;
import net.rusnet.taskmanager_old.commons.domain.usecase.LoadTask;
import net.rusnet.taskmanager_old.commons.domain.usecase.LoadTasks;
import net.rusnet.taskmanager_old.commons.domain.usecase.UseCase;
import net.rusnet.taskmanager_old.commons.utils.Injection;
import net.rusnet.taskmanager_old.tasksdisplay.domain.TaskFilter;

import java.util.Date;
import java.util.List;

public class TaskAlarmService extends JobIntentService {

    public static final String ACTION_UPDATE_ONE = "ACTION_UPDATE_ONE";
    public static final String ACTION_UPDATE_ALL = "ACTION_UPDATE_ALL";
    public static final String ACTION_REMOVE = "ACTION_REMOVE";
    public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";
    private static final int JOB_ID = 1313;
    private static final int NO_TASK_ID = -1;
    private static final int MAX_INT_LENGTH = 1000000000;
    private static final String SPACE = " ";

    private LoadTask mLoadTask;
    private LoadTasks mLoadTasks;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, TaskAlarmService.class, JOB_ID, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLoadTask = Injection.provideLoadTaskUseCase(getApplicationContext());
        mLoadTasks = Injection.provideLoadTasksUseCase(getApplicationContext());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        switch (action) {
            case ACTION_UPDATE_ONE:
                long taskId = intent.getLongExtra(EXTRA_TASK_ID, NO_TASK_ID);
                if (taskId == NO_TASK_ID) return;
                mLoadTask.execute(taskId, new UseCase.Callback<Task>() {
                    @Override
                    public void onResult(@NonNull Task result) {
                        updateTaskAlarm(result);
                    }
                });
                break;
            case ACTION_UPDATE_ALL:
                TaskFilter filter = new TaskFilter(TaskType.ANY, null, false);
                mLoadTasks.execute(filter, new UseCase.Callback<List<Task>>() {
                    @Override
                    public void onResult(@NonNull List<Task> result) {
                        for (Task task : result) {
                            if (task.getReminderDate() != null) updateTaskAlarm(task);
                        }
                    }
                });
                break;
            case ACTION_REMOVE:
                long taskIdToRemove = intent.getLongExtra(EXTRA_TASK_ID, NO_TASK_ID);
                if (taskIdToRemove != NO_TASK_ID) removeTaskAlarms(taskIdToRemove);
                break;
        }
    }

    private void updateTaskAlarm(Task task) {
        Intent intent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);

        long taskId = task.getId();
        int pendingIntendId = (int) (taskId % MAX_INT_LENGTH);
        intent.putExtra(TaskAlarmReceiver.EXTRA_ID, pendingIntendId);

        String notificationText = getTextForNotification(task);
        intent.putExtra(TaskAlarmReceiver.EXTRA_NOTIFICATION_TEXT, notificationText);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, pendingIntendId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        if (task.getReminderDate() != null
                && task.getReminderDate().getTime() > System.currentTimeMillis()) {
            long triggerAtMillis = task.getReminderDate().getTime();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void removeTaskAlarms(long taskIdToRemove) {
        Intent intent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);

        int pendingIntendId = (int) (taskIdToRemove % MAX_INT_LENGTH);
        intent.putExtra(TaskAlarmReceiver.EXTRA_ID, pendingIntendId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, pendingIntendId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    @NonNull
    private String getTextForNotification(@NonNull Task task) {
        String dateText = "";
        switch (task.getDateType()) {
            case NO_DATE:
                dateText = getString(R.string.without_date);
                break;
            case DEADLINE:
                dateText = getString(R.string.before);
            case FIXED:
                Date date = task.getEndDate();
                if (date != null) {
                    String dateAsString = date.toString();
                    dateText = dateText + SPACE + dateAsString;
                }
                break;
        }

        String format = getString(R.string.task_alarm_task_description);

        return String.format(format, task.getName(), dateText);
    }

}
