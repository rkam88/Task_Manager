package net.rusnet.taskmanager.edittask.presentation;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.rusnet.taskmanager.R;
import net.rusnet.taskmanager.commons.presentation.ConfirmationDialogFragment;
import net.rusnet.taskmanager.commons.domain.model.DateType;
import net.rusnet.taskmanager.commons.domain.model.Task;
import net.rusnet.taskmanager.commons.domain.model.TaskType;
import net.rusnet.taskmanager.commons.data.source.TasksRepository;
import net.rusnet.taskmanager.taskalarm.TaskAlarmService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class EditTaskActivity extends AppCompatActivity
        implements EditTaskContract.View,
        DatePickerFragment.OnDatePickerDialogResultListener,
        TimePickerDialog.OnTimeSetListener,
        ConfirmationDialogFragment.ConfirmationDialogListener {

    public static final String EXTRA_IS_TASK_NEW = "net.rusnet.taskmanager.AddTaskActivity.IsTaskNew";
    public static final String EXTRA_TASK_ID = "net.rusnet.taskmanager.AddTaskActivity.TaskId";

    private static final int NO_TASK_ID = -1;
    private static final int SPINNER_POSITION_INBOX = 0;
    private static final int SPINNER_POSITION_ACTIVE = 1;
    private static final int SPINNER_POSITION_POSTPONED = 2;
    private static final String DATE_PICKER_TAG = "datePicker";
    private static final String TIME_PICKER_TAG = "timePicker";
    private static final String CONFIRMATION_DIALOG_TAG = "CONFIRMATION_DIALOG_TAG";
    private static final String KEY_SELECTED_DATE_TYPE = "KEY_SELECTED_DATE_TYPE";
    private static final String KEY_DATE = "KEY_DATE";
    private static final String KEY_TASK = "KEY_TASK";
    private static final String KEY_REMINDER_DATE = "KEY_REMINDER_DATE";
    private static final String KEY_REMINDER_DATE_SET = "KEY_REMINDER_DATE_SET";
    private static final String KEY_REMINDER_TIME_SET = "KEY_REMINDER_TIME_SET";
    private static final String KEY_REMOVE_REMINDER_BUTTON_VISIBILITY = "KEY_REMOVE_REMINDER_BUTTON_VISIBILITY";
    private static final String KEY_WAITING_FOR_DATE = "KEY_WAITING_FOR_DATE";
    private static final int NO_SAVED_ID = -1;
    private static final String SPACE = " ";
    private static final String UNKNOWN = "?";
    private static final int DATE_NONE = 0;
    private static final int DATE_FOR_TASK = 1;
    private static final int DATE_FOR_REMINDER = 2;
    private static final String PATTERN_TIME = "HH:mm";
    private static final String PATTERN_DATE = "yyyy.MM.dd";
    private static final long DATE_IS_NULL = -1;

    private boolean mIsTaskNew;
    private long mTaskId;

    private Toolbar mToolbar;

    private EditTaskContract.Presenter mEditTaskPresenter;

    private EditText mTaskNameEditText;
    private Spinner mTaskCategorySpinner;
    private TextView mTaskDateTextView;
    private Date mSelectedTaskDate;
    private RadioGroup mTaskDateRadioGroup;
    private int mCheckedRadioButtonId = NO_SAVED_ID;

    private int mWaitingForDate = DATE_NONE;

    private TextView mTaskReminderTextView;
    private Button mRemoveReminderButton;
    private Button mSetReminderDateButton;
    private Button mSetReminderTimeButton;
    private Date mReminderDate;
    private boolean mIsReminderDateSet = false;
    private boolean mIsReminderTimeSet = false;
    private View mLoadingFrameLayout;

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
                if (!dataIsFilled()) break;
                mTaskNameEditText.clearFocus();

                String name = mTaskNameEditText.getText().toString();
                TaskType taskType = getTaskType(mTaskCategorySpinner.getSelectedItem().toString());
                DateType dateType = (DateType) findViewById(mCheckedRadioButtonId).getTag();
                Date endDate = (dateType == DateType.NO_DATE) ? null : mSelectedTaskDate;
                Date reminderDate = (mIsReminderDateSet && mIsReminderTimeSet) ? mReminderDate : null;
                if (mIsTaskNew) {
                    mEditTaskPresenter.createNewTask(name, taskType, dateType, endDate, reminderDate);
                } else {
                    mTask.setName(name);
                    mTask.setTaskType(taskType);
                    mTask.setDateType(dateType);
                    mTask.setEndDate(endDate);
                    mTask.setReminderDate(reminderDate);
                    mEditTaskPresenter.updateTask(mTask);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        String dialogText = getString(R.string.exit_without_saving);
        ConfirmationDialogFragment newFragment;
        newFragment = ConfirmationDialogFragment.newInstance(dialogText);
        newFragment.show(getSupportFragmentManager(), CONFIRMATION_DIALOG_TAG);
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
        mSelectedTaskDate = mTask.getEndDate();
        updateDateTextView();

        if (mTask.getReminderDate() != null) {
            mRemoveReminderButton.setVisibility(View.VISIBLE);
            mIsReminderDateSet = true;
            mIsReminderTimeSet = true;
            mReminderDate = mTask.getReminderDate();
            updateReminderTextView();
        }
    }

    @Override
    public void updateTaskAlarm(long taskId) {
        Intent workIntent = new Intent(TaskAlarmService.ACTION_UPDATE_ONE);
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

    public void onRadioButtonClicked(View view) {
        mTaskNameEditText.clearFocus();

        if (((RadioButton) view).isChecked()) {
            switch (view.getId()) {
                case R.id.radio_button_no_date:
                    mCheckedRadioButtonId = mTaskDateRadioGroup.getCheckedRadioButtonId();
                    mSelectedTaskDate = null;
                    updateDateTextView();
                    break;
                case R.id.radio_button_fixed_date:
                case R.id.radio_button_deadline:
                    mWaitingForDate = DATE_FOR_TASK;
                    DatePickerFragment newFragment;
                    if (mSelectedTaskDate == null) {
                        newFragment = DatePickerFragment.newInstance();
                    } else {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(mSelectedTaskDate.getTime());
                        newFragment = DatePickerFragment.newInstance(calendar);
                    }
                    newFragment.show(getSupportFragmentManager(), DATE_PICKER_TAG);
                    break;
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        switch (mWaitingForDate) {
            case DATE_FOR_TASK:
                mCheckedRadioButtonId = mTaskDateRadioGroup.getCheckedRadioButtonId();
                calendar.set(year, month, dayOfMonth);
                mSelectedTaskDate = new Date(calendar.getTimeInMillis());
                updateDateTextView();
                break;
            case DATE_FOR_REMINDER:
                mRemoveReminderButton.setVisibility(View.VISIBLE);
                ;
                calendar.set(year, month, dayOfMonth);
                mReminderDate.setTime(calendar.getTimeInMillis());
                mIsReminderDateSet = true;
                updateReminderTextView();
                break;
        }
        mWaitingForDate = DATE_NONE;
    }

    @Override
    public void onDatePickerDialogCancelClick() {
        mTaskDateRadioGroup.check(mCheckedRadioButtonId);
        mWaitingForDate = DATE_NONE;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mRemoveReminderButton.setVisibility(View.VISIBLE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mReminderDate.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        mReminderDate.setTime(calendar.getTimeInMillis());
        mIsReminderTimeSet = true;
        updateReminderTextView();
    }

    @Override
    public void onPositiveResponse() {
        super.onBackPressed();
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
        initLoadingScreen();
        initViews(savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_SELECTED_DATE_TYPE, mCheckedRadioButtonId);
        if (mSelectedTaskDate != null) outState.putLong(KEY_DATE, mSelectedTaskDate.getTime());
        if (mTask != null) outState.putSerializable(KEY_TASK, mTask);
        outState.putSerializable(KEY_REMINDER_DATE, mReminderDate);
        outState.putBoolean(KEY_REMINDER_DATE_SET, mIsReminderDateSet);
        outState.putBoolean(KEY_REMINDER_TIME_SET, mIsReminderTimeSet);
        outState.putInt(KEY_REMOVE_REMINDER_BUTTON_VISIBILITY, mRemoveReminderButton.getVisibility());
        outState.putInt(KEY_WAITING_FOR_DATE, mWaitingForDate);
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

    private void initLoadingScreen() {
        mLoadingFrameLayout = findViewById(R.id.loading_frame_layout);
    }

    private void initViews(@Nullable Bundle savedInstanceState) {
        initTaskNameInput();

        initTaskCategoryInput();

        initDatePickerInput();

        initReminderInput();

        if (mIsTaskNew && savedInstanceState == null) {
            mTaskNameEditText.requestFocus();
        } else if (savedInstanceState != null) {
            mCheckedRadioButtonId = savedInstanceState.getInt(KEY_SELECTED_DATE_TYPE);
            mTaskDateRadioGroup.check(mCheckedRadioButtonId);
            long dateAsLong = savedInstanceState.getLong(KEY_DATE, DATE_IS_NULL);
            mSelectedTaskDate = (dateAsLong != -1) ? null : new Date(dateAsLong);
            updateDateTextView();

            mTask = (Task) savedInstanceState.getSerializable(KEY_TASK);

            mReminderDate = (Date) savedInstanceState.getSerializable(KEY_REMINDER_DATE);
            mIsReminderDateSet = savedInstanceState.getBoolean(KEY_REMINDER_DATE_SET);
            mIsReminderTimeSet = savedInstanceState.getBoolean(KEY_REMINDER_TIME_SET);
            mRemoveReminderButton.setVisibility(savedInstanceState.getInt(KEY_REMOVE_REMINDER_BUTTON_VISIBILITY));
            if (mIsReminderDateSet || mIsReminderTimeSet) updateReminderTextView();
            mWaitingForDate = savedInstanceState.getInt(KEY_WAITING_FOR_DATE);
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

    private void initReminderInput() {
        mTaskReminderTextView = findViewById(R.id.text_view_task_reminder);
        mRemoveReminderButton = findViewById(R.id.button_remove_reminder);
        mSetReminderDateButton = findViewById(R.id.button_set_reminder_date);
        mSetReminderTimeButton = findViewById(R.id.button_set_reminder_time);
        mReminderDate = new Date(System.currentTimeMillis());

        mRemoveReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReminderDate = new Date(System.currentTimeMillis());
                mIsReminderDateSet = false;
                mIsReminderTimeSet = false;
                mTaskReminderTextView.setText(R.string.no_reminders);
                mRemoveReminderButton.setVisibility(View.GONE);
            }
        });

        mSetReminderDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWaitingForDate = DATE_FOR_REMINDER;
                DatePickerFragment newFragment;
                if (mIsReminderDateSet) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(mReminderDate.getTime());
                    newFragment = DatePickerFragment.newInstance(calendar);
                } else {
                    newFragment = DatePickerFragment.newInstance();
                }
                newFragment.show(getSupportFragmentManager(), DATE_PICKER_TAG);
            }
        });

        mSetReminderTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment newFragment;
                if (mIsReminderTimeSet) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(mReminderDate.getTime());
                    newFragment = TimePickerFragment.newInstance(calendar);
                } else {
                    newFragment = TimePickerFragment.newInstance();
                }
                newFragment.show(getSupportFragmentManager(), TIME_PICKER_TAG);
            }
        });

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
                text = text + SPACE + (new SimpleDateFormat(PATTERN_DATE)).format(mSelectedTaskDate);
                break;
        }
        mTaskDateTextView.setText(text);
    }

    private void updateReminderTextView() {
        String text = (mIsReminderDateSet) ? getCalendarDateAsString(mReminderDate) : UNKNOWN;
        text += SPACE + getString(R.string.at) + SPACE;
        text += (mIsReminderTimeSet) ? getCalendarTimeAsString(mReminderDate) : UNKNOWN;
        mTaskReminderTextView.setText(text);
    }

    private String getCalendarTimeAsString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(PATTERN_TIME);
        return format.format(date.getTime());
    }

    private String getCalendarDateAsString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(PATTERN_DATE);
        return format.format(date.getTime());
    }

    private boolean dataIsFilled() {
        if (mTaskNameEditText.getText().toString().isEmpty()) {
            Toast.makeText(EditTaskActivity.this, R.string.please_enter_a_task_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mIsReminderDateSet && !mIsReminderTimeSet) {
            Toast.makeText(EditTaskActivity.this, R.string.please_enter_the_time_for_the_reminder, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!mIsReminderDateSet && mIsReminderTimeSet) {
            Toast.makeText(EditTaskActivity.this, R.string.please_enter_a_date_for_the_reminder, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
