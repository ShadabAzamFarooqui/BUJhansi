package com.example.hp.stickpick.utils;

import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Shadab Azam Farooqui on 20-Jan-18.
 */

public class EventBussHandler extends AppCompatActivity {
    public EventBussHandler(){
        EventBus.getDefault().register(this);
    }
    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}
