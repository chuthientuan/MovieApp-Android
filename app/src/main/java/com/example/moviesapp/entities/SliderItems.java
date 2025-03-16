package com.example.moviesapp.entities;

import com.google.gson.annotations.SerializedName;

public class SliderItems {
    @SerializedName("backdrop_path")
    private String backdrop_path;
    @SerializedName("title")
    private String title;
    @SerializedName("release_date")
    private String release_date;

    public String getBackdrop_path() {
        return "https://image.tmdb.org/t/p/w500" + backdrop_path;
    }

    public String getTitle() {
        return title;
    }

    public String getRelease_date() {
        return release_date;
    }
}
