package com.conx2share.conx2share.ui.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.GetEventAsync;
import com.conx2share.conx2share.async.RegisterAttendanceAsync;
import com.conx2share.conx2share.model.Event;
import com.conx2share.conx2share.model.EventResponse;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.RsvpResponse;
import com.conx2share.conx2share.streaming.CameraActivity;
import com.conx2share.conx2share.streaming.EventVideoActivity;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.conx2share.conx2share.util.ValidationUtil;
import com.nispok.snackbar.SnackbarManager;

import java.util.Date;

import javax.inject.Inject;

import retrofit.RetrofitError;
import roboguice.inject.InjectView;

import static com.conx2share.conx2share.ui.events.EventActivity.EXTRA_EVENT_ID;

public class EventFragment extends BaseFragment {

    public static final String TAG = EventFragment.class.getSimpleName();

    private int mEventId;

    private Event mEvent;

    @InjectView(R.id.event_photo)
    ImageView mPhoto;

    @InjectView(R.id.watch_button)
    ImageView mWatchButton;

    @InjectView(R.id.event_datetime)
    TextView mDateTime;

    @InjectView(R.id.event_name)
    TextView mName;

    @InjectView(R.id.event_location)
    TextView mLocation;

    @InjectView(R.id.event_going_value)
    TextView mGoing;

    @InjectView(R.id.event_maybe_value)
    TextView mMaybe;

    @InjectView(R.id.event_attendees_value)
    TextView mAttendees;

    @InjectView(R.id.event_description)
    TextView mDescription;

    @InjectView(R.id.event_root)
    RelativeLayout mMainView;

    @InjectView(R.id.event_progress)
    ProgressBar mProgress;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @InjectView(R.id.event_user_stats)
    LinearLayout mEventUserStats;

    @InjectView(R.id.event_options_container)
    RadioGroup mRsvpOptions;

    @InjectView(R.id.event_option_going)
    RadioButton mRsvpGoing;

    @InjectView(R.id.event_option_maybe)
    RadioButton mRsvpMaybe;

    private GetEventAsync mGetEventAsync;

    private String  mStreamEvent;
    private boolean mIsOwner;
    private boolean isStreaming;
    private boolean isEventLoading;
    private boolean tryingToWatchLiveSteam = false;

