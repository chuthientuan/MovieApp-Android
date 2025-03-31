package com.example.moviesapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class SearchFragment extends Fragment {
    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyYmU0MTRlYTZmZDg5NjFmOGQ2Y2Y0NjQ2MGJhMTgyZCIsIm5iZiI6MTc0MDM4NzQ3Ni42OTUwMDAyLCJzdWIiOiI2N2JjMzQ5NDc0MTE1MmIwNDIwYWJjMGEiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.jXswQOY-SSxrfNtB5DlxJt6MWHsqGaUieY9xvjV-lOs";
    private EditText edtSearch;
    private TextView txtNoMoive;
    private RecyclerView recyclerViewList;
    private MovieAdapter movieAdapter;
    private List<Movie> movies;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtSearch = requireActivity().findViewById(R.id.edtSearch);
        txtNoMoive = view.findViewById(R.id.txtNoMoive);
        recyclerViewList = view.findViewById(R.id.recyclerViewList);
        recyclerViewList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        movies = new ArrayList<>();
        movieAdapter = new MovieAdapter(movies, getContext());
        recyclerViewList.setAdapter(movieAdapter);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    searchMovies(query);
                    txtNoMoive.setVisibility(View.GONE);
                } else {
                    movies.clear();
                    movieAdapter.notifyDataSetChanged();
                    txtNoMoive.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchMovies(String query) {
        MovieApi movieApi = MovieClient.getRetrofit().create(MovieApi.class);
        Call<MovieResponse> call = movieApi.searchMovies(BEARER_TOKEN, query, "en-US");
        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movies.clear();
                    movies.addAll(response.body().getResults());
                    movieAdapter.notifyDataSetChanged();
                    if (movies.isEmpty()) {
                        txtNoMoive.setVisibility(View.VISIBLE);
                    } else {
                        txtNoMoive.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
            }
        });
    }
}
