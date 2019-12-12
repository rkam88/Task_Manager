package net.rusnet.taskmanager.edittask;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.rusnet.taskmanager.R;
import net.rusnet.taskmanager.model.Date;
import net.rusnet.taskmanager.model.DateType;
import net.rusnet.taskmanager.model.Task;
import net.rusnet.taskmanager.model.TaskType;
import net.rusnet.taskmanager.model.TasksRepository;

import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity
        implements EditTaskContract.View,
        DatePickerFragment.OnDatePickerDialogResultListener {

    public static final String TAG = "TAG_EditTaskActivity";

    public static final String EXTRA_IS_TASK_NEW = "net.rusnet.taskmanager.AddTaskActivity.IsTaskNew";
    public static final String EXTRA_TASK_ID = "net.rusnet.taskmanager.AddTaskActivity.TaskId";

    private static final int NO_TASK_ID = -1;
    private static final int SPINNER_POSITION_INBOX = 0;
    private static final int SPINNER_POSITION_ACTIVE = 1;
    private static final int SPINNER_POSITION_POSTPONED = 2;
    private static final String DATE_PICKER_TAG = "datePicker";
    private static final String KEY_SELECTED_DATE_TYPE = "KEY_SELECTED_DATE_TYPE";
    private static final String KEY_DATE = "KEY_DATE";
    private static final int NO_SAVED_ID = -1;

    private boolean mIsTaskNew;
    private long mTaskId;

    private Toolbar mToolbar;

    private EditTaskContract.Presenter mEditTaskPresenter;

    private EditText mTaskNameEditText;
    private Spinner mTaskCategorySpinner;
    private TextView mTaskDateTextView;
    private RadioGroup mTaskDateRadioGroup;
    private int mCheckedRadioButtonId = NO_SAVED_ID;

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
                    DateType dateType = (DateType) findViewById(mCheckedRadioButtonId).getTag();
                    Date endDate = (dateType == DateType.NO_DATE) ? null :
                            Date.parseString(mTaskDateTextView.getText().toString());
                    if (mIsTaskNew) {
                        mEditTaskPresenter.createNewTask(name, type, dateType, endDate);
                    } else {
                        mTask.setName(name);
                        mTask.setType(type.toString());
                        mTask.setDateType(dateType);
                        mTask.setEndDate(endDate);
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
        ((RadioButton) mTaskDateRadioGroup.findViewWithTag(mTask.getDateType())).setChecked(true);
        mCheckedRadioButtonId = mTaskDateRadioGroup.getCheckedRadioButtonId();
        if (mTask.getDateType() != DateType.NO_DATE) {
            Date date = mTask.getEndDate();
            if (date != null) mTaskDateTextView.setText(date.toString());
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Date date = new Date(true, year, month, dayOfMonth);
        mTaskDateTextView.setText(date.toString());
        mCheckedRadioButtonId = mTaskDateRadioGroup.getCheckedRadioButtonId();
    }

    public void onRadioButtonClicked(View view) {
        mTaskNameEditText.clearFocus();

        if (((RadioButton) view).isChecked()) {
            switch (view.getId()) {
                case R.id.radio_button_no_date:
                    mTaskDateTextView.setText(R.string.without_date);
                    mCheckedRadioButtonId = mTaskDateRadioGroup.getCheckedRadioButtonId();
                    break;
                case R.id.radio_button_fixed_date:
                case R.id.radio_button_deadline:
                    DatePickerFragment newFragment;
                    String taskDate = mTaskDateTextView.getText().toString();
                    if (taskDate.equals(getString(R.string.without_date))) {
                        newFragment = DatePickerFragment.newInstance();
                    } else {
                        Calendar currentDate = Date.parseString(taskDate).toCalendar();
                        newFragment = DatePickerFragment.newInstance(currentDate);
                    }
                    newFragment.show(getSupportFragmentManager(), DATE_PICKER_TAG);
                    break;
            }
        }
    }

    @Override
    public void onDatePickerDialogCancelClick() {
        mTaskDateRadioGroup.check(mCheckedRadioButtonId);
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_SELECTED_DATE_TYPE, mCheckedRadioButtonId);
        outState.putString(KEY_DATE, mTaskDateTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mCheckedRadioButtonId = savedInstanceState.getInt(KEY_SELECTED_DATE_TYPE);
        super.onRestoreInstanceState(savedInstanceState);
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
        initTaskNameInput();

        initTaskCategoryInput();

        initDatePickerInput();

        if (mIsTaskNew && savedInstanceState == null) {
            if (mTaskNameEditText.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        } else if (savedInstanceState != null) {
            mTaskDateRadioGroup.check(mCheckedRadioButtonId);
            mTaskDateTextView.setText(savedInstanceState.getString(KEY_DATE));
        } else {
            mEditTaskPresenter.loadTask(mTaskId);
        }
    }

    private void initTaskNameInput() {
        mTaskNameEditText = findViewById(R.id.edit_text_task_name);
        mTaskNameEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mTaskNameEditText.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mTaskNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) EditTaskActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (imm != null)
                        imm.hideSoftInputFromWindow(mTaskNameEditText.getWindowToken(), 0);
                }
            }
        });
    }

    private void initTaskCategoryInput() {
        String[] list = getResources().getStringArray(R.array.task_categories);
        mTaskCategorySpinner = findViewById(R.id.spinner_task_category);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.edit_task_category_spinner_item,
                list);
        mTaskCategorySpinner.setAdapter(adapter);
        mTaskCategorySpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTaskNameEditText.clearFocus();
                v.performClick();
                return true;
            }
        });
    }

    private void initDatePickerInput() {
        mTaskDateTextView = findViewById(R.id.text_view_task_date);
        mTaskDateRadioGroup = findViewById(R.id.radio_group_date_type);
        if (mCheckedRadioButtonId == NO_SAVED_ID)
            mCheckedRadioButtonId = mTaskDateRadioGroup.getCheckedRadioButtonId();
        findViewById(R.id.radio_button_no_date).setTag(DateType.NO_DATE);
        findViewById(R.id.radio_button_fixed_date).setTag(DateType.FIXED);
        findViewById(R.id.radio_button_deadline).setTag(DateType.DEADLINE);
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