    private RegisterAttendanceAsync mRegisterAttendanceAsync;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mEventId = getActivity().getIntent().getIntExtra(EXTRA_EVENT_ID, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRsvpGoing.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());
            if (v.isSelected()) {
                mRsvpMaybe.setSelected(false);
            }
            setAttendence(mEventId, v.isSelected(), mRsvpMaybe.isSelected());
        });

        mRsvpMaybe.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());
            if (v.isSelected()) {
                mRsvpGoing.setSelected(false);
            }
            setAttendence(mEventId, mRsvpGoing.isSelected(), v.isSelected());
        });

        mWatchButton.setOnClickListener(v -> {
            tryingToWatchLiveSteam = true;
            retryToLoadEvent();
        });
    }

    private void launchStreamingActivity() {
        Intent intent = new Intent(this.getContext(), CameraActivity.class);
        intent.putExtra(EventActivity.EXTRA_EVENT_ID, mEventId);
        startActivity(intent);
    }

    private void playEventVideo() {
        Intent intent = new Intent(this.getContext(), EventVideoActivity.class);
        intent.putExtra(EventActivity.EXTRA_EVENT_STREAM, mStreamEvent);
        startActivity(intent);
    }

    private void watchLiveStream() {
        if (isEventLoading) return;

        if (mIsOwner && isStreaming) {
            launchStreamingActivity();
        } else {
            if (mStreamEvent != null) {
                playEventVideo();
            } else {
                Date startStreamTime = new Date(mEvent.getStartTimeMillis());
                Date currentTime = new Date();
                if (startStreamTime.before(currentTime)) {
                    if (getActivity() != null) {
                        mSnackbarUtil.displaySnackBar(getActivity(), R.string.event_start_stream_not_live);
                    }
                } else {
                    if (getActivity() != null) {
                        mSnackbarUtil.displaySnackBar(getActivity(), R.string.event_not_start);
                    }
                }
//                retryToLoadEvent();
            }
        }
    }

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

    private void retryToLoadEvent() {
        isEventLoading = true;
        mProgress.setVisibility(View.VISIBLE);
        getEvent();
    }

    public void registerEventAttendance(int eventId, int status) {
        mProgress.setVisibility(View.VISIBLE);
        if (mRegisterAttendanceAsync != null) {
            return;
        }
        mRegisterAttendanceAsync = new RegisterAttendanceAsync(getActivity(), eventId, status) {
            @Override
            protected void onSuccess(Result<RsvpResponse> result) {
                updateEventWithRsvpStatus(result.getResource().getRsvp().getStatus());
                mProgress.setVisibility(View.GONE);
                mRegisterAttendanceAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                mProgress.setVisibility(View.GONE);
                if (getActivity() != null) {
                    mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.event_register_attendance_error, R
                            .string.retry, snackbar -> registerEventAttendance(eventId, status));
                }
                mRegisterAttendanceAsync = null;
            }
        }.executeInParallel();
    }

    private void updateEventWithRsvpStatus(String rsvpStatus) {
        if (!ValidationUtil.checkIfStringIsValid(rsvpStatus)) {
            rsvpStatus = "0";
        }
        mEvent.setRsvp_status(rsvpStatus);
        updateRsvpUi();
    }

    private void updateRsvpUi() {
        mRsvpGoing.setSelected(mEvent.getRsvp_status().equals("1"));
        mRsvpMaybe.setSelected(mEvent.getRsvp_status().equals("2"));

    }

    protected void getEvent() {
        if (mGetEventAsync != null) {
            Log.w(TAG, "Already getting event info, new request to get group info will be ignored");
            return;
        }

        mGetEventAsync = new GetEventAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<EventResponse> result) {
                enableProgressBar(false);
                mEvent = result.getResource().getEvent();
                if (mEvent.getIs_owner()) {
                    mEventUserStats.setVisibility(View.VISIBLE);
                    mRsvpOptions.setVisibility(View.GONE);
                } else {
                    mRsvpOptions.setVisibility(View.VISIBLE);
                    mEventUserStats.setVisibility(View.GONE);
                    updateRsvpUi();
                }
                Activity activity = getActivity();
                if (activity != null) {
                    ((EventActivity) activity).mEvent = mEvent;
                }
                setupEventInfo();
                /*adjustVisibility();
                setGroupStatus();
                getGroupPosts(mEventId);*/
                mGetEventAsync = null;
                isEventLoading = false;

                if (tryingToWatchLiveSteam) {
                    watchLiveStream();
                    tryingToWatchLiveSteam = false;
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not get group info", error);
                enableProgressBar(false);
                if (getActivity() != null) {
                    mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_get_group_text, R.string
                            .retry, snackbar -> {
                        getEvent();
                        SnackbarManager.dismiss();
                    });
                }
                mGetEventAsync = null;
                isEventLoading = false;
            }

        }.executeInParallel(mEventId);
    }

    @Override
    public void onResume() {
        super.onResume();
        enableProgressBar(true);
        getEvent();

    }

    public void setupEventInfo() {
        Activity activity = getActivity();
        if (activity != null) {
            Glide.with(activity).load(mEvent.getUrl()).dontAnimate().centerCrop().into(mPhoto);
        }
        mDateTime.setText(mEvent.getFormattedStartDateTime());
        mName.setText(mEvent.getName());
        if (ValidationUtil.checkIfStringIsValid(mEvent.getLocation())) {
            mLocation.setText(mEvent.getLocation());
        } else {
            mLocation.setText(R.string.no_location_given);
        }
        mGoing.setText(mEvent.getUsers_going());
        mMaybe.setText(mEvent.getUsers_maybe_going());
        mAttendees.setText("0"); // TODO find attendees value in events API json response
        mDescription.setText(mEvent.getDescription());
        if (mEvent.getIs_owner()) {
            mIsOwner = true;
        }
        try {
            if (mEvent.getLive_stream().getIos() != null)
                mStreamEvent = mEvent.getLive_stream().getIos();

            if (mEvent.getLive_stream().getAndroid() != null)
                mStreamEvent = mEvent.getLive_stream().getAndroid();

            if (mEvent.getBroadcast_info() != null ) {
                isStreaming = true;
                mWatchButton.setVisibility(View.VISIBLE);
            }

            if (mEvent.getDisposition().equals("FINISHED"))   {
               mWatchButton.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            mStreamEvent = null;
            mWatchButton.setVisibility(View.INVISIBLE);
        }

    }

    private void enableProgressBar(boolean enable) {
        mMainView.setVisibility(enable ? View.GONE : View.VISIBLE);
        mProgress.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    /*@Override
    protected void refreshPosts() {
        getEvent(mEventId);
    }*/
}
