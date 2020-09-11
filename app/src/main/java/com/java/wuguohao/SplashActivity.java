package com.java.wuguohao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SplashActivity extends AppCompatActivity implements DataHandler.BitmapFileSaver {
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
                dataHandler.setBitmapFileSaver(SplashActivity.this);
                dataHandler.readEvent(1, 30, "all", SplashActivity.this);
                dataHandler.readEvent(1, 30, "paper", SplashActivity.this);
                dataHandler.readEvent(1, 500, "event", SplashActivity.this);
                dataHandler.readData("China|Beijing",SplashActivity.this);
                dataHandler.readScholar(SplashActivity.this);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 50);

    }

    @Override
    public void saveBitmap(String name, Bitmap bitmap) {
        try {
            FileOutputStream outputStream = openFileOutput(name, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
