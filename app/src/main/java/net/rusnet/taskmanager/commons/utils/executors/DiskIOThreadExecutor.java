package net.rusnet.taskmanager.commons.utils.executors;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DiskIOThreadExecutor implements AppExecutor.WorkerThread {

    private static DiskIOThreadExecutor INSTANCE = null;

    private final Executor mDiskIOThreadExecutor;

    private DiskIOThreadExecutor() {
        mDiskIOThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public static DiskIOThreadExecutor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DiskIOThreadExecutor();
        }
        return INSTANCE;
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mDiskIOThreadExecutor.execute(command);
    }
}
