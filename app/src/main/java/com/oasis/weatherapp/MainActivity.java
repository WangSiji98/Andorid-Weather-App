package com.oasis.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue = null;

    private static final String SEARCH_STRING_KEY = "com.oasis.weatherapp.search";
    private static final String SHOW_FAVORITE_BUTTON = "com.oasis.weather.favoritebutton";

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Handler handler;
    private final int SPLASH_DISPLAY_LENGHT = 150;

    private List<WeatherInfo> weatherInfoFragmentList;
    private List<String> cityList;
    private FavoriteFragmentAdapter myAdapter;

    Context context = this;

    private static final String autoCompleteDataUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyD_ojwI96rsE2bDCU5tyH0eUDeSuCmPveM&type=(cities)&input=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("MainActivity 创建生命周期");
        SharedPreferences favoriteCitysSharedPref = this.getSharedPreferences(getString(R.string.favorite_citys), Context.MODE_PRIVATE);

        // 清空本地存储
//        SharedPreferences.Editor favoriteCitysEditor = favoriteCitysSharedPref.edit();
//        favoriteCitysEditor.clear().commit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.details_tool_bar);
        setSupportActionBar(toolbar);

        requestQueue = MyRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();

        viewPager = (ViewPager) findViewById(R.id.favorite_viewpager_main);
        tabLayout = (TabLayout) findViewById(R.id.favorite_tab_main);

        weatherInfoFragmentList = new ArrayList<>();
        cityList = new ArrayList<>();

        weatherInfoFragmentList.add(new WeatherInfo("Los Angeles"));
        cityList.add("Los Angeles");

        int currCityNums = favoriteCitysSharedPref.getInt(getString(R.string.favorite_city_num), 0);
        String currCityString = favoriteCitysSharedPref.getString(getString(R.string.favorite_city_string), "");
        System.out.println("当前城市列表有:" + String.valueOf(currCityNums));
        System.out.println("当前城市字符串为:" + currCityString);

        if (!"".equals(currCityString)) {
            String[] citys = currCityString.split(",");
            for (String currCity: citys) {
                cityList.add(currCity);
                weatherInfoFragmentList.add(new WeatherInfo(true, true, currCity));
            }
        }


        FragmentManager supportFragmentManager = getSupportFragmentManager();
        myAdapter = new FavoriteFragmentAdapter(supportFragmentManager, weatherInfoFragmentList, cityList);
        viewPager.setAdapter(myAdapter);
        viewPager.setOffscreenPageLimit(6);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void updateTabs() {
        SharedPreferences favoriteCitysSharedPref = this.getSharedPreferences(getString(R.string.favorite_citys), Context.MODE_PRIVATE);

        weatherInfoFragmentList = new ArrayList<>();
        cityList = new ArrayList<>();

        weatherInfoFragmentList.add(new WeatherInfo("Los Angeles"));
        cityList.add("Los Angeles");

        int currCityNums = favoriteCitysSharedPref.getInt(getString(R.string.favorite_city_num), 0);
        String currCityString = favoriteCitysSharedPref.getString(getString(R.string.favorite_city_string), "");
        System.out.println("当前城市列表有:" + String.valueOf(currCityNums));
        System.out.println("当前城市字符串为:" + currCityString);

        if (!"".equals(currCityString)) {
            String[] citys = currCityString.split(",");
            for (String currCity: citys) {
                cityList.add(currCity);
                weatherInfoFragmentList.add(new WeatherInfo(true, currCity));
            }
        }


        FragmentManager supportFragmentManager = getSupportFragmentManager();
        myAdapter = new FavoriteFragmentAdapter(supportFragmentManager, weatherInfoFragmentList, cityList);
        viewPager.setAdapter(myAdapter);
        viewPager.setOffscreenPageLimit(6);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search...");

        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent searchIntent = new Intent(MainActivity.this, Searchable.class);
                searchIntent.putExtra(SEARCH_STRING_KEY, query);
                startActivity(searchIntent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("触发自动填充");

                JsonObjectRequest autoCompleteDataJsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, autoCompleteDataUrl + newText, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONObject autoCompleteData = response;
                            System.out.println("获取自动填充建议数据!\n");
                            try {
                                JSONArray predictions = autoCompleteData.getJSONArray("predictions");
                                int length = predictions.length();
                                String[] autoCompleteAdvise = new String[length];
                                for (int i = 0; i < length; ++i) {
                                    String[] currDiscriptions = predictions.getJSONObject(i).getString("description").split(", ");
                                    autoCompleteAdvise[i] = currDiscriptions[0] + ", " + WeatherMapper.getStateFullName(currDiscriptions[1]);
                                }

                                SearchView.SearchAutoComplete autoComplete  =  searchView.findViewById(androidx.appcompat.R.id.search_src_text);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    context, R.layout.spinner_item, autoCompleteAdvise
                                );
                                autoComplete.setAdapter(adapter);

                                autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                                        Intent searchIntent = new Intent(MainActivity.this, Searchable.class);
                                        searchIntent.putExtra(SEARCH_STRING_KEY, (String) adapter.getItem(position));
                                        startActivity(searchIntent);
                                    }
                                });

                            } catch (JSONException e) {
                                System.out.println("自动填充解析Json出错!");
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("获取自动填充建议数据出错!\n" + error.toString());
                        }
                    });

                requestQueue.add(autoCompleteDataJsonObjectRequest);


                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}