package com.conx2share.conx2share.ui.groups;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class GroupMembersInviteActivity extends BaseActionBarActivity {

    private static final String TAG = GroupMembersInviteActivity.class.getSimpleName();

    private Group mGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members_invite);
        if (savedInstanceState == null) {

            if (getIntent().hasExtra(Group.EXTRA)) {
                Log.d(TAG, "has group extra");
                mGroup = getIntent().getParcelableExtra(Group.EXTRA);
            } else {
                Log.d(TAG, "does not have group extra");
                throw new IllegalStateException(TAG + " requires Group extra");
            }

            Fragment fragment = GroupMembersInviteFragment.newInstance();
            Bundle args = new Bundle();
            args.putInt("GroupId", mGroup.getId()); // TODO - Extract to constant
            fragment.setArguments(args);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.group_members_fragment_content, fragment).commit();
        }
    }
}
