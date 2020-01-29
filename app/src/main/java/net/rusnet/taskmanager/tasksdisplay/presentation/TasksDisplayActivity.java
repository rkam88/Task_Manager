package net.rusnet.taskmanager.tasksdisplay.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import net.rusnet.taskmanager.R;
import net.rusnet.taskmanager.commons.domain.model.DateType;
import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.domain.model.TaskType;
import net.rusnet.taskmanager.commons.presentation.ConfirmationDialogFragment;
import net.rusnet.taskmanager.commons.utils.Injection;
import net.rusnet.taskmanager.edittask.presentation.EditTaskActivity;
import net.rusnet.taskmanager.taskalarm.TaskAlarmService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TasksDisplayActivity extends AppCompatActivity
        implements TasksDisplayContract.View,
        ConfirmationDialogFragment.ConfirmationDialogListener {

    private static final String CONFIRMATION_DIALOG_TAG = "CONFIRMATION_DIALOG_TAG";
    private static final String KEY_TASK_VIEW_TYPE = "KEY_TASK_VIEW_TYPE";
    private static final String KEY_SELECTED_TASKS_POSITIONS_LIST = "KEY_SELECTED_TASKS_POSITIONS_LIST";
    private static final String KEY_LAYOUT_MANAGER_STATE = "KEY_LAYOUT_MANAGER_STATE";
    private static final String PREFERENCES_NAME = "Settings";
    private static final String KEY_FIRST_LAUNCH = "firstLaunch";
    private static final TaskViewType DEFAULT_TASK_VIEW_TYPE = TaskViewType.INBOX;
    private static final int REQUEST_CODE_ADD_NEW_TASK = 1;
    private static final int REQUEST_CODE_EDIT_TASK = 2;
    private static final int NO_DRAG_DIRS = 0;
    private static final int ZERO = 0;

    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationViewDrawer;
    private Map<TaskViewType, TextView> mTaskCountMap = new HashMap<>();

    private RecyclerView mTasksRecyclerView;
    private TasksAdapter mTasksAdapter;

    private TasksDisplayContract.Presenter mTaskDisplayPresenter;
    private TaskViewType mTaskViewType;

    private FloatingActionButton mAddTaskFAB;

    private ActionMode.Callback mSelectTasksActionModeCallback;
    private ActionMode mCurrentActionMode;
    private Set<Integer> mSelectedTasksPositions;

    private Parcelable mLayoutManagerSavedState;

    private View mLoadingFrameLayout;

    @Override
    public void updateTasksViewType(@NonNull TaskViewType type) {
        mTaskViewType = type;
        setTitle(type.getTitle());
    }

    @Override
    public void updateTaskList(@Nullable List<Task> taskList) {
        mTasksAdapter.setTasks(taskList);
        mTasksAdapter.notifyDataSetChanged();

        if (mLayoutManagerSavedState != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) mTasksRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.onRestoreInstanceState(mLayoutManagerSavedState);
                mLayoutManagerSavedState = null;
            }
        }
    }

    @Override
    public void updateTaskCount(@NonNull TaskViewType type, @NonNull String newCount) {
        //noinspection ConstantConditions
        mTaskCountMap.get(type).setText(newCount);
    }

    @Override
    public void removeTaskAlarms(long taskId) {
        Intent workIntent = new Intent(TaskAlarmService.ACTION_REMOVE);
        workIntent.putExtra(TaskAlarmService.EXTRA_TASK_ID, taskId);
        TaskAlarmService.enqueueWork(this, workIntent);
    }

    @Override
    public void showLoadingScreen() {
        mLoadingFrameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingScreen() {
        mLoadingFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void updateTaskAlarm(long taskId) {
        Intent workIntent = new Intent(TaskAlarmService.ACTION_UPDATE_ONE);
        workIntent.putExtra(TaskAlarmService.EXTRA_TASK_ID, taskId);
        TaskAlarmService.enqueueWork(this, workIntent);
    }

    @Override
    public void onPositiveResponse() {
        List<Task> tasksToDelete = new ArrayList<>();
        for (Integer position : mSelectedTasksPositions) {
            tasksToDelete.add(mTasksAdapter.getTaskAtPosition(position));
        }
        mTaskDisplayPresenter.deleteTasks(tasksToDelete);
        mCurrentActionMode.finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_display);

        initToolbar();
        initNavigationDrawer();
        initLoadingScreen();
        initRecycler();
        initPresenter(savedInstanceState);
        initFAB();
        initContextualMenu(savedInstanceState);

        setupOnFirstLaunch();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_NEW_TASK:
                case REQUEST_CODE_EDIT_TASK:
                    mTaskDisplayPresenter.setTasksViewType(mTaskViewType);
                    mTaskDisplayPresenter.updateAllTaskCount();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(KEY_TASK_VIEW_TYPE, mTaskViewType);

        if (mCurrentActionMode != null) {
            ArrayList<Integer> positionsList = new ArrayList<>(mSelectedTasksPositions);
            outState.putIntegerArrayList(KEY_SELECTED_TASKS_POSITIONS_LIST, positionsList);
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) mTasksRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            Parcelable state = layoutManager.onSaveInstanceState();
            outState.putParcelable(KEY_LAYOUT_MANAGER_STATE, state);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mTasksRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            mLayoutManagerSavedState = savedInstanceState.getParcelable(KEY_LAYOUT_MANAGER_STATE);
            layoutManager.onRestoreInstanceState(mLayoutManagerSavedState);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white);
        setSupportActionBar(mToolbar);
    }

    private void initNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationViewDrawer = findViewById(R.id.navigationViewDrawer);

        mNavigationViewDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        mTaskCountMap.put(TaskViewType.INBOX, getTextViewCounter(R.id.nav_inbox));
        mTaskCountMap.put(TaskViewType.TODAY, getTextViewCounter(R.id.nav_active_today));
        mTaskCountMap.put(TaskViewType.THIS_WEEK, getTextViewCounter(R.id.nav_active_this_week));
        mTaskCountMap.put(TaskViewType.ACTIVE_ALL, getTextViewCounter(R.id.nav_active_all));
        mTaskCountMap.put(TaskViewType.POSTPONED, getTextViewCounter(R.id.nav_postponed));
        mTaskCountMap.put(TaskViewType.COMPLETED, getTextViewCounter(R.id.nav_completed));
    }

    private void selectDrawerItem(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_inbox:
                mTaskDisplayPresenter.setTasksViewType(TaskViewType.INBOX);
                break;
            case R.id.nav_active_today:
                mTaskDisplayPresenter.setTasksViewType(TaskViewType.TODAY);
                break;
            case R.id.nav_active_this_week:
                mTaskDisplayPresenter.setTasksViewType(TaskViewType.THIS_WEEK);
                break;
            case R.id.nav_active_all:
                mTaskDisplayPresenter.setTasksViewType(TaskViewType.ACTIVE_ALL);
                break;
            case R.id.nav_postponed:
                mTaskDisplayPresenter.setTasksViewType(TaskViewType.POSTPONED);
                break;
            case R.id.nav_completed:
                mTaskDisplayPresenter.setTasksViewType(TaskViewType.COMPLETED);
                break;
        }

        mDrawerLayout.closeDrawers();
    }

    @NonNull
    private TextView getTextViewCounter(@IdRes int menuItemId) {
        Menu menu = mNavigationViewDrawer.getMenu();
        MenuItem menuItem = menu.findItem(menuItemId);
        View actionView = menuItem.getActionView();
        return actionView.findViewById(R.id.text_view_task_count);
    }

    private void initLoadingScreen() {
        mLoadingFrameLayout = findViewById(R.id.loading_frame_layout);
    }

    private void initRecycler() {
        mTasksAdapter = new TasksAdapter(null);
        mTasksAdapter.setOnItemClickListener(new TasksAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                if (mCurrentActionMode == null) {
                    Intent intent = new Intent(
                            TasksDisplayActivity.this,
                            EditTaskActivity.class
                    );
                    long taskId = mTasksAdapter.getTaskAtPosition(position).getId();
                    intent.putExtra(EditTaskActivity.EXTRA_IS_TASK_NEW, false);
                    intent.putExtra(EditTaskActivity.EXTRA_TASK_ID, taskId);
                    startActivityForResult(intent, REQUEST_CODE_EDIT_TASK);
                } else {
                    updateRecyclerViewSelection(position);
                }
            }
        });
        mTasksAdapter.setOnItemLongClickListener(new TasksAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                if (mCurrentActionMode == null) {
                    mCurrentActionMode = startSupportActionMode(mSelectTasksActionModeCallback);
                    updateRecyclerViewSelection(position);
                }
            }
        });

        mTasksRecyclerView = findViewById(R.id.recycler_view_tasks);
        mTasksRecyclerView.setAdapter(mTasksAdapter);

        addSwipeToCompleteTaskCallback();
    }

    private void updateRecyclerViewSelection(int position) {
        if (mSelectedTasksPositions.contains(position)) {
            mSelectedTasksPositions.remove(position);
        } else {
            mSelectedTasksPositions.add(position);
        }

        mTasksAdapter.setSelectedTasksPositions(mSelectedTasksPositions);
        mTasksAdapter.notifyItemChanged(position);

        if (mSelectedTasksPositions.size() == ZERO) {
            mCurrentActionMode.finish();
        } else {
            updateContextualToolbarTitle();
        }
    }

    private void updateContextualToolbarTitle() {
        String title = getResources().getQuantityString(
                R.plurals.n_tasks_selected,
                mSelectedTasksPositions.size(),
                mSelectedTasksPositions.size());
        mCurrentActionMode.setTitle(title);
    }

    private void addSwipeToCompleteTaskCallback() {
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        NO_DRAG_DIRS,
                        ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public boolean isItemViewSwipeEnabled() {
                        return (mTaskViewType != TaskViewType.COMPLETED && mCurrentActionMode == null);
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Task task = mTasksAdapter.getTaskAtPosition(position);
                        mTaskDisplayPresenter.markTaskAsCompleted(task);
                    }

                    @Override
                    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                        final View foregroundView = ((TasksAdapter.ViewHolder) viewHolder).mForegroundView;
                        getDefaultUIUtil().clearView(foregroundView);
                    }

                    @Override
                    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                        if (viewHolder != null) {
                            final View foregroundView = ((TasksAdapter.ViewHolder) viewHolder).mForegroundView;
                            getDefaultUIUtil().onSelected(foregroundView);
                        }
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                            int actionState, boolean isCurrentlyActive) {
                        final View foregroundView = ((TasksAdapter.ViewHolder) viewHolder).mForegroundView;
                        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                                actionState, isCurrentlyActive);
                    }

                    @Override
                    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                                int actionState, boolean isCurrentlyActive) {
                        final View foregroundView = ((TasksAdapter.ViewHolder) viewHolder).mForegroundView;
                        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                                actionState, isCurrentlyActive);
                    }

                });

        helper.attachToRecyclerView(mTasksRecyclerView);
    }

    private void initPresenter(@Nullable Bundle savedInstanceState) {
        TaskViewType type = null;
        if (savedInstanceState != null) {
            type = (TaskViewType) savedInstanceState.getSerializable(KEY_TASK_VIEW_TYPE);
        }
        type = (type == null) ? DEFAULT_TASK_VIEW_TYPE : type;

        mTaskDisplayPresenter = new TasksDisplayPresenter(
                this,
                Injection.provideLoadTasksUseCase(getApplicationContext()),
                Injection.provideGetTaskCountUseCase(getApplicationContext()),
                Injection.provideDeleteTasksUseCase(getApplicationContext()));

        mTaskDisplayPresenter.setTasksViewType(type);
        mTaskDisplayPresenter.updateAllTaskCount();
    }

    private void initFAB() {
        mAddTaskFAB = findViewById(R.id.fab_add_task);
        mAddTaskFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        TasksDisplayActivity.this,
                        EditTaskActivity.class
                );
                intent.putExtra(EditTaskActivity.EXTRA_IS_TASK_NEW, true);
                startActivityForResult(intent, REQUEST_CODE_ADD_NEW_TASK);
            }
        });
    }

    private void initContextualMenu(@Nullable Bundle savedInstanceState) {
        mSelectTasksActionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.tasks_display_contextual_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mAddTaskFAB.hide();

                mSelectedTasksPositions = new HashSet<>();
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        String dialogText;
                        if (mSelectedTasksPositions.size() == 1) {
                            dialogText = getString(R.string.delete_selected_task);
                        } else {
                            dialogText = getString(R.string.delete_selected_tasks);
                        }
                        ConfirmationDialogFragment newFragment;
                        newFragment = ConfirmationDialogFragment.newInstance(dialogText);
                        newFragment.show(getSupportFragmentManager(), CONFIRMATION_DIALOG_TAG);
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mTasksAdapter.setSelectedTasksPositions(new HashSet<Integer>());
                for (Integer selectedTask : mSelectedTasksPositions) {
                    mTasksAdapter.notifyItemChanged(selectedTask);
                }
                mSelectedTasksPositions = new HashSet<>();
                mCurrentActionMode = null;

                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mAddTaskFAB.show();
            }
        };

        if (savedInstanceState != null) {
            ArrayList<Integer> positionsList = savedInstanceState.getIntegerArrayList(KEY_SELECTED_TASKS_POSITIONS_LIST);
            if (positionsList != null) {
                mCurrentActionMode = startSupportActionMode(mSelectTasksActionModeCallback);
                mSelectedTasksPositions = new HashSet<>(positionsList);
                mTasksAdapter.setSelectedTasksPositions(mSelectedTasksPositions);
                for (Integer selectedTasksPosition : mSelectedTasksPositions) {
                    mTasksAdapter.notifyItemChanged(selectedTasksPosition);
                }
                updateContextualToolbarTitle();
            }
        }
    }

    private void setupOnFirstLaunch() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean isFirstLaunch = sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true);
        if (isFirstLaunch) {
            List<Task> tasks = new ArrayList<>();

            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_MONTH, -1);

            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);

            Calendar inTenMinutes = Calendar.getInstance();
            inTenMinutes.add(Calendar.MINUTE, 10);

            tasks.add(new Task(getString(R.string.tutorial_task_1), TaskType.INBOX, DateType.NO_DATE, null, null));
            tasks.add(new Task(getString(R.string.tutorial_task_2), TaskType.INBOX, DateType.NO_DATE, null, null));
            tasks.add(new Task(getString(R.string.tutorial_task_3), TaskType.INBOX, DateType.NO_DATE, null, null));
            tasks.add(new Task(getString(R.string.tutorial_task_4), TaskType.POSTPONED, DateType.FIXED, new Date(yesterday.getTimeInMillis()), null));
            tasks.add(new Task(getString(R.string.tutorial_task_5), TaskType.POSTPONED, DateType.FIXED, new Date(tomorrow.getTimeInMillis()), null));
            tasks.add(new Task(getString(R.string.tutorial_task_6), TaskType.POSTPONED, DateType.DEADLINE, new Date(tomorrow.getTimeInMillis()), null));
            tasks.add(new Task(getString(R.string.tutorial_task_7), TaskType.POSTPONED, DateType.NO_DATE, null, new Date(inTenMinutes.getTimeInMillis())));
            tasks.add(new Task(getString(R.string.tutorial_task_8), TaskType.POSTPONED, DateType.NO_DATE, null, null));

            mTaskDisplayPresenter.createFirstLaunchTasks(tasks);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_FIRST_LAUNCH, false);
            editor.apply();
        }

    }
}
