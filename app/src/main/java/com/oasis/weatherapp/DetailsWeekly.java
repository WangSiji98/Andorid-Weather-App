package com.oasis.weatherapp;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.HIGradient;
import com.highsoft.highcharts.common.HIStop;
import com.highsoft.highcharts.common.hichartsclasses.*;
import com.highsoft.highcharts.core.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DetailsWeekly extends Fragment {

    private RequestQueue requestQueue = null;
    private JSONArray dailyTemperatureData = null;
    Gson gson = new Gson();

    private Handler handler;
    private final int PROGRESS_BAR_TIME = 150;

    private String location;

    public DetailsWeekly(String _location) {
        this.location = _location;
    }

    public static DetailsWeekly newInstance(String _location) {
        DetailsWeekly fragment = new DetailsWeekly(_location);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("详情页2location为:" + location);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_details_weekly, container, false);

        getActivity().findViewById(R.id.details_progressbar).setVisibility(View.VISIBLE);

        requestQueue = MyRequestQueue.getInstance(this.getActivity().getApplicationContext()).getRequestQueue();

        String weatherDataUrl = "https://cs571hw8-331704.wl.r.appspot.com/weather_local_2";
        String dailyTemperatureDataUrl = "https://cs571hw8-331704.wl.r.appspot.com/daily_temperature?location=";
//        String dailyTemperatureDataUrl = "https://cs571hw8-331704.wl.r.appspot.com/daily_temperature?location=";

        JsonArrayRequest dailyTemperatureDataJsonObjectRequest = new JsonArrayRequest
            (Request.Method.GET, dailyTemperatureDataUrl + location, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    dailyTemperatureData = response;
                    System.out.println("获取每日温度数据");
                    plotAreaRangeChart(view);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("获取每日温度数据出错!\n" + error.toString());
                }
            });

        requestQueue.add(dailyTemperatureDataJsonObjectRequest);

//        plotAreaRangeChart(view);
        return view;
    }

    public void plotAreaRangeChart(View view) {

        HIChartView chartView = view.findViewById(R.id.gague_chart);
        HIOptions options = new HIOptions();
        chartView.setOptions(options);

        HIChart chart = new HIChart();
        chart.setType("arearange");
        chart.setZoomType("x");
        options.setChart(chart);

        HITitle title = new HITitle();
        title.setText("Temperature variation by day");
        options.setTitle(title);

        HIXAxis xaxis = new HIXAxis();
//        xaxis.setType("datetime");
        HIDay hiDay = new HIDay();
        hiDay.setMain("%d");
        HIDateTimeLabelFormats hiDateTimeLabelFormats = new HIDateTimeLabelFormats();
        hiDateTimeLabelFormats.setDay(hiDay);
        xaxis.setDateTimeLabelFormats(hiDateTimeLabelFormats);
        options.setXAxis(new ArrayList<HIXAxis>(){{add(xaxis);}});

        HIYAxis yaxis = new HIYAxis();
        yaxis.setTitle(new HITitle());
        options.setYAxis(new ArrayList<HIYAxis>(){{add(yaxis);}});

        HITooltip tooltip = new HITooltip();
        tooltip.setShadow(true);
        tooltip.setValueSuffix("°C");
        options.setTooltip(tooltip);

        HILegend legend = new HILegend();
        legend.setEnabled(false);
        options.setLegend(legend);

        HIArearange series = new HIArearange();
        series.setName("Temperatures");

        HIGradient gradient = new HIGradient(0, 0, 0, 1);
        LinkedList<HIStop> stops = new LinkedList<>();
        stops.add(new HIStop(0, HIColor.initWithRGB(244, 180, 0)));
        stops.add(new HIStop(1, HIColor.initWithRGB(153, 193, 227)));
        series.setFillColor(HIColor.initWithLinearGradient(gradient, stops));

        int length = dailyTemperatureData.length();
        Object[][] data = new Object[length][3];

        try {
            for (int i = 0; i < length; ++i) {
                JSONArray curr = dailyTemperatureData.getJSONArray(i);
                data[i][0] = curr.getString(0);
                data[i][1] = curr.getDouble(1);
                data[i][2] = curr.getDouble(2);
            }
        } catch (JSONException e) {
            System.out.println("JSON 转化出错");
            e.printStackTrace();
        }

        HIPlotOptions hiPlotOptions = new HIPlotOptions();
        HIArearange hiArearange = new  HIArearange();

        series.setData(new ArrayList<>(Arrays.asList(data)));
        options.setSeries(new ArrayList<>(Arrays.asList(series)));

        chartView.setOptions(options);

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