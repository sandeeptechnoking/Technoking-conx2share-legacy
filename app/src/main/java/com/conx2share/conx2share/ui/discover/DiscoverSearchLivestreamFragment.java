package com.conx2share.conx2share.ui.discover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.DiscoverLiveStreamAdapter;
import com.conx2share.conx2share.model.LiveEvent;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DiscoverSearchLivestreamFragment extends BaseFragment {

    private static final String TAG = DiscoverSearchLivestreamFragment.class.getSimpleName();
    View view;

    @BindView(R.id.discover_users_list_view)
    RecyclerView liveStreamList;

    @BindView(R.id.discover_livestream_progress_bar)
    ProgressBar progressBar;

    private List<LiveEvent> liveEvents = new ArrayList<>();

    private DiscoverLiveStreamAdapter discoverLiveStreamAdapter;

    @Inject
    NetworkClient networkClient;


    public static Fragment newInstance() {
        return new DiscoverSearchLivestreamFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_discover_search_livestream, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        discoverLiveStreamAdapter = new DiscoverLiveStreamAdapter(liveEvents);

        liveStreamList.setHasFixedSize(true);
        liveStreamList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.HORIZONTAL));
        liveStreamList.setAdapter(discoverLiveStreamAdapter);

        searchLiveStreams(null);
    }

    public void onEventMainThread(DiscoverFragment.LoadDiscoverSearchEvent event) {
        if (getActivity() == null) return;
        searchLiveStreams(event.getSearchTerms());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.bind(this, view).unbind();
    }

    private void searchLiveStreams(String query) {
        progressBar.setVisibility(View.VISIBLE);
        liveStreamList.setVisibility(View.GONE);
        addSubscription(networkClient.getLiveStreamList(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(liveEventsList -> {
                    if (liveEventsList == null) return;
                    liveEvents.clear();
                    liveEvents.addAll(liveEventsList.getLiveEvents());

                    discoverLiveStreamAdapter.notifyDataSetChanged();
                    setListVisible();
                }, throwable -> {
                    Log.e(TAG, "searchLiveStreams: ", throwable);
                    mSnackbarUtil.displaySnackBar(getActivity(), R.string.cant_get_live_list);
                    setListVisible();
                }));
    }

    private void setListVisible() {
        progressBar.setVisibility(View.GONE);
        liveStreamList.setVisibility(View.VISIBLE);
    }
}