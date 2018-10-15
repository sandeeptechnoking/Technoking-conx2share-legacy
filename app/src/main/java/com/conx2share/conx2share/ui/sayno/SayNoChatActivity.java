package com.conx2share.conx2share.ui.sayno;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.chat.HttpBasedChatEngineImpl;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.model.TimeDividerMessage;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.models.param.SayNoChatParams;
import com.conx2share.conx2share.ui.base.BaseAppCompatActivity;
import com.conx2share.conx2share.ui.base.ChatController;
import com.conx2share.conx2share.ui.base.MessageCell;
import com.conx2share.conx2share.ui.base.MessageTimeCell;
import com.conx2share.conx2share.ui.feed.FeedActivity;
import com.conx2share.conx2share.ui.messaging.MediaViewerActivity;
import com.conx2share.conx2share.ui.sayno.cell.AnonymableMessageCell;
import com.conx2share.conx2share.ui.sayno.choose.SayNoTypeChooseActivity;
import com.conx2share.conx2share.ui.sayno.dialog.SayNoConfirmationDialogFragment;
import com.conx2share.conx2share.ui.view.MarginItemDecorator;
import com.conx2share.conx2share.util.MediaHelper;
import com.conx2share.conx2share.util.PermissionUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.celladapter.CellAdapter;
import roboguice.inject.InjectView;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.conx2share.conx2share.ui.sayno.AttachmentHolder.Type;

public class SayNoChatActivity extends BaseAppCompatActivity {

    private static final String TAG = SayNoChatActivity.class.getName();

    private static final String CHAT_ID_KEY = "chat_id_key";
    private static final String CANCELED_ATTACH_KEY = "cancel_attach_key";
    private static final String FIRST_MESSAGE_KEY = "first_message_key";

    private static final int PERMISSION_CAMERA_RESULT = 1000;

    @InjectView(R.id.say_no_chat_toolbar)
    Toolbar toolbar;

    @InjectView(R.id.say_no_chat_recycler_view)
    RecyclerView chatRecyclerView;

    @InjectView(R.id.say_no_chat_input_edit_text)
    EditText textInput;

    @InjectView(R.id.say_no_chat_send_button)
    ImageButton chatSendButton;

    @Inject
    NetworkClient networkClient;

    @Inject
    PreferencesUtil preferencesUtil;

    @Inject
    SnackbarUtil snackBarUtil;

    private ChatController chatController;

    private GoogleApiClient googleApiClient;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private RoundedImageView anonymityIv;

    private SayNoCellAdapter sayNoCellAdapter;
    private AlertDialog anonymousAlertDialog;

    private ProgressDialog progressDialog;

    private boolean isFirstTimeMessage = true;
    private boolean canceled;

    private Location location;
    private boolean loading;

