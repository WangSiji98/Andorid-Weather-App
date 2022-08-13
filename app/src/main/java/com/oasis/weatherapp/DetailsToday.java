package com.oasis.weatherapp;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;


public class DetailsToday extends Fragment {

    private RequestQueue requestQueue = null;
    private JSONObject weatherData = null;
    Gson gson = new Gson();


    private Handler handler;
    private final int PROGRESS_BAR_TIME = 150;

    private String location;

    private static final String WEATHER_DATA_KEY = "com.oasis.weatherapp.watherdata";

    public DetailsToday(String _location) {
        this.location = _location;
    }

    public static DetailsToday newInstance(String _location) {
        DetailsToday fragment = new DetailsToday(_location);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_today, container, false);

        getActivity().findViewById(R.id.details_progressbar).setVisibility(View.VISIBLE);


        requestQueue = MyRequestQueue.getInstance(this.getActivity().getApplicationContext()).getRequestQueue();
        String weatherDataUrl = "https://cs571hw8-331704.wl.r.appspot.com/weather?location=";
//        String weatherDataUrl = "https://cs571hw8-331704.wl.r.appspot.com/weather?location=";


        JsonObjectRequest weatherDataJsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, weatherDataUrl + location, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    weatherData = response;
                    System.out.println("onCreateView生命周期Details Today获取天气数据");
                    renderDetails(view);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Details Today获取天气数据出错!\n" + error.toString());
                }
            });

        requestQueue.add(weatherDataJsonObjectRequest);
        return view;
    }

    private void renderDetails(View view) {

        try {
            JSONObject todayWeatherData = weatherData.getJSONObject("data").getJSONArray("timelines")
                .getJSONObject(0).getJSONArray("intervals").getJSONObject(0)
                .getJSONObject("values");

            String todayWindSpeed = todayWeatherData.getString("windSpeed");
            String todayPressure = todayWeatherData.getString("pressureSurfaceLevel");
            String todayPreciptation = todayWeatherData.getString("precipitationProbability");
            String todayTemperature = todayWeatherData.getString("temperature");
            String todayWeatherCode = todayWeatherData.getString("weatherCode");
            String todayHumidity = todayWeatherData.getString("humidity");
            String todayVisibility = todayWeatherData.getString("visibility");
            String todayCloudCover = todayWeatherData.getString("cloudCover");
            String todayUvIndex = todayWeatherData.getString("uvIndex");

            try {

                TextView todayDetailsWindSpeedView = view.findViewById(R.id.today_details_windspeed);
                todayDetailsWindSpeedView.setText(todayWindSpeed + " mph");

                TextView todayDetailsPressureView = view.findViewById(R.id.today_details_pressure);
                todayDetailsPressureView.setText(todayPressure + " inHg");

                TextView todayDetailsPreciptationView = view.findViewById(R.id.today_details_preciptation);
                todayDetailsPreciptationView.setText(todayPreciptation + " %");

                TextView todayDetailsTemperatureView = view.findViewById(R.id.today_details_temperature);
                todayDetailsTemperatureView.setText(todayTemperature + " ℉");

                ImageView todayDetailsWeatherIconView = view.findViewById(R.id.today_details_weather_icon);
                todayDetailsWeatherIconView
                    .setImageResource(WeatherMapper.getWeatherIconId(todayWeatherCode));

                TextView todayDetailsWeatherTypeView = view.findViewById(R.id.today_details_weather_type);
                todayDetailsWeatherTypeView.setText(WeatherMapper.getWeatherType(todayWeatherCode));

                TextView todayDetailsHumidityView = view.findViewById(R.id.today_details_humidity);
                todayDetailsHumidityView.setText(todayHumidity + "%");

                TextView todayDetailsVisibilityView = view.findViewById(R.id.today_details_visibility);
                todayDetailsVisibilityView.setText(todayVisibility + " mi");

                TextView todayDetailsCloudCoverView = view.findViewById(R.id.today_details_cloudcover);
                todayDetailsCloudCoverView.setText(todayCloudCover + "%");

                TextView todayDetailsOzoneView = view.findViewById(R.id.today_details_uvindex);
                todayDetailsOzoneView.setText(todayUvIndex);
            } catch (Exception e) {
                System.out.println("渲染详情页出错");
                e.printStackTrace();
            }

        } catch (JSONException e) {
            System.out.println("JSON 解析出错");
            e.printStackTrace();
        }

        // 手动延迟一波
        handler = new Handler();
        // 延迟SPLASH_DISPLAY_LENGHT时间然后跳转到MainActivity
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().findViewById(R.id.details_progressbar).setVisibility(View.GONE);
            }
        }, PROGRESS_BAR_TIME);

    }
}