package com.conx2share.conx2share.ui.news;

import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.NewsAdapter;
import com.conx2share.conx2share.model.NewsItem;
import com.conx2share.conx2share.model.NewsSource;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.web_view.WebViewActivity;
import com.conx2share.conx2share.ui.web_view.WebViewFragment;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.google.inject.Inject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

//import butterknife.InjectView;
import roboguice.inject.InjectView;

public class NewsFragment extends BaseFragment {

    @Inject
    SnackbarUtil mSnackbarUtil;

    @InjectView(R.id.news_source_logo)
    ImageView mNewsSourceLogo;

    @InjectView(R.id.news_search)
    EditText mNewsSearch;

    @InjectView(R.id.news_list_view)
    ListView mNewsListView;

    @InjectView(R.id.news_progress_bar)
    ProgressBar mNewsProgressBar;

    public static String TAG = NewsFragment.class.getSimpleName();

    public static final String EXTRA_NEWS_SOURCE = "newsSource";

    private NewsSource mNewsSource;

    private NewsAdapter mNewsAdapter;

    private ArrayList<NewsItem> mNewsItems;

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNewsListView.setOnItemClickListener((adapterView, view1, i, l) -> {
            if(getActivity() != null) {
                NewsItem newsItem = (NewsItem) mNewsListView.getItemAtPosition(i);
                if (newsItem.getLink() != null && !TextUtils.isEmpty(newsItem.getLink())) {
                    Intent webViewIntent = new Intent(getActivity(), WebViewActivity.class);
                    webViewIntent.putExtra(WebViewFragment.EXTRA_WEB_URI, newsItem.getLink());
                    webViewIntent.putExtra(WebViewActivity.EXTRA_SCREEN_TITLE, getString(R.string.news));
                    startActivity(webViewIntent);
                } else {
                    Log.w(TAG, "Did not have a good URL for news item");
                }
            }
        });

        mNewsSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterNewsList();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mNewsItems == null) {
            mNewsItems = new ArrayList<>();
        } else {
            mNewsItems.clear();
        }

        if(getActivity().getIntent().hasExtra(EXTRA_NEWS_SOURCE)) {
            mNewsSource = getActivity().getIntent().getParcelableExtra(EXTRA_NEWS_SOURCE);
        }

        if(mNewsSource != null) {
            new ConnectToRSSFeedAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mNewsSource.getUrl());
            if(mNewsSource.getBannerImageUrl() != null) {
                Glide.with(getActivity()).load(mNewsSource.getImageUrl()).centerCrop().dontAnimate().into(mNewsSourceLogo);
            }
        }
    }

    private void filterNewsList(){
        if (!TextUtils.isEmpty(mNewsSearch.getText())) {
            ArrayList<NewsItem> filteredNewsList = new ArrayList<>();

            for (NewsItem newsItem : mNewsItems) {
                if (newsItem.getTitle() != null && newsItem.getTitle().toLowerCase().trim().contains(mNewsSearch.getText().toString().toLowerCase().trim())) {
                    filteredNewsList.add(newsItem);
                }
            }
            setupNewsListViewAdapter(filteredNewsList);
        } else {
            setupNewsListViewAdapter();
        }
    }

    private void launchFailureToReadRSSSnackbar(){
        mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.unable_to_load_rss_feed);
    }

    private InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error getting input stream from URL", e);
            return null;
        }
    }

    private void setupNewsListViewAdapter(ArrayList<NewsItem> filteredNewsItems) {
        if(getActivity() != null) {
            mNewsAdapter = new NewsAdapter(getActivity(), filteredNewsItems);
            mNewsListView.setAdapter(mNewsAdapter);
        }
    }

    private void setupNewsListViewAdapter() {
        if(getActivity() != null) {
            mNewsAdapter = new NewsAdapter(getActivity(), mNewsItems);
            mNewsListView.setAdapter(mNewsAdapter);
        }
    }

    protected class ConnectToRSSFeedAsync extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF_8");

                boolean insideItem = false;
                boolean firstItem = true; // This is used to cut down on logs for unhandled tags, it's set to true once here and then after the first item it's set to false
                NewsItem newsItem = new NewsItem();

                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if(xpp.getName() != null) {
                            switch (xpp.getName().toLowerCase()) {
                                case "item":
                                    insideItem = true;
                                    newsItem = new NewsItem();
                                    break;
                                case "title":
                                    if (insideItem) {
                                        newsItem.setTitle(xpp.nextText());
                                    }
                                    break;
                                case "description":
                                    if (insideItem) {
                                        newsItem.setDescription(android.text.Html.fromHtml(xpp.nextText()).toString());
                                    }
                                    break;
                                case "pubdate":
                                    if (insideItem) {
                                        newsItem.setPubDate(xpp.nextText());
                                    }
                                    break;
                                case "link":
                                    if (insideItem) {
                                        newsItem.setLink(xpp.nextText());
                                    }
                                    break;
                                default:
                                    if(firstItem) {
                                        Log.w(TAG, "Unhandled tag: " + xpp.getName() + ", insideItem: " + insideItem);
                                    }
                                    break;
                            }
                        } else {
                            Log.e(TAG, "Tag name was null");
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                        firstItem = false;
                        mNewsItems.add(newsItem);
                    }

                    eventType = xpp.next();
                }

            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException while trying to ready RSS feed", e);
                return false;
            } catch (XmlPullParserException e) {
                Log.e(TAG, "XmlPullParserException while trying to read RSS Feed", e);
                return false;
            } catch (IOException e) {
                Log.e(TAG, "IOException while trying to read RSS", e);
                return false;
            } catch (Exception e) {
                Log.e(TAG, "Some other random exception occurred while trying to connect to RSS feed", e);
                return false;
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mNewsListView.setVisibility(View.GONE);
            mNewsProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(getActivity() != null) {
                mNewsListView.setVisibility(View.VISIBLE);
                mNewsProgressBar.setVisibility(View.GONE);
                if (aBoolean) {
                    setupNewsListViewAdapter();
                } else {
                    launchFailureToReadRSSSnackbar();
                }
            }
        }
    }
}