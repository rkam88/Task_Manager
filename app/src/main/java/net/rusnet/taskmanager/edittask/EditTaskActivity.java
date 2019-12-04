package net.rusnet.taskmanager.edittask;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.rusnet.taskmanager.R;

public class EditTaskActivity extends AppCompatActivity implements EditTaskContract.View {

    public static final String EXTRA_IS_TASK_NEW = "net.rusnet.taskmanager.AddTaskActivity.IsTaskNew";

    private boolean mIsTaskNew;

    private Toolbar mToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        mIsTaskNew = getIntent().getBooleanExtra(EXTRA_IS_TASK_NEW, true);

        initToolbar();

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

    @Override
    public void onBackPressed() {
        //todo: show dialog for confirmation ("discard changes?") if user started editing
        super.onBackPressed();
    }
}
