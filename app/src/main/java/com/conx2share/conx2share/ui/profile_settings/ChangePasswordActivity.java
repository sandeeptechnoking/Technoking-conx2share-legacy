package com.conx2share.conx2share.ui.profile_settings;

import com.conx2share.conx2share.ui.base.BaseRxAppCompatActivity;
import com.google.inject.Inject;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.ApiUser;
import com.conx2share.conx2share.model.Creds;
import com.conx2share.conx2share.model.DeviceAttributes;
import com.conx2share.conx2share.model.UpdatePasswordWrapper;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.network.models.response.SignUpResponse;
import com.conx2share.conx2share.util.PreferencesUtil;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

import butterknife.ButterKnife;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChangePasswordActivity extends BaseRxAppCompatActivity {

    private static String TAG = ChangePasswordActivity.class.getSimpleName();

    @Inject
    NetworkClient networkClient;

    @Inject
    PreferencesUtil preferencesUtil;

    @InjectView(R.id.old_password_input)
    EditText mOldPasswordInput;

    @InjectView(R.id.new_password_input)
    EditText mNewPasswordInput;

    @InjectView(R.id.confirm_new_password_input)
    EditText mConfirmNewPasswordInput;

    @InjectView(R.id.change_password_submit_button)
    Button mChangePasswordSubmitButton;

    private String mNewPassword;

    private User mUser;

    private String mAuthToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        mChangePasswordSubmitButton.setOnClickListener(v -> submitChangePassword());
    }

    public void submitChangePassword() {
        String oldPassword = mOldPasswordInput.getText().toString();
        mNewPassword = mNewPasswordInput.getText().toString();
        String confirmNewPassword = mConfirmNewPasswordInput.getText().toString();

        String email = preferencesUtil.getEmail();

        DeviceAttributes deviceAttributes = new DeviceAttributes("android", preferencesUtil.getDeviceToken());
        ApiUser apiUser = new ApiUser(email, oldPassword, deviceAttributes, Locale.getDefault().getLanguage());

        mUser = new User(mNewPassword);
        if (mNewPassword.equals(confirmNewPassword)) {
            mAuthToken = preferencesUtil.getAuthToken();
            preferencesUtil.setAuthToken(null);
            Log.i(TAG, "AUTH TOKEN: " + preferencesUtil.getAuthToken());
            loginAsync(new Creds(apiUser));
        } else {
            buildPasswordNotEqualDialog();
        }

    }

    public void buildLoginFailedDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.old_password_error));
        alert.setMessage(getString(R.string.the_old_password_you_entered_is_invalid));

        alert.setNegativeButton(getString(R.string.ok), (dialog, whichButton) -> {
        });

        alert.show();
    }

    public void buildPasswordNotEqualDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.change_password_error));
        alert.setMessage(getString(R.string.the_new_password_and_confirm_password_dont_match));

        alert.setNegativeButton(getString(R.string.ok), (dialog, whichButton) -> {
        });

        alert.show();
    }

    public void buildSuccessPasswordChangeDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.Success));
        alert.setMessage(getString(R.string.you_password_has_been_updated));

        alert.setNegativeButton(getString(R.string.ok), (dialog, whichButton) -> {
            finish();
        });

        alert.show();
    }

    public void buildFailedPasswordChangeDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.change_password_error));
        alert.setMessage(getString(R.string.please_try_again));

        alert.setNegativeButton(getString(R.string.ok), (dialog, whichButton) -> {
        });

        alert.show();
    }

    public void loginAsync(Creds creds) {

        addSubscription(networkClient.signIn(creds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(signUpResponse -> updatePasswordAsync(),
                        throwable -> {
                            preferencesUtil.setAuthToken(mAuthToken);
                            buildLoginFailedDialog();
                        }));
    }

    private void updatePasswordAsync() {
        UpdatePasswordWrapper updatePasswordWrapper = new UpdatePasswordWrapper(mAuthToken, mNewPassword, mUser.getId());
        addSubscription(networkClient.updatePassword(updatePasswordWrapper, String.valueOf(preferencesUtil.getAuthUser().getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getUserResponse -> {
                            buildSuccessPasswordChangeDialog();
                        },
                        throwable -> {
                            buildFailedPasswordChangeDialog();
                        }));

    }
}
