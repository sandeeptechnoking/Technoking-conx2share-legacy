package com.conx2share.conx2share.chat;

import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.util.CountingTypedFile;

import java.io.File;
import java.util.List;

import rx.Completable;
import rx.Observable;
import rx.Single;

public interface ChatEngine {

    Completable connect(int chatId);

    Completable disconnect();

    Observable<Message> sendMessage(String text);

    Observable<Message> sendPhotoMessage(String text, File photo);

    Observable<Message> sendVideoMessage(String text, File video);

    Observable<Message> sendAudioMessage(String text, File audio, String audioLength);

    Observable<Message> receiveMessage();

    Single<Integer> connectedTo();

    Single<Boolean> isConnected();

    Observable<List<Message>> getChatHistory(Integer page);
}