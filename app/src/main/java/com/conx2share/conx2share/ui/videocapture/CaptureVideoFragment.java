package com.conx2share.conx2share.ui.videocapture;


import com.conx2share.conx2share.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

public class CaptureVideoFragment extends Fragment implements View.OnClickListener, SurfaceHolder.Callback {

    public static final String TAG = CaptureVideoFragment.class.getSimpleName();

    public static final int MAX_TIME = 60000 * 5;

    MediaRecorder recorder;

    SurfaceHolder holder;

    File mFile;

    boolean recording = false;

    private Button mButton;

    private Callbacks mCallback;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity doesn't implement Callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_capture_video, container, false);
        recorder = new MediaRecorder();
        initRecorder();
        mButton = (Button) view.findViewById(R.id.btnCaptureVideo);
        mButton.setOnClickListener(this);
        SurfaceView cameraView = (SurfaceView) view.findViewById(R.id.surfaceCamera);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraView.setClickable(true);
        cameraView.setOnClickListener(this);

        return view;
    }

    @SuppressLint({"SdCardPath", "NewApi"})
    private void initRecorder() {

//        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
//        Camera camera = recorder.get
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setVideoFrameRate(30);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setMaxDuration(MAX_TIME);

        recorder.setOrientationHint(90);

        mFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + "conxvideo.mp4");
        recorder.setOutputFile(mFile.getPath());
    }

    private void prepareRecorder() {
        recorder.setPreviewDisplay(holder.getSurface());
        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            //finish();
        } catch (IOException e) {
            e.printStackTrace();
            //finish();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCaptureVideo:
                try {
                    if (recording) {
                        recorder.stop();
                        recording = false;
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        mCallback.returnFilePath(mFile.getPath());
                    } else {
                        recording = true;
                        recorder.start();
                        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        } else {
                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception capturing video", e);
                }

            default:
                break;

        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        prepareRecorder();
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if (recording) {
                recorder.stop();
                recording = false;
            }
            recorder.release();
            // finish();
        } catch (Exception e) {
            Log.e(TAG, "Exception stopping recording", e);
        }

    }

    public interface Callbacks {

        void returnFilePath(String filePath);
    }

}
