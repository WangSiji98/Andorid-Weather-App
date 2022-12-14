package com.oasis.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass. Use the {@link WeatherInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherInfo extends Fragment {

    private RequestQueue requestQueue = null;
    private JSONObject weatherData = null;
    private JSONArray dailyTemperatureData = null;
    Gson gson = new Gson();

    private boolean addedFavorite;

    private String location;

    private boolean showFloatingBtn = false;

    private String city = "-1";

    private String state;

    private String formatAddress = "";

    SharedPreferences favoriteCitysSharedPref;

    private final int PROGRESS_BAR_TIME = 150;

    private Handler handler;

    private boolean inMain = false;

    private static final String WEATHER_DATA_KEY = "com.oasis.weatherapp.watherdata";
    private static final String DAILY_TEMPERATURE_DATA_KEY = "com.oasis.weatherapp.temperaturedata";

    // URL
    private static final String weatherDataUrlLocal = "https://cs571hw8-331704.wl.r.appspot.com/weather_local_2";
    private static final String weatherDataUrl = "https://cs571hw8-331704.wl.r.appspot.com/weather?location=";
//    private static final String weatherDataUrl = "https://cs571hw8-331704.wl.r.appspot.com/weather?location=";
    private static final String googleMapsUrl = "https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyD_ojwI96rsE2bDCU5tyH0eUDeSuCmPveM&language=en_US&address=";

    public WeatherInfo() {
        // Required empty public constructor
    }

    public WeatherInfo(boolean _showFloatingBtn) {
        this.showFloatingBtn = _showFloatingBtn;
    }

    public WeatherInfo(String _city) {
        this.city = _city;
    }

    public WeatherInfo(boolean _showFloatingBtn, String _city) {
        this.showFloatingBtn = _showFloatingBtn;
        this.city = _city;
    }

    public WeatherInfo(boolean _showFloatingBtn, boolean _inMain, String _city) {
        this.showFloatingBtn = _showFloatingBtn;
        this.city = _city;
        this.inMain = _inMain;
    }

    public static WeatherInfo newInstance() {
        WeatherInfo fragment = new WeatherInfo();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addedFavorite = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_info, container, false);

        try {
            getActivity().findViewById(R.id.main_progressbar).setVisibility(View.VISIBLE);
        } catch (Exception e) {

        }
        try {
            getActivity().findViewById(R.id.search_progressbar).setVisibility(View.VISIBLE);
        } catch (Exception e) {

        }

        favoriteCitysSharedPref = getActivity().getSharedPreferences(getString(R.string.favorite_citys), Context.MODE_PRIVATE);

        FloatingActionButton favoriteBtn = view.findViewById(R.id.addOrRemoveFavorite);

        // ????????????????????????
        String[] cityArray = favoriteCitysSharedPref.getString(getString(R.string.favorite_city_string), "").split(",");
        for (String curr: cityArray) {
            if (this.city.equals(curr) && !"-1".equals(curr)) {
                this.addedFavorite = true;
                favoriteBtn.setImageResource(R.drawable.map_marker_minus);
                break;
            }
        }

        if (showFloatingBtn) {
            favoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addedFavorite = !addedFavorite;
                    if (addedFavorite) {
                        String toastMessage = city + ", " + state + "was added to favorites";
                        Toast.makeText(getActivity(), toastMessage,
                            Toast.LENGTH_SHORT).show();

                        // ????????????
                        favoriteBtn.setImageResource(R.drawable.map_marker_minus);
                        SharedPreferences.Editor favoriteCitysEditor = favoriteCitysSharedPref.edit();

                        // ??????????????????
                        int currLen = favoriteCitysSharedPref.getInt(getString(R.string.favorite_city_num), 0);
                        favoriteCitysEditor.putInt(getString(R.string.favorite_city_num), currLen + 1);
                        System.out.println("?????????????????????:" + String.valueOf(favoriteCitysSharedPref.getInt(getString(R.string.favorite_city_num), 0)));

                        // ??????????????????????????????
                        String currCityString = favoriteCitysSharedPref.getString(getString(R.string.favorite_city_string), "");
                        if ("".equals(currCityString)) {
                            favoriteCitysEditor.putString(getString(R.string.favorite_city_string), city);
                        } else {
                            favoriteCitysEditor.putString(getString(R.string.favorite_city_string), currCityString + "," + city);
                        }

                        // ??????
                        favoriteCitysEditor.commit();

                    } else {
                        SharedPreferences.Editor favoriteCitysEditor = favoriteCitysSharedPref.edit();
                        // ????????????
                        favoriteBtn.setImageResource(R.drawable.map_marker_plus);

                        String toastMessage = city + ", " + state + "was removed from favorites";
                        Toast.makeText(getActivity(), toastMessage,
                            Toast.LENGTH_SHORT).show();
                        // ??????????????????????????????
                        String currCityString = favoriteCitysSharedPref.getString(getString(R.string.favorite_city_string), "");
                        if (!"".equals(currCityString)) {
                            String[] currCitys = currCityString.split(",");
                            StringBuffer stringBuffer = new StringBuffer();
                            for (String tmpCity: currCitys) {
                                if (!tmpCity.equals(city)) {
                                    stringBuffer.append(tmpCity);
                                    stringBuffer.append(",");
                                }
                            }
                            favoriteCitysEditor.putString(getString(R.string.favorite_city_string), stringBuffer.toString());
                            // ??????
                            favoriteCitysEditor.commit();
                        }
                        if (inMain) {
                            Intent intent=new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                            getActivity().overridePendingTransition(0, 0);
                        }
                    }
                }
            });
        } else {
            favoriteBtn.setVisibility(View.GONE);
        }

        requestQueue = MyRequestQueue.getInstance(this.getActivity().getApplicationContext()).getRequestQueue();


        System.out.println("????????????????????????????????????????????????:" + this.city);

        // ????????????????????????
        JsonObjectRequest geoDataJsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, googleMapsUrl + this.city, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("???????????????????????????");

                    // ?????????????????????
                    // ????????????
                    try {
                        JSONObject geometry = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry");
                        String lat = geometry.getJSONObject("location").getString("lat");
                        String lng = geometry.getJSONObject("location").getString("lng");
                        location = lat + "," + lng;
                        System.out.println("????????????????????????:" + location);
                        formatAddress = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                        state = formatAddress.split(", ")[1];
                        System.out.println("????????????:" + state);

                        try {
                            // ????????????????????????
                            JsonObjectRequest weatherDataJsonObjectRequest = new JsonObjectRequest
                                (Request.Method.GET, weatherDataUrl + location, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        weatherData = response;
                                        System.out.println("??????????????????");
                                        System.out.println("????????????");
                                        renderView(view);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println("????????????????????????!\n" + error.toString());
                                    }
                                });

                            requestQueue.add(weatherDataJsonObjectRequest);

                        } catch (Exception e) {
                            System.out.println("?????????????????????????????????");
                        }

                    } catch (JSONException e) {
                        System.out.println("????????????????????????Json?????????");
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("??????????????????????????????!\n" + error.toString());
                }
            });

        requestQueue.add(geoDataJsonObjectRequest);

        // ???????????????
        View btn_details = view.findViewById(R.id.go_details_btn);

        btn_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Details.class);
                intent.putExtra(getString(R.string.details_current_city), city);
                intent.putExtra(getString(R.string.details_current_state), state);
                intent.putExtra(getString(R.string.details_location), location);
                startActivity(intent);
            }
        });


        return view;
    }

    private void renderView(View view) {
        try {
            JSONArray parsedWeatherData = weatherData.getJSONObject("data").getJSONArray("timelines")
                .getJSONObject(0).getJSONArray("intervals");

            // ??????????????????
            JSONObject todayWeatherData = parsedWeatherData.getJSONObject(0);
            String todayWeatherCode = todayWeatherData.getJSONObject("values").getString("weatherCode");
            String todayTemperature = todayWeatherData.getJSONObject("values").getString("temperature");
            String todayHumidity = todayWeatherData.getJSONObject("values").getString("humidity");
            String todayWindSpeed = todayWeatherData.getJSONObject("values").getString("windSpeed");
            String todayVisibility = todayWeatherData.getJSONObject("values").getString("visibility");
            String todayPressure = todayWeatherData.getJSONObject("values").getString("pressureSurfaceLevel");
            System.out.println("?????????????????????");
            System.out.println("??????code:" + todayWeatherCode);
            System.out.println("??????:" + todayTemperature);
            System.out.println("??????:" + todayHumidity);
            System.out.println("??????:" + todayWindSpeed);
            System.out.println("?????????:" + todayVisibility);
            System.out.println("??????:" + todayPressure);

            // ??????????????????
            List<String> dailyWeatherCodeList = new ArrayList<>();
            List<String> dailyTemperatureLowList = new ArrayList<>();
            List<String> dailyTemperatureHighList = new ArrayList<>();
            List<String> dailyDateList = new ArrayList<>();

            // ????????????????????????????????????????????????
            final int MAX_TAB_ROW = 13;

            for (int i = 1; i < MAX_TAB_ROW; ++i) {
                JSONObject tmpWeatherData = parsedWeatherData.getJSONObject(i);
                dailyWeatherCodeList.add(tmpWeatherData.getJSONObject("values").getString("weatherCode"));
                dailyTemperatureLowList.add(tmpWeatherData.getJSONObject("values").getString("temperatureMin"));
                dailyTemperatureHighList.add(tmpWeatherData.getJSONObject("values").getString("temperatureMax"));
                dailyDateList.add(tmpWeatherData.getString("startTime"));
            }
            System.out.println("???????????????????????????");
            System.out.println("??????code??????:" + dailyWeatherCodeList.toString());
            System.out.println("??????????????????:" + dailyTemperatureHighList.toString());
            System.out.println("??????????????????:" + dailyTemperatureLowList.toString());

            try {
                // ??????????????????????????????1
                ImageView todayWeatherIconView = view.findViewById(R.id.today_weather_icon);
                todayWeatherIconView.setImageResource(WeatherMapper.getWeatherIconId(todayWeatherCode));
                TextView todayWeatherTypeView = view.findViewById(R.id.today_weather_type);
                todayWeatherTypeView.setText(WeatherMapper.getWeatherType(todayWeatherCode));
                TextView todayTemperatureView = view.findViewById(R.id.today_temperature);
                todayTemperatureView.setText(todayTemperature + "???");
                TextView mainAddressView = view.findViewById(R.id.main_address);
                mainAddressView.setText(formatAddress);
                // ??????????????????????????????2
                TextView todayHumidityView = view.findViewById(R.id.today_humidity);
                todayHumidityView.setText(todayHumidity + "%");
                TextView todayWindSpeedView = view.findViewById(R.id.today_windspeed);
                todayWindSpeedView.setText(todayWindSpeed + "mph");
                TextView todayVisibilityView = view.findViewById(R.id.today_visibility);
                todayVisibilityView.setText(todayVisibility + "mi");
                TextView todayPressureView = view.findViewById(R.id.today_pressure);
                todayPressureView.setText(todayPressure + "inHg");
                // ??????????????????????????????
                for (int i = 0; i < MAX_TAB_ROW - 1; ++i) {
                    TextView currDateView = view.findViewById(WeatherMapper.getFutureDateViewId(i));
                    currDateView.setText(getShortDateString(dailyDateList.get(i)));
                    ImageView currWeatherIconView = view.findViewById(WeatherMapper.getFutureWeatherIconViewId(i));
                    currWeatherIconView.setImageResource(WeatherMapper.getWeatherIconId(dailyWeatherCodeList.get(i)));
                    TextView currTempHighView = view.findViewById(WeatherMapper.getFutureTemperatureHighViewId(i));
                    String tempHigh = dailyTemperatureHighList.get(i);
                    currTempHighView.setText(tempHigh.substring(0, 2));
                    TextView currTempLowView = view.findViewById(WeatherMapper.getFutureTemperatureLowViewId(i));
                    String tempLow = dailyTemperatureLowList.get(i);
                    currTempLowView.setText(tempLow.substring(0, 2));
                }

            } catch (Exception e) {
                System.out.println("??????fragment??????");
                e.printStackTrace();
            }

        } catch (JSONException e) {
            System.out.println("??????Json????????????");
            e.printStackTrace();
        }

        // ??????????????????
        handler = new Handler();
        // ??????SPLASH_DISPLAY_LENGHT?????????????????????MainActivity
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    getActivity().findViewById(R.id.main_progressbar).setVisibility(View.GONE);
                } catch (Exception e) {

                }
                try {
                    getActivity().findViewById(R.id.search_progressbar).setVisibility(View.GONE);
                } catch (Exception e) {

                }
            }
        }, PROGRESS_BAR_TIME);


    }

    String getShortDateString(String dateString) {
        return dateString.substring(0, 10);
    }
}