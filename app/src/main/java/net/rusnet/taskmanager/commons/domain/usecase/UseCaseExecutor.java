package net.rusnet.taskmanager.commons.domain.usecase;

import androidx.annotation.NonNull;

import net.rusnet.taskmanager.commons.utils.executors.AppExecutor;

public class UseCaseExecutor {

    private AppExecutor mMainThreadExecutor;
    private AppExecutor mWorkerThreadExecutor;

    public UseCaseExecutor(
            @NonNull AppExecutor mainThreadExecutor,
            @NonNull AppExecutor workerThreadExecutor) {
        mMainThreadExecutor = mainThreadExecutor;
        mWorkerThreadExecutor = workerThreadExecutor;
    }

    <Q, R> void execute(
            @NonNull final UseCase<Q, R> useCase,
            @NonNull final Q requestValues,
            @NonNull final UseCase.Callback<R> callback) {
        mWorkerThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                R result = useCase.doInBackground(requestValues);
                postResult(result, callback);
            }
        });
    }

    private <R> void postResult(
            @NonNull final R result,
            @NonNull final UseCase.Callback<R> callback) {
        mMainThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onResult(result);
            }
        });
    }

}
