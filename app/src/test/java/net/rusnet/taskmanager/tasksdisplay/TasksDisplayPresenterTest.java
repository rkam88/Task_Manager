package net.rusnet.taskmanager.tasksdisplay;

import net.rusnet.taskmanager.commons.domain.model.DateType;
import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.data.source.TaskDataSource;
import net.rusnet.taskmanager.commons.domain.model.TaskType;
import net.rusnet.taskmanager.tasksdisplay.presentation.TaskViewType;
import net.rusnet.taskmanager.tasksdisplay.presentation.TasksDisplayContract;
import net.rusnet.taskmanager.tasksdisplay.presentation.TasksDisplayPresenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TasksDisplayPresenterTest {

    private static final int WANTED_NUMBER_OF_INVOCATIONS = 6;
    private static final long DATE_A_WEEK_FROM_TODAY = 604800000;

    @Mock
    private TasksDisplayContract.View mTasksDisplayView;

    @Mock
    private TaskDataSource mTasksRepository;

    private TasksDisplayPresenter mTasksDisplayPresenter;

    private List<Task> mTestTasks;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        mTasksDisplayPresenter = new TasksDisplayPresenter(
                mTasksDisplayView,
                mTasksRepository);
        mTasksDisplayPresenter.setTasksViewType(TaskViewType.INBOX);
    }

    @Test
    public void setTasksViewType() throws NoSuchFieldException, IllegalAccessException {
        mTestTasks = createTestTaskList();

        //TODO: implement automatic testing for all types
//        TaskViewType type = TaskViewType.INBOX;
//        TaskViewType type = TaskViewType.ACTIVE_ALL;
//        TaskViewType type = TaskViewType.TODAY;
//        TaskViewType type = TaskViewType.THIS_WEEK;
        TaskViewType type = TaskViewType.POSTPONED;
//        TaskViewType type = TaskViewType.COMPLETED;


        TaskType taskType = TaskType.ANY;
        boolean isCompleted = false;
        boolean useDateRange = false;
        Date startDate = new Date(0);
        Date endDate = null;
        switch (type) {
            case INBOX:
                taskType = TaskType.INBOX;
                isCompleted = false;
                useDateRange = false;
                break;
            case TODAY:
                taskType = TaskType.ACTIVE;
                isCompleted = false;
                useDateRange = true;
                endDate = new Date(System.currentTimeMillis());
                break;
            case THIS_WEEK:
                taskType = TaskType.ACTIVE;
                isCompleted = false;
                useDateRange = true;
                endDate = new Date(System.currentTimeMillis() + DATE_A_WEEK_FROM_TODAY);
                break;
            case ACTIVE_ALL:
                taskType = TaskType.ACTIVE;
                isCompleted = false;
                useDateRange = false;
                break;
            case POSTPONED:
                taskType = TaskType.POSTPONED;
                isCompleted = false;
                useDateRange = false;
                break;
            case COMPLETED:
                taskType = TaskType.ANY;
                isCompleted = true;
                useDateRange = false;
                break;
        }

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                TaskDataSource.LoadTasksCallback loadTasksCallback =
                        (TaskDataSource.LoadTasksCallback) invocation.getArguments()[5];
                loadTasksCallback.onTasksLoaded(mTestTasks);
                return null;
            }
        }).when(mTasksRepository).loadTasks(
                eq(taskType),
                eq(isCompleted),
                eq(useDateRange),
                eq(startDate),
                eq(endDate),
                any(TaskDataSource.LoadTasksCallback.class));

        mTasksDisplayPresenter.setTasksViewType(type);

        Field field = TasksDisplayPresenter.class.getDeclaredField("mTaskViewType");
        field.setAccessible(true);
        Assert.assertEquals(type,
                field.get(mTasksDisplayPresenter));

        verify(mTasksRepository).loadTasks(
                eq(taskType),
                eq(isCompleted),
                eq(useDateRange),
                eq(startDate),
                eq(endDate),
                any(TaskDataSource.LoadTasksCallback.class));


        InOrder inOrder = Mockito.inOrder(mTasksDisplayView);
        inOrder.verify(mTasksDisplayView).showLoadingScreen();
        inOrder.verify(mTasksDisplayView).updateTasksViewType(type);
        inOrder.verify(mTasksDisplayView).hideLoadingScreen();
        inOrder.verify(mTasksDisplayView).updateTaskList(mTestTasks);
        inOrder.verifyNoMoreInteractions();

    }

    @Test
    public void updateAllTaskCount() {
        final int taskCount = 55;

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                TaskDataSource.LoadTasksCountCallback callback =
                        (TaskDataSource.LoadTasksCountCallback) invocation.getArguments()[2];
                callback.onTasksCountLoaded(taskCount);
                return null;
            }
        }).when(mTasksRepository).loadTasksCount(
                any(TaskType.class),
                anyBoolean(),
                any(TaskDataSource.LoadTasksCountCallback.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                TaskDataSource.LoadTasksCountCallback callback =
                        (TaskDataSource.LoadTasksCountCallback) invocation.getArguments()[5];
                callback.onTasksCountLoaded(taskCount);
                return null;
            }
        }).when(mTasksRepository).loadTasksCount(
                any(TaskType.class),
                anyBoolean(),
                anyBoolean(),
                any(Date.class),
                any(Date.class),
                any(TaskDataSource.LoadTasksCountCallback.class));

        mTasksDisplayPresenter.updateAllTaskCount();
        InOrder inOrder = Mockito.inOrder(mTasksDisplayView);
        inOrder.verify(mTasksDisplayView, times(WANTED_NUMBER_OF_INVOCATIONS)).updateTaskCount(any(TaskViewType.class), eq(String.valueOf(taskCount)));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void markTaskAsCompleted() {
        final Task task = createTestTaskList().get(0);
        final int taskCount = 55;

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                TaskDataSource.LoadTasksCountCallback callback =
                        (TaskDataSource.LoadTasksCountCallback) invocation.getArguments()[2];
                callback.onTasksCountLoaded(taskCount);
                return null;
            }
        }).when(mTasksRepository).loadTasksCount(
                any(TaskType.class),
                anyBoolean(),
                any(TaskDataSource.LoadTasksCountCallback.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                TaskDataSource.LoadTasksCountCallback callback =
                        (TaskDataSource.LoadTasksCountCallback) invocation.getArguments()[5];
                callback.onTasksCountLoaded(taskCount);
                return null;
            }
        }).when(mTasksRepository).loadTasksCount(
                any(TaskType.class),
                anyBoolean(),
                anyBoolean(),
                any(Date.class),
                any(Date.class),
                any(TaskDataSource.LoadTasksCountCallback.class));

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TaskDataSource.UpdateTaskCallback callback =
                        (TaskDataSource.UpdateTaskCallback) invocation.getArguments()[1];
                callback.onTaskUpdated();
                return null;
            }
        }).when(mTasksRepository).updateTask(
                eq(task),
                any(TaskDataSource.UpdateTaskCallback.class));

        mTasksDisplayPresenter.markTaskAsCompleted(task);

        InOrder inOrder = Mockito.inOrder(mTasksDisplayView);
        inOrder.verify(mTasksDisplayView).showLoadingScreen();
        inOrder.verify(mTasksDisplayView).removeTaskAlarms(task.getId());
        inOrder.verify(mTasksDisplayView).hideLoadingScreen();
        inOrder.verify(mTasksDisplayView, times(WANTED_NUMBER_OF_INVOCATIONS)).updateTaskCount(any(TaskViewType.class), eq(String.valueOf(taskCount)));
        inOrder.verifyNoMoreInteractions();

        verify(mTasksRepository).updateTask(eq(task), any(TaskDataSource.UpdateTaskCallback.class));

    }

    @Test
    public void createFirstLaunchTasks() {
        final List<Task> tasks = createTestTaskList();

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TaskDataSource.CreateTasksCallback callback =
                        (TaskDataSource.CreateTasksCallback) invocation.getArguments()[1];
                callback.onTasksCreated();
                return null;
            }
        }).when(mTasksRepository).createTasks(
                eq(tasks),
                any(TaskDataSource.CreateTasksCallback.class));

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TaskDataSource.LoadTasksCallback callback =
                        (TaskDataSource.LoadTasksCallback) invocation.getArguments()[2];
                callback.onTasksLoaded(tasks);
                return null;
            }
        }).when(mTasksRepository).loadTasks(
                eq(TaskType.ANY),
                eq(false),
                any(TaskDataSource.LoadTasksCallback.class));

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                TaskDataSource.LoadTasksCountCallback callback =
                        (TaskDataSource.LoadTasksCountCallback) invocation.getArguments()[2];
                callback.onTasksCountLoaded(tasks.size());
                return null;
            }
        }).when(mTasksRepository).loadTasksCount(
                any(TaskType.class),
                anyBoolean(),
                any(TaskDataSource.LoadTasksCountCallback.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                TaskDataSource.LoadTasksCountCallback callback =
                        (TaskDataSource.LoadTasksCountCallback) invocation.getArguments()[5];
                callback.onTasksCountLoaded(tasks.size());
                return null;
            }
        }).when(mTasksRepository).loadTasksCount(
                any(TaskType.class),
                anyBoolean(),
                anyBoolean(),
                any(Date.class),
                any(Date.class),
                any(TaskDataSource.LoadTasksCountCallback.class));

        mTasksDisplayPresenter.createFirstLaunchTasks(tasks);

        verify(mTasksDisplayView, atLeastOnce()).showLoadingScreen();
        verify(mTasksRepository).createTasks(eq(tasks), any(TaskDataSource.CreateTasksCallback.class));
        verify(mTasksRepository).loadTasks(eq(TaskType.ANY), eq(false), any(TaskDataSource.LoadTasksCallback.class));
        verify(mTasksDisplayView, atLeastOnce()).updateTaskAlarm(anyLong());
        verify(mTasksDisplayView, times(WANTED_NUMBER_OF_INVOCATIONS)).updateTaskCount(any(TaskViewType.class), eq(String.valueOf(tasks.size())));

    }

    private List<Task> createTestTaskList() {
        List<Task> tasks = new ArrayList<>();

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        Calendar inTenMinutes = Calendar.getInstance();
        inTenMinutes.add(Calendar.MINUTE, 10);

        tasks.add(new Task(1, "1", TaskType.INBOX, DateType.NO_DATE, null, false, null));
        tasks.add(new Task(2, "2", TaskType.INBOX, DateType.NO_DATE, null, false, null));
        tasks.add(new Task(3, "3", TaskType.INBOX, DateType.NO_DATE, null, false, null));
        tasks.add(new Task(4, "4", TaskType.POSTPONED, DateType.FIXED, new Date(yesterday.getTimeInMillis()), false, null));
        tasks.add(new Task(5, "5", TaskType.POSTPONED, DateType.FIXED, new Date(tomorrow.getTimeInMillis()), false, null));
        tasks.add(new Task(6, "6", TaskType.POSTPONED, DateType.DEADLINE, new Date(tomorrow.getTimeInMillis()), false, null));
        tasks.add(new Task(7, "7", TaskType.POSTPONED, DateType.NO_DATE, null, false, new Date(inTenMinutes.getTimeInMillis())));
        tasks.add(new Task(8, "8", TaskType.POSTPONED, DateType.NO_DATE, null, false, null));

        return tasks;
    }
}