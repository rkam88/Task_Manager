package net.rusnet.taskmanager.commons.utils.executors;


public class TestExecutor implements AppExecutor.MainThread, AppExecutor.WorkerThread {

    @Override
    public void execute(Runnable command) {
        command.run();
    }
}