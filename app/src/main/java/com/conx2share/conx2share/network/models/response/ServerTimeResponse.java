package com.conx2share.conx2share.network.models.response;

import java.util.Date;

public class ServerTimeResponse {

    private Date time;

    public ServerTimeResponse(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
