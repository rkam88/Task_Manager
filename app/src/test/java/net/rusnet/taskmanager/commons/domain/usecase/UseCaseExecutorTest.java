package net.rusnet.taskmanager.commons.domain.usecase;

import net.rusnet.taskmanager.commons.utils.executors.AppExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UseCaseExecutorTest<Q, R> {

    private UseCaseExecutor mUseCaseExecutor;
    private Object[] mMocks;

    @Mock
    private AppExecutor.MainThread mMainThreadExecutor;
    @Mock
    private AppExecutor.WorkerThread mWorkerThreadExecutor;
    @Mock
    private UseCase<Q, R> mUseCase;
    @Mock
    private UseCase.Callback<R> mCallback;
    @Mock
    private Q mRequestValues;
    @Mock
    private R mResult;

    @Before
    public void setUp() {
        mMocks = new Object[]{
                mMainThreadExecutor,
                mWorkerThreadExecutor,
                mUseCase,
                mCallback,
                mRequestValues,
                mResult};

        mUseCaseExecutor = new UseCaseExecutor(mMainThreadExecutor, mWorkerThreadExecutor);

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }
        }).when(mMainThreadExecutor).execute(any(Runnable.class));

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }
        }).when(mWorkerThreadExecutor).execute(any(Runnable.class));

        when(mUseCase.doInBackground(mRequestValues)).thenReturn(mResult);
    }

    @Test
    public void execute() {
        mUseCaseExecutor.execute(mUseCase, mRequestValues, mCallback);

        InOrder inOrder = inOrder(mUseCase,
                mCallback,
                mWorkerThreadExecutor,
                mMainThreadExecutor);
        inOrder.verify(mWorkerThreadExecutor).execute(any(Runnable.class));
        inOrder.verify(mUseCase).doInBackground(mRequestValues);
        inOrder.verify(mMainThreadExecutor).execute(any(Runnable.class));
        inOrder.verify(mCallback).onResult(mResult);

        verifyNoMoreInteractions(mMocks);
    }
}