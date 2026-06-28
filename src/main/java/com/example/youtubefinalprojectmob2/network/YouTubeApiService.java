package com.example.youtubefinalprojectmob2.network;

import com.example.youtubefinalprojectmob2.model.YouTubeSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YouTubeApiService {

    @GET("search")
    Call<YouTubeSearchResponse> searchVideos(
            @Query("part") String part,
            @Query("q") String query,
            @Query("type") String type,
            @Query("maxResults") int maxResults,
            @Query("key") String apiKey
    );
}
