package com.conx2share.conx2share.ui.news;

import com.google.inject.Inject;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.NewsAdapter;
import com.conx2share.conx2share.adapter.NewsSourceGridViewAdapter;
import com.conx2share.conx2share.async.GetNewsSourcesAsync;
import com.conx2share.conx2share.model.NewsItem;
import com.conx2share.conx2share.model.NewsSource;
import com.conx2share.conx2share.model.NewsSources;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.SnackbarManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.util.ArrayList;
//import butterknife.InjectView;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class NewsIndexFragment extends BaseFragment{

    @Inject
    SnackbarUtil mSnackbarUtil;

    @InjectView(R.id.news_index_grid_view)
    GridView mNewsIndexGridView;

    @InjectView(R.id.news_index_progress_bar)
    ProgressBar mNewsIndexProgressBar;

    @InjectView(R.id.news_index_search)
    EditText mNewsIndexSearch;

    public static String TAG = NewsIndexFragment.class.getSimpleName();

    private NewsSourceGridViewAdapter mNewsSourceGridViewAdapter;

    private GetNewsSourcesAsync mGetNewsSourcesAsync;

    private ArrayList<NewsSource> mNewsSources;

    public static NewsIndexFragment newInstance() {
        return new NewsIndexFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_index, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNewsIndexGridView.setOnItemClickListener((adapterView, view1, i, l) -> {
            if (getActivity() != null) {
                NewsSource newsSource = mNewsSources.get(i);
                Intent intent = new Intent(getActivity(), NewsActivity.class);
                intent.putExtra(NewsFragment.EXTRA_NEWS_SOURCE, newsSource);
                getActivity().startActivity(intent);
            }
        });

        mNewsIndexSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterNewsSourceList();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mNewsIndexGridView.setVisibility(View.GONE);
        mNewsIndexProgressBar.setVisibility(View.VISIBLE);
        getNewsSources();
    }

    private void filterNewsSourceList(){
        if (!TextUtils.isEmpty(mNewsIndexSearch.getText())) {
            ArrayList<NewsSource> fiteredNewsSources = new ArrayList<>();

            for (NewsSource newsSource : mNewsSources) {
                if (newsSource.getName() != null && newsSource.getName().toLowerCase().trim().contains(mNewsIndexSearch.getText().toString().toLowerCase().trim())) {
                    fiteredNewsSources.add(newsSource);
                }
            }
            setupNewsIndexGridView(fiteredNewsSources);
        } else {
            setupNewsIndexGridView();
        }
    }

    private void setupNewsIndexGridView(){
        if(getActivity() != null) {
            mNewsSourceGridViewAdapter = new NewsSourceGridViewAdapter(getActivity(), mNewsSources);
            mNewsIndexGridView.setAdapter(mNewsSourceGridViewAdapter);
        }
    }

    private void setupNewsIndexGridView(ArrayList<NewsSource> filteredNewsSources) {
        if(getActivity() != null) {
            mNewsSourceGridViewAdapter = new NewsSourceGridViewAdapter(getActivity(), filteredNewsSources);
            mNewsIndexGridView.setAdapter(mNewsSourceGridViewAdapter);
        }
    }

    protected void getNewsSources(){
        if(mGetNewsSourcesAsync != null) {
            Log.w(TAG, "Request to get new sources already in progress...new request will be ignored");
            return;
        }

        mGetNewsSourcesAsync = new GetNewsSourcesAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<NewsSources> result) {
                if(getActivity() != null) {
                    mNewsIndexGridView.setVisibility(View.VISIBLE);
                    mNewsIndexProgressBar.setVisibility(View.GONE);
                    if (result != null && result.getResource() != null && result.getResource().getNewSources() != null) {
                        if (result.getResource().getNewSources().size() > 0) {
                            mNewsSources = result.getResource().getNewSources();
                            setupNewsIndexGridView();
                        } else {
                            mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.no_news_sources_return_from_server);
                        }
                    }
                }
                mGetNewsSourcesAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Error getting news sources", error);
                if(getActivity() != null) {
                    mNewsIndexGridView.setVisibility(View.VISIBLE);
                    mNewsIndexProgressBar.setVisibility(View.GONE);
                    mSnackbarUtil.showRetry(getActivity(), R.string.unable_to_get_news_soruces, snackbar -> {
                        getNewsSources();
                        SnackbarManager.dismiss();
                    });
                }
                mGetNewsSourcesAsync = null;
            }
        }.executeInParallel();
    }
}