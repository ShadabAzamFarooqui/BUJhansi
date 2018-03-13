package com.example.hp.stickpick.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.utils.ParameterConstants;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {


    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle("Video Lectures");
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#ffffff"));

        ((TextView) findViewById(R.id.text1)).setOnClickListener(this);
        ((TextView) findViewById(R.id.text2)).setOnClickListener(this);
        ((TextView) findViewById(R.id.text3)).setOnClickListener(this);
        ((TextView) findViewById(R.id.text4)).setOnClickListener(this);
        ((TextView) findViewById(R.id.text5)).setOnClickListener(this);
        ((TextView) findViewById(R.id.text6)).setOnClickListener(this);
        ((TextView) findViewById(R.id.text7)).setOnClickListener(this);
        ((TextView) findViewById(R.id.text8)).setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(VideoActivity.this, VideoWatchActivity.class);
        switch (v.getId()) {
            case R.id.text1:
                intent.putExtra(ParameterConstants.YOUTUBE_CODE, ParameterConstants.YOUTUBE_VIDEO_LINK_1);
                intent.putExtra(ParameterConstants.YOUTUBE_TITLE, ParameterConstants.YOUTUBE_VIDEO_TITLE_1);
                intent.putExtra(ParameterConstants.YOUTUBE_CONTENT, getResources().getString(R.string.automobile));
                break;

            case R.id.text2:
                intent.putExtra(ParameterConstants.YOUTUBE_CODE, ParameterConstants.YOUTUBE_VIDEO_LINK_2);
                intent.putExtra(ParameterConstants.YOUTUBE_TITLE, ParameterConstants.YOUTUBE_VIDEO_TITLE_2);
                intent.putExtra(ParameterConstants.YOUTUBE_CONTENT, getResources().getString(R.string.SixSense));
                break;

            case R.id.text3:
                intent.putExtra(ParameterConstants.YOUTUBE_CODE, ParameterConstants.YOUTUBE_VIDEO_LINK_3);
                intent.putExtra(ParameterConstants.YOUTUBE_TITLE, ParameterConstants.YOUTUBE_VIDEO_TITLE_3);
                intent.putExtra(ParameterConstants.YOUTUBE_CONTENT, getResources().getString(R.string.computer));
                break;

            case R.id.text4:
                intent.putExtra(ParameterConstants.YOUTUBE_CODE, ParameterConstants.YOUTUBE_VIDEO_LINK_4);
                intent.putExtra(ParameterConstants.YOUTUBE_TITLE, ParameterConstants.YOUTUBE_VIDEO_TITLE_4);
                intent.putExtra(ParameterConstants.YOUTUBE_CONTENT, getResources().getString(R.string.java));
                break;

            case R.id.text5:
                intent.putExtra(ParameterConstants.YOUTUBE_CODE, ParameterConstants.YOUTUBE_VIDEO_LINK_5);
                intent.putExtra(ParameterConstants.YOUTUBE_TITLE, ParameterConstants.YOUTUBE_VIDEO_TITLE_5);
                intent.putExtra(ParameterConstants.YOUTUBE_CONTENT, getResources().getString(R.string.iOS));
                break;

            case R.id.text6:
                intent.putExtra(ParameterConstants.YOUTUBE_CODE, ParameterConstants.YOUTUBE_VIDEO_LINK_6);
                intent.putExtra(ParameterConstants.YOUTUBE_TITLE, ParameterConstants.YOUTUBE_VIDEO_TITLE_6);
                intent.putExtra(ParameterConstants.YOUTUBE_CONTENT, getResources().getString(R.string.linux));
                break;

            case R.id.text7:
                intent.putExtra(ParameterConstants.YOUTUBE_CODE, ParameterConstants.YOUTUBE_VIDEO_LINK_7);
                intent.putExtra(ParameterConstants.YOUTUBE_TITLE, ParameterConstants.YOUTUBE_VIDEO_TITLE_7);
                intent.putExtra(ParameterConstants.YOUTUBE_CONTENT, getResources().getString(R.string.hacking));
                break;
            case R.id.text8:
                intent.putExtra(ParameterConstants.YOUTUBE_CODE, ParameterConstants.YOUTUBE_VIDEO_LINK_8);
                intent.putExtra(ParameterConstants.YOUTUBE_TITLE, ParameterConstants.YOUTUBE_VIDEO_TITLE_8);
                intent.putExtra(ParameterConstants.YOUTUBE_CONTENT, getResources().getString(R.string.sdlc));
                break;

        }
        startActivity(intent);
    }
}




