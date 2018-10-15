package com.conx2share.conx2share.ui.livestream;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.AuthUser;
import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.model.Event;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.models.response.BusinessesResponse;
import com.conx2share.conx2share.network.models.response.GetEventListResponse;
import com.conx2share.conx2share.network.models.response.GetGroupListResponse;
import com.conx2share.conx2share.streaming.CameraActivity;
import com.conx2share.conx2share.ui.base.BaseAppCompatActivity;
import com.conx2share.conx2share.ui.livestream.adapter.AssociatedEventsRecyclerAdapter;
import com.conx2share.conx2share.ui.livestream.adapter.DividerItemTypeDecoration;
import com.conx2share.conx2share.ui.livestream.adapter.StreamAsRecyclerAdapter;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.conx2share.conx2share.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;
import butterknife.OnClick;
import roboguice.inject.InjectView;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class StreamHomeActivity extends BaseAppCompatActivity {

    private static final String TAG = StreamHomeActivity.class.getSimpleName();

    private static final String EXTRAS_IS_GROUP = "from_group_key";
    private static final String EXTRAS_STREAMER_ID = "streamer_id_key";

    @BindView(R.id.stream_toolbar)
    Toolbar toolbar;

    @BindView(R.id.stream_description_input_layout)
    TextInputLayout streamDescriptionInputLayout;

    @BindView(R.id.associated_event_dropdown_arrow)
    ImageView associatedEventDropdownArrow;

    @BindView(R.id.stream_as_image)
    AvatarImageView streamAsImage;

    @BindView(R.id.associated_event_image)
    AvatarImageView associatedEventImage;

    @BindView(R.id.stream_as_value_tv)
    TextView streamAsValueTv;

    @BindView(R.id.associated_event_value_tv)
    TextView associatedEventTv;

    @BindView(R.id.associated_event_container)
    View associatedEventContainer;

    @Inject
    NetworkClient networkClient;

    @Inject
    PreferencesUtil preferencesUtil;

    private PopupWindow streamAsPopup;
    private RecyclerView popupRecyclerView;

    private AuthUser authUser;

    private List<Group> groupList = new ArrayList<>();
    private List<Business> businessList = new ArrayList<>();
    private List<Event> eventList = new ArrayList<>();

    private StreamAsRecyclerAdapter streamAsAdapter;
    private AssociatedEventsRecyclerAdapter associatedEventsAdapter;

    private int streamerId;
    private StreamMode streamMode;

    private enum StreamMode {
        USER, GROUP, EVENT
    }

    public static void startFromHome(Context context) {
        Intent intent = new Intent(context, StreamHomeActivity.class);
        intent.putExtra(EXTRAS_IS_GROUP, false);
        context.startActivity(intent);
    }

    public static void startFromGroup(Context context, int groupId) {
        Intent intent = new Intent(context, StreamHomeActivity.class);
        intent.putExtra(EXTRAS_IS_GROUP, true);
        intent.putExtra(EXTRAS_STREAMER_ID, groupId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_home);
        ButterKnife.bind(this);

        parseExtras(getIntent().getExtras());
        initActionBar();
        initViews();
        if (streamMode == StreamMode.USER) {
            applyUserSelection(authUser);
        } else {
            applyGroupMode();
        }

        loadGroupsAsync();
        loadBusinessesAsync();
    }

    private void parseExtras(Bundle extras) {
        authUser = preferencesUtil.getAuthUser();
        boolean isGroup = extras.getBoolean(EXTRAS_IS_GROUP, false);
        streamerId = isGroup ? extras.getInt(EXTRAS_STREAMER_ID) : authUser.getId();
        streamMode = isGroup ? StreamMode.GROUP : StreamMode.USER;
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle("New Livestream");
        }
    }

    private void initViews() {
        streamAsAdapter = new StreamAsRecyclerAdapter();
        associatedEventsAdapter = new AssociatedEventsRecyclerAdapter();

        View popupView = LayoutInflater.from(this).inflate(R.layout.window_popup_stream_as, null);
        streamAsPopup = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        popupRecyclerView = (RecyclerView) popupView.findViewById(R.id.stream_as_recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        popupRecyclerView.setLayoutManager(mLayoutManager);
        popupRecyclerView.addItemDecoration(new DividerItemTypeDecoration(this));

        streamAsAdapter.setUser(authUser);
        streamAsAdapter.setGroups(groupList);
        streamAsAdapter.setBusinesses(businessList);
        streamAsAdapter.setItemClickListener(new StreamAsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onUserClick(AuthUser user) {
                applyUserSelection(user);
            }

            @Override
            public void onGroupClick(Group group) {
                applyGroupSelection(group);
            }

            @Override
            public void onBusinessClick(Business business) {
                applyBusinessSelection(business);
            }
        });
        popupRecyclerView.setAdapter(streamAsAdapter);

        streamAsPopup.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent)));
        streamAsPopup.setOutsideTouchable(true);

        associatedEventsAdapter.setEvents(eventList);
        associatedEventsAdapter.setItemClickListener(new AssociatedEventsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onNoEventClick() {
                applyEventSelection(null);
            }

            @Override
            public void onEventClick(Event event) {
                applyEventSelection(event);
            }
        });
    }

    private void applyUserSelection(AuthUser user) {
        hideStreamAsPopup();
        Glide.clear(associatedEventImage);
        associatedEventImage.setVisibility(View.INVISIBLE);
        associatedEventTv.setText(R.string.streaming_no_upcoming_event);
        associatedEventContainer.setVisibility(View.GONE);
        streamDescriptionInputLayout.setVisibility(View.VISIBLE);
        setStreamerData(user.getAvatar().getAvatar().getThumbUrl(), user.getFirstName(), user.getLastName());

        streamMode = StreamMode.USER;
        streamerId = user.getId();
    }

    private void applyGroupSelection(Group group) {
        hideStreamAsPopup();
        associatedEventContainer.setVisibility(View.VISIBLE);
        associatedEventImage.setVisibility(View.INVISIBLE);
        streamDescriptionInputLayout.setVisibility(View.VISIBLE);
        setStreamerData(group.getGroupAvatarUrl(), group.getName(), "");
        chooseGroup(group.getId(), "group");

        streamMode = StreamMode.GROUP;
        streamerId = group.getId();
    }

    private void applyGroupMode() {
        associatedEventContainer.setVisibility(View.VISIBLE);
        setStreamerData("", "", "");
        chooseGroup(streamerId, "group");
    }

    private void applyBusinessSelection(Business business) {
        hideStreamAsPopup();
        associatedEventContainer.setVisibility(View.VISIBLE);
        setStreamerData(business.getAvatar().getAvatar().getThumbUrl(), business.getName(), "");
        chooseGroup(business.getId(), "business");

        streamMode = StreamMode.GROUP;
        streamerId = business.getId();
    }

    private void applyEventSelection(Event event) {
        hideStreamAsPopup();
        if (event != null) {
            setAssociatedEventData(event.getUrl(), event.getName());
            streamDescriptionInputLayout.setVisibility(View.GONE);

            streamMode = StreamMode.EVENT;
            streamerId = event.getId();

        } else {
            Glide.clear(associatedEventImage);
            associatedEventImage.setVisibility(View.INVISIBLE);
            associatedEventTv.setText(R.string.stream_as_no_event);
            streamDescriptionInputLayout.setVisibility(View.VISIBLE);
        }

    }

    private void setStreamerData(String imageUrl, String firstName, String lastName) {
        streamAsImage.initView(imageUrl, firstName, lastName);
        streamAsValueTv.setText(String.format("%s %s", firstName, lastName));
    }

    private void setAssociatedEventData(String eventImageUrl, String name) {
        associatedEventImage.setVisibility(View.VISIBLE);
        associatedEventImage.initView(eventImageUrl, name);
        associatedEventTv.setText(name);
    }

    private void handleEventsResult(List<Event> events) {
        Collections.reverse(events);
        eventList.clear();
        eventList.addAll(events);
        boolean disabledEvents = events.isEmpty();
        associatedEventDropdownArrow.setVisibility(disabledEvents ? View.INVISIBLE : View.VISIBLE);
        associatedEventTv.setEnabled(!disabledEvents);
        associatedEventTv.setText(disabledEvents ? R.string.streaming_no_upcoming_event : R.string.stream_as_no_event);

        associatedEventsAdapter.notifyDataSetChanged();
    }

    private void handleGroupsResult(List<Group> groups) {
        groupList.clear();
        groupList.addAll(groups);

        if (streamMode == StreamMode.GROUP) {
            Observable.from(groups)
                    .filter(group -> group.getId() == streamerId)
                    .subscribe(group -> {
                        streamAsImage.initView(group.getGroupAvatarUrl(), group.getName());
                        streamAsValueTv.setText(group.getName());
                    }, throwable -> Log.e(TAG, "groups filtering error"));
        }

        streamAsAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.start_stream_btn)
    public void startStream() {
        switch (streamMode) {
            case USER:
                CameraActivity.startUserStream(this, streamerId);
                break;
            case GROUP:
                CameraActivity.startGroupStream(this, streamerId);
                break;
            case EVENT:
                CameraActivity.startEventStream(this, streamerId);
                break;
        }
        finish();
    }

    @OnClick({R.id.stream_as_value_tv, R.id.stream_as_dropdown_arrow})
    public void showStreamAsPopup() {
        if (streamAsPopup.isShowing()) {
            streamAsPopup.dismiss();
        }
        popupRecyclerView.setAdapter(streamAsAdapter);
        streamAsPopup.showAsDropDown(streamAsValueTv, 0, 0);
    }

    @OnClick({R.id.associated_event_value_tv, R.id.associated_event_dropdown_arrow})
    public void showAssociatedEventsPopup() {
        if (streamAsPopup.isShowing()) {
            streamAsPopup.dismiss();
        }
        popupRecyclerView.setAdapter(associatedEventsAdapter);
        streamAsPopup.showAsDropDown(streamAsValueTv, 0, 0);
    }

    private void chooseGroup(int groupId, String type) {
        associatedEventTv.setText(R.string.streaming_no_upcoming_event);
        loadGroupEventsAsync(groupId, type);
    }


    private void hideStreamAsPopup() {
        if (streamAsPopup.isShowing()) {
            streamAsPopup.dismiss();
        }
    }

    private void loadGroupsAsync() {
        networkClient.getGroupsAsObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(GetGroupListResponse::getGroups)
                .flatMapIterable(groups -> groups)
                .filter(Group::isOwner)
                .toList()
                .subscribe(this::handleGroupsResult, throwable -> groupList.clear());
    }

    private void loadBusinessesAsync() {
        networkClient.getMyBusinessAsObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(BusinessesResponse::getBusinesses)
                .flatMapIterable(businesses -> businesses)
                .filter(Business::getIsOwner)
                .toList()
                .subscribe(businesses -> {
                    businessList.clear();
                    businessList.addAll(businesses);
                    streamAsAdapter.notifyDataSetChanged();
                }, throwable -> businessList.clear());
    }

    private void loadGroupEventsAsync(int groupId, String type) {
        Calendar beforeDateLimit = Calendar.getInstance();
        Calendar afterDateLimit = Calendar.getInstance();
        afterDateLimit.add(Calendar.HOUR_OF_DAY, -24);
        beforeDateLimit.add(Calendar.HOUR_OF_DAY, 24);

        networkClient.getGroupEventsAsObservable(groupId, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map((Func1<GetEventListResponse, List<Event>>) GetEventListResponse::getEvents)
                .flatMapIterable(events -> events)
                .filter(event -> {
                    Date eventDate = new Date(event.getStartTimeMillis());
                    return eventDate.after(afterDateLimit.getTime()) && eventDate.before(beforeDateLimit.getTime()) ;
                })
                .toList()
                .subscribe(this::handleEventsResult, throwable -> eventList.clear());
    }

}
