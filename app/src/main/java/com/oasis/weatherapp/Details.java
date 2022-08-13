package com.oasis.weatherapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

public class Details extends AppCompatActivity {

    Gson gson = new Gson();

    private String city;
    private String state;
    private String location;

    private static String[] mTitle = {"TODAY","WEEKLY","WEATHER DATA"};
    private static int[] mIcon = {R.drawable.calendar_today_png, R.drawable.trending_up_png, R.drawable.thermometer_low_png};
    private static final String WEATHER_DATA_KEY = "com.oasis.weatherapp.watherdata";
    private static final String DAILY_TEMPERATURE_DATA_KEY = "com.oasis.weatherapp.temperaturedata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        findViewById(R.id.details_progressbar).setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        String passedCity = intent.getStringExtra(getString(R.string.details_current_city));
        System.out.println("传递到Details的当前城市为:" + passedCity);
        city = passedCity;

        String passedState = intent.getStringExtra(getString(R.string.details_current_state));
        System.out.println("传递到Details的当前州为:" + passedState);
        state = passedState;

        location = intent.getStringExtra(getString(R.string.details_location));
        System.out.println("传递到Details的当前location为:" + location);

        Toolbar toolbar = (Toolbar) findViewById(R.id.details_tool_bar);
        toolbar.setTitle(city + ", " + WeatherMapper.getStateFullName(state));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TabLayout mTabLayout = findViewById(R.id.details_tab);
        ViewPager2 viewPager2 = findViewById(R.id.details_viewpage2);
        MyFragmentAdapter myFragmentAdapter = new MyFragmentAdapter(this, location);
        viewPager2.setAdapter(myFragmentAdapter);
        @SuppressLint("ResourceAsColor") TabLayoutMediator mediator = new TabLayoutMediator(mTabLayout, viewPager2, true, (tab, position) -> {
            tab.setText(mTitle[position]);
            tab.setIcon(mIcon[position]);
            System.out.println("当前页面为:" + String.valueOf(viewPager2.getCurrentItem()));
        });
        mediator.attach();

        mTabLayout.setOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(Tab tab) {
            }

            @Override
            public void onTabReselected(Tab tab) {
            }
        });

        findViewById(R.id.details_progressbar).setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.details_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.details_menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void sendTwitter(MenuItem item) {
        String url = "http://www.twitter.com/intent/tweet?hashtags=CSCI571WeatherSearch&text=Check%20out%20New%20York,%20NY,%20USA's%20weather!%20It%20is%2056.85℉!";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}