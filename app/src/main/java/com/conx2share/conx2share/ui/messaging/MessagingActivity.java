package com.conx2share.conx2share.ui.messaging;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.chat.ChatStateHolder;
import com.conx2share.conx2share.chat.HttpBasedChatEngineImpl;
import com.conx2share.conx2share.model.Friend;
import com.conx2share.conx2share.model.IncomingAudioMessage;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.model.TimeDividerMessage;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.ui.base.AudioCell;
import com.conx2share.conx2share.ui.base.BaseAppCompatActivity;
import com.conx2share.conx2share.ui.base.ChatController;
import com.conx2share.conx2share.ui.base.MessageCell;
import com.conx2share.conx2share.ui.base.MessageTimeCell;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.conx2share.conx2share.ui.view.MarginItemDecorator;
import com.conx2share.conx2share.util.AudioHelper;
import com.conx2share.conx2share.util.CountingTypedFile;
import com.conx2share.conx2share.util.MediaHelper;
import com.conx2share.conx2share.util.PermissionUtil;
import com.conx2share.conx2share.util.SimpleAudioHelper;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.techery.celladapter.CellAdapter;
import roboguice.inject.InjectView;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;


public class MessagingActivity extends BaseAppCompatActivity {
    private static final int SEC_IN_MIN = 60;
    private static String TAG = MessagingActivity.class.getSimpleName();

    private static final int PERMISSION_PHOTO_CAMERA_RESULT = 100;
    private static final int PERMISSION_VIDEO_CAMERA_RESULT = 101;
    private static final int PERMISSION_AUDIO_RECORD_RESULT = 103;

    public static final String EXTRA_FRIEND = "extra-friend";

    @InjectView(R.id.messaging_toolbar)
    Toolbar toolbar;

    @InjectView(R.id.messaging_toolbar_title)
    TextView toolbarTitle;

    @InjectView(R.id.messaging_friend_icon)
    AvatarImageView messagingFriendIcon;

    @InjectView(R.id.chat_recycler_view)
    RecyclerView chatRecyclerView;

    @InjectView(R.id.chat_input_edit_text)
    EditText messageEt;

    @InjectView(R.id.chat_send_button)
    ImageButton chatSendButton;

    @InjectView(R.id.text_message_layout)
    LinearLayout textMessageLayout;

    @InjectView(R.id.audio_length)
    TextView audioLength;

    @InjectView(R.id.audio_send_bt)
    ImageView audioSendBt;

    @InjectView(R.id.audio_cancel_bt)
    ImageView audioCancelBt;

    @InjectView(R.id.recording_red_dot)
    ImageView redDot;

    @InjectView(R.id.audio_message_layout)
    RelativeLayout audioMessageLayout;

    @Inject
    NetworkClient networkClient;

    @Inject
    SnackbarUtil snackBarUtil;

    private ChatController chatController;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private AudioHelper audioRecorder;
    private Friend friend;
    private boolean loading;

    public static void start(Context context,
                             @NonNull Friend friend) {
        context.startActivity(new Intent(context, MessagingActivity.class)
                .putExtra(EXTRA_FRIEND, friend));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_right);

        toolbar.setNavigationOnClickListener(v -> {
            if (!isTaskRoot()) {
                finish();
            } else {
                startActivity(NavUtils.getParentActivityIntent(MessagingActivity.this));
                finish();
            }
        });

        friend = getIntent().getParcelableExtra(EXTRA_FRIEND);

        toolbarTitle.setText(new StringBuilder()
                .append(friend.getFriendFirstName().trim())
                .append(" ")
                .append(friend.getFriendLastName().trim()));
        messagingFriendIcon.initView(friend);

        CellAdapter chatAdapter = new CellAdapter();
        chatAdapter.registerCell(Message.class, MessageCell.class, new MessageCell.MessageCellListener() {
            @Override
            public void onImageOpen(String imageUrl) {
                MediaViewerActivity.startInImageViewMode(MessagingActivity.this, imageUrl);
            }

            @Override
            public void onVideoOpen(String videoUrl) {
                MediaViewerActivity.startInVideoViewMode(MessagingActivity.this, videoUrl);
            }
        });
        chatAdapter.registerCell(TimeDividerMessage.class, MessageTimeCell.class,null);

