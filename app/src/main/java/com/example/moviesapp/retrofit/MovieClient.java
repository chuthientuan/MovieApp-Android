package com.example.moviesapp.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieClient {
    public static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyYmU0MTRlYTZmZDg5NjFmOGQ2Y2Y0NjQ2MGJhMTgyZCIsIm5iZiI6MTc0MDM4NzQ3Ni42OTUwMDAyLCJzdWIiOiI2N2JjMzQ5NDc0MTE1MmIwNDIwYWJjMGEiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.jXswQOY-SSxrfNtB5DlxJt6MWHsqGaUieY9xvjV-lOs";
    private static final String BASE_URL = "https://api.themoviedb.org";
    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
