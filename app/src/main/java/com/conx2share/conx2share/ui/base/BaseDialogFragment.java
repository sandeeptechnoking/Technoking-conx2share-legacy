package com.conx2share.conx2share.ui.base;

import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import roboguice.fragment.RoboDialogFragment;

public class BaseDialogFragment extends RoboDialogFragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }
}
