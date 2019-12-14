package net.rusnet.taskmanager.taskalarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import net.rusnet.taskmanager.R;

public class TaskAlarmReceiver extends BroadcastReceiver {

    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_NOTIFICATION_TEXT = "EXTRA_NOTIFICATION_TEXT";

    private static final String CHANEL_ID = "Channel_1";
    private static final int NO_VALUE = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            createNotificationChannel(context);

            int id = intent.getIntExtra(EXTRA_ID, NO_VALUE);
            String notificationText = intent.getStringExtra(EXTRA_NOTIFICATION_TEXT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    context,
                    CHANEL_ID
            );

            builder
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(context.getString(R.string.reminder))
                    .setContentText(notificationText)
                    .setAutoCancel(true);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null)
                notificationManager.notify(id, builder.build());
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANEL_ID,
                    context.getString(R.string.task_alarm_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(context.getString(R.string.task_alarm_channel_description));
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
