package com.conx2share.conx2share.ui.groups;

import com.conx2share.conx2share.ui.base.BaseActionBarActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public class GroupActivity extends BaseActionBarActivity {

    public static final String EXTRA_GROUP_ID = "groupId";
    public static final String EXTRA_AUTO_PLAY = "autoPlay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getIntent() != null) {
            Fragment fragment = GroupFragment.newInstance();
            int groupId = getIntent().getIntExtra(EXTRA_GROUP_ID, 0);
            boolean autoPlay = getIntent().getBooleanExtra(EXTRA_AUTO_PLAY,false);
            Bundle bundle = new Bundle();
            bundle.putInt(EXTRA_GROUP_ID, groupId);
            bundle.putBoolean(EXTRA_AUTO_PLAY,autoPlay);
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, fragment);
            ft.commit();
        }
    }
}
