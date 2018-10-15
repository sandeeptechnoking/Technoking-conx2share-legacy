package com.conx2share.conx2share.ui.message_friends;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.MessageFriendsAdapter;
import com.conx2share.conx2share.model.Friend;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.GetFriendsResponse;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.network.models.response.MessagesResponse;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.messaging.MessagingActivity;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import java.util.ArrayList;

import javax.inject.Inject;

//import butterknife.InjectView;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MessageFriendsFragment extends BaseFragment {

    public static final String TAG = MessageFriendsFragment.class.getSimpleName();

    public static final String FRIEND_KEY = "Friend";

    @Inject
    NetworkClient networkClient;

    @butterknife.BindView(R.id.search_for_friends_to_message_edit_text)
    EditText searchForFriendsToMessageEditText;

    @InjectView(R.id.message_friends_view)
    ListView mMessageFriendsView;

    @InjectView(R.id.message_friends_progress_bar)
    ProgressBar mMessageFriendsProgressBar;

    private String mFriendId;

    private ArrayList<Message> mMessages;

    private ArrayList<User> mUsers;

    private ArrayList<Friend> mFriends = new ArrayList<>();

    private MessageFriendsAdapter mMessageFriendsAdapter;

    private TextWatcher watch;

    public static MessageFriendsFragment newInstance() {
        return new MessageFriendsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_friends, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        gatherFriendsInformation();
    }

    public void createFriendData() {
        ArrayList<Message> friendMessages = new ArrayList<>();
        if (mFriends != null) {
            mFriends.clear();
        }
        for (User user : mUsers) {

            Log.d(TAG, "Adding friend: " + user.getFirstName() + " " + user.getLastName() + " has id: " + user.getId());
            for (int i = 0; i < mMessages.size(); i++) {
                if (mMessages.get(i).getUserId() == user.getId()) {
                    friendMessages.add(mMessages.get(i));
                }
            }

            mFriends.add(new Friend(user.getId(), friendMessages.size(), user.getFirstName(), user.getLastName(), user.getAvatar().getAvatar().getUrl(), friendMessages, user.getUsername()));

            friendMessages.clear();
        }

        Log.d(TAG, "Total # of friends: " + (mFriends != null ? mFriends.size() : 0));

        setupMessageFriendsAdapter();
    }

    public void filterFriendsList() {
        if (!TextUtils.isEmpty(searchForFriendsToMessageEditText.getText())) {
            ArrayList<Friend> filteredFriendsList = new ArrayList<>();

            for (Friend friend : mFriends) {
                String stringToSearch = (friend.getFriendFirstName() + friend.getFriendLastName()).toLowerCase().replaceAll("\\s+", "");
                String searchCriteria = searchForFriendsToMessageEditText.getText().toString().toLowerCase().replaceAll("\\s+", "");
                if (stringToSearch.contains(searchCriteria)) {
                    filteredFriendsList.add(friend);
                    Log.d(TAG, "Adding user from search: " + stringToSearch);
                }
            }
            setupMessageFriendsAdapter(filteredFriendsList);
        } else {
            setupMessageFriendsAdapter();
        }
    }

    public void gatherFriendsInformation() {
        getFriendsAsync();
    }

    public Friend getChatFriend(int position) {
        Friend friend = (Friend) mMessageFriendsView.getItemAtPosition(position);

        String fullName = friend.getFriendFirstName() + " " + friend.getFriendLastName();
        mFriendId = String.valueOf(friend.getFriendId());
        Log.i(TAG, "Friend " + fullName + " has id " + mFriendId);

        return friend;
    }

    public void setTextWatcher() {
        watch = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterFriendsList();
            }
        };

        searchForFriendsToMessageEditText.addTextChangedListener(watch);
    }

    public void setOnClickListener() {
        mMessageFriendsView.setOnItemClickListener((parent, view, position, id) -> {
            Friend friend = getChatFriend(position);

            if (friend != null && getActivity() != null) {
                Intent messagingActivity = new Intent(getActivity(), MessagingActivity.class);
                messagingActivity.putExtra(MessagingActivity.EXTRA_FRIEND, friend);
                startActivity(messagingActivity);
            }
        });
    }

    public void setupMessageFriendsAdapter() {
        if (mFriends != null) {
            mMessageFriendsAdapter = new MessageFriendsAdapter(mFriends, getActivity());
            mMessageFriendsView.setAdapter(mMessageFriendsAdapter);
        } else {
            Log.w(TAG, "No friends");
        }
    }

    public void setupMessageFriendsAdapter(ArrayList<Friend> filteredFriendsList) {
        if (filteredFriendsList != null) {
            mMessageFriendsAdapter = new MessageFriendsAdapter(filteredFriendsList, getActivity());
            mMessageFriendsView.setAdapter(mMessageFriendsAdapter);
        } else {
            Log.w(TAG, "No friends");
        }
    }

    public void getFriendsAsync() {
        mMessageFriendsView.setVisibility(View.GONE);
        mMessageFriendsProgressBar.setVisibility(View.VISIBLE);
        addSubscription(networkClient.getFriends(null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getFriendsResponse -> {
                            mUsers = getFriendsResponse.getUsers();
                            getUnreadMessages();
                        },
                        throwable -> {
                            mMessageFriendsView.setVisibility(View.VISIBLE);
                            mMessageFriendsProgressBar.setVisibility(View.GONE);
                            SnackbarManager.show(
                                    Snackbar.with(getActivity().getApplicationContext())
                                            .type(SnackbarType.MULTI_LINE)
                                            .text(getString(R.string.unable_to_get_friends_text))
                                            .actionLabel(getString(R.string.retry))
                                            .actionListener(snackbar -> {
                                                getFriendsAsync();
                                                SnackbarManager.dismiss();
                                            })
                                    , getActivity());
                        }));

    }

    private void getUnreadMessages() {
        addSubscription(networkClient
                .getUnreadMessages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messagesResponse -> {
                    if (getActivity() != null) {
                        mMessageFriendsView.setVisibility(View.VISIBLE);
                        mMessageFriendsProgressBar.setVisibility(View.GONE);
                        if (messagesResponse != null) {
                            mMessages = messagesResponse.getMessages();
                            createFriendData();
                            setTextWatcher();
                            setOnClickListener();
                        } else {
                            SnackbarManager.show(
                                    Snackbar.with(getActivity().getApplicationContext())
                                            .type(SnackbarType.MULTI_LINE)
                                            .text(getString(R.string.unable_to_get_unread_messages_text))
                                            .actionLabel(getString(R.string.retry))
                                            .actionListener(snackbar -> {
                                                getUnreadMessages();
                                                SnackbarManager.dismiss();
                                            })
                                    , getActivity());
                        }
                    }
                }, throwable -> Log.e(TAG, "Could not get unread messages", throwable)));
    }
}