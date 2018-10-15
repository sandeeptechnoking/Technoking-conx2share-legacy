package com.conx2share.conx2share.ui.settings;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.AuthUser;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.NetworkService;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.reusableviews.InputFormView;
import com.conx2share.conx2share.util.LogUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.RoboActionBarActivity;
import com.google.gson.JsonObject;
import com.google.inject.Key;

import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.inject.RoboInjector;
import roboguice.util.RoboContext;

public class ContactSupportActivity extends RoboActionBarActivity {

    @InjectView(R.id.contact_support_toolbar)
    public Toolbar toolbar;

    @InjectView(R.id.contact_support_name_input)
    public InputFormView nameInput;

    @InjectView(R.id.contact_support_email_input)
    public InputFormView emailInput;

    @InjectView(R.id.contact_support_message_input)
    public InputFormView messageInput;

    public FreshdeskService networkClient;

    @Inject
    public PreferencesUtil preferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_support);
        ButterKnife.bind(this);

        final RoboInjector injector = RoboGuice.getInjector(this);
        injector.injectMembersWithoutViews(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        setupNetwork();

        prefillValues();
    }

    private void setupNetwork() {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("https://conx2share.freshdesk.com")
                .build();
        adapter.setLogLevel(LogUtil.getRestAdapterLogLevel());
        networkClient = adapter.create(FreshdeskService.class);
    }

    private void prefillValues() {

        final AuthUser authUser = preferencesUtil.getAuthUser();
        nameInput.getEditText().setText(authUser.getFirstName() + " " + authUser.getLastName());
        emailInput.getEditText().setText(authUser.getEmail());
    }

    private void sendReport() {

        ProgressDialog progressDialog = ProgressDialog.show(this, "Sending your message", "");
        progressDialog.setCancelable(false);

        String name = nameInput.getEditText().getText().toString();
        String email = emailInput.getEditText().getText().toString();
        String message = messageInput.getEditText().getText().toString();

        if (nameInput.checkValid("Please Enter Your Name")
                && emailInput.checkValid("Please Enter Your Email")
                && emailInput.checkValidEmail()
                && emailInput.checkValid("Please Enter a Message")) {
            new Thread(() -> {
                sendContactSupport(name, email, message);
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(ContactSupportActivity.this)
                            .setTitle("Message Sent")
                            .setMessage("Thank you! We'll get back to you as soon as we can.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .show();
                });
            }).start();
        }

    }

    public Result<Response> sendContactSupport(String name, String email, String message) {
        try {
            String apiKey = "RWXSE05BWi8wYQvUQbK";
            String password = "X";
            String tokenToEncode = apiKey + ":" + password;
            String authHeader = "Basic " + Base64.encodeToString(tokenToEncode.getBytes(), Base64.NO_WRAP);

            String versionInfo = "OS: Android, Version: ";
            try {
                String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                versionInfo = versionInfo + versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", name);
            jsonObject.addProperty("email", email);
            jsonObject.addProperty("description", message);
            jsonObject.addProperty("status", 2);
            jsonObject.addProperty("priority", 1);
            jsonObject.addProperty("subject", "Support needed, " + versionInfo);

            return new Result<>(networkClient.sendContactSupport(authHeader, jsonObject));
        } catch (RetrofitError error) {
            return new Result<>(error);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_support, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.send_report) {
            sendReport();
        }
        return super.onOptionsItemSelected(item);
    }
}
