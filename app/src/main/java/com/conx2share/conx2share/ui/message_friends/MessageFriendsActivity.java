package com.conx2share.conx2share.ui.message_friends;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;
import com.conx2share.conx2share.ui.sayno.SayNoFlowInteractor;

import javax.inject.Inject;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.inject.InjectView;


public class MessageFriendsActivity extends BaseActionBarActivity {

    public static final String TAG = MessageFriendsActivity.class.getSimpleName();

    @InjectView(R.id.message_friends_toolbar)
    Toolbar mMessageFriendsToolbar;

    @Inject
    SayNoFlowInteractor sayNoFlowInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_friends);
        ButterKnife.bind(this);

        setSupportActionBar(mMessageFriendsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        View messageFriendsBackButton = findViewById(R.id.message_friends_back_button);
        messageFriendsBackButton.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messager_menu, menu);
        menu.findItem(R.id.say_no_item)
                .getActionView()
                .setOnClickListener(v -> sayNoFlowInteractor.startSayNo(MessageFriendsActivity.this));

        return super.onCreateOptionsMenu(menu);
    }
}