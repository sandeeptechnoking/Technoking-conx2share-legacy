package com.conx2share.conx2share.ui.sayno.intro;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.inject.InjectView;

public class SayNoIntroPageFragment extends Fragment {

    private static final String EXTRA_LOGO_RES = "logo-res";
    private static final String EXTRA_HEADER_RES = "header-res";
    private static final String EXTRA_TEXT_RES = "text-res";

    @InjectView(R.id.say_no_intro_page_logo)
    ImageView logo;

    @InjectView(R.id.say_no_intro_page_header)
    TextView header;

    @InjectView(R.id.say_no_intro_page_text)
    TextView text;

    public static SayNoIntroPageFragment newInstance(@DrawableRes int logoRes,
                                                     @StringRes int headerRes,
                                                     @StringRes int textRes) {
        Bundle args = new Bundle(3);
        args.putInt(EXTRA_LOGO_RES, logoRes);
        args.putInt(EXTRA_HEADER_RES, headerRes);
        args.putInt(EXTRA_TEXT_RES, textRes);

        SayNoIntroPageFragment fragment = new SayNoIntroPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_say_no_into, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        checkParams(args);

        Glide.with(view.getContext()).
                load(args.getInt(EXTRA_LOGO_RES))
                .centerCrop()
                .into(logo);
        header.setText(args.getInt(EXTRA_HEADER_RES));
        text.setText(args.getInt(EXTRA_TEXT_RES));
    }

    private void checkParams(Bundle args) {
        if (!args.containsKey(EXTRA_LOGO_RES)
                && !args.containsKey(EXTRA_HEADER_RES)
                && !args.containsKey(EXTRA_TEXT_RES)) {
            throw new IllegalArgumentException("Not all arguments passed");
        }
    }
}