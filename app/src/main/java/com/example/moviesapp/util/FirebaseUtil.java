package com.example.moviesapp.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtil {
    public static String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static boolean isLogginedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static DatabaseReference getDataUser() {
        return FirebaseDatabase.getInstance().getReference("users").child(getCurrentUserId());
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }
}
