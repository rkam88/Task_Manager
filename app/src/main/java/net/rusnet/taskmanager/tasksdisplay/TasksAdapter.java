package net.rusnet.taskmanager.tasksdisplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import net.rusnet.taskmanager.R;
import net.rusnet.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;

    public interface OnItemClickListener {
        void onItemClicked(long taskId);
    }

    public interface OnItemLongClickListener {
        void onItemLongClicked(long taskId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTaskNameTextView;

        ViewHolder(View itemView) {
            super(itemView);

            mTaskNameTextView = itemView.findViewById(R.id.text_view_task_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            long taskId = mTasks.get(position).getId();
                            mClickListener.onItemClicked(taskId);
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
                            long taskId = mTasks.get(position).getId();
                            mLongClickListener.onItemLongClicked(taskId);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    private List<Task> mTasks;

    TasksAdapter(@Nullable List<Task> tasks) {
        mTasks = (tasks == null) ? null : new ArrayList<>(tasks);
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
}
