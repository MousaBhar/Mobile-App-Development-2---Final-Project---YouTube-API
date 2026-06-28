package com.example.youtubefinalprojectmob2.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.youtubefinalprojectmob2.R;
import com.example.youtubefinalprojectmob2.model.Video;
import com.example.youtubefinalprojectmob2.util.Constants;

import java.util.Locale;

import android.content.Intent;
import android.net.Uri;

/**
 * Tab 2: shows the details of whichever video the user tapped in
 * Tab 1's RecyclerView. If nothing has been selected yet, MainActivity
 * defaults this to the first item in the loaded list (per the
 * project's requirement: "Tab 2 must display item details, defaulting
 * to the first item if none is selected").
 */
public class VideoDetailsFragment extends Fragment {

    private ScrollView scrollDetails;
    private LinearLayout placeholderLayout;
    private ImageView imgDetailThumbnail;
    private TextView txtDetailTitle;
    private TextView txtDetailChannel;
    private TextView txtDetailPublished;
    private TextView txtDetailDescription;
    private Button btnWatch;

    // Holds the currently selected video so it survives until the
    // view is recreated (e.g. on a tab swipe back to this Fragment).
    private Video currentVideo;

    public VideoDetailsFragment() {
        super(R.layout.fragment_video_details);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollDetails = view.findViewById(R.id.scrollDetails);
        placeholderLayout = view.findViewById(R.id.placeholderLayout);
        imgDetailThumbnail = view.findViewById(R.id.imgDetailThumbnail);
        txtDetailTitle = view.findViewById(R.id.txtDetailTitle);
        txtDetailChannel = view.findViewById(R.id.txtDetailChannel);
        txtDetailPublished = view.findViewById(R.id.txtDetailPublished);
        txtDetailDescription = view.findViewById(R.id.txtDetailDescription);
        btnWatch = view.findViewById(R.id.btnWatch);

        // Prefer arguments passed at construction time (e.g. process
        // restart / configuration change); fall back to whatever was
        // set in-memory via showVideo() before the view existed.
        if (getArguments() != null && getArguments().getSerializable(Constants.ARG_SELECTED_VIDEO) != null) {
            currentVideo = (Video) getArguments().getSerializable(Constants.ARG_SELECTED_VIDEO);
        }

        if (currentVideo != null) {
            displayVideo(currentVideo);
        }
    }

    /**
     * Called by MainActivity (passing data between Fragments) whenever
     * the user selects a video from Tab 1, or to set the default
     * (first item) when results first load.
     */
    public void showVideo(Video video) {
        if (video == null) return;
        currentVideo = video;

        if (isAdded() && txtDetailTitle != null) {
            displayVideo(video);
        }
        // If the view isn't ready yet, currentVideo will be picked
        // up in onViewCreated() once it is.
    }

    private void displayVideo(Video video) {
        placeholderLayout.setVisibility(View.GONE);
        scrollDetails.setVisibility(View.VISIBLE);

        txtDetailTitle.setText(video.getTitle());
        txtDetailChannel.setText(video.getChannelTitle());
        txtDetailPublished.setText(
                getString(R.string.published_on, formatDate(video.getPublishedAt())));
        txtDetailDescription.setText(
                video.getDescription() == null || video.getDescription().isEmpty()
                        ? getString(R.string.no_results)
                        : video.getDescription());

        Glide.with(requireContext())
                .load(video.getThumbnailUrl())
                .placeholder(R.drawable.bg_thumbnail_placeholder)
                .error(R.drawable.bg_thumbnail_placeholder)
                .centerCrop()
                .into(imgDetailThumbnail);

        btnWatch.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getYoutubeUrl()));
            startActivity(intent);
        });
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        try {
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
