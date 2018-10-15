package com.conx2share.conx2share.model.event;


import com.conx2share.conx2share.model.Expiration;

public class ExpirationEvent {

    private Expiration mExpiration;

    public ExpirationEvent(Expiration expiration) {
        mExpiration = expiration;
    }

    public Expiration getExpiration() {
        return mExpiration;
    }

    public void setExpiration(Expiration expiration) {
        mExpiration = expiration;
    }
}
