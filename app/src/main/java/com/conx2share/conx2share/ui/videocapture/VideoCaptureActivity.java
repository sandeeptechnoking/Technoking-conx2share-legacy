package com.conx2share.conx2share.ui.videocapture;

import com.conx2share.conx2share.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;


public class VideoCaptureActivity extends FragmentActivity implements CaptureVideoFragment.Callbacks {

    public static final String FILE_PATH_KEY = "filePath";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_capture);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        CaptureVideoFragment fragment = new CaptureVideoFragment();

        ft.replace(R.id.fragment_holder, fragment, CaptureVideoFragment.TAG);
        ft.commit();
    }

    @Override
    public void returnFilePath(String filePath) {
        Intent intent = new Intent();
        intent.putExtra(FILE_PATH_KEY, filePath);
        setResult(RESULT_OK, intent);
        finish();
    }
}
