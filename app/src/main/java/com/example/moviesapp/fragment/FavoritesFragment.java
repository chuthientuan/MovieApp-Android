package com.example.moviesapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moviesapp.R;
import com.example.moviesapp.entities.User;
import com.example.moviesapp.util.FirebaseUtil;

public class FavoritesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadFavorites() {
        FirebaseUtil.getDataUser().get().addOnSuccessListener(dataSnapshot -> {
                    User currentUser = dataSnapshot.getValue(User.class);
                    if (currentUser != null && currentUser.getFavorites() != null) {
                        for (String movieId : currentUser.getFavorites().keySet()) {
                            //Ham
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FavoritesFragment", "Error loading favorites", e));
    }
}
