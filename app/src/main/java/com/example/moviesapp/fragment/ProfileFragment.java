package com.example.moviesapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.R;
import com.example.moviesapp.activities.LoginActivity;
import com.example.moviesapp.activities.SeeAllActivity;
import com.example.moviesapp.adapters.MovieAdapter;
import com.example.moviesapp.entities.Movie;
import com.example.moviesapp.entities.User;
import com.example.moviesapp.interfaces.MovieApi;
import com.example.moviesapp.response.MovieResponse;
import com.example.moviesapp.retrofit.MovieClient;
import com.example.moviesapp.util.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private RecyclerView rvFavoriteMovies;
    private MovieAdapter movieAdapter;
    private MovieApi apiService;

    private TextView See_all_favorites;
    private List<Movie> movies;

    private User currentUser;
    private  Button btnLogout;
    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyYmU0MTRlYTZmZDg5NjFmOGQ2Y2Y0NjQ2MGJhMTgyZCIsIm5iZiI6MTc0MDM4NzQ3Ni42OTUwMDAyLCJzdWIiOiI2N2JjMzQ5NDc0MTE1MmIwNDIwYWJjMGEiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.jXswQOY-SSxrfNtB5DlxJt6MWHsqGaUieY9xvjV-lOs";
    private Movie movie;

    private TextView username_text;

    private  TextView email_text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflating layout cho Fragment và trả về view
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("API", "Thông báo log của API");
        // Initialize views
        username_text = view.findViewById(R.id.username_text);
        email_text = view.findViewById(R.id.email_text);

        btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
        movies = new ArrayList<>();
        rvFavoriteMovies = view.findViewById(R.id.favorites_recycler);
        rvFavoriteMovies.setLayoutManager(new LinearLayoutManager(getContext() , LinearLayoutManager.HORIZONTAL,false));
        movieAdapter = new MovieAdapter(movies ,  getContext());
        rvFavoriteMovies.setAdapter(movieAdapter);

        See_all_favorites  = view.findViewById(R.id.see_all_favorites);
        See_all_favorites.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SeeAllActivity.class);
            intent.putExtra("", "");
            startActivity(intent);
        });

        // Set up "See all" click
        view.findViewById(R.id.see_all_favorites).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SeeAllActivity.class);
            startActivity(intent);
        });

        // Initialize API service
        apiService = MovieClient.getRetrofit().create(MovieApi.class);
        // Load favorite movies
        loadFavoriteMovies();
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
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void loadFavoriteMovies() {
        // Call API to get favorite movies
        Call<MovieResponse> call = apiService.getTopMovies(BEARER_TOKEN, "en-US", 1);
        call.enqueue(new Callback<MovieResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    if (movieResponse != null) {
                        // Xử lý dữ liệu ở đây
                        movies.clear();
                        movies.addAll(movieResponse.getResults());
                        movieAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("API_ERROR", "Error Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
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
                }
            } else {
                Toast.makeText(getContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
