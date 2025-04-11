package com.example.moviesapp.repository;

import androidx.annotation.NonNull;

import com.example.moviesapp.entities.Movie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteRepository {
    private final DatabaseReference favoritesRef;
    private final String userId;

    public FavoriteRepository() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        favoritesRef = FirebaseDatabase.getInstance().getReference("favorites").child(userId);
    }

    public void addToFavorites(Movie movie, OnCompleteListener<Void> listener) {
        favoritesRef.child(String.valueOf(movie.getId())).setValue(true)
                .addOnCompleteListener(listener);
    }

    public void removeFromFavorites(Movie movie, OnCompleteListener<Void> listener) {
        favoritesRef.child(String.valueOf(movie.getId())).removeValue()
                .addOnCompleteListener(listener);
    }

    public void getFavoriteIds(final OnFavoriteIdsLoaded callback) {
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Integer> favoriteIds = new ArrayList<>();
                for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                    favoriteIds.add(Integer.parseInt(idSnapshot.getKey()));
                }
                callback.onLoaded(favoriteIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public interface OnFavoriteIdsLoaded {
        void onLoaded(List<Integer> favoriteIds);
        void onError(String error);
    }
}