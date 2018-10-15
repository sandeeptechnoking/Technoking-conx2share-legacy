package com.conx2share.conx2share.ui.base;

import com.conx2share.conx2share.util.AboutFrameUtil;

import android.os.Bundle;
import android.view.View;

public abstract class BaseProfileFragment extends BasePostListFragment {

    public static final String TAG = BaseProfileFragment.class.getSimpleName();

    private AboutFrameUtil mAboutFrameUtil;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAboutFrameUtil = new AboutFrameUtil(getActivity(), view);
    }

    protected void setAboutText(String text) {
        mAboutFrameUtil.setAboutText(text);
    }
}
