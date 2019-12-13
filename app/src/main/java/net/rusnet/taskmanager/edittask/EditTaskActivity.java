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
    private static final String SPACE = " ";

    private boolean mIsTaskNew;
    private long mTaskId;

    private Toolbar mToolbar;

    private EditTaskContract.Presenter mEditTaskPresenter;

    private EditText mTaskNameEditText;
    private Spinner mTaskCategorySpinner;
    private TextView mTaskDateTextView;
    private Date mSelectedDate;
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
                    TaskType taskType = getTaskType(mTaskCategorySpinner.getSelectedItem().toString());
                    DateType dateType = (DateType) findViewById(mCheckedRadioButtonId).getTag();
                    Date endDate = (dateType == DateType.NO_DATE) ? null : mSelectedDate;
                    if (mIsTaskNew) {
                        mEditTaskPresenter.createNewTask(name, taskType, dateType, endDate);
                    } else {
                        mTask.setName(name);
                        mTask.setTaskType(taskType);
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

        int position = getSpinnerPosition(mTask.getTaskType());
        mTaskCategorySpinner.setSelection(position);
        ((RadioButton) mTaskDateRadioGroup.findViewWithTag(mTask.getDateType())).setChecked(true);
        mCheckedRadioButtonId = mTaskDateRadioGroup.getCheckedRadioButtonId();
        mSelectedDate = mTask.getEndDate();
        updateDateTextView();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCheckedRadioButtonId = mTaskDateRadioGroup.getCheckedRadioButtonId();
        mSelectedDate = new Date(true, year, month, dayOfMonth);
        updateDateTextView();
    }

    public void onRadioButtonClicked(View view) {
        mTaskNameEditText.clearFocus();

        if (((RadioButton) view).isChecked()) {
            switch (view.getId()) {
                case R.id.radio_button_no_date:
                    mCheckedRadioButtonId = mTaskDateRadioGroup.getCheckedRadioButtonId();
                    mSelectedDate = null;
                    updateDateTextView();
                    break;
                case R.id.radio_button_fixed_date:
                case R.id.radio_button_deadline:
                    DatePickerFragment newFragment;
                    if (mSelectedDate == null) {
                        newFragment = DatePickerFragment.newInstance();
                    } else {
                        newFragment = DatePickerFragment.newInstance(mSelectedDate.toCalendar());
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
        if (mSelectedDate != null) outState.putString(KEY_DATE, mSelectedDate.toString());
        super.onSaveInstanceState(outState);
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
            mCheckedRadioButtonId = savedInstanceState.getInt(KEY_SELECTED_DATE_TYPE);
            mTaskDateRadioGroup.check(mCheckedRadioButtonId);
            String dateAsString = savedInstanceState.getString(KEY_DATE);
            mSelectedDate = (dateAsString == null) ? null : Date.parseString(dateAsString);
            updateDateTextView();
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

    private int getSpinnerPosition(TaskType type) {
        if (type.equals(TaskType.INBOX)) {
            return SPINNER_POSITION_INBOX;
        } else if (type.equals(TaskType.ACTIVE)) {
            return SPINNER_POSITION_ACTIVE;
        } else if (type.equals(TaskType.POSTPONED)) {
            return SPINNER_POSITION_POSTPONED;
        }
        throw new IllegalArgumentException();
    }


    private void updateDateTextView() {
        String text = "";
        switch ((DateType) findViewById(mCheckedRadioButtonId).getTag()) {
            case NO_DATE:
                text = mTaskDateTextView.getContext().getString(R.string.without_date);
                break;
            case DEADLINE:
                text = mTaskDateTextView.getContext().getString(R.string.before);
            case FIXED:
                text = text + SPACE + mSelectedDate.toString();
                break;
        }
        mTaskDateTextView.setText(text);
    }
}
