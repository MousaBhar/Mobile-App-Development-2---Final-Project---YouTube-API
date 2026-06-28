package com.example.youtubefinalprojectmob2.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {

    private static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";

    private static Retrofit retrofit;

    private RetrofitClient() {
        // no instances
    }

    public static YouTubeApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(YouTubeApiService.class);
    }
}
