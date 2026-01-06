package com.example.integration.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.integration.R;
import com.example.integration.entity.Habit;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {
    private List<Habit> habitList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int position, Habit habit);
    }

    public HabitAdapter(List<Habit> habitList, OnItemClickListener listener) {
        this.habitList = habitList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragement_item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        holder.habitTextView.setText(habit.getHabitText());

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(position, habit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return habitList != null ? habitList.size() : 0;
    }

    public void setHabitList(List<Habit> habitList) {
        this.habitList = habitList;
        notifyDataSetChanged();
    }

    public void addHabit(Habit habit) {
        habitList.add(0, habit);
        notifyItemInserted(0);
    }

    public void removeHabit(int position) {
        habitList.remove(position);
        notifyItemRemoved(position);
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView habitTextView;
        Button deleteButton;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitTextView = itemView.findViewById(R.id.habit_text);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}