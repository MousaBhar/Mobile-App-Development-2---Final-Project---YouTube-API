package com.example.youtubefinalprojectmob2.model;

import com.google.gson.annotations.SerializedName;

public class YouTubeSearchItem {

    @SerializedName("id")
    private VideoId id;

    @SerializedName("snippet")
    private Snippet snippet;

    public VideoId getId() {
        return id;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public static class VideoId {
        @SerializedName("videoId")
        private String videoId;

        public String getVideoId() {
            return videoId;
        }
    }

    public static class Snippet {
        @SerializedName("title")
        private String title;

        @SerializedName("description")
        private String description;

        @SerializedName("channelTitle")
        private String channelTitle;

        @SerializedName("publishedAt")
        private String publishedAt;

        @SerializedName("thumbnails")
        private Thumbnails thumbnails;

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getChannelTitle() {
            return channelTitle;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public Thumbnails getThumbnails() {
            return thumbnails;
        }
    }

    public static class Thumbnails {
        @SerializedName("high")
        private ThumbnailInfo high;

        @SerializedName("medium")
        private ThumbnailInfo medium;

        @SerializedName("default")
        private ThumbnailInfo defaultThumb;

        /** Returns the best available thumbnail URL, falling back gracefully. */
        public String getBestUrl() {
            if (high != null && high.url != null) return high.url;
            if (medium != null && medium.url != null) return medium.url;
            if (defaultThumb != null && defaultThumb.url != null) return defaultThumb.url;
            return null;
        }
    }

    public static class ThumbnailInfo {
        @SerializedName("url")
        private String url;
    }
}
