package com.oasis.weatherapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import java.util.List;

public class FavoriteFragmentAdapter extends FragmentStatePagerAdapter {

    private List<WeatherInfo> fragmentList;
    private List<String> titleList;

    public FavoriteFragmentAdapter(FragmentManager fm, List<WeatherInfo> fragments, List<String> titles) {
        super(fm);
        this.titleList = titles;
        this.fragmentList = fragments;
    }

    public FavoriteFragmentAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        try {
            return (Fragment) fragmentList.get(position);
        } catch (Exception e) {
            System.out.println("创建或获取Favorite Fragment出错！");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}