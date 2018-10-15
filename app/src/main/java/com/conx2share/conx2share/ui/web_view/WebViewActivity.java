package com.conx2share.conx2share.ui.web_view;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActivity;

import roboguice.inject.InjectView;

public class WebViewActivity extends BaseActivity {

    public static String TAG = WebViewActivity.class.getSimpleName();
    public static String EXTRA_SCREEN_TITLE = "screenTitle";

    @InjectView(R.id.web_view_back_button)
    ImageButton mWebViewBackButton;

    @InjectView(R.id.web_view)
    WebView mWebView;

    @InjectView(R.id.news_title)
    TextView mScreenTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        mScreenTitle.setText(getIntent().getStringExtra(EXTRA_SCREEN_TITLE));

        mWebViewBackButton.setOnClickListener(view -> {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
