package com.example.moviesapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.moviesapp.R;
import com.example.moviesapp.activities.SeeAllActivity;
import com.example.moviesapp.adapters.FetchMoviesAdapter;
import com.example.moviesapp.adapters.SlidersAdapter;
import com.example.moviesapp.entities.Movie;
import com.example.moviesapp.entities.SliderItems;
import com.example.moviesapp.entities.User;
import com.example.moviesapp.interfaces.MovieApi;
import com.example.moviesapp.response.MovieResponse;
import com.example.moviesapp.response.SliderResponse;
import com.example.moviesapp.retrofit.MovieClient;
import com.example.moviesapp.util.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExplorerFragment extends Fragment {
    private final Handler slideHandler = new Handler();
    private MovieApi movieApi = MovieClient.getRetrofit().create(MovieApi.class);
    private ViewPager2 viewPager2;
    private final Runnable slidersRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };
    private User currentUser;
    private EditText edtSearch;
    private TextView txtUserName;
    private TextView txtEmail;
    private ImageView imgAvatar;
    private ImageView backImg;
    private List<SliderItems> sliderItems;
    private SlidersAdapter slidersAdapter;
    private ProgressBar progressBarBanner;

    private RecyclerView recyclerViewTopMovies;
    private ProgressBar progressBarTopMovies;
    private List<Movie> topMovies;
    private FetchMoviesAdapter topMoviesAdapter;

    private RecyclerView recyclerViewUpcoming;
    private ProgressBar progressBarUpcoming;
    private List<Movie> upcomingMovies;
    private FetchMoviesAdapter upcomingMovieAdapter;

    private RecyclerView recyclerViewNowPlaying;
    private ProgressBar progressBarNowPlaying;
    private List<Movie> nowPlayingMovies;
    private FetchMoviesAdapter nowPlayingMovieAdapter;

    private TextView txtSeeAllTopMovies;
    private TextView txtSeeAllUpcoming;
    private TextView txtSeeAllNowPlaying;
    private String selectedImagePath = "";

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
        txtUserName = view.findViewById(R.id.txtUserName);
        txtEmail = view.findViewById(R.id.txtEmail);
        imgAvatar = view.findViewById(R.id.imgAvatar);

        recyclerViewTopMovies = view.findViewById(R.id.recyclerViewTopMovies);
        progressBarTopMovies = view.findViewById(R.id.progressBarTopMovies);
        edtSearch = view.findViewById(R.id.edtSearch);
        backImg = view.findViewById(R.id.backImg);

        topMovies = new ArrayList<>();
        recyclerViewTopMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        topMoviesAdapter = new FetchMoviesAdapter(topMovies, this);
        recyclerViewTopMovies.setAdapter(topMoviesAdapter);

        recyclerViewUpcoming = view.findViewById(R.id.recyclerViewUpcoming);
        progressBarUpcoming = view.findViewById(R.id.progressBarUpcoming);
        upcomingMovies = new ArrayList<>();
        upcomingMovieAdapter = new FetchMoviesAdapter(upcomingMovies, this);
        recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewUpcoming.setAdapter(upcomingMovieAdapter);

        recyclerViewNowPlaying = view.findViewById(R.id.recyclerViewNowPlaying);
        progressBarNowPlaying = view.findViewById(R.id.progressBarNowPlaying);
        nowPlayingMovies = new ArrayList<>();
        nowPlayingMovieAdapter = new FetchMoviesAdapter(nowPlayingMovies, this);
        recyclerViewNowPlaying.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewNowPlaying.setAdapter(nowPlayingMovieAdapter);

        txtSeeAllTopMovies = view.findViewById(R.id.txtSeeAllTopMovies);
        txtSeeAllUpcoming = view.findViewById(R.id.txtSeeAllUpcoming);
        txtSeeAllNowPlaying = view.findViewById(R.id.txtSeeAllNowPlaying);
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
        txtSeeAllNowPlaying.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SeeAllActivity.class);
            intent.putExtra("titleNowPlayingMovie", "Now Playing Movies");
            startActivity(intent);
        });
        bannerSlider();
        fetchMovies();
        fetchTopMovies();
        fetchUpcomingMovies();
        fetchNowPlayingMovies();
        edtSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Fragment selectedFragment = null;
                selectedFragment = new SearchFragment();
                requireActivity().findViewById(R.id.scrollView).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.fragment_search).setVisibility(View.VISIBLE);
                requireActivity().findViewById(R.id.layoutProfile).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.chipNavigationBar).setVisibility(View.GONE);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_search, new SearchFragment())
                        .addToBackStack(null)
                        .commit();
                backImg.setVisibility(View.VISIBLE);
            }
        });
        backImg.setOnClickListener(v -> {
            hideKeyboard();
            requireActivity().findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
            requireActivity().findViewById(R.id.fragment_search).setVisibility(View.GONE);
            requireActivity().findViewById(R.id.layoutProfile).setVisibility(View.VISIBLE);
            requireActivity().findViewById(R.id.chipNavigationBar).setVisibility(View.VISIBLE);
            backImg.setVisibility(View.GONE);
            requireActivity().getSupportFragmentManager().popBackStack();
            edtSearch.setText("");
            edtSearch.clearFocus();
            edtSearch.setFocusableInTouchMode(true);
        });
        loadUserProfile();
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
        Call<SliderResponse> call = movieApi.getPopularMovies(MovieClient.BEARER_TOKEN, "en-US", 1);
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
        Call<MovieResponse> call = movieApi.getTopMovies(MovieClient.BEARER_TOKEN, "en-US", 1);
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
        Call<MovieResponse> call = movieApi.getUpcomingMovies(MovieClient.BEARER_TOKEN, "en-US", 1);
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

    private void fetchNowPlayingMovies() {
        Call<MovieResponse> call = movieApi.getNowPlayingMovies(MovieClient.BEARER_TOKEN, "en-US", 1);
        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    nowPlayingMovies.addAll(response.body().getResults());
                    nowPlayingMovieAdapter.notifyDataSetChanged();
                    progressBarNowPlaying.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
            }
        });
    }

    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadUserProfile() {
        FirebaseUtil.getDataUser().get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                currentUser = task.getResult().getValue(User.class);
                if (currentUser != null) {
                    txtEmail.setText(currentUser.getEmail());
                    txtUserName.setText("Hello " + currentUser.getUserName());
                    if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                        if (isAdded()) {
                            Glide.with(this).load(currentUser.getAvatar()).into(imgAvatar);
                        }
                    } else {
                        imgAvatar.setImageResource(R.drawable.avatar_default);
                    }
                }
            } else {
                Toast.makeText(getContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
