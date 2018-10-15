package com.conx2share.conx2share.ui.feed;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.AddFriendsAdapter;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.base.BaseAppCompatActivity;
import com.conx2share.conx2share.ui.message_friends.MessageFriendsFragment;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.conx2share.conx2share.util.ViewUtil;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AddUsersActivity extends BaseAppCompatActivity {

    public static final String START_FOR_PARAMETER = "AddFriendsActivity.startFor";
    public static final int START_FOR_FAVORITE = 1;

    @Inject
    NetworkClient networkClient;

    @Inject
    SnackbarUtil snackBarUtil;

    @BindView(R.id.add_friends_toolbar)
    Toolbar toolbar;
    @BindView(R.id.add_friends_search_for_friends)
    EditText searchForFriends;
    @BindView(R.id.add_friends_list)
    RecyclerView friendsList;
    @BindView(R.id.add_friends_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.add_friends_clear_search)
    ImageView clearSearch;


    private List<User> mUsers = new ArrayList<>();

    private List<User> searchedUsers = new ArrayList<>();

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private int activityMode;

    public static MessageFriendsFragment newInstance() {
        return new MessageFriendsFragment();
    }

    public static void startForAddFavorite(Context context) {
        context.startActivity(new Intent(context, AddUsersActivity.class)
                .putExtra(START_FOR_PARAMETER, START_FOR_FAVORITE));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favorite);
        ButterKnife.bind(this);

        activityMode = getIntent().getIntExtra(START_FOR_PARAMETER, 0);
        if (activityMode == 0) {
            finish();
            throw new IllegalStateException("Start AddFriendsActivity without key mode");
        }
        clearSearch.setTag(true);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        friendsList.setAdapter(new AddFriendsAdapter(searchedUsers, (userId, isCheckedNow) -> {
            changeUserFavoriteMode(userId, isCheckedNow);
        }));
    }

    private void changeUserFavoriteMode(int userId, boolean isCheckedNow) {
        Observable<Void> observable;
        if (isCheckedNow) {
            observable = networkClient.favoriteUser(String.valueOf(userId));
        } else {
            observable = networkClient.unfavoriteUser(String.valueOf(userId));
        }
        compositeSubscription.add(observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> snackBarUtil.displaySnackBar(AddUsersActivity.this, isCheckedNow ?
                                R.string.user_add_to_favorites : R.string.user_remove_from_favorites),
                        throwable -> snackBarUtil.displaySnackBar(AddUsersActivity.this, isCheckedNow ?
                                R.string.user_favoriting_failed : R.string.user_unfavoriting_failed)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFriendsAsync();
    }

    public void filterFriendsList(String s) {
        searchedUsers.clear();
        if (TextUtils.isEmpty(s)) {
            searchedUsers.addAll(mUsers);
        } else {
            for (User user : mUsers) {
                if (user.isUserMatchQuery(s)) {
                    searchedUsers.add(user);
                }
            }
        }
        friendsList.getAdapter().notifyDataSetChanged();
    }

    @OnClick(R.id.add_friends_clear_search)
    void onClearSearchClick() {
        if (!(Boolean) clearSearch.getTag()) {
            searchForFriends.setText("");
            clearSearch.setImageResource(R.drawable.v_ic_search_white);
            ViewUtil.hideKeyboard(this);
            clearSearch.setTag(true);
            searchForFriends.setHint(R.string.edit_favorites);
        }else{
            clearSearch.setImageResource(R.drawable.v_ic_delete_white);
            searchForFriends.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchForFriends, InputMethodManager.SHOW_IMPLICIT);
            clearSearch.setTag(false);
            searchForFriends.setHint("");
        }
    }

    @OnTextChanged(R.id.add_friends_search_for_friends)
    void onSearchTextChange(CharSequence text) {
        filterFriendsList(text.toString());
        if (text.length() < 1){
            clearSearch.setImageResource(R.drawable.v_ic_search_white);
            clearSearch.setTag(true);
        }else{
            clearSearch.setImageResource(R.drawable.v_ic_delete_white);
            clearSearch.setTag(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeSubscription.unsubscribe();
        ViewUtil.hideKeyboard(this);
    }

    public void getFriendsAsync() {
        friendsList.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        compositeSubscription.add(networkClient.getFriends(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    friendsList.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                })
                .subscribe(getFriendsResponse -> {
                            mUsers.clear();
                            mUsers.addAll(getFriendsResponse.getUsers());
                            searchedUsers.clear();
                            searchedUsers.addAll(getFriendsResponse.getUsers());
                            friendsList.getAdapter().notifyDataSetChanged();
                        },
                        throwable -> snackBarUtil.showSnackBarWithAction(this,
                                R.string.unable_to_get_friends_text,
                                R.string.retry,
                                snackbar -> {
                                    getFriendsAsync();
                                    SnackbarManager.dismiss();
                                })));
    }

}
