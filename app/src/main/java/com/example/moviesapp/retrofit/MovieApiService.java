package com.example.moviesapp.retrofit;

import com.example.moviesapp.entities.Movie;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiService {
    @GET("movie/{id}")
    Call<Movie> getMovieById(
            @Path("id") int id,
            @Query("api_key") String apiKey
    );
}