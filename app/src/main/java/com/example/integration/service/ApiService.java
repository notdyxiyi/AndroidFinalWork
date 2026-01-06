package com.example.integration.service;


import com.example.integration.entity.Habit;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/habits")
    Call<List<Habit>> getHabits();

    @POST("api/habits")
    Call<Habit> addHabit(@Body Habit habit);

    @DELETE("api/habits/{id}")
    Call<Void> deleteHabit(@Path("id") int id);
}