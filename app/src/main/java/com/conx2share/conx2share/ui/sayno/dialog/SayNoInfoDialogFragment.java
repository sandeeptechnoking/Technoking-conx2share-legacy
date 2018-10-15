package com.conx2share.conx2share.ui.sayno.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.conx2share.conx2share.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SayNoInfoDialogFragment extends DialogFragment {

    public static SayNoInfoDialogFragment newInstance() {
        return new SayNoInfoDialogFragment();
    }

    public SayNoInfoDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_say_no_info, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.say_no_info_dialog_got_it_btn)
    void onGotItClicked() {
        dismiss();
    }
}