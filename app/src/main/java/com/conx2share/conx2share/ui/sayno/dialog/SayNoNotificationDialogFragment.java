package com.conx2share.conx2share.ui.sayno.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.conx2share.conx2share.R;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import butterknife.OnClick;
import roboguice.inject.InjectView;

public class SayNoNotificationDialogFragment extends DialogFragment {
    private static final String EXTRA_TEXT_ID = "text-id";

    private static final String EXTRA_CANCELABLE = "cancelable";

    @InjectView(R.id.say_no_notification_dialog_text)
    TextView textTv;

    private NotificationDialogInteraction notificationDialogInteraction;

    public static SayNoNotificationDialogFragment newInstance(@StringRes int textResId) {
        return newInstance(textResId, true);
    }

    public static SayNoNotificationDialogFragment newInstance(@StringRes int textResId,
                                                              boolean cancelable) {
        Bundle args = new Bundle(2);
        args.putInt(EXTRA_TEXT_ID, textResId);
        args.putBoolean(EXTRA_CANCELABLE, cancelable);

        SayNoNotificationDialogFragment fragment = new SayNoNotificationDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_say_no_pending_dialog, container, false);
        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        setCancelable(args.getBoolean(EXTRA_CANCELABLE));
        textTv.setText(args.getInt(EXTRA_TEXT_ID));
        return view;
    }

    @Override
    public void onDetach() {
        notificationDialogInteraction = null;
        super.onDetach();
    }

    @OnClick(R.id.say_no_notification_dialog_return_btn)
    void onReturnClicked() {
        dismiss();

        if (notificationDialogInteraction != null) {
            notificationDialogInteraction.onReturn();
        }
    }

    public SayNoNotificationDialogFragment setNotificationDialogInteraction(NotificationDialogInteraction notificationDialogInteraction) {
        this.notificationDialogInteraction = notificationDialogInteraction;
        return this;
    }

    public interface NotificationDialogInteraction {
        void onReturn();
    }
}