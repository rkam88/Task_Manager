package net.rusnet.taskmanager_old.tasksdisplay.presentation;

import android.content.Context;
import android.graphics.Paint;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import net.rusnet.taskmanager_old.R;
import net.rusnet.taskmanager_old.commons.domain.model.DateType;
import net.rusnet.taskmanager_old.commons.domain.model.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private static final String PATTERN_TIME = "HH:mm";
    private static final String PATTERN_DATE = "yyyy.MM.dd";
    private static final String SPACE = " ";
    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClicked(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTaskNameTextView;
        TextView mTaskDateTextView;
        TextView mTaskReminderTextView;
        ConstraintLayout mForegroundView, mBackgroundView;

        ViewHolder(View itemView) {
            super(itemView);

            mTaskNameTextView = itemView.findViewById(R.id.text_view_task_name);
            mTaskDateTextView = itemView.findViewById(R.id.text_view_task_date);
            mTaskReminderTextView = itemView.findViewById(R.id.text_view_task_reminder);
            mBackgroundView = itemView.findViewById(R.id.view_background);
            mForegroundView = itemView.findViewById(R.id.view_foreground);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mClickListener.onItemClicked(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mLongClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mLongClickListener.onItemLongClicked(position);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    private List<Task> mTasks;
    private Set<Integer> mSelectedTasksPositions;

    TasksAdapter(@Nullable List<Task> tasks) {
        mTasks = (tasks == null) ? null : new ArrayList<>(tasks);
        mSelectedTasksPositions = new HashSet<>();
    }

    @NonNull
    @Override
    public TasksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.tasks_display_item_task, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = mTasks.get(position);

        TextView textViewTaskName = holder.mTaskNameTextView;
        textViewTaskName.setText(task.getName());

        TextView textViewTaskDate = holder.mTaskDateTextView;
        String dateText = "";
        switch (task.getDateType()) {
            case NO_DATE:
                dateText = textViewTaskDate.getContext().getString(R.string.without_date);
                break;
            case DEADLINE:
                dateText = textViewTaskDate.getContext().getString(R.string.before) + SPACE;
            case FIXED:
                Date date = task.getEndDate();
                if (date != null) {
                    String dateAsString = (new SimpleDateFormat(PATTERN_DATE)).format(date);
                    dateText = dateText + dateAsString;
                }
                break;
        }
        textViewTaskDate.setText(dateText);

        TextView reminderTextView = holder.mTaskReminderTextView;
        reminderTextView.setPaintFlags(
                reminderTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        if (task.getReminderDate() == null) {
            reminderTextView.setText(R.string.no_reminders);
        } else {
            String date = (new SimpleDateFormat(PATTERN_DATE)).format(task.getReminderDate().getTime());
            String time = (new SimpleDateFormat(PATTERN_TIME)).format(task.getReminderDate().getTime());
            String reminderText = date + SPACE + textViewTaskDate.getContext().getString(R.string.at) + SPACE + time;
            reminderTextView.setText(reminderText);
            if (task.getReminderDate().getTime() < System.currentTimeMillis()
                    || task.isCompleted()) {
                reminderTextView.setPaintFlags(
                        reminderTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        int color;
        if (!task.isCompleted()
                && (task.getDateType() == DateType.FIXED || task.getDateType() == DateType.DEADLINE)
                && task.getEndDate().before(new Date(System.currentTimeMillis()))
                && !DateUtils.isToday(task.getEndDate().getTime())) {
            color = textViewTaskDate.getContext().getResources().getColor(R.color.colorTextDelayedItem);
        } else {
            color = textViewTaskDate.getContext().getResources().getColor(R.color.colorTextBlack);
        }
        textViewTaskDate.setTextColor(color);

        if (mSelectedTasksPositions.contains(position)) {
            holder.mForegroundView.setBackgroundResource(R.color.colorItemSelectedBackground);
        } else {
            holder.mForegroundView.setBackgroundResource(R.color.colorItemBackground);
        }

    }

    @Override
    public int getItemCount() {
        if (mTasks != null) {
            return mTasks.size();
        } else return 0;
    }

    public void setTasks(@Nullable List<Task> tasks) {
        mTasks = (tasks == null) ? null : new ArrayList<>(tasks);
    }

    public Task getTaskAtPosition(int position) {
        return mTasks.get(position);
    }

    public void setSelectedTasksPositions(@NonNull Set<Integer> selectedTasksPositions) {
        mSelectedTasksPositions = new HashSet<>(selectedTasksPositions);
    }

}
