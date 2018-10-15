package com.conx2share.conx2share.ui.sayno.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.conx2share.conx2share.R;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import butterknife.OnClick;
import roboguice.inject.InjectView;

public class SayNoConfirmationDialogFragment extends DialogFragment {

    private static final String EXTRA_TEXT_ID = "text-id";

    private static final String EXTRA_POSITIVE_BTN_TEXT_ID = "create-text-id";

    private static final String EXTRA_CANCELABLE = "cancelable";

    @InjectView(R.id.say_no_confirmation_text)
    TextView textTv;

    @InjectView(R.id.say_no_confirmation_create_new)
    Button positiveBtn;

    private ConfirmationDialogInteraction callback;

    public static SayNoConfirmationDialogFragment newInstance(@StringRes int textId,
                                                              @StringRes int positiveTextId) {
        return newInstance(textId, positiveTextId, true);
    }

    public static SayNoConfirmationDialogFragment newInstance(@StringRes int textId,
                                                              @StringRes int positiveTextId,
                                                              boolean cancelable) {
        Bundle args = new Bundle(3);
        args.putInt(EXTRA_TEXT_ID, textId);
        args.putInt(EXTRA_POSITIVE_BTN_TEXT_ID, positiveTextId);
        args.putBoolean(EXTRA_CANCELABLE, cancelable);

        SayNoConfirmationDialogFragment fragment = new SayNoConfirmationDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SayNoConfirmationDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_say_no_report, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        textTv.setText(bundle.getInt(EXTRA_TEXT_ID));
        positiveBtn.setText(bundle.getInt(EXTRA_POSITIVE_BTN_TEXT_ID));

        setCancelable(bundle.getBoolean(EXTRA_CANCELABLE, true));
        return view;
    }

    @OnClick(R.id.say_no_confirmation_create_new)
    public void onPositiveButtonClicked() {
        if (callback != null) {
            callback.onPositiveButtonClicked();
        }

        dismiss();
    }

    @OnClick(R.id.say_no_confirmation_go_home)
    public void onNegativeButtonClicked() {
        if (callback != null) {
            callback.onNegativeButtonClicked();
        }

        dismiss();
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    public SayNoConfirmationDialogFragment setConfirmationDialogInteraction(ConfirmationDialogInteraction callback) {
        this.callback = callback;
        return this;
    }

    public interface ConfirmationDialogInteraction {
        void onPositiveButtonClicked();

        void onNegativeButtonClicked();
    }
}