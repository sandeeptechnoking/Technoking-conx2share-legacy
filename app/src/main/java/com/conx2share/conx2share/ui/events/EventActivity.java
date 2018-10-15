package com.conx2share.conx2share.ui.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.DeleteEventAsync;
import com.conx2share.conx2share.model.Event;
import com.conx2share.conx2share.model.EventResponse;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class EventActivity extends BaseActionBarActivity {

    public static final String EXTRA_EVENT_ID = "extra_event_id";
    public static final String EXTRA_EVENT_OWNER = "extra_event_owner";
    public static final String EXTRA_EVENT_STREAM = "extra_event_stream";

    private boolean mIsEventOwner;
    private int mEventId;
    private String groupType;

    @InjectView(R.id.event_toolbar)
    Toolbar mEventToolbar;

    public Event mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ButterKnife.bind(this);

        if (savedInstanceState == null && getIntent() != null) {
            mIsEventOwner = getIntent().getExtras().getBoolean(EXTRA_EVENT_OWNER);
            mEventId = getIntent().getExtras().getInt(EXTRA_EVENT_ID);
            groupType = getIntent().getExtras().getString(EventsListActivity.EXTRA_GROUP_TYPE);
        }

        setSupportActionBar(mEventToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        View followingBackButton = findViewById(R.id.event_back);
        followingBackButton.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mEventToolbar.getMenu().clear();
        mEventToolbar.inflateMenu(R.menu.event_detail_menu);
        mEventToolbar.getMenu().findItem(R.id.delete_event).setVisible(mIsEventOwner);
        mEventToolbar.getMenu().findItem(R.id.edit_event).setVisible(mIsEventOwner);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_event:
                showDeleteEventDialog();
                return true;
            case R.id.edit_event:
                launchEditEventActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchEditEventActivity() {
        Intent intent = new Intent(this, NewEventActivity.class);
        intent.putExtra(EventsListActivity.EXTRA_GROUP_OWNER, mIsEventOwner);
        intent.putExtra(EventsListActivity.EXTRA_EVENT_OBJECT, mEvent);
        intent.putExtra(EventsListActivity.EXTRA_GROUP_TYPE, groupType);
        intent.putExtra(EventsListActivity.EXTRA_GROUP_ID, mEvent.getGroup_id());
        startActivity(intent);
    }

    private void showDeleteEventDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.delete_event_dialog_title);
        adb.setMessage(R.string.delete_event_dialog_message);
        adb.setPositiveButton(R.string.continue_btn, (dialog, which) -> {
            deleteEvent();
            dialog.dismiss();
        });
        adb.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        adb.create().show();
    }

    private void deleteEvent() {
        new DeleteEventAsync(this, mEventId) {
            @Override
            protected void onSuccess(Result<EventResponse> result) {
                Toast.makeText(EventActivity.this, R.string.delete_event_success, Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Toast.makeText(EventActivity.this, R.string.delete_event_error, Toast.LENGTH_SHORT).show();
            }
        }.executeInParallel();
    }

}
