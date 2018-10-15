package com.conx2share.conx2share.ui.sign_up;

import com.conx2share.conx2share.BuildConfig;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.discover.DiscoverActivity;
import com.conx2share.conx2share.util.ForegroundUtil;
import com.conx2share.conx2share.util.MediaUploadUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.Statics;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;

public class BusinessSignUpWebActivity extends RoboActivity {

    public static final String TAG = BusinessSignUpWebActivity.class.getSimpleName();

    public static final String BUSINESS_USER_ID = "business_user_id";

    private final static int FILECHOOSER_CODE = 1;

    private final static int FILECHOOSER_LOLLIPOP_CODE = 2;

    private ValueCallback<Uri> mUploadMessage;

    // after business information is entered, we are redirected to a url like businesses/35
    public static final String BUSINESS_SETUP_COMPLETE_URL = Statics.BASE_URL + "/businesses/[0-9]+";

    // the '#' in this string will be replaced with the user's id, the authtoken will be appended
    private static final String BUSINESS_SIGNUP_URL = Statics.BASE_URL + "/businesses/new?user_id=#&auth_token=";

    @Inject
    private PreferencesUtil mPreferencesUtil;

    private ValueCallback<Uri[]> mFilePathCallback;

    private String mCameraPhotoPath;

    private AlertDialog mDialog;

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RoboGuice.getInjector(this).injectMembersWithoutViews(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_business_web_signup);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.loadUrl(makeUrl());

        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "onPageStarted: " + url);
                super.onPageStarted(view, url, favicon);

                boolean override = url.matches(BUSINESS_SETUP_COMPLETE_URL);

                if (override) {
                    progressBar.setVisibility(View.VISIBLE);
                    startDiscoverActivity();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d(TAG, String.format("onReceivedError {errorCode: %d, description: '%s', failingUrl: '%s'}", errorCode, description, failingUrl));
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading: " + url);

                return url.matches(BUSINESS_SETUP_COMPLETE_URL);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_CODE);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_CODE);
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser( i, "File Chooser" ), FILECHOOSER_CODE );
            }

            // For Android 5.0 - had to do a lot of research, look at:
            // - http://stackoverflow.com/questions/5907369/file-upload-in-webview
            // - https://gauntface.com/blog/2014/10/17/what-you-need-to-know-about-the-webview-in-l
            // - Logic based off of/taken from example here https://github.com/GoogleChrome/chromium-webview-samples/blob/master/input-file-example/app/src/main/java/inputfilesample/android/chrome/google/com/inputfilesample/MainFragment.java
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

                if(mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        Log.e(TAG, "Unable to create Image File", ex);
                    }

                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if(takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_CODE);

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ForegroundUtil.setAppInForeground(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ForegroundUtil.setAppInForeground(false);
    }

    @Override
    public void onBackPressed() {
        if (mDialog != null) {
            return;
        }

        mDialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.abort_business_details_confirmation))
                .setPositiveButton(R.string.abort, (dialog, which) -> {
                    mDialog = null;
                    startDiscoverActivity();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    mDialog = null;
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    private void startDiscoverActivity() {
        Intent intent = new Intent(this, DiscoverActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    private String makeUrl() {
        int businessId = getIntent().getIntExtra(BUSINESS_USER_ID, -1);
        if (businessId == -1) {
            throw new RuntimeException("Expected a business_user_id extra");
        }

        return BUSINESS_SIGNUP_URL.replaceFirst("#", String.valueOf(businessId)) + mPreferencesUtil.getAuthToken();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FILECHOOSER_CODE) {
            if (null == mUploadMessage) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else if (requestCode == FILECHOOSER_LOLLIPOP_CODE) {
            Uri[] results = null;

            if(resultCode == Activity.RESULT_OK) {
                if(data == null) {
                    if(mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        }
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