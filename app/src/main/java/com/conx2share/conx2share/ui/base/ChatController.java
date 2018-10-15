package com.conx2share.conx2share.ui.base;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.EditText;

import com.conx2share.conx2share.chat.ChatEngine;
import com.conx2share.conx2share.model.IncomingAudioMessage;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.model.TimeDividerMessage;
import com.conx2share.conx2share.util.MediaHelper;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.techery.celladapter.CellAdapter;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public final class ChatController {

    Func1<Message, Message> audioMessageFun() {
        return message ->
            !TextUtils.isEmpty(message.getAudioUrl()) ? new IncomingAudioMessage(message) : message;
    }

    private static final long TEN_MINUTES = 10 * 60 * 1000;
    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private ChatEngine chatEngine;

    private Context context;

    private int chatId;

    private CellAdapter chatAdapter;

    public ChatController(Context context,
                          ChatEngine chatEngine,
                          CellAdapter chatAdapter) {
        this.context = context;
        this.chatEngine = chatEngine;
        this.chatAdapter = chatAdapter;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public Completable connect(int chatId) {
        return chatEngine.connect(chatId);
    }

    public Completable disconnect() {
        return chatEngine.disconnect();
    }

    public Single<Integer> connectedTo() {
        return chatEngine.connectedTo();
    }

    public Single<Boolean> isConnected() {
        return chatEngine.isConnected();
    }

    public Observable<Message> sendMessage(@NonNull String text) {
        return chatEngine.sendMessage(text)
                .map(audioMessageFun())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Message> sendVideoMessage(@NonNull String text, @NonNull File videoFile) {
        return chatEngine.sendVideoMessage(text, videoFile)
                .map(audioMessageFun())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Message> sendPhotoMessage(@NonNull String text, @NonNull File photoFile) {
        return chatEngine.sendPhotoMessage(text, photoFile)
                .map(audioMessageFun())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Message> sendAudioMessage(@NonNull String text, @NonNull File audioFile, String audioLength) {
        return chatEngine.sendAudioMessage(text, audioFile, audioLength)
                .map(audioMessageFun())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Message> receiveMessages() {
        return chatEngine.receiveMessage()
                .map(audioMessageFun())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Message> onActivityResultSingle(@NonNull String text, int requestCode, Intent data) {
        if (MediaHelper.canCatchPhotoResult(requestCode)) {
            //attach photo
            return sendPhotoMessage(text, MediaHelper.getPhotoFile());
        }

        if (MediaHelper.canCatchVideoResult(requestCode)) {
            //attach video
            return sendVideoMessage(text, MediaHelper.getVideoFile(context, data));
        }

        if (MediaHelper.canCatchPickMediaFileResult(requestCode)) {
            //attach media file
            File mediaFile = MediaHelper.getMediaFile(context, data);
            if (MediaHelper.isImage(mediaFile.getPath())) {
                return sendPhotoMessage(text, mediaFile);
            }

            if (MediaHelper.isVideo(mediaFile.getPath())) {
                return sendVideoMessage(text, mediaFile);
            }

            throw new IllegalArgumentException("Unknown media type for " + mediaFile.getName());
        }

        throw new IllegalStateException("Unknown requestCode " + requestCode);
    }

    public void handleMessageReceived(RecyclerView recyclerView,
                                      EditText inputEditText,
                                      @NonNull Message message) {
        if (!chatAdapter.contains(message)) {
            checkForAddingTimeCell(message, true);
            chatAdapter.addItem(message);

            recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
            inputEditText.setText(null);
        }
    }

    public Observable<List<Message>> getChatHistory(Integer page) {
        return chatEngine.getChatHistory(page)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void handlePagination(RecyclerView recyclerView, List<Message> messages) {
        if (messages == null || messages.isEmpty()) return;
        boolean itemWasAdded = false;
        for (Message message : messages) {
            if (!chatAdapter.contains(message)) {
                checkForAddingTimeCell(message, false);
                chatAdapter.addItem(0, message);
                itemWasAdded = true;
            }
        }
        if (itemWasAdded) recyclerView.smoothScrollToPosition(0);
    }

    private void checkForAddingTimeCell(@NonNull Message message, boolean addMessageToBottom) {
        if (chatAdapter.getItemCount() < 1) return;
        Message oldMessage;
        int msgIndex;
        if (addMessageToBottom) {
            msgIndex = chatAdapter.getItemCount() - 1;
        } else {
            msgIndex = 0;
        }
        if (chatAdapter.getItem(msgIndex) instanceof TimeDividerMessage) return;
        oldMessage = (Message) chatAdapter.getItem(msgIndex);
        TimeDividerMessage timeMsg = getTimeMsg(message, oldMessage, addMessageToBottom);
        if (timeMsg != null) {
            chatAdapter.addItem(addMessageToBottom ? msgIndex + 1: msgIndex, timeMsg);
        }
    }

    @Nullable
    private TimeDividerMessage getTimeMsg(Message newMessage, Message oldMessage, boolean addMessageToBottom) {
        if (newMessage.getCreatedAt() == null || oldMessage.getCreatedAt() == null) return null;
        Date newDate, oldDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            newDate = dateFormat.parse(newMessage.getCreatedAt());
            oldDate = dateFormat.parse(oldMessage.getCreatedAt());
            if (Math.abs(newDate.getTime() - oldDate.getTime()) > TEN_MINUTES) {
                return new TimeDividerMessage(addMessageToBottom ? newDate : oldDate, oldMessage.getId());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}