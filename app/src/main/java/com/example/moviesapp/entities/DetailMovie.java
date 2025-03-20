package com.example.moviesapp.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailMovie extends Film {
    @SerializedName("overview")
    private String overview;
    @SerializedName("release_date")
    private String release_date;
    @SerializedName("runtime")
    private int runtime;
    @SerializedName("genres")
    private List<Genre> genres;
    @SerializedName("vote_average")
    private String imdb;
    @SerializedName("cast")
    private List<Actor> actors;

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    public String getPoster_path() {
        return super.getPoster_path();
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public int getRuntime() {
        return runtime;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getVote_average() {
        return imdb;
    }

    public List<Actor> getActors() {
        return actors;
    }
}
