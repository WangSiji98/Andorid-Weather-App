package com.oasis.weatherapp;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class LaunchActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGHT = 1500;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉标题栏，继承 Activity
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏ActionBar，继承 AppCompatActivity
//        this.getSupportActionBar().hide();
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launch);

        handler = new Handler();
        // 延迟SPLASH_DISPLAY_LENGHT时间然后跳转到MainActivity
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this,
                    MainActivity.class);
                startActivity(intent);
                LaunchActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGHT);
    }
}