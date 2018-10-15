package com.conx2share.conx2share.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LiveEventWrapper {
    @SerializedName("live_streams")
    List<LiveEvent> liveEvents;

    public List<LiveEvent> getLiveEvents() {
        return liveEvents;
    }

}
