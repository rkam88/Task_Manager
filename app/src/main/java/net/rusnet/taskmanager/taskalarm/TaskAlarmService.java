package net.rusnet.taskmanager.taskalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import net.rusnet.taskmanager.R;
import net.rusnet.taskmanager.commons.data.source.TaskDataSource;
import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.utils.Injection;

import java.util.Date;

public class TaskAlarmService extends JobIntentService {

    public static final String ACTION_UPDATE_ONE = "ACTION_UPDATE_ONE";
    public static final String ACTION_UPDATE_ALL = "ACTION_UPDATE_ALL";
    public static final String ACTION_REMOVE = "ACTION_REMOVE";
    public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";
    private static final int JOB_ID = 1313;
    private static final int NO_TASK_ID = -1;
    private static final int MAX_INT_LENGTH = 1000000000;
    private static final String SPACE = " ";

    private TaskDataSource mTasksRepository;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, TaskAlarmService.class, JOB_ID, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mTasksRepository = Injection.provideTasksRepository(getApplicationContext());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
//        String action = intent.getAction();
//        if (action == null) return;
//        switch (action) {
//            case ACTION_UPDATE_ONE:
//                long taskId = intent.getLongExtra(EXTRA_TASK_ID, NO_TASK_ID);
//                if (taskId == NO_TASK_ID) return;
//                mTasksRepository.loadTask(taskId, new TaskDataSource.LoadTaskCallback() {
//                    @Override
//                    public void onTaskLoaded(Task task) {
//                        updateTaskAlarm(task);
//                    }
//                });
//                break;
//            case ACTION_UPDATE_ALL:
//                mTasksRepository.loadTasks(TaskType.ANY, false, new TaskDataSource.LoadTasksCallback() {
//                    @Override
//                    public void onTasksLoaded(List<Task> tasks) {
//                        for (Task task : tasks) {
//                            if (task.getReminderDate() != null) updateTaskAlarm(task);
//                        }
//                    }
//                });
//                break;
//            case ACTION_REMOVE:
//                long taskIdToRemove = intent.getLongExtra(EXTRA_TASK_ID, NO_TASK_ID);
//                if (taskIdToRemove != NO_TASK_ID) removeTaskAlarms(taskIdToRemove);
//                break;
//        }
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
