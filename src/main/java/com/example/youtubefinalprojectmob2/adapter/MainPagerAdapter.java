package com.example.youtubefinalprojectmob2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.youtubefinalprojectmob2.fragment.SearchResultsFragment;
import com.example.youtubefinalprojectmob2.fragment.VideoDetailsFragment;

/**
 * Supplies the two Fragments (Tab 1: results list, Tab 2: details)
 * to the ViewPager2 hosted in MainActivity, wired together with
 * TabLayoutMediator there.
 */
public class MainPagerAdapter extends FragmentStateAdapter {

    private static final int TAB_COUNT = 2;

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new SearchResultsFragment();
        } else {
            return new VideoDetailsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}
