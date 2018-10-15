package com.conx2share.conx2share.util;

import com.conx2share.conx2share.ui.view.ringdroid.CheapSoundFile;
import com.conx2share.conx2share.ui.view.ringdroid.WaveformView;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;

public class AudioRecordUtil {

    public static final String TAG = AudioRecordUtil.class.getSimpleName();

    private MediaRecorder mMediaRecorder;

    private WaveformView mWaveformView;

    private ProgressBar mVolumeBar;

    private boolean mIsRecording;

    private Context mContext;

    private File mAudioFile;

    public boolean recordAudioTo(String fileName, WaveformView waveformView, ProgressBar volumeBar, Context context) {
        mMediaRecorder = new MediaRecorder();
        mAudioFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS) + "/" + fileName + ".aac");
        mVolumeBar = volumeBar;
        mWaveformView = waveformView;
        mContext = context;
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setOutputFile(mAudioFile.getAbsolutePath());
        mMediaRecorder.setMaxDuration(1000 * 60 * 2);
        try {
            mMediaRecorder.prepare();
            mVolumeBar.setVisibility(View.VISIBLE);
            mWaveformView.setVisibility(View.GONE);

            mMediaRecorder.start();
            mIsRecording = true;
            new VolumeUpdateAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            Log.e(TAG, "Could not record audio", e);
            mIsRecording = false;
        }

        return mIsRecording;
    }

    public File stopRecordingAndReturnFile() {
        mIsRecording = false;
        if (mMediaRecorder != null) {
            try {
                Log.d(TAG, "Stopping and releasing recorder");
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                renderWaveForm();
            } catch (RuntimeException e) {
                Log.e(TAG, "MediaRecorder not initialized");
                mAudioFile = null;
            }
            mMediaRecorder = null;
        }
        return mAudioFile;
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    private void renderWaveForm() {
        try {
            Log.d(TAG, "Rendering wav form");
            mWaveformView.setSoundFile(CheapSoundFile.create(mAudioFile.getAbsolutePath(), fractionComplete -> true));
            mVolumeBar.setVisibility(View.GONE);
            mWaveformView.setVisibility(View.VISIBLE);
            mWaveformView.post(this::fitWaveForm);
        } catch (IOException e) {
            Log.e(TAG, "Failed to find sound file. Exception: " + e.toString());
        }
    }

    private void fitWaveForm() {
        double viewPixelWidth = mWaveformView.getMeasuredWidth();
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(mAudioFile.getAbsolutePath());
            mediaPlayer.prepare();
            while (mWaveformView.secondsToPixels(mediaPlayer.getDuration() / 1000) > viewPixelWidth) {
                mWaveformView.zoomOut();
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not fit waveform");
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    private class VolumeUpdateAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(50);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mIsRecording && mContext != null) {
                int shortVolume = mMediaRecorder.getMaxAmplitude();
                mVolumeBar.setMax(Short.MAX_VALUE / 2);
                mVolumeBar.setProgress(shortVolume);
                new VolumeUpdateAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

}