        chatAdapter.registerCell(IncomingAudioMessage.class, AudioCell.class, null);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.addItemDecoration(new MarginItemDecorator(this, R.dimen.chat_item_margin));
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    getPreviousMessages(recyclerView, chatAdapter);
                }
            }
        });
      
        chatController = new ChatController(this,
                new HttpBasedChatEngineImpl(networkClient),
                chatAdapter);
        audioRecorder = new SimpleAudioHelper(this, time -> {
            audioLength.setText(time);
            redDot.setVisibility(redDot.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        });
        connectToChat();
        ChatStateHolder.getInstance().setChatActive(true, friend.getFriendId());
    }

    private void getPreviousMessages(RecyclerView recyclerView, CellAdapter chatAdapter) {
        int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();

        if (firstVisibleItemPosition == 0) {
            if (!loading && chatAdapter.getItem(0) instanceof Message) {
                loading = true;
                compositeSubscription.add(chatController.getChatHistory(((Message) chatAdapter.getItem(0)).getId())
                        .subscribe(this::handlePagination,
                                throwable -> showNoPreviousMsg()));
            }
        }
    }

    private void showNoPreviousMsg() {
        snackBarUtil.displaySnackBar(this, R.string.no_previos_msg);
        loading = false;
    }

    private void handlePagination(List<Message> messages) {
        chatController.handlePagination(chatRecyclerView, messages);
        loading = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_FRIEND, friend);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        friend = savedInstanceState.getParcelable(EXTRA_FRIEND);
    }

    @OnClick(R.id.messaging_friend_icon)
    void onFriendIconClicked() {
        ProfileActivity.start(this, friend.getFriendId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        ChatStateHolder.getInstance().setChatActive(false, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
        if (audioRecorder != null) audioRecorder.cancelRecording();
    }

    @OnTextChanged(R.id.chat_input_edit_text)
    void onMessageTextChange(CharSequence text) {
        if (getText().isEmpty()) {
            chatSendButton.setImageResource(R.drawable.ic_mic);
        } else {
            chatSendButton.setImageResource(R.drawable.ic_send);
        }
    }

    @OnClick(R.id.chat_photo_btn)
    void onAttachPhotoClicked() {
        requestCameraToOpen(PERMISSION_PHOTO_CAMERA_RESULT, this::showPhotoAttachmentDialog);
    }

    @OnClick(R.id.chat_video_btn)
    void onAttachVideoClicked() {
        requestCameraToOpen(PERMISSION_VIDEO_CAMERA_RESULT, this::showVideoAttachmentDialog);
    }

    @OnClick(R.id.chat_send_button)
    void onSendClicked() {
        String text = getText();
        if (TextUtils.isEmpty(text)) {
            requestAudioRecording();
        } else {
            chatSendButton.setEnabled(false);
            compositeSubscription.add(chatController.sendMessage(text)
                    .subscribe((message) -> {
                                handleMessage(message);
                                chatSendButton.setEnabled(true);
                            },
                            throwable -> {
                                Log.e(TAG, "Cannot send message", throwable);
                                unableSendMessageSnackBar();
                                chatSendButton.setEnabled(true);
                            }));
        }
    }

    private void requestAudioRecording() {
        if (PermissionUtil.hasAudioRecordPermission(this)) {
            audioMessageLayout.setVisibility(View.VISIBLE);
            textMessageLayout.setVisibility(View.GONE);
            audioRecorder.startRecording();
        } else {
            PermissionUtil.requestAudioPermission(this, PERMISSION_AUDIO_RECORD_RESULT);
        }
    }

    private void hideAudioPanel() {
        audioMessageLayout.setVisibility(View.GONE);
        textMessageLayout.setVisibility(View.VISIBLE);
        messageEt.requestFocus();
    }

    @OnClick(R.id.audio_cancel_bt)
    void onAudioCancel() {
        hideAudioPanel();
        audioRecorder.cancelRecording();
    }

    @OnClick(R.id.audio_send_bt)
    void onAudioSendBtClick() {
        String aLength = getAudioTime();
        File aFile = audioRecorder.stopRecording();
        if (aFile != null && aFile.exists()) {
            compositeSubscription.add(chatController.sendAudioMessage(getText(), aFile, aLength)
                    .subscribe(this::handleMessage,
                            throwable -> {
                                Log.e(TAG, "Cannot send message", throwable);
                                unableSendMessageSnackBar();
                            }));
        } else {
            snackBarUtil.displaySnackBar(this, R.string.cant_send_audio_record);
        }
        hideAudioPanel();
    }

    private String getAudioTime() {
        final int TWO_MINUTES = 120;
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        try {
            Date date = sdf.parse(audioLength.getText().toString());
            Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
            calendar.setTime(date);
            int minute = calendar.get(Calendar.MINUTE);
            int sec = calendar.get(Calendar.SECOND);
            return String.valueOf(TWO_MINUTES - (minute * SEC_IN_MIN + sec));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            ProgressDialog dialog = ProgressDialog.show(this, "Uploading...", null);
            compositeSubscription.add(chatController
                    .onActivityResultSingle(getText(), requestCode, data)
                    .subscribe(message -> {
                                dialog.dismiss();
                                handleMessage(message);
                            },
                            throwable -> {
                                Log.e(TAG, "Cannot send message with attachment", throwable);
                                unableSendMessageSnackBar();
                            }));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!PermissionUtil.verifyPermissions(grantResults)) return;

        if (requestCode == PERMISSION_PHOTO_CAMERA_RESULT) {
            showPhotoAttachmentDialog();
        } else if (requestCode == PERMISSION_VIDEO_CAMERA_RESULT) {
            showVideoAttachmentDialog();
        } else if (requestCode == PERMISSION_AUDIO_RECORD_RESULT) {
            requestAudioRecording();
        }
    }

    private void connectToChat() {
        compositeSubscription.add(networkClient.getChatWith(friend.getFriendId())
                .flatMap(chatHolder -> chatController.connect(chatHolder.getChat().getId())
                        .toObservable())
                .toCompletable()
                .andThen(chatController.receiveMessages())
                .subscribe(this::handleMessage,
                        throwable -> {
                            Log.e(TAG, "Cannot receive messages", throwable);
                            unablePullMessagesSnackBar();
                        }));
    }

    private String getText() {
        return messageEt.getText().toString().trim();
    }

    private void handleMessage(Message message) {
        chatController.handleMessageReceived(chatRecyclerView, messageEt, message);
    }

    private void requestCameraToOpen(int permissionResult, Action0 openAction) {
        if (PermissionUtil.hasCameraPermission(this)) {
            openAction.call();
        } else {
            PermissionUtil.requestCameraPermission(this, permissionResult);
        }
    }

    private void showPhotoAttachmentDialog() {
        MediaHelper.showPictureAttachmentDialog(this, new MediaHelper.PhotoChooser() {
            @Override
            public void onPickFile() {
                MediaHelper.startActivityForPickingMediaFile(MessagingActivity.this, MediaHelper.IMAGE_MIME_TYPES);
            }

            @Override
            public void onTakePhoto() {
                try {
                    MediaHelper.startActivityForTakingPhoto(MessagingActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showVideoAttachmentDialog() {
        MediaHelper.showVideoAttachmentDialog(this, new MediaHelper.VideoChooser() {
            @Override
            public void onPickFile() {
                MediaHelper.startActivityForPickingMediaFile(MessagingActivity.this, MediaHelper.VIDEO_MIME_TYPES);
            }

            @Override
            public void onTakeVideo() {
                MediaHelper.startActivityForTakingVideo(MessagingActivity.this);
            }
        });
    }

    private void unablePullMessagesSnackBar() {
        snackBarUtil.showSnackBarWithAction(this, R.string.unable_to_get_messages_text, R.string.retry, snackbar -> {
            connectToChat();
            SnackbarManager.dismiss();
        });
    }

    private void unableSendMessageSnackBar() {
        SnackbarManager.show(
                Snackbar.with(getApplicationContext())
                        .type(SnackbarType.MULTI_LINE)
                        .text(getString(R.string.unable_to_send_message_text))
                , this);
    }
}