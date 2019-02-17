package com.example.clock.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.clock.models.Tab;

import java.util.ArrayList;
import java.util.List;

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Tab> mListTabs = new ArrayList<>();

    public TabFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return mListTabs.get(i).getContent();
    }

    @Override
    public int getCount() {
        return mListTabs.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mListTabs.get(position).getTitle();
    }

    public void addNewTab(Tab tab) {
        mListTabs.add(tab);
    }

}
