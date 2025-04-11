package com.example.moviesapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.R;
import com.example.moviesapp.adapters.MovieAdapter;
import com.example.moviesapp.entities.Movie;
import com.example.moviesapp.repository.FavoriteRepository;
import com.example.moviesapp.retrofit.MovieApiService;
import com.example.moviesapp.retrofit.MovieClient;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FavoritesFragment extends Fragment implements MovieAdapter.OnFavoriteClickListener {
    private RecyclerView rvFavorites;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private MovieAdapter adapter;
    private List<Movie> favoriteMovies;
    private FavoriteRepository favoriteRepository;
    private MovieApiService movieApiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        initViews(view);
        initMovieService();
        setupRecyclerView();
        loadFavoriteMovies();
        return view;
    }

    private void initViews(View view) {
        rvFavorites = view.findViewById(R.id.rvFavorites);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        favoriteRepository = new FavoriteRepository();
        favoriteMovies = new ArrayList<>();
    }

    private void initMovieService() {
        movieApiService = MovieClient.getRetrofit().create(MovieApiService.class);
    }

    private void setupRecyclerView() {
        adapter = new MovieAdapter(favoriteMovies, requireContext(), this);
        rvFavorites.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvFavorites.setAdapter(adapter);
    }

    private void loadFavoriteMovies() {
        showLoading();
        favoriteRepository.getFavoriteIds(new FavoriteRepository.OnFavoriteIdsLoaded() {
            @Override
            public void onLoaded(List<Integer> favoriteIds) {
                if (favoriteIds.isEmpty()) {
                    showEmptyState();
                } else {
                    loadMovieDetails(favoriteIds);
                }
            }

            @Override
            public void onError(String error) {
                showError(error);
            }
        });
    }

    private void loadMovieDetails(List<Integer> movieIds) {
        showLoading();
        favoriteMovies.clear();

        final AtomicInteger loadedCount = new AtomicInteger(0);
        final int totalMovies = movieIds.size();

        for (Integer movieId : movieIds) {
            movieApiService.getMovieById(movieId, MovieClient.BEARER_TOKEN).enqueue(new retrofit2.Callback<Movie>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<Movie> call, @NonNull retrofit2.Response<Movie> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Movie movie = response.body();
                        movie.setFavorite(true);
                        favoriteMovies.add(movie);
                    }

                    checkLoadingComplete(loadedCount.incrementAndGet(), totalMovies);
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<Movie> call, @NonNull Throwable t) {
                    checkLoadingComplete(loadedCount.incrementAndGet(), totalMovies);
                }
            });
        }
    }

    @MainThread
    private void checkLoadingComplete(int loadedCount, int totalMovies) {
        if (loadedCount == totalMovies) {
            if (favoriteMovies.isEmpty()) {
                showEmptyState();
            } else {
                adapter.notifyDataSetChanged();
                showContent();
            }
        }
    }

    @Override
    public void onFavoriteClick(Movie movie) {
        if (movie.isFavorite()) {
            favoriteRepository.removeFromFavorites(movie, task -> {
                if (task.isSuccessful()) {
                    movie.setFavorite(false);
                    favoriteMovies.remove(movie);
                    adapter.notifyDataSetChanged();
                    if (favoriteMovies.isEmpty()) {
                        showEmptyState();
                    }
                    showMessage("Removed from favorites");
                } else {
                    showError("Failed to remove from favorites");
                }
            });
        }
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

    private void showError(String message) {
        showMessage(message);
        showEmptyState();
    }

    private void showMessage(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
