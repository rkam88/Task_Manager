package net.rusnet.taskmanager.taskalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InitAlarmBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent workIntent = new Intent(TaskAlarmService.ACTION_UPDATE_ALL);
            TaskAlarmService.enqueueWork(context, workIntent);
        }
    }
}