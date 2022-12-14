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
import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.hichartsclasses.HIBackground;
import com.highsoft.highcharts.common.hichartsclasses.HICSSObject;
import com.highsoft.highcharts.common.hichartsclasses.HIChart;
import com.highsoft.highcharts.common.hichartsclasses.HIData;
import com.highsoft.highcharts.common.hichartsclasses.HIDataLabels;
import com.highsoft.highcharts.common.hichartsclasses.HIEvents;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIPane;
import com.highsoft.highcharts.common.hichartsclasses.HIPlotOptions;
import com.highsoft.highcharts.common.hichartsclasses.HISolidgauge;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HITooltip;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;
import com.highsoft.highcharts.core.HIChartView;
import com.highsoft.highcharts.core.HIFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.json.JSONException;
import org.json.JSONObject;


public class DetailsWeatherData extends Fragment {

    private RequestQueue requestQueue = null;
    private JSONObject weatherData = null;
    Gson gson = new Gson();

    private Handler handler;
    private final int PROGRESS_BAR_TIME = 150;

    private String location;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String renderIconsString = "function renderIcons() {" +
        "                            if(!this.series[0].icon) {" +
        "                               this.series[0].icon = this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8]).attr({'stroke': '#303030','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[0].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[0].points[0].shapeArgs.innerR -(this.series[0].points[0].shapeArgs.r - this.series[0].points[0].shapeArgs.innerR) / 2); if(!this.series[1].icon) {this.series[1].icon = this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8,'M', 8, -8, 'L', 16, 0, 8, 8]).attr({'stroke': '#ffffff','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[1].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[1].points[0].shapeArgs.innerR -(this.series[1].points[0].shapeArgs.r - this.series[1].points[0].shapeArgs.innerR) / 2); if(!this.series[2].icon) {this.series[2].icon = this.renderer.path(['M', 0, 8, 'L', 0, -8, 'M', -8, 0, 'L', 0, -8, 8, 0]).attr({'stroke': '#303030','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[2].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[2].points[0].shapeArgs.innerR -(this.series[2].points[0].shapeArgs.r - this.series[2].points[0].shapeArgs.innerR) / 2);}";

    private String mParam1;
    private String mParam2;

    public DetailsWeatherData(String _location) {
        this.location = _location;
    }

