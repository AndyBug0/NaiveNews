package com.java.wuguohao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_page);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new DataHandler().readEvent(1, 10, "all", SplashActivity.this);
                new DataHandler().readData("China",SplashActivity.this);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 50);
    }
}
