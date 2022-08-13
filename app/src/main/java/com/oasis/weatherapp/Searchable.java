package com.oasis.weatherapp;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class Searchable extends AppCompatActivity {

    private static final String SEARCH_STRING_KEY = "com.oasis.weatherapp.search";
    private static final String googleMapsUrl = "https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyD_ojwI96rsE2bDCU5tyH0eUDeSuCmPveM&language=en_US&address=";

    private RequestQueue requestQueue = null;
    private String formatAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        requestQueue = MyRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();

        CardView searchProgressBar = findViewById(R.id.search_progressbar);
        searchProgressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        String query = intent.getStringExtra(SEARCH_STRING_KEY);
        String queryCity = query.split(",")[0];
        System.out.println("目前搜索的城市信息为:" + queryCity);

        JsonObjectRequest geoDataJsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, googleMapsUrl + queryCity, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("获取地理位置信息！");

                    // 获取经纬度信息
                    // 获取地址
                    try {
                        formatAddress = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                        Toolbar toolbar = (Toolbar) findViewById(R.id.details_tool_bar);
                        toolbar.setTitle(formatAddress);
                        setSupportActionBar(toolbar);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                    } catch (JSONException e) {
                        System.out.println("解析地理位置信息Json出错！");
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("获取地理位置信息出错!\n" + error.toString());
                }
            });

        requestQueue.add(geoDataJsonObjectRequest);

        WeatherInfo weatherInfoFragment = new WeatherInfo(true, queryCity);
        Bundle searchBundle = new Bundle();
        searchBundle.putString(SEARCH_STRING_KEY, queryCity);
        weatherInfoFragment.setArguments(searchBundle);
        getSupportFragmentManager().beginTransaction().add(R.id.search_frag_container, weatherInfoFragment).commit();
    }
}