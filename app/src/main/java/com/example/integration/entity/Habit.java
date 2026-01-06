package com.example.integration.entity;


import com.google.gson.annotations.SerializedName;

public class Habit {
    @SerializedName("id")
    private int id;

    @SerializedName("habit_text")
    private String habitText;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public Habit() {
    }

    public Habit(String habitText) {
        this.habitText = habitText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHabitText() {
        return habitText;
    }

    public void setHabitText(String habitText) {
        this.habitText = habitText;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}