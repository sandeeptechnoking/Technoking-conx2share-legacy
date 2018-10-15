package com.conx2share.conx2share.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.conx2share.conx2share.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;


public class SimpleAudioHelper implements AudioHelper {

    private static final String AUDIO_SUFFIX = ".mp4";
    private static final int MAX_DURATION_MS = 1000 * 60 * 2;
    private static final int ONE_SEC = 1000;

    private String audioFilePath;
    private MediaRecorder mediaRecorder;
    private MediaPlayer player;
    private Context context;
    private TimeListener timeListener;
    private StopPlayListener stopPlayListener;

    private CountDownTimer countDownTimer;

    @Inject
    SnackbarUtil snackBarUtil;

    public SimpleAudioHelper(StopPlayListener stopPlayListener, @Nullable TimeListener timeListener) {
        this.stopPlayListener = stopPlayListener;
        this.timeListener = timeListener;
    }

    public SimpleAudioHelper(@NonNull Context context, @Nullable TimeListener timeListener) {
        this.context = context;
        this.timeListener = timeListener;
        countDownTimer = new CountDownTimer(MAX_DURATION_MS, ONE_SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                onTickExecutor(millisUntilFinished);
            }

            @Override
            public void onFinish() {
            }
        };
    }

    private void onTickExecutor(long millisUntilFinished) {
        if (timeListener != null) {
            String time = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
            );
            timeListener.onRecordTimeChange(time);
        }
    }

    @Override
    public boolean startRecording() {
        if(!PermissionUtil.hasAudioRecordPermission(context)) return false;

        if (mediaRecorder != null) {
            cancelRecording();
        }
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        audioFilePath = getAudioFileName();
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setMaxDuration(MAX_DURATION_MS);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            countDownTimer.start();
            return true;
        } catch (IOException e) {
            cancelRecording();
            snackBarUtil.displaySnackBar(context, R.string.cant_start_audio_record);
            return false;
        }
    }

    @Override
    public File stopRecording() {
        countDownTimer.cancel();
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (!TextUtils.isEmpty(audioFilePath)) {
            File aFile = new File(audioFilePath);
            if (aFile.exists()) {
                return aFile;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void cancelRecording() {
        stopRecording();
        deleteAudioFile();
    }

    @Override
    public boolean startPlaying(String url, @IntRange(from = 0, to = 120) Integer maxDurationSec) {
        countDownTimer = new CountDownTimer(maxDurationSec * ONE_SEC, ONE_SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                onTickExecutor(millisUntilFinished);
            }

            @Override
            public void onFinish() {
            }
        };
        if (player != null) {
            stopPlaying();
        }
        try {
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(url);
            player.setOnPreparedListener(mp -> {
                player.start();
                countDownTimer.start();
            });
            player.setOnCompletionListener(mp -> stopPlaying());
            player.setOnErrorListener((mp, what, extra) -> {
                stopPlaying();
                return false;
            });
            player.prepareAsync();
            return true;
        } catch (Exception e) {
            stopPlaying();
            return false;
        }
    }

    @Override
    public void stopPlaying() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (stopPlayListener != null) {
            stopPlayListener.onStopPlaying();
        }
    }

    private void deleteAudioFile() {
        if (!TextUtils.isEmpty(audioFilePath)) {
            File aFile = new File(audioFilePath);
            if (aFile.exists()) {
                aFile.delete();
            }
        }
        audioFilePath = null;
    }

    private String getAudioFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return context.getExternalCacheDir().getAbsolutePath() + "MSG_" + timeStamp + AUDIO_SUFFIX;
    }

    public interface TimeListener {
        void onRecordTimeChange(String time);
    }

    public interface StopPlayListener {
        void onStopPlaying();
    }
}
