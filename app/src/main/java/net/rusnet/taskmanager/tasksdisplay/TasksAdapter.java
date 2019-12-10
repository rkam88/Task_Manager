package net.rusnet.taskmanager.tasksdisplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import net.rusnet.taskmanager.R;
import net.rusnet.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

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
        ConstraintLayout mForegroundView, mBackgroundView;

        ViewHolder(View itemView) {
            super(itemView);

            mTaskNameTextView = itemView.findViewById(R.id.text_view_task_name);
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
