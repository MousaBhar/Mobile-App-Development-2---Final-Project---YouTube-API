package com.example.youtubefinalprojectmob2.util;

/** https://console.cloud.google.com/apis/library/youtube.googleapis.com */
public class Constants {

    // TODO: put your own YouTube Data API v3 key here before running the app
    public static final String YOUTUBE_API_KEY = "AIzaSyAEk7F_bbhTFUWxwJXDn5fzxviwCJYk7EY";

    public static final String SEARCH_PART = "snippet";
    public static final String SEARCH_TYPE = "video";
    public static final int MAX_RESULTS = 25;

    public static final String NOTIFICATION_CHANNEL_ID = "youtube_search_channel";
    public static final int NOTIFICATION_ID_SUCCESS = 1001;
    public static final int NOTIFICATION_ID_ERROR = 1002;

    public static final String ARG_SELECTED_VIDEO = "arg_selected_video";

    private Constants() {
        // no instances
    }
}
