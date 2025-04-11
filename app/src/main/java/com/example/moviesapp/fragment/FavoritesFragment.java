package com.example.moviesapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.R;
import com.example.moviesapp.adapters.MovieAdapter;
import com.example.moviesapp.entities.DetailMovie;
import com.example.moviesapp.entities.Movie;
import com.example.moviesapp.entities.User;
import com.example.moviesapp.interfaces.MovieApi;
import com.example.moviesapp.retrofit.MovieClient;
import com.example.moviesapp.util.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesFragment extends Fragment {
    private RecyclerView rvFavorites;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private MovieAdapter adapter;
    private List<Movie> favoriteMovies;
    private MovieApi movieApi = MovieClient.getRetrofit().create(MovieApi.class);
    private int loadedCount = 0;
    private int totalFavorites = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        loadFavoriteMovies();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FirebaseUtil.shouldReloadFavorites) {
            loadFavoriteMovies();
            FirebaseUtil.shouldReloadFavorites = false;
        }
    }

    private void initViews(View view) {
        rvFavorites = view.findViewById(R.id.rvFavorites);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        favoriteMovies = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new MovieAdapter(favoriteMovies, requireContext());
        rvFavorites.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvFavorites.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadFavoriteMovies() {
        showLoading();
        favoriteMovies.clear();
        loadedCount = 0;
        adapter.notifyDataSetChanged();
        FirebaseUtil.getDataUser().get().addOnSuccessListener(dataSnapshot -> {
            User currentUser = dataSnapshot.getValue(User.class);
            if (currentUser != null && currentUser.getFavorites() != null) {
                totalFavorites = currentUser.getFavorites().size();
                if (totalFavorites == 0) {
                    showEmptyState();
                    return;
                }
                for (String movieId : currentUser.getFavorites().keySet()) {
                    loadMovieDetails(Integer.parseInt(movieId));
                }
            } else {
                showEmptyState();
            }
        });
    }

    private void loadMovieDetails(int movieId) {
        showLoading();
        Call<DetailMovie> call = movieApi.getMovieDetail(MovieClient.BEARER_TOKEN, movieId, "en-US");
        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<DetailMovie> call, @NonNull Response<DetailMovie> response) {
                loadedCount++;
                if (response.isSuccessful() && response.body() != null) {
                    DetailMovie movie = response.body();
                    boolean isAlreadyInList = false;
                    for (Movie m : favoriteMovies) {
                        if (m.getId() == movie.getId()) {
                            isAlreadyInList = true;
                            break;
                        }
                    }
                    if (!isAlreadyInList) {
                        favoriteMovies.add(movie);
                    }
                }
                if (loadedCount == totalFavorites) {
                    if (favoriteMovies.isEmpty()) {
                        showEmptyState();
                    } else {
                        adapter.notifyDataSetChanged();
                        showContent();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DetailMovie> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvFavorites.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showContent() {
        progressBar.setVisibility(View.GONE);
        rvFavorites.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        rvFavorites.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }
}
