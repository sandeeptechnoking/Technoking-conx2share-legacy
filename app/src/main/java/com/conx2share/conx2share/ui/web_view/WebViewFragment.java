package com.conx2share.conx2share.ui.web_view;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

//import butterknife.InjectView;
import roboguice.inject.InjectView;


public class WebViewFragment extends BaseFragment {

    public static String TAG = WebViewFragment.class.getSimpleName();

    public static String EXTRA_WEB_URI = "webURI";

    @InjectView(R.id.web_view)
    WebView mWebView;

    @InjectView(R.id.web_view_progress_bar)
    ProgressBar mWebViewProgressBar;

    private String mWebURI;

    public static WebViewFragment newInstance() {
        return new WebViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            if (getActivity().getIntent() != null) {
                if (getActivity().getIntent().hasExtra(EXTRA_WEB_URI)) {
                    mWebURI = getActivity().getIntent().getStringExtra(EXTRA_WEB_URI);
                } else {
                    Log.e(TAG, "No webURI passed in");
                }
            } else {
                Log.w(TAG, "No intent");
            }
        }
        Log.d(TAG, "webURI: " + mWebURI);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.clearCache(true);

        if (mWebURI != null) {
            mWebView.loadUrl(mWebURI);
        } else {
            if (getActivity() != null) {
                mWebViewProgressBar.setVisibility(View.GONE);
                SnackbarManager.show(
                        Snackbar.with(getActivity().getApplicationContext())
                                .type(SnackbarType.MULTI_LINE)
                                .text(getString(R.string.unable_to_load_web_page))
                        , getActivity());
            }
        }

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mWebView.setVisibility(View.GONE);
                mWebViewProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.setVisibility(View.VISIBLE);
                mWebViewProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
