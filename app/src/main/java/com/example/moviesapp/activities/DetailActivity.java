package com.example.moviesapp.activities;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviesapp.R;
import com.example.moviesapp.adapters.ActorAdapter;
import com.example.moviesapp.adapters.GenreAdapter;
import com.example.moviesapp.entities.Actor;
import com.example.moviesapp.entities.DetailMovie;
import com.example.moviesapp.entities.Genre;
import com.example.moviesapp.interfaces.MovieApi;
import com.example.moviesapp.retrofit.MovieClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyYmU0MTRlYTZmZDg5NjFmOGQ2Y2Y0NjQ2MGJhMTgyZCIsIm5iZiI6MTc0MDM4NzQ3Ni42OTUwMDAyLCJzdWIiOiI2N2JjMzQ5NDc0MTE1MmIwNDIwYWJjMGEiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.jXswQOY-SSxrfNtB5DlxJt6MWHsqGaUieY9xvjV-lOs";
    private ImageView backImg, moviePic;
    private TextView txtTitle, txtMovieTimes, movieSummary, txtImbd;
    private RecyclerView recyclerViewCast;
    private ActorAdapter actorAdapter;
    private List<Actor> actors;
    private AppCompatButton btnWatchTrailer;
    private BlurView blurView;
    private int movieId;

    private RecyclerView recyclerViewGenre;
    private GenreAdapter genreAdapter;
    private List<Genre> genres;
    private DetailMovie detailMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        backImg = findViewById(R.id.backImg);
        moviePic = findViewById(R.id.moviePic);
        txtTitle = findViewById(R.id.txtTitle);
        txtMovieTimes = findViewById(R.id.txtMovieTimes);
        movieSummary = findViewById(R.id.movieSummary);
        txtImbd = findViewById(R.id.txtImbd);
        btnWatchTrailer = findViewById(R.id.btnWatchTrailer);

        blurView = findViewById(R.id.blurView);
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        blurView.setupWith(rootView, new RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(10f);
        blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        blurView.setClipToOutline(true);

        recyclerViewGenre = findViewById(R.id.recyclerViewGenre);
        recyclerViewGenre.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        genres = new ArrayList<>();
        genreAdapter = new GenreAdapter(genres, this);
        recyclerViewGenre.setAdapter(genreAdapter);

        recyclerViewCast = findViewById(R.id.recyclerViewCast);
        recyclerViewCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        actors = new ArrayList<>();
        actorAdapter = new ActorAdapter(actors, this);
        recyclerViewCast.setAdapter(actorAdapter);

        movieId = getIntent().getIntExtra("movieId", 0);
        backImg.setOnClickListener(v -> finish());
        fetchMovieDetails();
        fetchActor();
    }

    private void fetchMovieDetails() {
        MovieApi movieApi = MovieClient.getRetrofit().create(MovieApi.class);
        Call<DetailMovie> call = movieApi.getMovieDetail(BEARER_TOKEN, movieId, "en-US");
        call.enqueue(new Callback<>() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<DetailMovie> call, @NonNull Response<DetailMovie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    detailMovie = response.body();
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions = requestOptions.transform(new CenterCrop(),
                            new GranularRoundedCorners(0, 0, 50, 50));
                    Glide.with(DetailActivity.this)
                            .load(detailMovie.getPoster_path())
                            .apply(requestOptions)
                            .into(moviePic);
                    txtTitle.setText(detailMovie.getTitle());
                    txtMovieTimes.setText(detailMovie.getRelease_date() + " - " + detailMovie.getRuntime() + " minutes");
                    movieSummary.setText(detailMovie.getOverview());
                    txtImbd.setText("IMDB: " + detailMovie.getVote_average());
                    genres.clear();
                    genres.addAll(detailMovie.getGenres());
                    genreAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DetailMovie> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
            }
        });
    }

    private void fetchActor() {
        MovieApi movieApi = MovieClient.getRetrofit().create(MovieApi.class);
        Call<DetailMovie> call = movieApi.getMovieCredits(BEARER_TOKEN, movieId, "en-US");
        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<DetailMovie> call, @NonNull Response<DetailMovie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    detailMovie = response.body();
                    actors.clear();
                    List<Actor> mainActors = detailMovie.getActors().stream()
                            .filter(actor -> actor.getOrder() <= 5)
                            .collect(Collectors.toList());
                    actors.addAll(mainActors);
                    actorAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DetailMovie> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
            }
        });
    }
}