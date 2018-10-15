package com.conx2share.conx2share.ui.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.BuildConfig;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.FeedDrawerAdapter;
import com.conx2share.conx2share.async.RegisterDeviceAsync;
import com.conx2share.conx2share.model.AuthUser;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.model.RegisteredDeviceResponse;
import com.conx2share.conx2share.model.event.UpdateProfileImageEvent;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.ui.business.MyBusinessesActivity;
import com.conx2share.conx2share.ui.contact.ContactActivity;
import com.conx2share.conx2share.ui.dialog.ShareDialogFragment;
import com.conx2share.conx2share.ui.discover.DiscoverActivity;
import com.conx2share.conx2share.ui.feed.FeedActivity;
import com.conx2share.conx2share.ui.friends.FriendsIndexActivity;
import com.conx2share.conx2share.ui.groups.GroupsIndexActivity;
import com.conx2share.conx2share.ui.messaging_index.MessageIndexActivity;
import com.conx2share.conx2share.ui.news.NewsIndexActivity;
import com.conx2share.conx2share.ui.notifications.NotificationsActivity;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.ui.profile.ProfileFragment;
import com.conx2share.conx2share.ui.profile_settings.ProfileSettingsFragment;
import com.conx2share.conx2share.ui.settings.SettingsActivity;
import com.conx2share.conx2share.ui.subscription.SubscriptionActivity;
import com.conx2share.conx2share.util.CXFirebaseInstanceIDService;
import com.conx2share.conx2share.util.EventBusUtil;
import com.conx2share.conx2share.util.ForegroundUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.inject.Inject;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;
import retrofit.RetrofitError;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public abstract class BaseDrawerActivity extends BaseAppCompatActivity {

    public static final String TAG = BaseDrawerActivity.class.getSimpleName();

    @Inject
    PreferencesUtil mPreferencesUtil;

    @BindView(R.id.base_drawer_toolbar_conx_icon)
    ImageView mToolbarConxIcon;

    @BindView(R.id.base_drawer_toolbar_title)
    TextView mToolbarTitle;

    @BindView(R.id.base_drawer_toolbar_icon_and_title)
    LinearLayout mToolbarIconAndTitle;

    @BindView(R.id.base_drawer_toolbar_full_logo)
    ImageView mToolbarFullLogo;

    @BindView(R.id.base_drawer_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_feed_drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.activity_feed_drawer)
    ListView mDrawerList;

    @BindView(R.id.item_feed_header_picture)
    RoundedImageView mUserAvatarImageView;

    @BindView(R.id.item_feed_header_settings_button)
    ImageView mSettingsButton;

    @BindView(R.id.item_feed_header_firstname)
    TextView mFirstnameText;

    @BindView(R.id.item_feed_header_lastname)
    TextView mLastnameText;

    @BindView(R.id.base_drawer_toolbar_placeholder)
    FrameLayout mBaseDrawerToolbarPlaceholder;

    @BindView(R.id.base_drawer_toolbar_expanded_placeholder)
    FrameLayout mBaseDrawerToolbarExpandedPlaceholder;

    @BindView(R.id.subscription_restore_holder)
    RelativeLayout mSubscriptionRestoreHolder;

    @BindView(R.id.subscription_restore)
    TextView mSubscriptionRestore;

    @BindView(R.id.item_feed_header_handle)
    TextView mHandleTextView;

    @Inject
    NetworkClient networkClient;

    @Inject
    PreferencesUtil preferencesUtil;

    private Fragment mFragment;
    private ActionBarDrawerToggle mDrawerToggle;

    private String mAvatarUrl;

    private boolean notificationsBadgeLoading, groupsBadgeLoading, businessesBadgeLoading;
    private int notificationBadgeCount, groupsBadgeCount, businessesBadgeCount, messagesBadgeCount;

    private ArrayList<Message> mMessages = new ArrayList<>();
    private FeedDrawerAdapter mDrawerAdapter;
    private List<BaseDrawerItem> mBaseDrawerItems = new ArrayList<>();

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_drawer_activity);
        EventBusUtil.getEventBus().register(this);

        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

        if (savedInstanceState == null) {

            mFragment = initializeFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_content, mFragment);
            ft.commit();
        }

        ButterKnife.bind(this);

        getNotificationBadgeCount();
        getGroupsBadgeCount();
        getBusinessesBadgeCount();

        setupToolbar();
        setupViews();

        getPushNotificationToken();
        checkForUpdate();
    }

    private void getPushNotificationToken() {
        FirebaseApp.initializeApp(this);
        if (!preferencesUtil.getTokenSent()) {
            Intent intent = new Intent(this, CXFirebaseInstanceIDService.class);
            startService(intent);
        }
        try {
            if (!preferencesUtil.getTokenSent()) {
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                sendRegistrationToServer(refreshedToken);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void getNotificationBadgeCount() {
        if (notificationsBadgeLoading) return;

        notificationsBadgeLoading = true;

        compositeSubscription.add(networkClient.getNotificationBadgeCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(badgeCountResponse -> {
                    if (badgeCountResponse != null) {
                        notificationBadgeCount = badgeCountResponse.getUnread_non_message_badge_count();
                        messagesBadgeCount = badgeCountResponse.getUnread_message_badge_count();
                        updateNotificationBadge();
                    }
                    notificationsBadgeLoading = false;
                }, throwable -> {
                    Log.e(TAG, "Error getting notifications badge count", throwable);
                    notificationsBadgeLoading = false;
                }));
    }

    protected void getGroupsBadgeCount() {
        if (groupsBadgeLoading) return;

        groupsBadgeLoading = true;

        compositeSubscription.add(networkClient.getGroupsBadgeCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(badgeCountResponse -> {
                    if (badgeCountResponse != null) {
                        groupsBadgeCount = badgeCountResponse.getBadgeCount();
                        updateGroupsBadge();
                    }
                    groupsBadgeLoading = false;
                }, throwable -> {
                    Log.e(TAG, "Error getting groups badge count", throwable);
                    groupsBadgeLoading = false;
                }));
    }

    protected void getBusinessesBadgeCount() {
        if (businessesBadgeLoading) return;

        businessesBadgeLoading = true;

        compositeSubscription.add(networkClient.getBusinessesBadgeCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(badgeCountResponse -> {
                    if (badgeCountResponse != null) {
                        businessesBadgeCount = badgeCountResponse.getBadgeCount();
                        updateBusinessesBadge();
                    }
                    businessesBadgeLoading = false;
                }, throwable -> {
                    Log.e(TAG, "Error getting businesses badge count", throwable);
                    businessesBadgeLoading = false;
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.getEventBus().unregister(this);
        compositeSubscription.clear();
    }

    public abstract Fragment initializeFragment();

    public class BaseDrawerItem {
        private String title;
        private int resourceId, count;

        public BaseDrawerItem(int resourceId, int count) {
            this.title = getResources().getString(resourceId);
            this.resourceId = resourceId;
            this.count = count;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getResourceId() {
            return resourceId;
        }

        public void setResourceId(int resourceId) {
            this.resourceId = resourceId;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    private void setupToolbar() {

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mBaseDrawerItems = new ArrayList<>();
        mBaseDrawerItems.add(new BaseDrawerItem(R.string.drawer_home, 0));
        mBaseDrawerItems.add(new BaseDrawerItem(R.string.drawer_discover, 0));
        mBaseDrawerItems.add(new BaseDrawerItem(R.string.drawer_notifications, notificationBadgeCount));
        mBaseDrawerItems.add(new BaseDrawerItem(R.string.drawer_messages, messagesBadgeCount));
        //mBaseDrawerItems.add(new BaseDrawerItem(R.string.drawer_friends, 0));
        mBaseDrawerItems.add(new BaseDrawerItem(R.string.drawer_groups, groupsBadgeCount));
        mBaseDrawerItems.add(new BaseDrawerItem(R.string.news, 0));
        mBaseDrawerItems.add(new BaseDrawerItem(R.string.drawer_invite, 0));
        //mBaseDrawerItems.add(new BaseDrawerItem(R.string.drawer_plus, 0));
        // baseDrawerItems.add(new BaseDrawerItem(R.string.drawer_eshopping, 0));
        // baseDrawerItems.add(new BaseDrawerItem(R.string.drawer_games, 0));
        mBaseDrawerItems.add(new BaseDrawerItem(R.string.drawer_businesses, businessesBadgeCount));

        mDrawerAdapter = new FeedDrawerAdapter(this, R.layout.item_feed_drawer, mBaseDrawerItems);
        mDrawerList.setAdapter(mDrawerAdapter);

        mDrawerList.setOnItemClickListener((parent, view, position, id) -> {
            int itemId = mBaseDrawerItems.get(position).getResourceId();
            switch (itemId) {
                case R.string.drawer_home:
                    Intent feedIntent = new Intent(BaseDrawerActivity.this, FeedActivity.class);
                    startActivity(feedIntent);
                    finish();
                    break;
                case R.string.drawer_discover:
                    Intent discover = new Intent(BaseDrawerActivity.this, DiscoverActivity.class);
                    startActivity(discover);
                    finish();
                    break;
                case R.string.drawer_notifications:
                    Intent notificationsIntent = new Intent(BaseDrawerActivity.this, NotificationsActivity.class);
                    startActivity(notificationsIntent);
                    finish();
                    break;
                case R.string.drawer_messages:
                    Intent messagesIntent = new Intent(BaseDrawerActivity.this, MessageIndexActivity.class);
                    startActivity(messagesIntent);
                    finish();
                    break;
                case R.string.drawer_friends:
                    Intent friendsIndexIntent = new Intent(BaseDrawerActivity.this, FriendsIndexActivity.class);
                    startActivity(friendsIndexIntent);
                    finish();
                    break;
                case R.string.drawer_groups:
                    Intent groupsIntent = new Intent(BaseDrawerActivity.this, GroupsIndexActivity.class);
                    startActivity(groupsIntent);
                    finish();
                    break;
                case R.string.news:
                    Intent newsIntent = new Intent(BaseDrawerActivity.this, NewsIndexActivity.class);
                    startActivity(newsIntent);
                    finish();
                    break;
                case R.string.drawer_invite:
                    Intent inviteIntent = new Intent(BaseDrawerActivity.this, ContactActivity.class);
                    startActivity(inviteIntent);
                    finish();
                    break;
                case R.string.drawer_plus:
                    Intent subscriptionsIntent = new Intent(BaseDrawerActivity.this, SubscriptionActivity.class);
                    startActivity(subscriptionsIntent);
                    finish();
                    break;
                case R.string.drawer_eshopping:
                    String url;
                    if (BuildConfig.FLAVOR.equals("production")) {
                        url = "https://conx2share.com/shop";
                    } else {
                        url = "http://conx2share.staging.metova.com/shop";
                    }
                    Intent webIntent = new Intent(Intent.ACTION_VIEW);
                    webIntent.setData(Uri.parse(url));
                    startActivity(webIntent);
                    if (mDrawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) {
                        mDrawerLayout.closeDrawers();
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                        return;
                    }
                    break;
                case R.string.drawer_games:
                    Toast.makeText(getApplicationContext(), getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    break;
                case R.string.drawer_businesses:
                    Intent businessesIntent = new Intent(BaseDrawerActivity.this, MyBusinessesActivity.class);
                    startActivity(businessesIntent);
                    finish();
                    break;
                default:
                    Object item = parent.getAdapter().getItem(position);
                    Log.w(TAG, "Unhandled click on draw list position: " + position + ", item: " + item);
                    break;
            }

        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string
                .feed_activity_drawer_toggle_open, R.string.feed_activity_drawer_toggle_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getNotificationBadgeCount();
                getGroupsBadgeCount();
                getBusinessesBadgeCount();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mToolbar.getWindowToken(), 0);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void setupViews() {
        final AuthUser authUser = mPreferencesUtil.getAuthUser();
        mAvatarUrl = (authUser == null ? null : (authUser.getAvatar() == null ? null : (authUser.getAvatar()
                .getAvatar() == null ? null : (authUser.getAvatar().getAvatar().getUrl()))));
        Log.d(TAG, "Loading user avatar: " + (mAvatarUrl != null ? mAvatarUrl : "null"));

        if (mAvatarUrl != null && !mAvatarUrl.isEmpty()) {
            Glide.with(this).load(mAvatarUrl).centerCrop().placeholder(R.drawable.friend_placeholder).error(R
                    .drawable.friend_placeholder).dontAnimate().into(mUserAvatarImageView);
        }

        String firstName = "";
        String lastName = "";
        String handle = "";

        if (authUser != null) {
            if (authUser.getFirstName() != null) {
                firstName = authUser.getFirstName();
            }
            if (authUser.getLastName() != null) {
                lastName = authUser.getLastName();
            }
            if (authUser.getUsername() != null) {
                handle = "@" + authUser.getUsername();
            }
        }
        mFirstnameText.setText(firstName);
        mLastnameText.setText(lastName);
        mHandleTextView.setText(handle);

        mUserAvatarImageView.setOnClickListener(v -> {

            String profileId = (authUser != null ? authUser.getId() + "" : null);

            if (profileId != null) {
                mDrawerLayout.closeDrawers();
                Intent intent = new Intent(BaseDrawerActivity.this, ProfileActivity.class);
                intent.putExtra(ProfileFragment.PROFILEID_KEY, profileId);
                startActivity(intent);
            }
        });

        mSettingsButton.setOnClickListener(v -> {
            if (authUser != null && authUser.getId() != null) {
                mDrawerLayout.closeDrawers();
                Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                intent.putExtra(ProfileSettingsFragment.EXTRA_PROFILE_ID, authUser.getId());
                startActivity(intent);
            } else {
                if (authUser == null) {
                    Log.wtf(TAG, "authUser is null");
                } else {
                    Log.wtf(TAG, "authUser has null id");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareDialogFragment dialogFragment = (ShareDialogFragment) getSupportFragmentManager().findFragmentByTag
                (ShareDialogFragment.TAG);
        if (dialogFragment != null) {
            dialogFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public Fragment getFragment() {

        return mFragment;
    }

    public void onEventMainThread(UpdateProfileImageEvent event) {
        mAvatarUrl = event.getImageUrl();
        Glide.with(this).load(event.getImageUrl()).centerCrop().placeholder(R.drawable.friend_placeholder).error(R
                .drawable.friend_placeholder).dontAnimate().into(mUserAvatarImageView);
    }

    public void displayRestore() {
        mSubscriptionRestoreHolder.setVisibility(View.VISIBLE);
        mSubscriptionRestore.setVisibility(View.VISIBLE);
    }

    public void setTitle(int stringId, int textSize) {
        mBaseDrawerToolbarPlaceholder.setVisibility(View.GONE);
        mToolbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        mToolbarTitle.setText(getString(stringId));
    }

    public void setTitle(int stringId) {
        mToolbarTitle.setText(getString(stringId));
    }

    public void setTitleWithExpandedPlaceholder(int stringId) {
        mToolbarTitle.setText(getString(stringId));
        mBaseDrawerToolbarPlaceholder.setVisibility(View.GONE);
        mBaseDrawerToolbarExpandedPlaceholder.setVisibility(View.VISIBLE);
    }

    public void showFullLogoInsteadOfTitleAndIcon() {
        mToolbarIconAndTitle.setVisibility(View.GONE);
        mToolbarTitle.setVisibility(View.GONE);
        mToolbarConxIcon.setVisibility(View.GONE);
        mToolbarFullLogo.setVisibility(View.VISIBLE);
    }

    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ForegroundUtil.setAppInForeground(true);
        setupViews();
    }

    private void updateNotificationBadge() {
        for (int i = 0; i < mBaseDrawerItems.size(); i++) {
            BaseDrawerItem drawerItem = mBaseDrawerItems.get(i);
            if (drawerItem.title.equals(getString(R.string.drawer_notifications))) {
                drawerItem.setCount(notificationBadgeCount);
            } else if (drawerItem.title.equals(getString(R.string.drawer_messages))) {
                drawerItem.setCount(messagesBadgeCount);
            }
        }
        mDrawerAdapter.notifyDataSetChanged();
    }

    private void updateGroupsBadge() {
        for (int i = 0; i < mBaseDrawerItems.size(); i++) {
            BaseDrawerItem drawerItem = mBaseDrawerItems.get(i);
            if (drawerItem.title.equals(getString(R.string.drawer_groups))) {
                drawerItem.setCount(groupsBadgeCount);
                mDrawerAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private void updateBusinessesBadge() {
        for (int i = 0; i < mBaseDrawerItems.size(); i++) {
            BaseDrawerItem drawerItem = mBaseDrawerItems.get(i);
            if (drawerItem.title.equals(getString(R.string.drawer_businesses))) {
                drawerItem.setCount(businessesBadgeCount);
                mDrawerAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ForegroundUtil.setAppInForeground(false);
    }

    private void sendRegistrationToServer(String token) {
        new RegisterDeviceAsync(this) {
            @Override
            protected void onSuccess(Result<RegisteredDeviceResponse> result) {
                RegisteredDeviceResponse response = result.getResource();
                if (response != null) {
                    preferencesUtil.setRegisteredDeviceId(response.device.deviceId);
                }
                preferencesUtil.setTokenSent(true);
            }

            @Override
            protected void onFailure(RetrofitError error) {
                preferencesUtil.setTokenSent(false);
            }
        }.executeInParallel(token);
    }

    private void checkForUpdate() {
        compositeSubscription.add(networkClient.getUpdateInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updateResponse -> {
                    if (updateResponse != null && updateResponse.getUpdate_required()) {
                        new AlertDialog.Builder(BaseDrawerActivity.this)
                                .setCancelable(false)
                                .setTitle(updateResponse.getTitle())
                                .setMessage(updateResponse.getMessage())
                                .setPositiveButton("Update", (dialogInterface, i) -> {
                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                })
                                .show();
                    }
                }, throwable -> {
                    //nothing to show
                })
        );
    }
}