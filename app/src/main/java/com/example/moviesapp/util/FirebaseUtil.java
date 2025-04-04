package com.example.moviesapp.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseUtil {
    public static String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    public static boolean isLogginedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }
}
