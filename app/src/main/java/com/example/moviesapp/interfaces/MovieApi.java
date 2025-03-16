package com.example.moviesapp.interfaces;

import com.example.moviesapp.response.SliderResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface MovieApi {
    @GET("/3/movie/popular")
    Call<SliderResponse> getPopularMovies(
            @Header("Authorization") String authToken,
            @Query("language") String language,
            @Query("page") int page
    );
}
