package com.example.moviesapp.activities;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moviesapp.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class TrailerActivity extends AppCompatActivity {
    YouTubePlayer youTubePlayer;
    private YouTubePlayerView youtubePlayerView;
    private String videoKey;
    private FrameLayout full_screen_view_container;
    private TextView trailerTitle;
    private TextView trailerDescription;
    private ImageView backImg;
    private boolean isFullScreen = false;
    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (isFullScreen) {
                youTubePlayer.toggleFullscreen();
            } else {
                finish();
            }
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trailer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        backImg = findViewById(R.id.backImg);
        backImg.setOnClickListener(v -> finish());
        trailerTitle = findViewById(R.id.trailerTitle);
        trailerDescription = findViewById(R.id.trailerDescription);
        trailerTitle.setText(getIntent().getStringExtra("title") + " - Official Trailer");
        trailerDescription.setText(getIntent().getStringExtra("description"));
        videoKey = getIntent().getStringExtra("videoKey");
        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        full_screen_view_container = findViewById(R.id.full_screen_view_container);

        getLifecycle().addObserver(youtubePlayerView);
        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .fullscreen(1)
                .build();
        youtubePlayerView.setEnableAutomaticInitialization(false);
        youtubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                TrailerActivity.this.youTubePlayer = youTubePlayer;
                youTubePlayer.loadVideo(videoKey, 0);
                youTubePlayer.pause();
            }
        }, iFramePlayerOptions);

        youtubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View view, @NonNull Function0<Unit> function0) {
                isFullScreen = true;
                youtubePlayerView.setVisibility(View.GONE);
                full_screen_view_container.setVisibility(View.VISIBLE);
                full_screen_view_container.addView(view);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Objects.requireNonNull(getWindow().getInsetsController()).hide(WindowInsetsCompat.Type.systemBars());
                }
            }

            @Override
            public void onExitFullscreen() {
                isFullScreen = false;
                youtubePlayerView.setVisibility(View.VISIBLE);
                full_screen_view_container.setVisibility(View.GONE);
                full_screen_view_container.removeAllViews();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Objects.requireNonNull(getWindow().getInsetsController()).show(WindowInsetsCompat.Type.systemBars());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youtubePlayerView.release();
    }
}