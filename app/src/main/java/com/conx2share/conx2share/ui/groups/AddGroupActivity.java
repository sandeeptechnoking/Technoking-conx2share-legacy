package com.conx2share.conx2share.ui.groups;

import com.conx2share.conx2share.ui.base.BaseActionBarActivity;
import com.conx2share.conx2share.util.MediaUploadUtil;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public class AddGroupActivity extends BaseActionBarActivity {

    private static final String TAG = AddGroupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Fragment fragment = AddGroupFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, fragment);
            ft.commit();
        }
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
