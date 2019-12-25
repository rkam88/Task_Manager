package net.rusnet.taskmanager.commons.domain.usecase;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.utils.executors.AppExecutor;

public abstract class UseCase<Q extends UseCase.RequestValues, R extends UseCase.Result> {

    private Callback<R> mCallback;

    private AppExecutor.WorkerThread mWorkerThreadExecutor;
    private AppExecutor.MainThread mMainThreadExecutor;

    public UseCase(@NonNull AppExecutor.MainThread mainThreadExecutor,
                   @NonNull AppExecutor.WorkerThread workerThreadExecutor) {
        mMainThreadExecutor = mainThreadExecutor;
        mWorkerThreadExecutor = workerThreadExecutor;
    }

    private void postResult(@NonNull final R result) {
        mMainThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mCallback.onResult(result);
            }
        });
    }

    public void execute(@NonNull final Q requestValues, @NonNull Callback<R> callback) {
        mCallback = callback;
        mWorkerThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                R result = doInBackground(requestValues);
                postResult(result);
            }
        });
    }

    @NonNull
    protected abstract R doInBackground(@NonNull Q requestValues);

    public interface RequestValues {
    }

    public interface Result {
    }

    public interface Callback<R> {
        void onResult(@NonNull R result);
    }
}
