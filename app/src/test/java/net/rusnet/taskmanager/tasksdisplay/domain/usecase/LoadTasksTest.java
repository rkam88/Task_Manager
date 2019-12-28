package net.rusnet.taskmanager.tasksdisplay.domain.usecase;

import net.rusnet.taskmanager.commons.data.source.TaskDataSource;
import net.rusnet.taskmanager.commons.domain.model.DateType;
import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.domain.model.TaskType;
import net.rusnet.taskmanager.commons.domain.usecase.UseCaseExecutor;
import net.rusnet.taskmanager.tasksdisplay.domain.TaskFilter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoadTasksTest {

    private LoadTasks mLoadTasks;
    private List<Task> mSampleTasks;
    private Object[] mMocks;

    @Mock
    private UseCaseExecutor mUseCaseExecutor;
    @Mock
    private TaskDataSource mTaskDataSource;
    @Mock
    private TaskFilter mTaskFilter;

    @Before
    public void setUp() {
        mMocks = new Object[]{
                mUseCaseExecutor,
                mTaskDataSource,
                mTaskDataSource};

        mLoadTasks = new LoadTasks(mUseCaseExecutor, mTaskDataSource);

        mSampleTasks = getSampleTaskList();
        when(mTaskDataSource.loadTasks(mTaskFilter)).thenReturn(mSampleTasks);
    }

    @Test
    public void loadTasksTest() {

        List<Task> result = mLoadTasks.doInBackground(mTaskFilter);

        verify(mTaskDataSource).loadTasks(mTaskFilter);
        verifyNoMoreInteractions(mMocks);

        assertEquals(mSampleTasks, result);
    }

    private List<Task> getSampleTaskList() {
        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task(1, "Sample task", TaskType.INBOX, DateType.NO_DATE, null, false, null));
        return taskList;
    }

}