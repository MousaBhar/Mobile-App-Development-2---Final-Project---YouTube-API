package com.example.youtubefinalprojectmob2.adapter;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.youtubefinalprojectmob2.R;
import com.example.youtubefinalprojectmob2.model.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private final List<Video> videoList = new ArrayList<>();
    private final OnVideoClickListener listener;

    public VideoAdapter(OnVideoClickListener listener) {
        this.listener = listener;
    }

    public void setVideos(List<Video> newVideos) {
        videoList.clear();
        if (newVideos != null) {
            videoList.addAll(newVideos);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        videoList.clear();
        notifyDataSetChanged();
    }

    public List<Video> getVideos() {
        return videoList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);
        holder.bind(video, listener);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgThumbnail;
        private final TextView txtTitle;
        private final TextView txtChannel;
        private final TextView txtPublished;
        private final ImageButton btnMore;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtChannel = itemView.findViewById(R.id.txtChannel);
            txtPublished = itemView.findViewById(R.id.txtPublished);
            btnMore = itemView.findViewById(R.id.btnMore);
        }

        void bind(Video video, OnVideoClickListener listener) {
            txtTitle.setText(video.getTitle());
            txtChannel.setText(video.getChannelTitle());
            txtPublished.setText(formatDate(video.getPublishedAt()));

            Glide.with(itemView.getContext())
                    .load(video.getThumbnailUrl())
                    .placeholder(R.drawable.bg_thumbnail_placeholder)
                    .error(R.drawable.bg_thumbnail_placeholder)
                    .centerCrop()
                    .into(imgThumbnail);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onVideoClick(video);
            });

            btnMore.setOnClickListener(v -> showPopupMenu(v, video, listener));
        }

        private void showPopupMenu(View anchor, Video video, OnVideoClickListener listener) {
            PopupMenu popupMenu = new PopupMenu(anchor.getContext(), anchor);
            popupMenu.inflate(R.menu.menu_video_item);
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_view_details) {
                    if (listener != null) listener.onVideoClick(video);
                    return true;
                } else if (id == R.id.action_share) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT,
                            video.getTitle() + "\n" + video.getYoutubeUrl());
                    anchor.getContext().startActivity(Intent.createChooser(
                            shareIntent, anchor.getContext().getString(R.string.menu_share)));
                    return true;
                } else if (id == R.id.action_open_youtube) {
                    Intent openIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(video.getYoutubeUrl()));
                    anchor.getContext().startActivity(openIntent);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        }

        private String formatDate(String isoDate) {
            if (TextUtils.isEmpty(isoDate)) return "";
            try {
                // YouTube API returns e.g. 2026-06-12T10:00:00Z
                String datePart = isoDate.split("T")[0];
                String[] parts = datePart.split("-");
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                int monthIndex = Integer.parseInt(parts[1]) - 1;
                return String.format(Locale.getDefault(), "%s %s, %s",
                        months[monthIndex], parts[2], parts[0]);
            } catch (Exception e) {
                return isoDate;
            }
        }
    }
}
