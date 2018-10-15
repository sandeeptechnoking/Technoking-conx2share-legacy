package com.conx2share.conx2share.streaming;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.events.EventActivity;

import java.io.IOException;

public class EventVideoActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private static final String EXTRA_EVENT_STREAM = "extra_event_stream";

    private String mStreamEvent;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private MediaController mediaController;
    private MediaPlayer mediaPlayer;

    private ProgressBar progressBar;

    public static void start(Context context, @NonNull String url) {
        context.startActivity(new Intent(context, EventVideoActivity.class)
                .putExtra(EventActivity.EXTRA_EVENT_STREAM, url));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mStreamEvent = getIntent().getStringExtra(EXTRA_EVENT_STREAM);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_player);

        progressBar = (ProgressBar) findViewById(R.id.event_player_progress_bar);
        surfaceView = (SurfaceView) findViewById(R.id.event_video_view);
        surfaceView.setClickable(true);
        surfaceView.setOnClickListener(v -> showMediaController());

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mediaController = new MediaController(this);
        mediaController.setEnabled(false);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setScreenOnWhilePlaying(true);

        findViewById(R.id.exit_button).setOnClickListener(v -> finish());
    }


    @Override
    protected void onStop() {
        hideMediaController();
        super.onStop();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mediaPlayer.setDataSource(mStreamEvent);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();

        boolean portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (videoHeight > videoWidth) {
            if (portrait) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return;
            }
        } else {
            if (portrait) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                return;
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            }
        }

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        surfaceView.setLayoutParams(lp);

        mediaController.setMediaPlayer(new OnMediaPlayerController(mediaPlayer));
        mediaController.setAnchorView(surfaceView);
        mediaController.setEnabled(true);

        mediaPlayer.setDisplay(surfaceHolder);
        mediaPlayer.start();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.stop();
    }

    private void showMediaController() {
        if (mediaController != null && !mediaController.isShowing()) {
            mediaController.show();
        }
    }

    private void hideMediaController() {
        if (mediaController != null && mediaController.isShowing()) {
            mediaController.hide();
        }
    }
}