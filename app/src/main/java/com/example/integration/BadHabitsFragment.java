package com.example.integration;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.integration.Adapter.HabitAdapter;
import com.example.integration.entity.Habit;
import com.example.integration.network.RetrofitClient;
import com.example.integration.R;
import com.example.integration.service.ApiService;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class BadHabitsFragment extends Fragment {

    private RecyclerView recyclerView;
    private HabitAdapter habitAdapter;
    private TextInputEditText etHabit;
    private Button btnAdd;
    private ProgressBar progressBar;
    private List<Habit> habitList = new ArrayList<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_badhabits_record, container, false);

        // 初始化视图
        recyclerView = view.findViewById(R.id.rv_habits);
        etHabit = view.findViewById(R.id.et_habit);
        btnAdd = view.findViewById(R.id.btn_add);
        progressBar = view.findViewById(R.id.progress_bar);

        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        habitAdapter = new HabitAdapter(habitList, new HabitAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position, Habit habit) {
                deleteHabit(position, habit);
            }
        });
        recyclerView.setAdapter(habitAdapter);

        // 初始化API服务
        apiService = RetrofitClient.getApiService();

        // 设置添加按钮点击事件
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHabit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次返回Fragment时刷新数据
        loadHabits();
    }

    private void loadHabits() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getHabits().enqueue(new Callback<List<Habit>>() {
            @Override
            public void onResponse(Call<List<Habit>> call, Response<List<Habit>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    habitList.clear();
                    habitList.addAll(response.body());
                    habitAdapter.setHabitList(habitList);
                } else {
                    Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Habit>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addHabit() {
        String habitText = etHabit.getText().toString().trim();
        if (habitText.isEmpty()) {
            Toast.makeText(getContext(), "请输入陋习内容", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Habit newHabit = new Habit(habitText);
        apiService.addHabit(newHabit).enqueue(new Callback<Habit>() {
            @Override
            public void onResponse(Call<Habit> call, Response<Habit> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Habit addedHabit = response.body();
                    etHabit.setText("");
                    habitAdapter.addHabit(addedHabit);
                    Toast.makeText(getContext(), "添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "添加失败: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Habit> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteHabit(int position, Habit habit) {
        progressBar.setVisibility(View.VISIBLE);
        apiService.deleteHabit(habit.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // 彻底隐藏不占位
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    habitAdapter.removeHabit(position);
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "删除失败: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}