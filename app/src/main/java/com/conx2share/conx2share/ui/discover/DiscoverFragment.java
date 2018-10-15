package com.conx2share.conx2share.ui.discover;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.DiscoverViewPagerAdapter;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.util.EventBusUtil;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import roboguice.inject.InjectView;

public class DiscoverFragment extends BaseFragment {

    public static final String TAG = DiscoverFragment.class.getSimpleName();

    public static final String HASHTAG = "hashtag";

    @InjectView(R.id.discover_search_ac_tv)
    EditText mSearch;

    @InjectView(R.id.discover_view_pager)
    ViewPager mViewPager;

    @InjectView(R.id.discover_view_pager_title_strip)
    TabLayout mDiscoverPagerTitleStrip;

    private final TextWatcher mSearchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // NO OP
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // NO OP
        }

        @Override
        public void afterTextChanged(Editable s) {
            String searchTerms = mSearch.getText().toString();
            if(searchTerms.trim().length() > 0) {
                EventBusUtil.getEventBus().post(new LoadDiscoverSearchEvent(searchTerms));
            } else {
                EventBusUtil.getEventBus().post(new LoadDiscoverSearchEvent(null));
            }
        }
    };

    public static Fragment newInstance(Bundle arguments) {
        DiscoverFragment fragment = new DiscoverFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DiscoverViewPagerAdapter discoverViewPagerAdapter = new DiscoverViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(discoverViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(discoverViewPagerAdapter.getCount());
        mDiscoverPagerTitleStrip.setupWithViewPager(mViewPager);
//        mDiscoverPagerTitleStrip.setDrawFullUnderline(true);

        if (getArguments() != null && getArguments().getString(HASHTAG) != null) {
            mSearch.setText(getArguments().getString(HASHTAG));
            new Handler().post(() -> EventBusUtil.getEventBus().post(new LoadDiscoverSearchEvent(getArguments().getString(HASHTAG))));
        }

        mSearch.addTextChangedListener(mSearchTextWatcher);
    }

    public class LoadDiscoverSearchEvent {

        private String searchTerms;

        public LoadDiscoverSearchEvent(String searchTerms) {
            this.searchTerms = searchTerms;
        }

        public String getSearchTerms() {
            return searchTerms;
        }
    }
}
