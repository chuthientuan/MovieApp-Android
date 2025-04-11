package com.example.moviesapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviesapp.R;
import com.example.moviesapp.activities.EditProfileActivity;
import com.example.moviesapp.activities.LoginActivity;
import com.example.moviesapp.adapters.MovieAdapter;
import com.example.moviesapp.entities.DetailMovie;
import com.example.moviesapp.entities.Movie;
import com.example.moviesapp.entities.User;
import com.example.moviesapp.interfaces.MovieApi;
import com.example.moviesapp.retrofit.MovieClient;
import com.example.moviesapp.util.FirebaseUtil;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private RecyclerView rvFavoriteMovies;
    private MovieAdapter movieAdapter;

    private List<Movie> movies;

    private User currentUser;
    private Button btnLogout;
    private Movie movie;
    private MovieApi movieApi = MovieClient.getRetrofit().create(MovieApi.class);
    private TextView see_all_favorites;
    private ImageView user_avatar;
    private TextView username_text;
    private TextView txtEdit;
    private TextView email_text;
    private int loadedCount = 0;
    private int totalFavorites = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @SuppressLint("CutPasteId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize views
        username_text = view.findViewById(R.id.username_text);
        email_text = view.findViewById(R.id.email_text);
        user_avatar = view.findViewById(R.id.user_avatar);
        see_all_favorites = view.findViewById(R.id.see_all_favorites);
        txtEdit = view.findViewById(R.id.txtEdit);
        txtEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
        movies = new ArrayList<>();
        rvFavoriteMovies = view.findViewById(R.id.favorites_recycler);
        rvFavoriteMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        movieAdapter = new MovieAdapter(movies, getContext());
        rvFavoriteMovies.setAdapter(movieAdapter);

        see_all_favorites.setOnClickListener(v -> {
            ChipNavigationBar chipNavigationBar = requireActivity().findViewById(R.id.chipNavigationBar);
            chipNavigationBar.setItemSelected(R.id.favorites, true);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FavoritesFragment())
                    .addToBackStack(null)
                    .commit();
        });
        loadFavoriteMovies();
        loadUserProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FirebaseUtil.shouldReloadFavorites) {
            loadFavoriteMovies();
            FirebaseUtil.shouldReloadFavorites = false;
        }
        loadUserProfile();
    }

    private void showLogoutConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    FirebaseUtil.logout();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadFavoriteMovies() {
        loadedCount = 0;
        movies.clear();
        movieAdapter.notifyDataSetChanged();
        FirebaseUtil.getDataUser().get().addOnSuccessListener(dataSnapshot -> {
            User currentUser = dataSnapshot.getValue(User.class);
            if (currentUser != null && currentUser.getFavorites() != null) {
                for (String movieId : currentUser.getFavorites().keySet()) {
                    loadMovieDetails(Integer.parseInt(movieId));
                }
            }
        });
    }

    private void loadMovieDetails(int movieId) {
        Call<DetailMovie> call = movieApi.getMovieDetail(MovieClient.BEARER_TOKEN, movieId, "en-US");
        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<DetailMovie> call, @NonNull Response<DetailMovie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DetailMovie movie = response.body();

                    boolean isAlreadyInList = false;
                    for (Movie m : movies) {
                        if (m.getId() == movie.getId()) {
                            isAlreadyInList = true;
                            break;
                        }
                    }

                    if (!isAlreadyInList) {
                        movies.add(movie);
                        movieAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DetailMovie> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
            }
        });
    }

    private void loadUserProfile() {
        FirebaseUtil.getDataUser().get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                currentUser = task.getResult().getValue(User.class);
                if (currentUser != null) {
                    email_text.setText(currentUser.getEmail());
                    username_text.setText(currentUser.getUserName());
                    if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                        Glide.with(this).load(currentUser.getAvatar()).into(user_avatar);
                    } else {
                        user_avatar.setImageResource(R.drawable.avatar_default);
                    }
                }
            } else {
                Toast.makeText(getContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
