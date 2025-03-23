package com.example.moviesapp.entities;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userName;
    private String email;
    private String avatar;
    private Map<Integer, String> favorites;

    public User() {
    }

    public User(String userName, String email, String avatar) {
        this.userName = userName;
        this.email = email;
        this.avatar = avatar;
        this.favorites = new HashMap<>();
    }
}
