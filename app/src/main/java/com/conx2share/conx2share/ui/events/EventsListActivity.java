package com.conx2share.conx2share.ui.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.EventIndexAdapter;
import com.conx2share.conx2share.adapter.GroupIndexAdapter;
import com.conx2share.conx2share.async.GetEventsForGroupAsync;
import com.conx2share.conx2share.async.RegisterAttendanceAsync;
import com.conx2share.conx2share.model.Event;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetEventListResponse;
import com.conx2share.conx2share.network.models.response.RsvpResponse;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;
import com.conx2share.conx2share.util.ComparatorUtil;
import com.conx2share.conx2share.util.EmergencyUtil;
import com.conx2share.conx2share.util.LogUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.conx2share.conx2share.util.ValidationUtil;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;
import retrofit.RetrofitError;

public class EventsListActivity extends BaseActionBarActivity implements GroupIndexAdapter.GroupIndexAdapterCallbacks,
        EventOptionResponder {

    public static final String EXTRA_GROUP_OWNER = "extra_group_owner";
    public static final String EXTRA_GROUP_ID = "extra_group_id";
    // type could be business or group
    public static final String EXTRA_GROUP_TYPE = "extra_group_type";
    public static final String EXTRA_EVENT_OBJECT = "extra_group_object";
    public static final String TAG = EventsListActivity.class.getSimpleName();

    private static int EVENT_CHANGED = 100;

    private boolean isGroupOwner;
    private int groupId;
    private String groupType;

    @Inject
    PreferencesUtil mPreferencesUtil;

    @BindView(R.id.event_list_toolbar)
    public Toolbar toolbar;

    @BindView(R.id.event_list_recyclerview)
    ListView recyclerView;

    @BindView(R.id.event_list_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.event_list_empty_text)
    TextView mNoEvents;

    @Inject
    SnackbarUtil mSnackbarUtil;

    private ArrayList<Event> mEvents;
    private EventIndexAdapter mEventIndexAdapter;
    private Integer mAuthUserId;
    private GetEventsForGroupAsync mGetEventsAsync;
    private RegisterAttendanceAsync mRegisterAttendanceAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        ButterKnife.bind(this);
        if (savedInstanceState == null && getIntent() != null) {
            isGroupOwner = getIntent().getExtras().getBoolean(EXTRA_GROUP_OWNER);
            groupId = getIntent().getExtras().getInt(EXTRA_GROUP_ID);
            groupType = getIntent().getExtras().getString(EXTRA_GROUP_TYPE);
        }

        setSupportActionBar(toolbar);

        setTitle(R.string.events);

        if (mPreferencesUtil.getAuthUser() != null && mPreferencesUtil.getAuthUser().getId() != null) {
            mAuthUserId = mPreferencesUtil.getAuthUser().getId();
        } else {
            EmergencyUtil.emergencyLogoutWithNotification(this, mPreferencesUtil);
        }

        recyclerView.setOnItemClickListener((parent, view1, position, id) -> {
            Intent groupActivityIntent = new Intent(this, EventActivity.class);
            groupActivityIntent.putExtra(EventActivity.EXTRA_EVENT_ID, mEvents.get(position).getId());
            groupActivityIntent.putExtra(EventActivity.EXTRA_EVENT_OWNER, mEvents.get(position).getIs_owner());
            groupActivityIntent.putExtra(EventsListActivity.EXTRA_GROUP_ID, mEvents.get(position).getGroup_id());
            groupActivityIntent.putExtra(EventsListActivity.EXTRA_GROUP_TYPE, groupType);
            startActivity(groupActivityIntent);
        });

        View followingBackButton = findViewById(R.id.event_back);
        followingBackButton.setOnClickListener(v -> finish());

        showProgressBar();
        getEvents();
    }
    
    private void hideProgressBar() {
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public void setupEventsIndexAdapter() {
        if (ValidationUtil.checkIfListIsValid(mEvents)) {
            mEventIndexAdapter = new EventIndexAdapter(mEvents, Glide.with(this), this);
            recyclerView.setAdapter(mEventIndexAdapter);
            enableListView(true);
        } else {
            enableListView(false);
        }
    }

    private void enableListView(boolean enable) {
        recyclerView.setVisibility(enable ? View.VISIBLE : View.GONE);
        mNoEvents.setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    private void showProgressBar() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void getEvents() {
        if (mGetEventsAsync != null) {
            Log.w(TAG, "Get event list request already in progress, new request will be ignored");
            return;
        }

        mGetEventsAsync = new GetEventsForGroupAsync(this, groupId, groupType) {
            @Override
            protected void onSuccess(Result<GetEventListResponse> result) {
                hideProgressBar();
                mEvents = new ArrayList<>();
                mEvents.addAll(result.getResource().getEvents());
                Collections.sort(mEvents, ComparatorUtil.EVENT_DATE_COMPARATOR);
                setupEventsIndexAdapter();
                mGetEventsAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.w(TAG, "Unable to get event", error);
                hideProgressBar();
                mSnackbarUtil.showSnackBarWithAction(EventsListActivity.this, R.string.unable_to_get_group_list_text, R
                        .string.retry, snackbar -> getEvents());
                mGetEventsAsync = null;
            }
        }.executeInParallel();
    }

    @Override
    public void onNearingEndOfList() {

    }

    public void registerEventAttendance(int eventId, int rsvpStatus) {
        progressBar.setVisibility(View.VISIBLE);
        if (mRegisterAttendanceAsync != null) {
            return;
        }
        mRegisterAttendanceAsync = new RegisterAttendanceAsync(this, eventId, rsvpStatus) {
            @Override
            protected void onSuccess(Result<RsvpResponse> result) {
                updateEventWithRsvpStatus(eventId, result.getResource().getRsvp().getStatus());
                progressBar.setVisibility(View.GONE);
                mRegisterAttendanceAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                progressBar.setVisibility(View.GONE);
                mSnackbarUtil.showSnackBarWithAction(EventsListActivity.this, R.string.event_register_attendance_error, R
                        .string.retry, snackbar -> registerEventAttendance(eventId, rsvpStatus));
                mRegisterAttendanceAsync = null;
            }
        }.executeInParallel();
    }

    private void updateEventWithRsvpStatus(int eventId, String rsvpStatus) {
        for (int i = 0; i < mEvents.size(); i++) {
            if (mEvents.get(i).getId() == eventId) {
                if (!ValidationUtil.checkIfStringIsValid(rsvpStatus)) {
                    rsvpStatus = "0";
                }
                mEvents.get(i).setRsvp_status(rsvpStatus);
                break;
            }
        }
        mEventIndexAdapter.notifyDataSetChanged();
    }

    @Override
    public void setAttendence(int eventId, boolean isGoing, boolean isMaybeGoing) {
        int rsvpStatus = 0;

        if (!isGoing && !isMaybeGoing) {
            rsvpStatus = 0;
        }
        if (isGoing && !isMaybeGoing) {
            rsvpStatus = 1;
        }
        if (!isGoing && isMaybeGoing) {
            rsvpStatus = 2;
        }

        registerEventAttendance(eventId, rsvpStatus);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_menu, menu);
        menu.findItem(R.id.create_event_action_button).setVisible(isGroupOwner);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_event_action_button:
                launchAddEventActivity(groupId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void launchAddEventActivity(int groupId) {
        if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "create event selected");
        }
        Intent intent = new Intent(this, NewEventActivity.class);
        intent.putExtra(EventsListActivity.EXTRA_GROUP_ID, groupId);
        intent.putExtra(EventsListActivity.EXTRA_GROUP_TYPE, groupType);
        startActivityForResult(intent, EVENT_CHANGED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EVENT_CHANGED) {
            getEvents();
        }
    }
}
