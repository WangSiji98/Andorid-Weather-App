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

        // 确定按钮是否显示
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

                        // 添加城市
                        favoriteBtn.setImageResource(R.drawable.map_marker_minus);
                        SharedPreferences.Editor favoriteCitysEditor = favoriteCitysSharedPref.edit();

                        // 获取当前数量
                        int currLen = favoriteCitysSharedPref.getInt(getString(R.string.favorite_city_num), 0);
                        favoriteCitysEditor.putInt(getString(R.string.favorite_city_num), currLen + 1);
                        System.out.println("当前城市列表有:" + String.valueOf(favoriteCitysSharedPref.getInt(getString(R.string.favorite_city_num), 0)));

                        // 获取当前的城市字符串
                        String currCityString = favoriteCitysSharedPref.getString(getString(R.string.favorite_city_string), "");
                        if ("".equals(currCityString)) {
                            favoriteCitysEditor.putString(getString(R.string.favorite_city_string), city);
                        } else {
                            favoriteCitysEditor.putString(getString(R.string.favorite_city_string), currCityString + "," + city);
                        }

                        // 提交
                        favoriteCitysEditor.commit();

                    } else {
                        SharedPreferences.Editor favoriteCitysEditor = favoriteCitysSharedPref.edit();
                        // 删除城市
                        favoriteBtn.setImageResource(R.drawable.map_marker_plus);

                        String toastMessage = city + ", " + state + "was removed from favorites";
                        Toast.makeText(getActivity(), toastMessage,
                            Toast.LENGTH_SHORT).show();
                        // 获取当前的城市字符串
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
                            // 提交
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


        System.out.println("开始请求地理位置信息，当前城市为:" + this.city);

        // 请求地理位置信息
        JsonObjectRequest geoDataJsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, googleMapsUrl + this.city, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("获取地理位置信息！");

                    // 获取经纬度信息
                    // 获取地址
                    try {
                        JSONObject geometry = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry");
                        String lat = geometry.getJSONObject("location").getString("lat");
                        String lng = geometry.getJSONObject("location").getString("lng");
                        location = lat + "," + lng;
                        System.out.println("当前经纬度信息为:" + location);
                        formatAddress = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                        state = formatAddress.split(", ")[1];
                        System.out.println("当前州为:" + state);

                        try {
                            // 请求当日天气数据
                            JsonObjectRequest weatherDataJsonObjectRequest = new JsonObjectRequest
                                (Request.Method.GET, weatherDataUrl + location, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        weatherData = response;
                                        System.out.println("获取天气数据");
                                        System.out.println("开始渲染");
                                        renderView(view);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println("获取天气数据出错!\n" + error.toString());
                                    }
                                });

                            requestQueue.add(weatherDataJsonObjectRequest);

                        } catch (Exception e) {
                            System.out.println("请求天气信息出错出错！");
                        }

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

        // 详情页按钮
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

            // 今日天气信息
            JSONObject todayWeatherData = parsedWeatherData.getJSONObject(0);
            String todayWeatherCode = todayWeatherData.getJSONObject("values").getString("weatherCode");
            String todayTemperature = todayWeatherData.getJSONObject("values").getString("temperature");
            String todayHumidity = todayWeatherData.getJSONObject("values").getString("humidity");
            String todayWindSpeed = todayWeatherData.getJSONObject("values").getString("windSpeed");
            String todayVisibility = todayWeatherData.getJSONObject("values").getString("visibility");
            String todayPressure = todayWeatherData.getJSONObject("values").getString("pressureSurfaceLevel");
            System.out.println("今日天气信息：");
            System.out.println("天气code:" + todayWeatherCode);
            System.out.println("温度:" + todayTemperature);
            System.out.println("湿度:" + todayHumidity);
            System.out.println("风速:" + todayWindSpeed);
            System.out.println("能见度:" + todayVisibility);
            System.out.println("气压:" + todayPressure);

            // 一周天气信息
            List<String> dailyWeatherCodeList = new ArrayList<>();
            List<String> dailyTemperatureLowList = new ArrayList<>();
            List<String> dailyTemperatureHighList = new ArrayList<>();
            List<String> dailyDateList = new ArrayList<>();

            // 布局最大列表数目，从次日开始计算
            final int MAX_TAB_ROW = 13;

            for (int i = 1; i < MAX_TAB_ROW; ++i) {
                JSONObject tmpWeatherData = parsedWeatherData.getJSONObject(i);
                dailyWeatherCodeList.add(tmpWeatherData.getJSONObject("values").getString("weatherCode"));
                dailyTemperatureLowList.add(tmpWeatherData.getJSONObject("values").getString("temperatureMin"));
                dailyTemperatureHighList.add(tmpWeatherData.getJSONObject("values").getString("temperatureMax"));
                dailyDateList.add(tmpWeatherData.getString("startTime"));
            }
            System.out.println("未来几日的天气信息");
            System.out.println("天气code列表:" + dailyWeatherCodeList.toString());
            System.out.println("最高温度列表:" + dailyTemperatureHighList.toString());
            System.out.println("最低温度列表:" + dailyTemperatureLowList.toString());

            try {
                // 开始渲染今日天气卡片1
                ImageView todayWeatherIconView = view.findViewById(R.id.today_weather_icon);
                todayWeatherIconView.setImageResource(WeatherMapper.getWeatherIconId(todayWeatherCode));
                TextView todayWeatherTypeView = view.findViewById(R.id.today_weather_type);
                todayWeatherTypeView.setText(WeatherMapper.getWeatherType(todayWeatherCode));
                TextView todayTemperatureView = view.findViewById(R.id.today_temperature);
                todayTemperatureView.setText(todayTemperature + "℉");
                TextView mainAddressView = view.findViewById(R.id.main_address);
                mainAddressView.setText(formatAddress);
                // 开始渲染今日天气卡片2
                TextView todayHumidityView = view.findViewById(R.id.today_humidity);
                todayHumidityView.setText(todayHumidity + "%");
                TextView todayWindSpeedView = view.findViewById(R.id.today_windspeed);
                todayWindSpeedView.setText(todayWindSpeed + "mph");
                TextView todayVisibilityView = view.findViewById(R.id.today_visibility);
                todayVisibilityView.setText(todayVisibility + "mi");
                TextView todayPressureView = view.findViewById(R.id.today_pressure);
                todayPressureView.setText(todayPressure + "inHg");
                // 开始渲染未来几日天气
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
                System.out.println("渲染fragment出错");
                e.printStackTrace();
            }

        } catch (JSONException e) {
            System.out.println("解析Json数据出错");
            e.printStackTrace();
        }

        // 手动延迟一波
        handler = new Handler();
        // 延迟SPLASH_DISPLAY_LENGHT时间然后跳转到MainActivity
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