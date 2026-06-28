package com.example.youtubefinalprojectmob2.network;

import android.os.AsyncTask;

import com.example.youtubefinalprojectmob2.model.Video;
import com.example.youtubefinalprojectmob2.model.YouTubeSearchItem;
import com.example.youtubefinalprojectmob2.model.YouTubeSearchResponse;
import com.example.youtubefinalprojectmob2.util.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/** Module 4.1 (AsyncTask and AsyncTaskLoader): */
public class SearchVideosTask extends AsyncTask<String, Void, List<Video>> {

    public interface SearchCallback {
        void onSearchSuccess(List<Video> videos, String query);
        void onSearchError(String message);
    }

    private final SearchCallback callback;
    private String errorMessage;
    private String lastQuery;

    public SearchVideosTask(SearchCallback callback) {
        this.callback = callback;
    }

    /** onPreExecute()-> runs on UI thread, hook before background work */

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

/** doInBackground()-> runs on a worker thread, performs the (synchronous) Retrofit call and maps the response into our Video model list */
    @Override
    protected List<Video> doInBackground(String... params) {
        String query = params[0];
        lastQuery = query;
        List<Video> videos = new ArrayList<>();

        try {
            YouTubeApiService service = RetrofitClient.getApiService();
            Call<YouTubeSearchResponse> call = service.searchVideos(
                    Constants.SEARCH_PART,
                    query,
                    Constants.SEARCH_TYPE,
                    Constants.MAX_RESULTS,
                    Constants.YOUTUBE_API_KEY
            );


            Response<YouTubeSearchResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                List<YouTubeSearchItem> items = response.body().getItems();
                if (items != null) {
                    for (YouTubeSearchItem item : items) {
                        if (item.getId() == null || item.getSnippet() == null) continue;

                        String videoId = item.getId().getVideoId();
                        YouTubeSearchItem.Snippet snippet = item.getSnippet();
                        String thumbnailUrl = snippet.getThumbnails() != null
                                ? snippet.getThumbnails().getBestUrl()
                                : null;

                        videos.add(new Video(
                                videoId,
                                snippet.getTitle(),
                                snippet.getDescription(),
                                snippet.getChannelTitle(),
                                snippet.getPublishedAt(),
                                thumbnailUrl
                        ));
                    }
                }
            } else {
                errorMessage = "HTTP " + response.code() + ": " + response.message();
            }
        } catch (Exception e) {
            errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown network error";
        }

        return videos;
    }

    /** onPostExecute()-> runs on UI thread, delivers results/errors back to the calling Fragment via the callback*/
    @Override
    protected void onPostExecute(List<Video> videos) {
        super.onPostExecute(videos);
        if (callback == null) return;

        if (errorMessage != null && videos.isEmpty()) {
            callback.onSearchError(errorMessage);
        } else {
            callback.onSearchSuccess(videos, lastQuery);
        }
    }
}
