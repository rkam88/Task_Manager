package net.rusnet.taskmanager.tasksdisplay;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import net.rusnet.taskmanager.R;
import net.rusnet.taskmanager.model.Task;
import net.rusnet.taskmanager.model.TasksRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TasksDisplayActivity extends AppCompatActivity implements TasksDisplayContract.View {

    public static final String TAG = "TasksDisplayActivity";

    private static final String TASK_VIEW_TYPE = "TASK_VIEW_TYPE";
    private static final TaskViewType DEFAULT_TASK_VIEW_TYPE = TaskViewType.INBOX;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationViewDrawer;

    private Map<TaskViewType, TextView> mTaskCountMap = new HashMap<>();

    private TaskViewType mTaskViewType;

    private TasksDisplayContract.Presenter mTaskDisplayPresenter;

    //todo: replace with recycler view
    private TextView mRecyclerPlaceholder;

    /////////////////////////////////////* PUBLIC methods */////////////////////////////////////
    @Override
    public void updateTasksViewType(@NonNull TaskViewType type) {
        mTaskViewType = type;
        setTitle(type.getTitle());
    }

    @Override
    public void updateTaskList(@Nullable List<Task> taskList) {
        //todo: replace with recycler view update
        if (!taskList.isEmpty()) mRecyclerPlaceholder.setText(taskList.get(0).getName());
    }

    @Override
    public void updateTaskCount(@NonNull TaskViewType type, @NonNull String newCount) {
        mTaskCountMap.get(type).setText(newCount);
    }//todo: handle setText() possible NPE???

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /////////////////////////////////////* PROTECTED methods */////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_display);

        initToolbar();
        initNavigationDrawer();

        TaskViewType type = null;
        if (savedInstanceState != null) {
            type = (TaskViewType) savedInstanceState.getSerializable(TASK_VIEW_TYPE);
        } else {
            mNavigationViewDrawer.getMenu().findItem(R.id.nav_inbox).setChecked(true);
        }
        type = (type == null) ? DEFAULT_TASK_VIEW_TYPE : type;

        //todo: replace with recycler view init
        mRecyclerPlaceholder = findViewById(R.id.text_view_recycler_placeholder);

        initPresenter(type);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(TASK_VIEW_TYPE, mTaskViewType);
        super.onSaveInstanceState(outState);
    }


    /////////////////////////////////////* PRIVATE methods */////////////////////////////////////
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
        mTaskCountMap.put(TaskViewType.ACTIVE, getTextViewCounter(R.id.nav_active));
        mTaskCountMap.put(TaskViewType.POSTPONED, getTextViewCounter(R.id.nav_postponed));
        mTaskCountMap.put(TaskViewType.COMPLETED, getTextViewCounter(R.id.nav_completed));
    }

    private void selectDrawerItem(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_inbox:
                mTaskDisplayPresenter.setTasksViewType(TaskViewType.INBOX);
                break;
            case R.id.nav_active:
                mTaskDisplayPresenter.setTasksViewType(TaskViewType.ACTIVE);
                break;
            case R.id.nav_postponed:
                mTaskDisplayPresenter.setTasksViewType(TaskViewType.POSTPONED);
                break;
            case R.id.nav_completed:
                mTaskDisplayPresenter.setTasksViewType(TaskViewType.COMPLETED);
                break;
        }

        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
    }

    private TextView getTextViewCounter(@IdRes int menuItemId) {
        Menu menu = mNavigationViewDrawer.getMenu();
        MenuItem menuItem = menu.findItem(menuItemId);
        View actionView = menuItem.getActionView();
        return actionView.findViewById(R.id.text_view_task_count);
    }

    private void initPresenter(TaskViewType type) {
        mTaskDisplayPresenter = new TasksDisplayPresenter(
                this,
                TasksRepository.getRepository(getApplication()),
                type);
    }

}
