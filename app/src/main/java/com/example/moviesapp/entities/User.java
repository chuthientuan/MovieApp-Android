package com.example.moviesapp.entities;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userName;
    private String email;
    private String avatar;
    private Map<String, String> favorites;

    public User() {
    }

    public User(String userName, String email, String avatar) {
        this.userName = userName;
        this.email = email;
        this.avatar = avatar;
        this.favorites = new HashMap<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Map<String, String> getFavorites() {
        return favorites;
    }

    public void setFavorites(Map<String, String> favorites) {
        this.favorites = favorites;
    }
}
