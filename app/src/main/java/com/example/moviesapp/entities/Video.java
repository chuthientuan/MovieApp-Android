package com.example.moviesapp.entities;

import com.google.gson.annotations.SerializedName;

public class Video {
    @SerializedName("key")
    private String key;
    @SerializedName("size")
    private int size;
    @SerializedName("type")
    private String type;

    public String getKey() {
        return key;
    }

    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
    }
}
