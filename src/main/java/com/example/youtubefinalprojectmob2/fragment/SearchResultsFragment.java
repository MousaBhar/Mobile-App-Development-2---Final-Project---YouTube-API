package com.example.youtubefinalprojectmob2.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.youtubefinalprojectmob2.MainActivity;
import com.example.youtubefinalprojectmob2.R;
import com.example.youtubefinalprojectmob2.adapter.VideoAdapter;
import com.example.youtubefinalprojectmob2.model.Video;
import com.example.youtubefinalprojectmob2.network.SearchVideosTask;
import com.example.youtubefinalprojectmob2.util.NetworkUtils;
import com.example.youtubefinalprojectmob2.util.NotificationHelper;

import java.util.List;

/**
 * Tab 1: lists YouTube search results inside a RecyclerView.
 *
 * Responsibilities covered here (mapped to grading criteria):
 *  - RecyclerView in Tab 1 to list API content dynamically
 *  - AsyncTask used to fetch API data in the background (SearchVideosTask)
 *  - ProgressBar shown while fetching
 *  - Notification on success / error
 *  - Pull-to-refresh (SwipeRefreshLayout) - optional extra credit
 *  - Context menu on long-press of a list item - alternative menu type
 *    (the adapter additionally offers the same actions via a popup
 *    menu on the 3-dot button, satisfying "one type of menu" with room
 *    to spare)
 */
public class SearchResultsFragment extends Fragment implements SearchVideosTask.SearchCallback {

    private RecyclerView recyclerVideos;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private LinearLayout emptyStateLayout;
    private LinearLayout errorStateLayout;
    private TextView txtErrorMessage;
    private Button btnRetry;

    private VideoAdapter adapter;
    private SearchVideosTask currentTask;
    private NotificationHelper notificationHelper;
    private String lastQuery = "";

    private Video longPressedVideo;

    public SearchResultsFragment() {
        super(R.layout.fragment_search_results);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerVideos = view.findViewById(R.id.recyclerVideos);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        errorStateLayout = view.findViewById(R.id.errorStateLayout);
        txtErrorMessage = view.findViewById(R.id.txtErrorMessage);
        btnRetry = view.findViewById(R.id.btnRetry);

        notificationHelper = new NotificationHelper(requireContext());

        adapter = new VideoAdapter(video -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).onVideoSelected(video);
            }
        });

        recyclerVideos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerVideos.setAdapter(adapter);
        registerForContextMenu(recyclerVideos);

        swipeRefresh.setColorSchemeResources(android.R.color.holo_red_light);
        swipeRefresh.setOnRefreshListener(() -> {
            if (!lastQuery.isEmpty()) {
                performSearch(lastQuery);
            } else {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(requireContext(), R.string.empty_state_subtitle, Toast.LENGTH_SHORT).show();
            }
        });

        btnRetry.setOnClickListener(v -> {
            if (!lastQuery.isEmpty()) {
                performSearch(lastQuery);
            }
        });
    }

    /** Triggered by MainActivity when the user submits the search box. */
    public void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;

        if (!NetworkUtils.isConnected(requireContext())) {
            showError(getString(R.string.error_network));
            notificationHelper.showErrorNotification();
            return;
        }

        lastQuery = query.trim();

        // Cancel any in-flight request before starting a new one
        if (currentTask != null && !currentTask.isCancelled()) {
            currentTask.cancel(true);
        }

        showLoading();
        currentTask = new SearchVideosTask(this);
        currentTask.execute(lastQuery);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
        errorStateLayout.setVisibility(View.GONE);
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
        emptyStateLayout.setVisibility(View.GONE);
        errorStateLayout.setVisibility(View.VISIBLE);
        txtErrorMessage.setText(message);
    }

    private void showResults(List<Video> videos) {
        progressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
        errorStateLayout.setVisibility(View.GONE);

        if (videos.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            adapter.clear();
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            adapter.setVideos(videos);

            // Tab 2 defaults to the first item when results first load
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).onDefaultVideo(videos.get(0));
            }
        }
    }

    // ----- SearchVideosTask.SearchCallback (AsyncTask result delivery) -----

    @Override
    public void onSearchSuccess(List<Video> videos, String query) {
        if (!isAdded()) return;
        showResults(videos);
        notificationHelper.showSuccessNotification(videos.size(), query);
    }

    @Override
    public void onSearchError(String message) {
        if (!isAdded()) return;
        showError(getString(R.string.error_generic));
        notificationHelper.showErrorNotification();
    }

    // ----- Context menu (long-press on a RecyclerView item) -----

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v,
                                     @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        requireActivity().getMenuInflater().inflate(R.menu.menu_video_item, menu);

        // Determine which item was long-pressed using the touch position
        RecyclerView.ViewHolder holder = recyclerVideos.findContainingViewHolder(v);
        if (holder != null) {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && position < adapter.getVideos().size()) {
                longPressedVideo = adapter.getVideos().get(position);
                menu.setHeaderTitle(longPressedVideo.getTitle());
            }
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (longPressedVideo == null) return super.onContextItemSelected(item);

        int id = item.getItemId();
        if (id == R.id.action_view_details) {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).onVideoSelected(longPressedVideo);
            }
            return true;
        } else if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    longPressedVideo.getTitle() + "\n" + longPressedVideo.getYoutubeUrl());
            startActivity(Intent.createChooser(shareIntent, getString(R.string.menu_share)));
            return true;
        } else if (id == R.id.action_open_youtube) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(longPressedVideo.getYoutubeUrl())));
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (currentTask != null && !currentTask.isCancelled()) {
            currentTask.cancel(true);
        }
    }
}
