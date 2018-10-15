package com.conx2share.conx2share.ui.messaging;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActivity;
import com.conx2share.conx2share.util.EventBusUtil;

import net.protyposis.android.spectaculum.InputSurfaceHolder;
import net.protyposis.android.spectaculum.VideoView;

import java.io.IOException;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.inject.InjectView;
import uk.co.senab.photoview.PhotoView;

public class MediaViewerActivity extends BaseActivity {

    private static final String TAG = MediaViewerActivity.class.getName();

    public static final String IMAGE_EXTRA_KEY_SMALL = "imageUrlSmall";
    public static final String IMAGE_EXTRA_KEY = "imageUrl";
    public static final String VIDEO_EXTRA_KEY = "videoUrl";
    public static final String AUDIO_EXTRA_KEY = "audioUrl";

    public static void startInImageViewMode(Context context, @NonNull String imageUrl) {
        startWith(context, IMAGE_EXTRA_KEY, imageUrl);
    }

    public static void startInVideoViewMode(Context context, @NonNull String videoUrl) {
        startWith(context, VIDEO_EXTRA_KEY, videoUrl);
    }

    public static void startInAudioViewMode(Context context, @NonNull String audioUrl) {
        startWith(context, AUDIO_EXTRA_KEY, audioUrl);
    }

    private static void startWith(Context context, @NonNull String extraKey, @NonNull String url) {
        context.startActivity(new Intent(context, MediaViewerActivity.class)
                .putExtra(extraKey, url));
    }

    @InjectView(R.id.photo_message_view)
    PhotoView photoMessageView;

    @InjectView(R.id.spectaculum_video_view)
    VideoView spectaculumVideoView;

    @InjectView(R.id.video_buffering_spinner)
    ProgressBar videoBufferingSpinner;

    private MediaPlayer mediaPlayer;
    private MediaController mediaController;

    private String imageUrl;
    private boolean errorShowed;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_message_view);
        ButterKnife.bind(this);

        EventBusUtil.getEventBus().register(this);

        Bundle b = getIntent().getExtras();
        imageUrl = b.getString(IMAGE_EXTRA_KEY);

        if (getIntent().hasExtra(VIDEO_EXTRA_KEY)) {
            String videoAddress = b.getString(VIDEO_EXTRA_KEY);
            playMediaByUri(videoAddress);
        } else if (getIntent().hasExtra(AUDIO_EXTRA_KEY)) {
            String audioAddress = b.getString(AUDIO_EXTRA_KEY);
            playMediaByUri(audioAddress);
        } else {
            String smallImageUrl = b.getString(IMAGE_EXTRA_KEY_SMALL);
            Glide.with(this)
                    .load(smallImageUrl)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            photoMessageView.post(()-> {
                                // Do something with bitmap here.
                                photoMessageView.setImageBitmap(bitmap);

                                Glide.with(MediaViewerActivity.this)
                                        .load(imageUrl)
                                        .asBitmap()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                                // Do something with bitmap here.
                                                photoMessageView.setImageBitmap(bitmap);
                                            }
                                        });
                            });
                        }
                    });
        }
    }

    private void loadBigImage(Drawable placeholder) {
        Glide.with(this)
                .load(imageUrl)
//                .placeholder(placeholder)
                .dontAnimate()
                .dontTransform()
                .into(photoMessageView);
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        super.onDestroy();
    }

    private void playMediaByUri(String videoUri) {
        photoMessageView.setVisibility(View.GONE);
        spectaculumVideoView.setVisibility(View.VISIBLE);

        spectaculumVideoView.setTouchEnabled(true);
        spectaculumVideoView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && mediaController != null) {
                long durationMs = event.getEventTime() - event.getDownTime();

                if (durationMs < 500) {
                    if (mediaController.isShowing()) {
                        mediaController.hide();
                    } else {
                        mediaController.show();
                    }
                }
            }

            spectaculumVideoView.onTouchEvent(event);
            return true;
        });


        spectaculumVideoView.setOnPreparedListener(mp -> {
            mediaPlayer = mp;

            initMediaController(new MediaPlayerControl());

            videoBufferingSpinner.setVisibility(View.GONE);
            mediaController.setEnabled(true);
            spectaculumVideoView.start();
        });

        spectaculumVideoView.setOnErrorListener((mp, what, extra) -> {
            if (!errorShowed) {
                Toast.makeText(MediaViewerActivity.this,
                        "Cannot play the video, please try again later",
                        Toast.LENGTH_LONG).show();
                errorShowed = true;
            }

            videoBufferingSpinner.setVisibility(View.GONE);
            mediaController.setEnabled(false);
            return true;
        });

        spectaculumVideoView.setVideoURI(Uri.parse(videoUri));
    }

    public void initMediaController(MediaController.MediaPlayerControl mediaPlayerControl) {
        mediaController = new MediaController(this);
        mediaController.setAnchorView(spectaculumVideoView);
        mediaController.setMediaPlayer(mediaPlayerControl);
        mediaController.requestFocus();
        mediaController.setEnabled(false);
    }

    private final class MediaPlayerControl implements MediaController.MediaPlayerControl {
        @Override
        public void start() {
            if (mediaPlayer != null) mediaPlayer.start();
        }

        @Override
        public void pause() {
            if (mediaPlayer != null) mediaPlayer.pause();
        }

        @Override
        public int getDuration() {
            return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
        }

        @Override
        public int getCurrentPosition() {
            return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
        }

        @Override
        public void seekTo(int pos) {
            if (mediaPlayer != null) mediaPlayer.seekTo(pos);
        }

        @Override
        public boolean isPlaying() {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        }

        @Override
        public int getBufferPercentage() {
            return 0;
        }

        @Override
        public boolean canPause() {
            return true;
        }

        @Override
        public boolean canSeekBackward() {
            return true;
        }

        @Override
        public boolean canSeekForward() {
            return true;
        }

        @Override
        public int getAudioSessionId() {
            return mediaPlayer != null ? mediaPlayer.getAudioSessionId() : 0;
        }
    }
}