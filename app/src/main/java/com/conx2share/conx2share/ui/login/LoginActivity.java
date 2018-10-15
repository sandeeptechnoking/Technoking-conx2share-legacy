package com.conx2share.conx2share.ui.login;

import com.conx2share.conx2share.BuildConfig;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActivity;
import com.conx2share.conx2share.util.Statics;
import com.crashlytics.android.Crashlytics;
import com.newrelic.agent.android.NewRelic;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends BaseActivity implements LoginFragment.LoginCallback {

    public static String TAG = LoginActivity.class.getSimpleName();

    ProgressDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.FLAVOR.equals("staging")) {
            showBaseURLDialog();
        }

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, LoginFragment.newInstance());
            ft.commit();
        }
        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.signing_in));
        mDialog.setCancelable(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLoginClicked(String email, String password) {
        mDialog.show();
    }

    public void showBaseURLDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("For Jon");

        // Set up the input
        final EditText input = new EditText(this);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Statics.BASE_URL = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
