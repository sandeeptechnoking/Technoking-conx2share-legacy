package com.conx2share.conx2share.network.models.param;

import com.conx2share.conx2share.model.MediaType;
import com.conx2share.conx2share.network.models.MessageWrapper;
import com.conx2share.conx2share.util.TypedUri;

import retrofit.mime.TypedFile;

public class MessageParameter {

    MediaType mediaType;

    MessageWrapper messageWrapper;

    Integer ttl;

    TypedUri typedUri;

    TypedFile typedFile;

    TypedFile videoThumbnail;

    public MessageParameter() {
        // NO OP
    }

    public MessageParameter(MediaType mediaType, MessageWrapper messageWrapper, Integer ttl, TypedUri typedUri) {
        this.mediaType = mediaType;
        this.messageWrapper = messageWrapper;
        this.ttl = ttl;
        this.typedUri = typedUri;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public MessageWrapper getMessageWrapper() {
        return messageWrapper;
    }

    public void setMessageWrapper(MessageWrapper messageWrapper) {
        this.messageWrapper = messageWrapper;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public TypedUri getTypedUri() {
        return typedUri;
    }

    public void setTypedUri(TypedUri typedUri) {
        this.typedUri = typedUri;
    }

    public TypedFile getTypedFile() {
        return typedFile;
    }

    public void setTypedFile(TypedFile typedFile) {
        this.typedFile = typedFile;
    }

    public TypedFile getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(TypedFile videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }
}
