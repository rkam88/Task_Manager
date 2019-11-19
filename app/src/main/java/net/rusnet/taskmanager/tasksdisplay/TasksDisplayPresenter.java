package net.rusnet.taskmanager.tasksdisplay;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class TasksDisplayPresenter implements TasksDisplayContract.Presenter {

    private WeakReference<TasksDisplayActivity> mTasksDisplayActivityWeakReference;
    private TaskViewType mTaskViewType;

    public TasksDisplayPresenter(@NonNull TasksDisplayActivity tasksDisplayActivity, @NonNull TaskViewType taskViewType) {
        mTasksDisplayActivityWeakReference = new WeakReference<>(tasksDisplayActivity);

        setTasksViewType(taskViewType);
        //todo: show loading screen
        //todo: load data
        //todo: hide loading screen
        //todo: update view with data result (recycler contents and task counts in nav bar) or show "no tasks" screen
    }

    @Override
    public void setTasksViewType(@NonNull TaskViewType type) {
        mTaskViewType = type;

        TasksDisplayActivity view = mTasksDisplayActivityWeakReference.get();
        if (view != null) {
            view.updateTasksViewType(type);
        }
    }
}
