package com.conx2share.conx2share.model;

public class EventResponse {

    private Event event;

    public EventResponse() {

    }

    public EventResponse(Event event) {
        setEvent(event);
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
