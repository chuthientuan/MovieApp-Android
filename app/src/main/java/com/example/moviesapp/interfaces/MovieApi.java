package com.example.moviesapp.interfaces;

import com.example.moviesapp.entities.DetailMovie;
import com.example.moviesapp.response.MovieResponse;
import com.example.moviesapp.response.SliderResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApi {
    @GET("/3/movie/popular")
    Call<SliderResponse> getPopularMovies(
            @Header("Authorization") String authToken,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("/3/movie/top_rated")
    Call<MovieResponse> getTopMovies(
            @Header("Authorization") String authToken,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("/3/movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(
            @Header("Authorization") String authToken,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("/3/movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(
            @Header("Authorization") String authToken,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("/3/movie/{movie_id}")
    Call<DetailMovie> getMovieDetail(
            @Header("Authorization") String authToken,
            @Path("movie_id") int movieId,
            @Query("language") String language
    );

    @GET("/3/movie/{movie_id}/credits")
    Call<DetailMovie> getMovieCredits(
            @Header("Authorization") String authToken,
            @Path("movie_id") int movieId,
            @Query("language") String language
    );

    @GET("/3/movie/{movie_id}/videos")
    Call<DetailMovie> getMovieTrailer(
            @Header("Authorization") String authToken,
            @Path("movie_id") int movieId,
            @Query("language") String language
    );

    @GET("/3/search/movie")
    Call<MovieResponse> searchMovies(
            @Header("Authorization") String authToken,
            @Query("query") String query,
            @Query("language") String language
    );
}
