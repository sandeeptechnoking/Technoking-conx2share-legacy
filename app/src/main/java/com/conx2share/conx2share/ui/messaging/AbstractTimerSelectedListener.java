package com.conx2share.conx2share.ui.messaging;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.util.PreferencesUtil;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

public abstract class AbstractTimerSelectedListener implements AdapterView.OnItemSelectedListener {

    public static final String TAG = AbstractTimerSelectedListener.class.getSimpleName();

    private PreferencesUtil mPreferencesUtil;

    private Context mContext;

    public AbstractTimerSelectedListener(PreferencesUtil preferencesUtil, Context context) {
        mPreferencesUtil = preferencesUtil;
        mContext = context;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mPreferencesUtil.setMessagingTimeValue(position);

        Integer ttl = null;
        if (position > 0) {
            String timeString = mContext.getResources().getStringArray(R.array.timers)[position];
            timeString = timeString.replaceAll("\\D", "");
            ttl = Integer.parseInt(timeString);
        }

        onSetTTL(ttl);
        onSetSelection(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // NO OP
    }

    public abstract void onSetSelection(int position);

    public abstract void onSetTTL(Integer ttl);

}
