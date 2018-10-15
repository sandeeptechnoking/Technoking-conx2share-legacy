package com.conx2share.conx2share.ui.wowza;;
/*
 *
 * WOWZA MEDIA SYSTEMS, LLC ("Wowza") CONFIDENTIAL
 * Copyright (c) 2005-2016 Wowza Media Systems, LLC, All Rights Reserved.
 *
 */

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.TextView;
import android.view.View;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerView extends android.support.v7.widget.AppCompatTextView {
    public static final long DEFAULT_REFRESH_INTERVAL = 1000L;

    private long mTimerStart;
    private ScheduledExecutorService mTimerThread;

    public TimerView(Context context) {
        super(context);
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void startTimer() {
        startTimer(DEFAULT_REFRESH_INTERVAL);
    }

    public synchronized void startTimer(long refreshInterval) {
        if (mTimerThread != null) return;

        setText("00:00:00");

        mTimerStart = System.currentTimeMillis();
        mTimerThread = Executors.newSingleThreadScheduledExecutor();
        mTimerThread.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int elapsedTime = (int) ((System.currentTimeMillis() - mTimerStart) / 1000);
                        long hours = elapsedTime / 3600L,
                                minutes = elapsedTime / 60L % 60L,
                                seconds = elapsedTime % 60L;

                        setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                    }
                });
            }
        }, refreshInterval, refreshInterval, TimeUnit.MILLISECONDS);

        setVisibility(View.VISIBLE);
    }

    public synchronized void stopTimer() {
        if (mTimerThread == null) return;

        mTimerThread.shutdown();
        mTimerThread = null;

        setVisibility(View.INVISIBLE);
        setText("00:00:00");
    }

    public synchronized boolean isRunning() {
        return mTimerThread != null;
    }
}
