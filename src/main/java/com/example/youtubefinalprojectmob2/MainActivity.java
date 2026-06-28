package com.example.youtubefinalprojectmob2;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.youtubefinalprojectmob2.adapter.MainPagerAdapter;
import com.example.youtubefinalprojectmob2.fragment.SearchResultsFragment;
import com.example.youtubefinalprojectmob2.fragment.VideoDetailsFragment;
import com.example.youtubefinalprojectmob2.model.Video;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private EditText editSearch;
    private ImageButton btnClearSearch;
    private MainPagerAdapter pagerAdapter;

    private Video defaultVideo;
    private boolean userHasSelectedVideo = false;
    private static final String API_KEY = "AIzaSyAEk7F_bbhTFUWxwJXDn5fzxviwCJYk7EY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        editSearch = findViewById(R.id.editSearch);
        btnClearSearch = findViewById(R.id.btnClearSearch);

        pagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? getString(R.string.tab_search) : getString(R.string.tab_details));
        }).attach();

        setupSearchBox();
    }

    private void setupSearchBox() {
        editSearch.setOnEditorActionListener((v, actionId, event) -> {
            boolean isSearchAction = actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            if (isSearchAction) {
                submitSearch();
                return true;
            }
            return false;
        });

        editSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? android.view.View.VISIBLE : android.view.View.GONE);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) { }
        });

        btnClearSearch.setOnClickListener(v -> editSearch.setText(""));
    }

    private void submitSearch() {
        String query = editSearch.getText().toString().trim();
        if (query.isEmpty()) return;

        userHasSelectedVideo = false;
        viewPager.setCurrentItem(0, true);

        SearchResultsFragment fragment = getSearchResultsFragment();
        if (fragment != null) {
            fragment.performSearch(query);
        }
    }

    public void onVideoSelected(Video video) {
        userHasSelectedVideo = true;
        VideoDetailsFragment fragment = getVideoDetailsFragment();
        if (fragment != null) {
            fragment.showVideo(video);
        }
        viewPager.setCurrentItem(1, true);
    }


    public void onDefaultVideo(Video video) {
        defaultVideo = video;
        if (!userHasSelectedVideo) {
            VideoDetailsFragment fragment = getVideoDetailsFragment();
            if (fragment != null) {
                fragment.showVideo(video);
            }
        }
    }

    private SearchResultsFragment getSearchResultsFragment() {
        Fragment f = getSupportFragmentManager()
                .findFragmentByTag("f" + pagerAdapter.getItemId(0));
        return f instanceof SearchResultsFragment ? (SearchResultsFragment) f : null;
    }

    private VideoDetailsFragment getVideoDetailsFragment() {
        Fragment f = getSupportFragmentManager()
                .findFragmentByTag("f" + pagerAdapter.getItemId(1));
        return f instanceof VideoDetailsFragment ? (VideoDetailsFragment) f : null;
    }

    //Options Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            submitSearch();
            return true;
        } else if (id == R.id.action_clear) {
            editSearch.setText("");
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about_title)
                .setMessage(R.string.about_message)
                .setPositiveButton(R.string.dialog_ok, null)
                .show();
    }
}
