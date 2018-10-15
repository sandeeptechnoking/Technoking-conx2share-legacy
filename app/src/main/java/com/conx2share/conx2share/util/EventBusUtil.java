package com.conx2share.conx2share.util;

import android.app.Application;

import de.greenrobot.event.EventBus;

public class EventBusUtil {

    private static EventBus mEventBus;

    public static EventBus getEventBus() {
        return EventBus.getDefault();
    }

    public static void registerForSyncEvents(Object receiver) {
        EventBus bus = getEventBus();
        if (!bus.isRegistered(receiver)) {
            bus.register(receiver);
        }

    }

    public static void unregisterForSyncEvents(Object receiver) {
        EventBus bus = getEventBus();
        if (bus.isRegistered(receiver)) {
            bus.unregister(receiver);
        }
    }
}