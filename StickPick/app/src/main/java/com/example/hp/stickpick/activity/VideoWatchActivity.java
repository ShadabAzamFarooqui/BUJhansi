package com.example.hp.stickpick.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.TextView;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class VideoWatchActivity extends YouTubeBaseActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_watch);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#ffffff"));

        Intent intent = getIntent();
        String title = intent.getStringExtra(ParameterConstants.YOUTUBE_TITLE);
        final String code = intent.getStringExtra(ParameterConstants.YOUTUBE_CODE);
        String content = intent.getStringExtra(ParameterConstants.YOUTUBE_CONTENT);
        collapsingToolbarLayout.setTitle(title);
        ((TextView) findViewById(R.id.contentText)).setText(content);

        YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.youtubePlayer);
        playerView.initialize(ParameterConstants.YOUTUBE_API, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                if (!b) {
                    youTubePlayer.cueVideo(code);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });

    }
}
