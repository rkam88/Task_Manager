package net.rusnet.taskmanager.commons.domain.usecase;

import androidx.annotation.NonNull;

public abstract class UseCase<Q, R> {

    private UseCaseExecutor mUseCaseExecutor;

    public UseCase(@NonNull UseCaseExecutor useCaseExecutor) {
        mUseCaseExecutor = useCaseExecutor;
    }

    public void execute(@NonNull final Q requestValues, @NonNull Callback<R> callback) {
        mUseCaseExecutor.execute(
                this,
                requestValues,
                callback);
    }

    @NonNull
    protected abstract R doInBackground(@NonNull Q requestValues);

    public interface Callback<R> {
        void onResult(@NonNull R result);
    }
}
