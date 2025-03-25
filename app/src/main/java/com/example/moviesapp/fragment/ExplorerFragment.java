package com.example.moviesapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.moviesapp.R;
import com.example.moviesapp.activities.SeeAllActivity;
import com.example.moviesapp.adapters.SlidersAdapter;
import com.example.moviesapp.adapters.TopMoviesAdapter;
import com.example.moviesapp.adapters.UpcomingMovieAdapter;
import com.example.moviesapp.entities.Movie;
import com.example.moviesapp.entities.SliderItems;
import com.example.moviesapp.interfaces.MovieApi;
import com.example.moviesapp.response.MovieResponse;
import com.example.moviesapp.response.SliderResponse;
import com.example.moviesapp.retrofit.MovieClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExplorerFragment extends Fragment {
    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyYmU0MTRlYTZmZDg5NjFmOGQ2Y2Y0NjQ2MGJhMTgyZCIsIm5iZiI6MTc0MDM4NzQ3Ni42OTUwMDAyLCJzdWIiOiI2N2JjMzQ5NDc0MTE1MmIwNDIwYWJjMGEiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.jXswQOY-SSxrfNtB5DlxJt6MWHsqGaUieY9xvjV-lOs";
    private final Handler slideHandler = new Handler();
    private ViewPager2 viewPager2;
    private final Runnable slidersRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };
    private List<SliderItems> sliderItems;
    private SlidersAdapter slidersAdapter;
    private ProgressBar progressBarBanner;

    private RecyclerView recyclerViewTopMovies;
    private ProgressBar progressBarTopMovies;
    private List<Movie> topMovies;
    private TopMoviesAdapter topMoviesAdapter;

    private RecyclerView recyclerViewUpcoming;
    private ProgressBar progressBarUpcoming;
    private List<Movie> upcomingMovies;
    private UpcomingMovieAdapter upcomingMovieAdapter;

    private TextView txtSeeAllTopMovies;
    private TextView txtSeeAllUpcoming;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explorer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager2 = view.findViewById(R.id.viewPage2);
        sliderItems = new ArrayList<>();
        slidersAdapter = new SlidersAdapter(sliderItems, viewPager2);
        viewPager2.setAdapter(slidersAdapter);
        progressBarBanner = view.findViewById(R.id.progressBarBanner);

        recyclerViewTopMovies = view.findViewById(R.id.recyclerViewTopMovies);
        progressBarTopMovies = view.findViewById(R.id.progressBarTopMovies);
        topMovies = new ArrayList<>();
        recyclerViewTopMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        topMoviesAdapter = new TopMoviesAdapter(topMovies, this);
        recyclerViewTopMovies.setAdapter(topMoviesAdapter);

        recyclerViewUpcoming = view.findViewById(R.id.recyclerViewUpcoming);
        progressBarUpcoming = view.findViewById(R.id.progressBarUpcoming);
        upcomingMovies = new ArrayList<>();
        upcomingMovieAdapter = new UpcomingMovieAdapter(upcomingMovies, this);
        recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewUpcoming.setAdapter(upcomingMovieAdapter);

        txtSeeAllTopMovies = view.findViewById(R.id.txtSeeAllTopMovies);
        txtSeeAllUpcoming = view.findViewById(R.id.txtSeeAllUpcoming);
        txtSeeAllTopMovies.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SeeAllActivity.class);
            intent.putExtra("titleTopMovie", "Top Movies");
            startActivity(intent);
        });
        txtSeeAllUpcoming.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SeeAllActivity.class);
            intent.putExtra("titleUpcomingMovie", "Upcoming Movies");
            startActivity(intent);
        });

        bannerSlider();
        fetchMovies();
        fetchTopMovies();
        fetchUpcomingMovies();
    }

    private void bannerSlider() {
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.setCurrentItem(1);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                slideHandler.removeCallbacks(slidersRunnable);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(slidersRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        slideHandler.postDelayed(slidersRunnable, 2000);
    }

    private void fetchMovies() {
        MovieApi movieApi = MovieClient.getRetrofit().create(MovieApi.class);
        Call<SliderResponse> call = movieApi.getPopularMovies(BEARER_TOKEN, "en-US", 1);

        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<SliderResponse> call, @NonNull Response<SliderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sliderItems.addAll(response.body().getResults());
                    slidersAdapter.notifyDataSetChanged();
                    progressBarBanner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SliderResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
            }
        });
    }

    private void fetchTopMovies() {
        MovieApi movieApi = MovieClient.getRetrofit().create(MovieApi.class);
        Call<MovieResponse> call = movieApi.getTopMovies(BEARER_TOKEN, "en-US", 1);
        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topMovies.addAll(response.body().getResults());
                    topMoviesAdapter.notifyDataSetChanged();
                    progressBarTopMovies.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
            }
        });
    }

    private void fetchUpcomingMovies() {
        MovieApi movieApi = MovieClient.getRetrofit().create(MovieApi.class);
        Call<MovieResponse> call = movieApi.getUpcomingMovies(BEARER_TOKEN, "en-US", 1);
        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    upcomingMovies.addAll(response.body().getResults());
                    upcomingMovieAdapter.notifyDataSetChanged();
                    progressBarUpcoming.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
            }
        });
    }
}
