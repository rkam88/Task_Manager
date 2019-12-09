package net.rusnet.taskmanager.edittask;

import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.rusnet.taskmanager.R;
import net.rusnet.taskmanager.model.Task;
import net.rusnet.taskmanager.model.TaskType;
import net.rusnet.taskmanager.model.TasksRepository;

public class EditTaskActivity extends AppCompatActivity implements EditTaskContract.View {

    public static final String TAG = "TAG_EditTaskActivity";

    public static final String EXTRA_IS_TASK_NEW = "net.rusnet.taskmanager.AddTaskActivity.IsTaskNew";
    public static final String EXTRA_TASK_ID = "net.rusnet.taskmanager.AddTaskActivity.TaskId";

    private static final int NO_TASK_ID = -1;
    private static final int SPINNER_POSITION_INBOX = 0;
    private static final int SPINNER_POSITION_ACTIVE = 1;
    private static final int SPINNER_POSITION_POSTPONED = 2;

    private boolean mIsTaskNew;
    private long mTaskId;

    private Toolbar mToolbar;

    private EditTaskContract.Presenter mEditTaskPresenter;

    private EditText mTaskNameEditText;
    private Spinner mTaskCategorySpinner;

    private Task mTask;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miSave:
                if (mTaskNameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(EditTaskActivity.this, R.string.please_enter_a_task_name, Toast.LENGTH_SHORT).show();
                } else {
                    String name = mTaskNameEditText.getText().toString();
                    TaskType type = getTaskType(mTaskCategorySpinner.getSelectedItem().toString());
                    if (mIsTaskNew) {
                        mEditTaskPresenter.createNewTask(name, type);
                    } else {
                        mTask.setName(name);
                        mTask.setType(type.toString());
                        mEditTaskPresenter.updateTask(mTask);
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //todo: show dialog for confirmation ("discard changes?") if user started editing
        super.onBackPressed();
    }

    @Override
    public void onTaskSavingFinished() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void updateView(Task task) {
        mTask = task;
        mTaskNameEditText.setText(mTask.getName());
        int position = getSpinnerPosition(mTask.getType());
        mTaskCategorySpinner.setSelection(position);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        mIsTaskNew = getIntent().getBooleanExtra(EXTRA_IS_TASK_NEW, true);
        if (!mIsTaskNew) mTaskId = getIntent().getLongExtra(EXTRA_TASK_ID, NO_TASK_ID);
        if (mTaskId == NO_TASK_ID) mIsTaskNew = true;

        initToolbar();
        initPresenter();
        initViews(savedInstanceState);

    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(mIsTaskNew ? R.string.create_new_task : R.string.edit_task);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initPresenter() {
        mEditTaskPresenter = new EditTaskPresenter(
                this,
                TasksRepository.getRepository(getApplication()));
    }

    private void initViews(@Nullable Bundle savedInstanceState) {
        mTaskNameEditText = findViewById(R.id.edit_text_task_name);
        mTaskNameEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mTaskNameEditText.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        mTaskCategorySpinner = findViewById(R.id.spinner_task_category);

        if (mIsTaskNew && savedInstanceState == null) {
            if (mTaskNameEditText.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        } else {
            mEditTaskPresenter.loadTask(mTaskId);
        }
    }

    @NonNull
    private TaskType getTaskType(String text) {
        if (text.equals(getString(R.string.inbox))) {
            return TaskType.INBOX;
        } else if (text.equals(getString(R.string.active))) {
            return TaskType.ACTIVE;
        } else if (text.equals(getString(R.string.postponed))) {
            return TaskType.POSTPONED;
        }
        throw new IllegalArgumentException(text);
    }

    private int getSpinnerPosition(String type) {
        if (type.equals(TaskType.INBOX.toString())) {
            return SPINNER_POSITION_INBOX;
        } else if (type.equals(TaskType.ACTIVE.toString())) {
            return SPINNER_POSITION_ACTIVE;
        } else if (type.equals(TaskType.POSTPONED.toString())) {
            return SPINNER_POSITION_POSTPONED;
        }
        throw new IllegalArgumentException(type);
    }

}
