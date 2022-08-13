package com.oasis.weatherapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherMapper {

    private static Map<String, Integer> weatherIconMap = new HashMap<>();
    private static Map<String, String> weatherTypeMap = new HashMap<>();
    private static Map<String, String> monthMap = new HashMap<>();
    private static final String[] weekDay = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private static Map<String, String> stateMap = new HashMap<>();

    static {
        weatherIconMap.put("4201", R.drawable.ic_rain_heavy);
        weatherIconMap.put("4001", R.drawable.ic_rain);
        weatherIconMap.put("4200", R.drawable.ic_rain_light);
        weatherIconMap.put("6201", R.drawable.ic_freezing_rain_heavy);
        weatherIconMap.put("6001", R.drawable.ic_freezing_rain);
        weatherIconMap.put("6200", R.drawable.ic_freezing_rain_light);
        weatherIconMap.put("6000", R.drawable.ic_freezing_drizzle);
        weatherIconMap.put("4000", R.drawable.ic_drizzle);
        weatherIconMap.put("7101", R.drawable.ic_ice_pellets_heavy);
        weatherIconMap.put("7000", R.drawable.ic_ice_pellets);
        weatherIconMap.put("7102", R.drawable.ic_ice_pellets_light);
        weatherIconMap.put("5101", R.drawable.ic_snow_heavy);
        weatherIconMap.put("5000", R.drawable.ic_snow);
        weatherIconMap.put("5100", R.drawable.ic_snow_light);
        weatherIconMap.put("5001", R.drawable.ic_flurries);
        weatherIconMap.put("8000", R.drawable.ic_tstorm);
        weatherIconMap.put("2100", R.drawable.ic_fog_light);
        weatherIconMap.put("2000", R.drawable.ic_fog);
        weatherIconMap.put("1001", R.drawable.ic_cloudy);
        weatherIconMap.put("1102", R.drawable.ic_mostly_cloudy);
        weatherIconMap.put("1101", R.drawable.ic_partly_cloudy_day);
        weatherIconMap.put("1100", R.drawable.ic_mostly_clear_day);
        weatherIconMap.put("1000", R.drawable.ic_clear_day);
        weatherIconMap.put("3000", R.drawable.light_wind);
        weatherIconMap.put("3001", R.drawable.wind);
        weatherIconMap.put("3002", R.drawable.strong_wind);
    }

    static {
        weatherTypeMap.put("4201", "Heavy Rain");
        weatherTypeMap.put("4001", "Rain");
        weatherTypeMap.put("4200", "Light Rain");
        weatherTypeMap.put("6201", "Heavy Freezing Rain");
        weatherTypeMap.put("6001", "Freezing Rain");
        weatherTypeMap.put("6200", "Light Freezing Rain");
        weatherTypeMap.put("6000", "Freezing Drizzle");
        weatherTypeMap.put("4000", "Drizzle");
        weatherTypeMap.put("7101", "Heavy Ice Pellets");
        weatherTypeMap.put("7000", "Ice Pellets");
        weatherTypeMap.put("7102", "Light Ice Pellets");
        weatherTypeMap.put("5101", "Heavy Snow");
        weatherTypeMap.put("5000", "Snow");
        weatherTypeMap.put("5100", "Light Snow");
        weatherTypeMap.put("5001", "Flurries");
        weatherTypeMap.put("8000", "Thunderstorm");
        weatherTypeMap.put("2100", "Light Fog");
        weatherTypeMap.put("2000", "Fog");
        weatherTypeMap.put("1001", "Cloudy");
        weatherTypeMap.put("1102", "Mostly Cloudy");
        weatherTypeMap.put("1101", "Partly Cloudy");
        weatherTypeMap.put("1100", "Mostly Clear");
        weatherTypeMap.put("1000", "Clear Sunny");
        weatherTypeMap.put("3000", "Light Wind");
        weatherTypeMap.put("3001", "Wind");
        weatherTypeMap.put("3002", "Strong Wind");
    }

    static {
        monthMap.put("01", "Jan");
        monthMap.put("02", "Feb");
        monthMap.put("03", "Mar");
        monthMap.put("04", "Apr");
        monthMap.put("05", "May");
        monthMap.put("06", "Jun");
        monthMap.put("07", "July");
        monthMap.put("08", "Aug");
        monthMap.put("09", "Sep");
        monthMap.put("10", "Oct");
        monthMap.put("11", "Nov");
        monthMap.put("12", "Dec");
    }

    private static final List<Integer> futureDateList = new ArrayList<Integer>(){{
        add(R.id.frag_date_0);
        add(R.id.frag_date_1);
        add(R.id.frag_date_2);
        add(R.id.frag_date_3);
        add(R.id.frag_date_4);
        add(R.id.frag_date_5);
        add(R.id.frag_date_6);
        add(R.id.frag_date_7);
        add(R.id.frag_date_8);
        add(R.id.frag_date_9);
        add(R.id.frag_date_10);
        add(R.id.frag_date_11);
    }};

    private static final List<Integer> futureWeatherCodeList = new ArrayList<Integer>(){{
        add(R.id.frag_future_weather_img_0);
        add(R.id.frag_future_weather_img_1);
        add(R.id.frag_future_weather_img_2);
        add(R.id.frag_future_weather_img_3);
        add(R.id.frag_future_weather_img_4);
        add(R.id.frag_future_weather_img_5);
        add(R.id.frag_future_weather_img_6);
        add(R.id.frag_future_weather_img_7);
        add(R.id.frag_future_weather_img_8);
        add(R.id.frag_future_weather_img_9);
        add(R.id.frag_future_weather_img_10);
        add(R.id.frag_future_weather_img_11);
    }};

    private static final List<Integer> futureTemperatureHighList = new ArrayList<Integer>(){{
        add(R.id.frag_temp_high_0);
        add(R.id.frag_temp_high_1);
        add(R.id.frag_temp_high_2);
        add(R.id.frag_temp_high_3);
        add(R.id.frag_temp_high_4);
        add(R.id.frag_temp_high_5);
        add(R.id.frag_temp_high_6);
        add(R.id.frag_temp_high_7);
        add(R.id.frag_temp_high_8);
        add(R.id.frag_temp_high_9);
        add(R.id.frag_temp_high_10);
        add(R.id.frag_temp_high_11);
    }};

    private static final List<Integer> futureTemperatureLowList = new ArrayList<Integer>(){{
        add(R.id.frag_temp_low_0);
        add(R.id.frag_temp_low_1);
        add(R.id.frag_temp_low_2);
        add(R.id.frag_temp_low_3);
        add(R.id.frag_temp_low_4);
        add(R.id.frag_temp_low_5);
        add(R.id.frag_temp_low_6);
        add(R.id.frag_temp_low_7);
        add(R.id.frag_temp_low_8);
        add(R.id.frag_temp_low_9);
        add(R.id.frag_temp_low_10);
        add(R.id.frag_temp_low_11);
    }};

    static {
        stateMap.put("AL", "Alabama");
        stateMap.put("AK", "Alaska");
        stateMap.put("AZ", "Arizona");
        stateMap.put("AR", "Arkansas");
        stateMap.put("CA", "California");
        stateMap.put("CO", "Colorado");
        stateMap.put("CT", "Connecticut");
        stateMap.put("DE", "Delaware");
        stateMap.put("DC", "District Of Columbia");
        stateMap.put("FL", " Florida");
        stateMap.put("GA", "Georgia");
        stateMap.put("HI", "Hawaii");
        stateMap.put("ID", "Idaho");
        stateMap.put("IL", "Illinois");
        stateMap.put("IN", "Indiana");
        stateMap.put("IA", "Iowa");
        stateMap.put("KS", "Kansas");
        stateMap.put("KY", "Kentucky");
        stateMap.put("LA", "Louisiana");
        stateMap.put("ME", "Maine");
        stateMap.put("MD", "Maryland");
        stateMap.put("MA", "Massachusetts");
        stateMap.put("MI", "Michigan");
        stateMap.put("MN", "Minnesota");
        stateMap.put("MS", "Mississippi");
        stateMap.put("MO", "Missouri");
        stateMap.put("MT", "Montana");
        stateMap.put("NE", "Nebraska");
        stateMap.put("NV", "Nevada");
        stateMap.put("NH", "New Hampshire");
        stateMap.put("NJ", "New Jersey");
        stateMap.put("NM", "New Mexico");
        stateMap.put("NY", "New York");
        stateMap.put("NC", "North Carolina");
        stateMap.put("ND", "North Dakota");
        stateMap.put("OH", "Ohio");
        stateMap.put("OK", "Oklahoma");
        stateMap.put("OR", "Oregon");
        stateMap.put("PA", "Pennsylvania");
        stateMap.put("RI", "Rhode Island");
        stateMap.put("SC", "South Carolina");
        stateMap.put("SD", "South Dakota");
        stateMap.put("TN", "Tennessee");
        stateMap.put("TX", "Texas");
        stateMap.put("UT", "Utah");
        stateMap.put("VT", "Vermont");
        stateMap.put("VA", "Virginia");
        stateMap.put("WA", "Washington");
        stateMap.put("WV", "West Virginia");
        stateMap.put("WI", "Wisconsin");
        stateMap.put("WY", "Wyoming");
    }



    static int getWeatherIconId(String weatherCode) {
        return weatherIconMap.get(weatherCode);
    }

    static String getWeatherType(String weatherCode) {
        return weatherTypeMap.get(weatherCode);
    }

    static String convertToMonth(String monthNum) {
        return monthMap.get(monthNum);
    }

    static String getWeekDate(int index) {
        return weekDay[index];
    }

    static int getFutureDateViewId(int index) {
        return futureDateList.get(index);
    }

    static int getFutureWeatherIconViewId(int index) {
        return futureWeatherCodeList.get(index);
    }

    static int getFutureTemperatureHighViewId(int index) {
        return futureTemperatureHighList.get(index);
    }

    static int getFutureTemperatureLowViewId(int index) {
        return futureTemperatureLowList.get(index);
    }

    static String getStateFullName(String state) {
        return stateMap.get(state);
    }

}
