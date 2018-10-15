package com.conx2share.conx2share.ui.feed.post;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.conx2share.conx2share.async.LoadBusinessesToBusAsyncTask;
import com.conx2share.conx2share.async.LoadGroupsToBusAsyncTask;
import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;
import com.conx2share.conx2share.util.LogUtil;
import com.conx2share.conx2share.util.MediaUploadUtil;

public class PostActivity extends BaseActionBarActivity {

    private static final String TAG = PostActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            PostReceiver postReceiver = null;
            Intent launchIntent = getIntent();

            if (launchIntent.hasExtra(Group.EXTRA)) {
                Log.d(TAG, "has group extra");
                try {
                    postReceiver = launchIntent.getParcelableExtra(Group.EXTRA);
                } catch (Exception e) {
                    if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "error obtaining group from extras");
                    }
                }
            } else if (launchIntent.hasExtra(Business.EXTRA)) {
                Log.d(TAG, "has business extra");
                try {
                    postReceiver = launchIntent.getParcelableExtra(Business.EXTRA);
                } catch (Exception e) {
                    if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "error obtaining business from extras");
                    }
                }
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            PostFragment fragment = PostFragment.newInstance(postReceiver);
            ft.add(android.R.id.content, fragment);
            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new LoadGroupsToBusAsyncTask(this).execute();
        new LoadBusinessesToBusAsyncTask(this).execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MediaUploadUtil.EXTRA_PHOTO_URI, MediaUploadUtil.getPhotoUri());
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Uri photoUri = savedInstanceState.getParcelable(MediaUploadUtil.EXTRA_PHOTO_URI);
        MediaUploadUtil.setPhotoUri(photoUri);
    }
}
