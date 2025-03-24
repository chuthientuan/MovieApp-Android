package com.example.moviesapp.util;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseUtil {
    public static String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
