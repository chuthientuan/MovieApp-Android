package com.example.moviesapp.entities;

import com.google.gson.annotations.SerializedName;

public class Movie {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("poster_path")
    private String poster_path;
    @SerializedName("backdrop_path")
    private String backdrop_path;
    private boolean isFavorite;

    public Movie() {
        // Required empty constructor for Firebase
    }

    public Movie(int id, String title, String poster_path, String backdrop_path) {
        this.id = id;
        this.title = title;
        this.poster_path = poster_path;
        this.backdrop_path = backdrop_path;
        this.isFavorite = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return "https://image.tmdb.org/t/p/w500" + poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getBackdrop_path() {
        return "https://image.tmdb.org/t/p/w500" + backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}