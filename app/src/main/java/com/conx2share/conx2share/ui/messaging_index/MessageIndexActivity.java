package com.conx2share.conx2share.ui.messaging_index;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseDrawerActivity;
import com.conx2share.conx2share.ui.feed.FeedActivity;
import com.conx2share.conx2share.ui.message_friends.MessageFriendsActivity;
import com.conx2share.conx2share.ui.sayno.SayNoFlowInteractor;

import javax.inject.Inject;

public class MessageIndexActivity extends BaseDrawerActivity {

    private Fragment mFragment;

    @Inject
    SayNoFlowInteractor sayNoFlowInteractor;

    @Override
    public Fragment initializeFragment() {
        if (mFragment == null) {
            mFragment = new MessageIndexFragment();
        }
        return mFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.messages);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getToolbar().getMenu().clear();
        getToolbar().inflateMenu(R.menu.message_index_menu);

        menu.findItem(R.id.say_no_item)
                .getActionView()
                .setOnClickListener(v -> sayNoFlowInteractor.startSayNo(MessageIndexActivity.this));
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            Intent upIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                upIntent = NavUtils.getParentActivityIntent(MessageIndexActivity.this);
            } else {
                upIntent = new Intent(getApplicationContext(), FeedActivity.class);
            }
            startActivity(upIntent);
            finish();
        } else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.say_no_item:
                sayNoFlowInteractor.startSayNo(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onCreateChat() {
        Intent messageFriendsActivity = new Intent(this, MessageFriendsActivity.class);
        startActivity(messageFriendsActivity);
    }
}
