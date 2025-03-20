package com.example.moviesapp.entities;

import com.google.gson.annotations.SerializedName;

public class Actor {
    @SerializedName("name")
    private String name;
    @SerializedName("profile_path")
    private String profile_path;
    @SerializedName("order")
    private int order;

    public String getName() {
        return name;
    }

    public String getProfile_path() {
        return "https://image.tmdb.org/t/p/w500" + profile_path;
    }

    public int getOrder() {
        return order;
    }
}
