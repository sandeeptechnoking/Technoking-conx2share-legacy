package com.conx2share.conx2share.ui.friends;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.FriendsIndexAdapter;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.GetFriendsResponse;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import javax.inject.Inject;

//import butterknife.InjectView;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class FriendsIndexFragment extends BaseFragment {

    public static final String PROFILEID_KEY = "profileId";

    public static final String TAG = FriendsIndexFragment.class.getSimpleName();

    @Inject
    NetworkClient networkClient;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @InjectView(R.id.friends_index_list_view)
    ListView mFriendsIndexListView;

    @InjectView(R.id.friends_index__progress_bar)
    ProgressBar mFriendsIndexProgressBar;

    private FriendsIndexAdapter mFriendsIndexAdapter;

    private ArrayList<User> mFriends;

    public static FriendsIndexFragment newInstance() {
        return new FriendsIndexFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_index, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFriendsIndexListView.setOnItemClickListener((parent, view1, position, id) -> {
            Adapter adapter = mFriendsIndexListView.getAdapter();
            User user = (User) adapter.getItem(position);

            Intent profileActivityIntent = new Intent(getActivity(), ProfileActivity.class);
            profileActivityIntent.putExtra(PROFILEID_KEY, String.valueOf(user.getId()));
            startActivity(profileActivityIntent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getFriendsAsync();
    }

    public void setupFriendsIndexAdapter() {
        if (mFriends != null && mFriends.size() > 0) {
            mFriends = sortFriends(mFriends);
            mFriendsIndexAdapter = new FriendsIndexAdapter(mFriends, getActivity(), this);
            mFriendsIndexListView.setAdapter(mFriendsIndexAdapter);
        }
    }

    private ArrayList<User> sortFriends(ArrayList<User> friends) {
        ArrayList<User> favoriteFriends = new ArrayList<>();
        ArrayList<User> notfavoriteFriends = new ArrayList<>();
        for (User user : friends) {
            if (user.getIsFavorite()) {
                favoriteFriends.add(user);
            } else {
                notfavoriteFriends.add(user);
            }
        }
        ArrayList<User> returnedFriends = new ArrayList<>();
        returnedFriends.addAll(favoriteFriends);
        returnedFriends.addAll(notfavoriteFriends);
        return returnedFriends;
    }

    public void getFriendsAsync() {
        if (getActivity() != null) {
            mFriendsIndexListView.setVisibility(View.GONE);
            mFriendsIndexProgressBar.setVisibility(View.VISIBLE);
        }
        addSubscription(networkClient.getFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    if (getActivity() != null) {
                        mFriendsIndexListView.setVisibility(View.VISIBLE);
                        mFriendsIndexProgressBar.setVisibility(View.GONE);
                    }
                })
                .subscribe(getFriendsResponse -> {
                            if (getActivity() != null) {
                                mFriends = getFriendsResponse.getUsers();
                                setupFriendsIndexAdapter();
                            }
                        }
                        , throwable -> mSnackbarUtil.showSnackBarWithAction(getActivity(),
                                R.string.unable_to_get_friends_text, R.string.retry,
                                snackbar -> {
                                    getFriendsAsync();
                                    SnackbarManager.dismiss();
                                })));

    }
}
