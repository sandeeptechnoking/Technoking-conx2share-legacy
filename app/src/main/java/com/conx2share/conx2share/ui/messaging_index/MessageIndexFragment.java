package com.conx2share.conx2share.ui.messaging_index;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.MessageIndexChatsAdapter;
import com.conx2share.conx2share.model.Chat;
import com.conx2share.conx2share.model.ChatsHolder;
import com.conx2share.conx2share.model.Friend;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.message_friends.MessageFriendsActivity;
import com.conx2share.conx2share.ui.messaging.MessagingActivity;
import com.conx2share.conx2share.util.CXFirebaseMessagingService;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;
import butterknife.OnClick;
//import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;

public class MessageIndexFragment extends BaseFragment {

    public static final String TAG = MessageIndexFragment.class.getSimpleName();

    @BindView(R.id.messageIndex_chats_listView)
    ListView mChatsListView;

    @BindView(R.id.messageIndex_savedMessages_listView)
    ListView mSavedMessagesListView;

    @BindView(R.id.message_index_progress_bar)
    ProgressBar mProgressBar;

    @Inject
    NetworkClient mNetworkClient;

    @Inject
    PreferencesUtil mPreferencesUtil;

    private ArrayList<Chat> mChats = new ArrayList<>();

    private MessageIndexChatsAdapter chatsAdapter;

    private int page = 1;

    private Integer totalPages;

    private boolean updateInProgress;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_index, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable
    final Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        final Button chatsButton = (Button) getActivity().findViewById(R.id.messageIndex_chats_button);
        final Button savedMessagesButton = (Button) getActivity().findViewById(R.id.messageIndex_saved_messages_button);

        chatsButton.setOnClickListener(v -> {
            mChatsListView.setVisibility(View.VISIBLE);
            mSavedMessagesListView.setVisibility(View.GONE);
            chatsButton.setBackgroundResource(R.drawable.message_index_selected_selector);
            savedMessagesButton.setBackgroundResource(R.drawable.message_index_unselected_selector);
        });

        savedMessagesButton.setOnClickListener(v -> {
            mChatsListView.setVisibility(View.GONE);
            mSavedMessagesListView.setVisibility(View.VISIBLE);
            savedMessagesButton.setBackgroundResource(R.drawable.message_index_selected_selector);
            chatsButton.setBackgroundResource(R.drawable.message_index_unselected_pressed);
        });

        getChatsAsync(page);

        mChatsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= mChats.size() && mChats.size() > 0) {
                    if (totalPages != null && totalPages > page && !updateInProgress) {
                        updateInProgress = true;
                        getChatsAsync(page + 1);
                    }
                }
            }
        });
        chatsAdapter = new MessageIndexChatsAdapter(getActivity(), mChats, mPreferencesUtil);
        mChatsListView.setAdapter(chatsAdapter);
        mChatsListView.setOnItemClickListener(new OnIndexItemClick());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  ButterKnife.reset(this);
        ButterKnife.bind(this, view).unbind();
    }

    private void launchChatsFailureDialog() {
        if (getActivity() != null) {
            SnackbarManager.show(
                    Snackbar.with(getActivity().getApplicationContext())
                            .type(SnackbarType.MULTI_LINE)
                            .text(getString(R.string.unable_to_get_chats_text))
                            .actionLabel(getString(R.string.retry))
                            .actionListener(snackbar -> {
                                getChatsAsync(page);
                                SnackbarManager.dismiss();
                            })
                    , getActivity());
        }
    }

    private void getChatsAsync(Integer page) {
        mProgressBar.setVisibility(View.VISIBLE);

        addSubscription(mNetworkClient.getIndexOfChats(page)
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> {
                    mProgressBar.setVisibility(View.GONE);
                    updateInProgress = false;
                })
                .subscribe(chatsHolder -> handleChatsSuccessfulCase(chatsHolder),
                        throwable -> launchChatsFailureDialog()));
    }

    private void handleChatsSuccessfulCase(ChatsHolder chatsHolder) {
        if (chatsHolder != null && chatsHolder.getChats() != null && chatsHolder.getChats().size() > 0) {
            mChats.addAll(chatsHolder.getChats());
            chatsAdapter.notifyDataSetChanged();
            totalPages = chatsHolder.getMeta().totalPages;
            page = chatsHolder.getMeta().currentPage;
        } else {
            Log.d(TAG, "No chats for user");
        }
    }

    private class OnIndexItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            User user;
            if (mChats.get(position).getUsers().get(0).getId() != mPreferencesUtil.getAuthUser().getId()) {
                user = mChats.get(position).getUsers().get(0);
            } else {
                user = mChats.get(position).getUsers().get(1);
            }
            Friend friend = new Friend(user.getId(), 0, user.getFirstName(), user.getLastName(), user.getAvatarUrl(), new ArrayList<>(), user.getUsername());
            MessagingActivity.start(getActivity(), friend);
            cancelMessageNotification();
        }
    }

    @OnClick(R.id.message_index_new_message)
    public void onCreateChat() {
        startActivity(new Intent(getActivity(), MessageFriendsActivity.class));
    }

    private void cancelMessageNotification() {
        Activity activity = getActivity();
        if (activity != null) {
            NotificationManagerCompat nMgr = NotificationManagerCompat.from(getActivity());
            nMgr.cancel(CXFirebaseMessagingService.NOTIFICATION_MESSAGES_IDENTIFIER);
        }
    }
}
