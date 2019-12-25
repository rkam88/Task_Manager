package net.rusnet.taskmanager.tasksdisplay.domain.usecase;

import net.rusnet.taskmanager.commons.data.source.TaskDataSource;
import net.rusnet.taskmanager.commons.domain.model.DateType;
import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.domain.model.TaskType;
import net.rusnet.taskmanager.commons.utils.executors.AppExecutor;
import net.rusnet.taskmanager.commons.utils.executors.TestExecutor;
import net.rusnet.taskmanager.tasksdisplay.domain.TaskFilter;
import net.rusnet.taskmanager.tasksdisplay.presentation.TaskViewType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoadTasksTest {

    private LoadTasks mLoadTasks;
    private AppExecutor.WorkerThread mWorkerThreadExecutor;
    private AppExecutor.MainThread mMainThreadExecutor;

    @Mock
    private TaskDataSource mTaskDataSource;
    @Mock
    private LoadTasks.Callback<LoadTasks.Result> mCallback;

    @Before
    public void setUp() {
        mWorkerThreadExecutor = new TestExecutor();
        mMainThreadExecutor = new TestExecutor();

        mLoadTasks = new LoadTasks(
                mMainThreadExecutor,
                mWorkerThreadExecutor,
                mTaskDataSource);
    }

    @Test
    public void loadTasksTest() {
        ArgumentCaptor<TaskFilter> taskFilterArgumentCaptor = ArgumentCaptor.forClass(TaskFilter.class);
        ArgumentCaptor<LoadTasks.Result> resultArgumentCaptor = ArgumentCaptor.forClass(LoadTasks.Result.class);

        List<TaskViewType> taskViewTypes = new ArrayList<>();
        taskViewTypes.add(TaskViewType.INBOX);
        taskViewTypes.add(TaskViewType.TODAY);
        taskViewTypes.add(TaskViewType.THIS_WEEK);
        taskViewTypes.add(TaskViewType.ACTIVE_ALL);
        taskViewTypes.add(TaskViewType.POSTPONED);
        taskViewTypes.add(TaskViewType.COMPLETED);

        List<TaskFilter> taskFilters = new ArrayList<>();
        List<List<Task>> sampleTaskLists = new ArrayList<>();
        List<LoadTasks.RequestValues> requestValues = new ArrayList<>();

        for (int i = 0; i < taskViewTypes.size(); i++) {
            taskFilters.add(new TaskFilter(taskViewTypes.get(i)));

            sampleTaskLists.add(getSampleTaskList(i));

            requestValues.add(new LoadTasks.RequestValues(taskFilters.get(i)));

            when(mTaskDataSource.loadTasks(taskFilters.get(i))).thenReturn(sampleTaskLists.get(i));

            mLoadTasks.execute(requestValues.get(i), mCallback);

        }

        verify(mTaskDataSource, times(taskViewTypes.size())).loadTasks(taskFilterArgumentCaptor.capture());
        verifyNoMoreInteractions(mTaskDataSource);
        verify(mCallback, times(taskViewTypes.size())).onResult(resultArgumentCaptor.capture());
        verifyNoMoreInteractions(mCallback);

        for (int i = 0; i < taskViewTypes.size(); i++) {
            assertEquals(taskFilters.get(i), taskFilterArgumentCaptor.getAllValues().get(i));
            assertEquals(sampleTaskLists.get(i), resultArgumentCaptor.getAllValues().get(i).getTasks());
        }

    }

    private List<Task> getSampleTaskList(int sampleTaskListId) {
        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task(1, "Task list " + sampleTaskListId, TaskType.INBOX, DateType.NO_DATE, null, false, null));
        return taskList;
    }

}