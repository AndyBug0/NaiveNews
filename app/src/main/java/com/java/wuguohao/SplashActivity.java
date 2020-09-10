package com.java.wuguohao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {
    private TextView loadingTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_page);
        loadingTip = (TextView) findViewById(R.id.loading_tip);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DataHandler dataHandler = new DataHandler();
                dataHandler.readEvent(1, 30, "all", SplashActivity.this);
                dataHandler.readEvent(1, 30, "paper", SplashActivity.this);
                dataHandler.readData("China|Beijing",SplashActivity.this);
                dataHandler.readMap("病毒", SplashActivity.this);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 50);

    }
}
