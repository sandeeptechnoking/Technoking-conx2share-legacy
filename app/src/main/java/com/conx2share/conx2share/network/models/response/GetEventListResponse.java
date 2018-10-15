package com.conx2share.conx2share.network.models.response;

import com.conx2share.conx2share.model.Event;

import java.util.ArrayList;

public class GetEventListResponse {

    private ArrayList<Event> events;

    public GetEventListResponse(ArrayList<Event> events) {
        this.events = events;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
