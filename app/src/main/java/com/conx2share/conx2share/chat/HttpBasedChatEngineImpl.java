package com.conx2share.conx2share.chat;

import android.support.annotation.NonNull;

import com.conx2share.conx2share.Conx2ShareApplication;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.models.response.SendMessageResponse;
import com.conx2share.conx2share.util.CountingTypedFile;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import retrofit.mime.TypedFile;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class HttpBasedChatEngineImpl implements ChatEngine {

    private static final int CHAT_NOT_CONNECTED_ID = -1;

    private static final int PULL_TIME_INTERVAL = 3;

    private NetworkClient networkClient;

    private volatile int chatId = CHAT_NOT_CONNECTED_ID;

    public HttpBasedChatEngineImpl(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    @Override
    public Completable connect(int chatId) {
        this.chatId = chatId;
        return Completable.complete();
    }

    @Override
    public Completable disconnect() {
        return Completable.complete();
    }

    @Override
    public Observable<Message> sendMessage(String text) {
        return sendMessageInternal(text);
    }

    @Override
    public Observable<Message> sendPhotoMessage(String text, File photo) {
        File compressedFile = Compressor.getDefault(Conx2ShareApplication.getInstance()).compressToFile(photo);
        return sendMessageInternal(text, new TypedFile("image/*", compressedFile), null, null, null);
    }

    @Override
    public Observable<Message> sendVideoMessage(String text, File video) {
        return sendMessageInternal(text, null, new TypedFile("video/*", video), null, null);
    }

    @Override
    public Observable<Message> sendAudioMessage(String text, File audio, String audioLength) {
        return sendMessageInternal(text, null, null, new TypedFile("audio/*", audio), audioLength);
    }

    @Override
    public Observable<Message> receiveMessage() {
        return Observable.interval(PULL_TIME_INTERVAL, TimeUnit.SECONDS)
                .startWith(0L)
                .flatMap(tick -> networkClient.getMessagesByChatId(chatId)
                        .flatMap(messagesResponse -> Observable.from(messagesResponse.getMessages())));
    }

    @Override
    public Single<Integer> connectedTo() {
        return Single.just(chatId);
    }

    @Override
    public Single<Boolean> isConnected() {
        return Single.just(chatId != CHAT_NOT_CONNECTED_ID);
    }

    @Override
    public Observable<List<Message>> getChatHistory(Integer page) {
        return networkClient.getChatHistory(chatId, page);
    }

    @NonNull
    private Observable<Message> sendMessageInternal(String text, TypedFile photo, TypedFile video, TypedFile audio, String audioLength) {
        return networkClient
                .sendChatMessage(text, chatId, photo, video, audio, audioLength)
                .map(SendMessageResponse::getMessage);
    }

    @NonNull
    private Observable<Message> sendMessageInternal(String text) {
        return sendMessageInternal(text, null, null, null, null);
    }
}