    public static DetailsWeatherData newInstance(String _location) {
        DetailsWeatherData fragment = new DetailsWeatherData(_location);
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
        View view = inflater.inflate(R.layout.fragment_details_weather_data, container, false);

        getActivity().findViewById(R.id.details_progressbar).setVisibility(View.VISIBLE);

        requestQueue = MyRequestQueue.getInstance(this.getActivity().getApplicationContext()).getRequestQueue();
        String weatherDataUrl = "https://cs571hw8-331704.wl.r.appspot.com/weather?location=";
//        String weatherDataUrl = "https://cs571hw8-331704.wl.r.appspot.com/weather?location=";

        JsonObjectRequest weatherDataJsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, weatherDataUrl + location, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    weatherData = response;
                    System.out.println("DetailsWeatherData??????????????????");
                    plotChart(view);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("DetailsWeatherData????????????????????????!\n" + error.toString());
                }
            });

        requestQueue.add(weatherDataJsonObjectRequest);



        return view;
    }

    private void plotChart(View view) {

        try {
            JSONObject todayWeatherData = weatherData.getJSONObject("data").getJSONArray("timelines")
                .getJSONObject(0).getJSONArray("intervals").getJSONObject(0)
                .getJSONObject("values");

            String todayCloudCover = todayWeatherData.getString("cloudCover");
            String todayPreciptation = todayWeatherData.getString("precipitationProbability");
            StringBuffer todayHumidity = new StringBuffer();
            for (char ch: todayWeatherData.getString("humidity").toCharArray()) {
                if (ch != '.') {
                    todayHumidity.append(ch);
                } else {
                    break;
                }
            }
            System.out.println("?????????3???????????????:" + todayHumidity.toString());

            try {
                HIChartView chartView = view.findViewById(R.id.gague_chart);

                HIOptions options = new HIOptions();
                chartView.setOptions(options);

                HIChart chart = new HIChart();
                chart.setType("solidgauge");
                chart.setEvents(new HIEvents());
                chart.getEvents().setRender(new HIFunction(renderIconsString));
                options.setChart(chart);

                HITitle title = new HITitle();
                title.setText("Stat Summary");
                title.setStyle(new HICSSObject());
                title.getStyle().setFontSize("24px");
                options.setTitle(title);

                HITooltip tooltip = new HITooltip();
                tooltip.setBorderWidth(0);
                tooltip.setBackgroundColor(HIColor.initWithName("none"));
                tooltip.setShadow(false);
                tooltip.setStyle(new HICSSObject());
                tooltip.getStyle().setFontSize("16px");
                tooltip.setPointFormat("{series.name}<br><span style=\"font-size:2em; color: {point.color}; font-weight: bold\">{point.y}%</span>");
                tooltip.setPositioner(
                    new HIFunction(
                        "function (labelWidth) {" +
                            "   return {" +
                            "       x: (this.chart.chartWidth - labelWidth) /2," +
                            "       y: (this.chart.plotHeight / 2) + 15" +
                            "   };" +
                            "}"
                    ));
                options.setTooltip(tooltip);

                HIPane pane = new HIPane();
                pane.setStartAngle(0);
                pane.setEndAngle(360);

                HIBackground paneBackground1 = new HIBackground();
                paneBackground1.setOuterRadius("112%");
                paneBackground1.setInnerRadius("88%");
                paneBackground1.setBackgroundColor(HIColor.initWithRGBA(102, 204, 0, 0.35));
                paneBackground1.setBorderWidth(0);

                HIBackground paneBackground2 = new HIBackground();
                paneBackground2.setOuterRadius("87%");
                paneBackground2.setInnerRadius("63%");
                paneBackground2.setBackgroundColor(HIColor.initWithRGBA(102, 178, 255, 0.35));
                paneBackground2.setBorderWidth(0);

                HIBackground paneBackground3 = new HIBackground();
                paneBackground3.setOuterRadius("62%");
                paneBackground3.setInnerRadius("38%");
                paneBackground3.setBackgroundColor(HIColor.initWithRGBA(255, 102, 102, 0.35));
                paneBackground3.setBorderWidth(0);

                pane.setBackground(new ArrayList<>(
                    Arrays.asList(paneBackground1, paneBackground2, paneBackground3)));
                options.setPane(pane);

                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setMax(100);
                yaxis.setLineWidth(0);
                yaxis.setTickPositions(new ArrayList<>()); // to remove ticks from the chart
                options.setYAxis(new ArrayList<>(Collections.singletonList(yaxis)));

                HIPlotOptions plotOptions = new HIPlotOptions();
                plotOptions.setSolidgauge(new HISolidgauge());
//        plotOptions.getSolidgauge().setDataLabels(new HIDataLabels());
//        plotOptions.getSolidgauge().getDataLabels().setEnabled(false);
                plotOptions.getSolidgauge().setLinecap("round");
                plotOptions.getSolidgauge().setStickyTracking(false);
                plotOptions.getSolidgauge().setRounded(true);
                options.setPlotOptions(plotOptions);

                HISolidgauge solidgauge1 = new HISolidgauge();
                solidgauge1.setName("Cloud Cover");
                HIData data1 = new HIData();
                data1.setColor(HIColor.initWithRGB(102, 204, 0));
                data1.setRadius("112%");
                data1.setInnerRadius("88%");
                data1.setY(Integer.parseInt(todayCloudCover));
                solidgauge1.setData(new ArrayList<>(Collections.singletonList(data1)));

                HISolidgauge solidgauge2 = new HISolidgauge();
                solidgauge2.setName("Precipitation");
                HIData data2 = new HIData();
                data2.setColor(HIColor.initWithRGB(102, 178, 255));
                data2.setRadius("87%");
                data2.setInnerRadius("63%");
                data2.setY(Integer.parseInt(todayPreciptation));
                solidgauge2.setData(new ArrayList<>(Collections.singletonList(data2)));

                HISolidgauge solidgauge3 = new HISolidgauge();
                solidgauge3.setName("Humidity");
                HIData data3 = new HIData();
                data3.setColor(HIColor.initWithRGB(255, 102, 102));
                data3.setRadius("62%");
                data3.setInnerRadius("38%");
                data3.setY(Integer.parseInt(todayHumidity.toString()));
                solidgauge3.setData(new ArrayList<>(Collections.singletonList(data3)));

                options.setSeries(new ArrayList<>(Arrays.asList(solidgauge1, solidgauge2, solidgauge3)));

                chartView.setOptions(options);

            } catch (Exception e) {
                System.out.println("????????????");
                e.printStackTrace();
            }


        } catch (JSONException e) {
            System.out.println("JSON ????????????");
            e.printStackTrace();
        }

        // ??????????????????
        handler = new Handler();
        // ??????SPLASH_DISPLAY_LENGHT?????????????????????MainActivity
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().findViewById(R.id.details_progressbar).setVisibility(View.GONE);
            }
        }, PROGRESS_BAR_TIME);
    }
}