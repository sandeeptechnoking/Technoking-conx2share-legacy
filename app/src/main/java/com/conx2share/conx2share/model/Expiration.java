package com.conx2share.conx2share.model;


import java.util.Date;

public class Expiration {

    private Integer messageId;

    private Date expTime;

    public Expiration(Integer messageId, Date expTime) {
        this.messageId = messageId;
        this.expTime = expTime;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Date getExpTime() {
        return expTime;
    }

    public void setExpTime(Date expTime) {
        this.expTime = expTime;
    }
}
