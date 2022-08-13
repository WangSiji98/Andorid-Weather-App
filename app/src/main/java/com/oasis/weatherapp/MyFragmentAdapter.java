package com.oasis.weatherapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyFragmentAdapter extends FragmentStateAdapter {

    private String location = "";

    public MyFragmentAdapter(@NonNull FragmentActivity fragmentActivity, String _location) {
        super(fragmentActivity);
        this.location = _location;
        System.out.println("Details适配器location为:" + location);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return DetailsToday.newInstance(location);
            case 1:
                return DetailsWeekly.newInstance(location);
            default:
                return DetailsWeatherData.newInstance(location);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
