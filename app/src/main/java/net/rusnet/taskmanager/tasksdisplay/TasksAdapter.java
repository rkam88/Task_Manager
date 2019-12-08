package net.rusnet.taskmanager.tasksdisplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.rusnet.taskmanager.R;
import net.rusnet.taskmanager.model.Task;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTaskNameTextView;

        ViewHolder(View itemView) {
            super(itemView);

            mTaskNameTextView = itemView.findViewById(R.id.text_view_task_name);
        }
    }

    private List<Task> mTasks;

    TasksAdapter(List<Task> tasks) {
        mTasks = tasks;
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

    public void setTasks(List<Task> tasks) {
        mTasks = tasks;
    }
}