    public static void start(Context context) {
        context.startActivity(new Intent(context, SayNoChatActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_say_no_chat);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        sayNoCellAdapter = new SayNoCellAdapter();
        sayNoCellAdapter.registerCell(Message.class, AnonymableMessageCell.class, new MessageCell.MessageCellListener() {
            @Override
            public void onImageOpen(String imageUrl) {
                MediaViewerActivity.startInImageViewMode(SayNoChatActivity.this, imageUrl);
            }

            @Override
            public void onVideoOpen(String videoUrl) {
                Log.e(TAG, videoUrl);
                showProgress();
                new Handler().postDelayed(() -> {
                    MediaViewerActivity.startInVideoViewMode(SayNoChatActivity.this, videoUrl);
                    dismissProgress();
                }, 4000);
            }
        });
        sayNoCellAdapter.registerCell(TimeDividerMessage.class, MessageTimeCell.class, null);

        sayNoCellAdapter.addItem(new Message(getString(R.string.default_first_message)));

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.addItemDecoration(new MarginItemDecorator(this, R.dimen.chat_item_margin));
        chatRecyclerView.setAdapter(sayNoCellAdapter);
        chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    getPreviousMessages(recyclerView, sayNoCellAdapter);
                }
            }
        });

        chatController = new ChatController(this,
                new HttpBasedChatEngineImpl(networkClient),
                sayNoCellAdapter);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        location = getLastKnownLocationIfPossible();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        //do nothing
                    }
                })
                .addApi(LocationServices.API)
                .build();

        if (savedInstanceState != null) {
            int chatId = savedInstanceState.getInt(CHAT_ID_KEY, 0);
            isFirstTimeMessage = savedInstanceState.getBoolean(FIRST_MESSAGE_KEY, false);
            canceled = savedInstanceState.getBoolean(CANCELED_ATTACH_KEY, false);
            restoreChat(chatId);
        } else {
            showAttachmentDialog();
        }
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
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.say_no_chat_menu, menu);
        anonymityIv = (RoundedImageView) menu.findItem(R.id.say_no_anonymous_toggle)
                .getActionView();
        anonymityIv.setOnClickListener(v -> showWarningAlertDialog());

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!SayNoTypeChooseActivity.canHandle(requestCode)
                && resultCode == RESULT_CANCELED) { //not obvious, in case where we cancel taking picture, video or picking a file
            canceled = true;
            return;
        }

        if (resultCode == RESULT_OK) {
            if (isFirstTimeMessage) {
                startFirstMessageFlow(requestCode, data);
            } else {
                compositeSubscription.add(chatController
                        .onActivityResultSingle(getText(), requestCode, data)
                        .subscribe(this::handleMessage,
                                throwable -> Log.e(TAG, "ERROR", throwable)));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CAMERA_RESULT) {
            showAttachmentDialog();
        }
    }

    @Override
    public void onBackPressed() {
        SayNoConfirmationDialogFragment
                .newInstance(R.string.say_no_chat_leave_confirmation_message, R.string.say_no_chat_leave_confirmation_continue)
                .setConfirmationDialogInteraction(new SayNoConfirmationDialogFragment.ConfirmationDialogInteraction() {
                    @Override
                    public void onNegativeButtonClicked() {
                        changeChatStatusIfNeeded();
                    }

                    @Override
                    public void onPositiveButtonClicked() {
                    }
                })
                .show(getSupportFragmentManager(), "leave");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(FIRST_MESSAGE_KEY, isFirstTimeMessage);
        outState.putBoolean(CANCELED_ATTACH_KEY, canceled);
        outState.putInt(CHAT_ID_KEY, chatController.getChatId());
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.say_no_logo)
    public void onLogoClicked() {
        FeedActivity.start(this);
    }

    @OnClick(R.id.say_no_chat_attachments_btn)
    public void onAddAttachmentClicked() {
        showAttachmentDialog();
    }

    @OnClick(R.id.say_no_chat_send_button)
    public void sendClicked() {
        String text = getText();
        if (TextUtils.isEmpty(text.trim())) {
            return;
        }

        if (canceled && sayNoCellAdapter.getItemCount() == 1) {
            performChoseAction(true, SayNoActivityType.CHAT, null);
        } else if (isFirstTimeMessage) {
            SayNoTypeChooseActivity.startForResult(this);
        } else {
            chatSendButton.setEnabled(false);
            compositeSubscription.add(chatController.sendMessage(text)
                    .subscribe((message) -> {
                                handleMessage(message);
                                chatSendButton.setEnabled(true);
                            },
                            throwable -> {
                                Toast.makeText(this, "Cannot sendObservable message", Toast.LENGTH_SHORT).show();
                                chatSendButton.setEnabled(true);
                            }));
        }
    }

    private void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private String getText() {
        return textInput.getText().toString();
    }

    private void handleMessage(Message message) {
        chatController.handleMessageReceived(chatRecyclerView, textInput, message);
    }

    private void showWarningAlertDialog() {
        if (anonymousAlertDialog == null) {
            anonymousAlertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.say_no_switch_anonymity_title)
                    .setMessage(R.string.say_no_switch_anonymity_warning_text)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        dialog.dismiss();

                        compositeSubscription.add(switchUserAnonymityModeObservable()
                                .subscribe(this::showUserAvatar,
                                        throwable -> Toast.makeText(this,
                                                R.string.say_no_switch_anonymity_mode_error, Toast.LENGTH_SHORT).show()));
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .create();
        }

        anonymousAlertDialog.show();
    }

    private void showAttachmentDialog() {
        if (PermissionUtil.hasCameraPermission(this)) {
            MediaHelper.showAttachmentDialog(this, new MediaHelper.AttachmentChooser() {
                @Override
                public void onTakePhoto() {
                    try {
                        MediaHelper.startActivityForTakingPhoto(SayNoChatActivity.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onTakeVideo() {
                    MediaHelper.startActivityForTakingVideo(SayNoChatActivity.this);
                }

                @Override
                public void onPickFile() {
                    MediaHelper.startActivityForPickImageOrVideo(SayNoChatActivity.this, MediaHelper.IMAGE_AND_VIDEO_MIME_TYPES);
                }
            }, dialog -> canceled = true);
        } else {
            PermissionUtil.requestCameraPermission(this, PERMISSION_CAMERA_RESULT);
        }
    }

    private void startFirstMessageFlow(int requestCode, Intent data) {
        if (SayNoTypeChooseActivity.canHandle(requestCode)) {
            boolean isAnonymous = SayNoTypeChooseActivity.isAnonymous(data);
            SayNoActivityType type = SayNoTypeChooseActivity.getType(data);
            AttachmentHolder attachmentHolder = SayNoTypeChooseActivity.getAttachmentHolder(data);

            performChoseAction(isAnonymous, type, attachmentHolder);
        } else {
            AttachmentHolder attachmentHolder = null;

            if (MediaHelper.canCatchPhotoResult(requestCode)) {
                //attach photo
                attachmentHolder = new AttachmentHolder(MediaHelper.getPhotoFile().getPath(), Type.IMAGE);
            } else if (MediaHelper.canCatchVideoResult(requestCode)) {
                //attach video
                attachmentHolder = new AttachmentHolder(MediaHelper.getVideoFile(this, data).getPath(), Type.VIDEO);
            } else if (MediaHelper.canCatchPickMediaFileResult(requestCode)) {
                //attach media file
                String mediaPath = MediaHelper.getMediaFile(this, data).getPath();
                if (MediaHelper.isImage(mediaPath)) {
                    attachmentHolder = new AttachmentHolder(mediaPath, Type.IMAGE);
                }

                if (MediaHelper.isVideo(mediaPath)) {
                    attachmentHolder = new AttachmentHolder(mediaPath, Type.VIDEO);
                }
            }

            SayNoTypeChooseActivity.startForResult(this, attachmentHolder);
        }
    }

    private void performChoseAction(boolean isAnonymous,
                                    SayNoActivityType type,
                                    AttachmentHolder attachmentHolder) {
        switch (type) {
            case CHAT:
                isFirstTimeMessage = false;
                canceled = false;
                if (!isAnonymous) {
                    showUserAvatar();
                }

                sayNoCellAdapter.setAnonymousMode(isAnonymous);
                startChat(isAnonymous, Completable.defer(() -> sendFirstObservable(attachmentHolder)
                        .toCompletable()));
                break;
            case REPORT:
                sendReport(isAnonymous, Completable.defer(() -> sendFirstObservable(attachmentHolder)
                        .toCompletable()));
                break;
        }
    }

    private void showUserAvatar() {
        Glide.with(this)
                .load(preferencesUtil.getAuthUser().getAvatar().getAvatar().getUrl())
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.friend_placeholder)
                .into(anonymityIv);
        anonymityIv.setClickable(false);
        sayNoCellAdapter.setAnonymousMode(false);
    }

    private Single<Message> sendFirstObservable(@Nullable AttachmentHolder holder) {
        if (holder == null) {
            return chatController.sendMessage(getText()).toSingle();
        } else {
            switch (holder.type) {
                case IMAGE:
                    return chatController.sendPhotoMessage(getText(), new File(holder.path)).toSingle();
                case VIDEO:
                    return chatController.sendVideoMessage(getText(), new File(holder.path)).toSingle();
                default:
                    throw new IllegalArgumentException("Unknown attachment type: " + holder.type);
            }
        }
    }

    private void startChat(boolean isAnonymous, Completable sendMessageCompletable) {
        compositeSubscription.add(
                createChat(SayNoChatParams.createChat(isAnonymous,
                        preferencesUtil.getSayNoGroupId(), location))
                        .andThen(sendMessageCompletable)
                        .andThen(chatController.receiveMessages())
                        .subscribe(this::handleMessage,
                                throwable -> Log.e("MESSAGE", "ERROR", throwable)));
    }

    private void sendReport(boolean isAnonymous, Completable sendMessageCompletable) {
        compositeSubscription.add(
                createChat(SayNoChatParams.createReport(isAnonymous, preferencesUtil.getSayNoGroupId(), location))
                        .andThen(sendMessageCompletable)
                        .subscribe(() -> SayNoConfirmationDialogFragment
                                        .newInstance(R.string.report_dialog_label, R.string.report_dialog_start_new, false)
                                        .setConfirmationDialogInteraction(new SayNoConfirmationDialogFragment.ConfirmationDialogInteraction() {
                                            @Override
                                            public void onNegativeButtonClicked() {
                                                FeedActivity.start(SayNoChatActivity.this);
                                            }

                                            @Override
                                            public void onPositiveButtonClicked() {
                                                start(SayNoChatActivity.this);
                                                finish();
                                            }
                                        })
                                        .show(getSupportFragmentManager(), "report"),
                                throwable -> Log.e(TAG, "ERROR", throwable)));
    }

    private void changeChatStatusIfNeeded() {
        JsonObject jsonObject = new JsonObject();
        JsonObject status = new JsonObject();
        status.addProperty("status", "closed");
        jsonObject.add("chat", status);

        compositeSubscription.add(chatController.isConnected()
                .flatMapCompletable(isConnected -> isConnected ? chatController.connectedTo()
                        .flatMapCompletable(chatId -> networkClient.getService()
                                .changeChatState(chatId, jsonObject)
                                .toCompletable()) : Completable.complete())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> FeedActivity.start(SayNoChatActivity.this),
                        throwable -> Log.e(TAG, "ERROR", throwable)));
    }

    private Completable switchUserAnonymityModeObservable() {
        return chatController.connectedTo()
                .flatMapCompletable(chatId -> networkClient.switchChatMode(chatId,
                        preferencesUtil.getAuthUser().getId(), false)
                        .toCompletable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Completable createChat(SayNoChatParams params) {
        return networkClient.getService()
                .startSayNoChat(params)
                .flatMap(postChatResponse -> {
                    int chatId = postChatResponse.getChat().getId();
                    chatController.setChatId(chatId);
                    return chatController.connect(chatId)
                            .toObservable();
                })
                .toCompletable();
    }

    private void restoreChat(int chatId) {
        if (chatId != 0) {
            isFirstTimeMessage = false;
            chatController.setChatId(chatId);
            compositeSubscription.add(chatController.connect(chatId)
                    .andThen(chatController.receiveMessages())
                    .subscribe(this::handleMessage, throwable -> Log.e("MESSAGE", "ERROR", throwable)));
        }
    }

    private Location getLastKnownLocationIfPossible() {
        if (ActivityCompat.checkSelfPermission(SayNoChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(SayNoChatActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

        return null;
    }
}