package com.example.hp.stickpick.activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.example.hp.stickpick.R;

public class WelcomeActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Window window = this.getWindow();
        try {
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        }catch (Error e){

        }
        Thread t1 = new Thread() {
            public void run() {
                try {
                    sleep(3 * 1000);
                    Intent i = new Intent(WelcomeActivity.this, SplashActivity.class);
                    startActivity(i);
                    finish();
                } catch (Exception e) {

                }
            }
        };
        t1.start();
    }

    @Override
    public void onBackPressed() {
    }
}
