package com.example.moviesapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.R;
import com.example.moviesapp.adapters.MovieAdapter;
import com.example.moviesapp.entities.Movie;
import com.example.moviesapp.interfaces.MovieApi;
import com.example.moviesapp.response.MovieResponse;
import com.example.moviesapp.retrofit.MovieClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeeAllActivity extends AppCompatActivity {
    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyYmU0MTRlYTZmZDg5NjFmOGQ2Y2Y0NjQ2MGJhMTgyZCIsIm5iZiI6MTc0MDM4NzQ3Ni42OTUwMDAyLCJzdWIiOiI2N2JjMzQ5NDc0MTE1MmIwNDIwYWJjMGEiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.jXswQOY-SSxrfNtB5DlxJt6MWHsqGaUieY9xvjV-lOs";
    private ImageView backImg;
    private ProgressBar progressBar;
    private LinearLayout paginationLayout;
    private TextView txtTitle;

    private RecyclerView recyclerViewList;
    private MovieAdapter movieAdapter;
    private List<Movie> movies;
    private int currentPage = 1;
    private String titleTopMovie;
    private String titleUpcomingMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_see_all);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        backImg = findViewById(R.id.backImg);
        progressBar = findViewById(R.id.progressBar);
        paginationLayout = findViewById(R.id.paginationLayout);
        txtTitle = findViewById(R.id.txtTitle);
        recyclerViewList = findViewById(R.id.recyclerViewList);
        recyclerViewList.setLayoutManager(new GridLayoutManager(this, 2));
        movies = new ArrayList<>();
        movieAdapter = new MovieAdapter(movies, this);
        recyclerViewList.setAdapter(movieAdapter);

        backImg.setOnClickListener(v -> finish());
        Intent intent = getIntent();
        titleTopMovie = intent.getStringExtra("titleTopMovie");
        titleUpcomingMovie = intent.getStringExtra("titleUpcomingMovie");
        if (titleTopMovie != null) {
            txtTitle.setText(titleTopMovie);
            fetchTopMovies(currentPage);
        } else if (titleUpcomingMovie != null) {
            txtTitle.setText(titleUpcomingMovie);
            fetchUpcomingMovies(currentPage);
        }
        setupPagination();
    }

    private void fetchTopMovies(int pageNumber) {
        MovieApi movieApi = MovieClient.getRetrofit().create(MovieApi.class);
        Call<MovieResponse> call = movieApi.getTopMovies(BEARER_TOKEN, "en-US", pageNumber);
        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movies.clear();
                    movies.addAll(response.body().getResults());
                    movieAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
            }
        });
    }

    private void fetchUpcomingMovies(int pageNumber) {
        MovieApi movieApi = MovieClient.getRetrofit().create(MovieApi.class);
        Call<MovieResponse> call = movieApi.getUpcomingMovies(BEARER_TOKEN, "en-US", pageNumber);
        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movies.clear();
                    movies.addAll(response.body().getResults());
                    movieAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
            }
        });
    }

    private void setupPagination() {
        paginationLayout.removeAllViews();

        int totalPages = 20; // Tổng số trang (có thể lấy từ API)
        int startPage = Math.max(2, currentPage - 2); // Luôn bắt đầu từ trang 2 nếu currentPage > 3
        int endPage = Math.min(totalPages - 1, currentPage + 2); // Kết thúc trước trang cuối nếu currentPage < totalPages - 2

        // Luôn thêm nút trang 1
        addPageButton(paginationLayout, 1);

        // Nếu trang bắt đầu từ 3 trở lên, thêm dấu "..."
        if (startPage > 2) {
            addEllipsis(paginationLayout);
        }

        // Hiển thị các trang từ startPage đến endPage
        for (int i = startPage; i <= endPage; i++) {
            addPageButton(paginationLayout, i);
        }

        // Nếu trang kết thúc trước totalPages - 1, thêm dấu "..."
        if (endPage < totalPages - 1) {
            addEllipsis(paginationLayout);
        }

        // Luôn thêm nút trang cuối cùng
        addPageButton(paginationLayout, totalPages);
    }

    private void addPageButton(LinearLayout parent, int pageNumber) {
        Button pageButton = new Button(this);
        pageButton.setText(String.valueOf(pageNumber));

        // Đặt kích thước nút
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                120,  // Chiều rộng (px)
                120   // Chiều cao (px)
        );
        params.setMargins(8, 8, 8, 8); // Thêm khoảng cách giữa các nút
        pageButton.setLayoutParams(params);

        // Đặt màu nền và viền cho nút
        if (pageNumber == currentPage) {
            pageButton.setBackgroundColor(getResources().getColor(R.color.red)); // Màu nổi bật khi đang chọn
            pageButton.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            pageButton.setBackgroundColor(getResources().getColor(R.color.white)); // Màu bình thường
            pageButton.setTextColor(getResources().getColor(android.R.color.black));
        }// Màu chữ

        // Bắt sự kiện click
        pageButton.setOnClickListener(v -> {
            currentPage = pageNumber;
            if (titleTopMovie != null) {
                fetchTopMovies(currentPage);
            } else if (titleUpcomingMovie != null) {
                fetchUpcomingMovies(currentPage);
            }
            setupPagination(); // Cập nhật lại danh sách số trang
        });

        parent.addView(pageButton);
    }

    private void addEllipsis(LinearLayout parent) {
        TextView ellipsis = new TextView(this);
        ellipsis.setText("...");
        ellipsis.setPadding(4, 0, 8, 0);
        parent.addView(ellipsis);
    }
}