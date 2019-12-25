package net.rusnet.taskmanager.commons.utils.executors;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

public class MainThreadExecutor implements AppExecutor.MainThread {

    private static MainThreadExecutor INSTANCE = null;

    private final Handler mHandler;

    private MainThreadExecutor() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static MainThreadExecutor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainThreadExecutor();
        }
        return INSTANCE;
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mHandler.post(command);
    }

}
