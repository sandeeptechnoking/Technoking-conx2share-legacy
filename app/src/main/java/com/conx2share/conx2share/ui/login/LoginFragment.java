package com.conx2share.conx2share.ui.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.conx2share.conx2share.BuildConfig;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.ApiUser;
import com.conx2share.conx2share.model.AuthUser;
import com.conx2share.conx2share.model.Creds;
import com.conx2share.conx2share.model.DeviceAttributes;
import com.conx2share.conx2share.model.ErrorMessage;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.reusableviews.InputFormView;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.feed.FeedActivity;
import com.conx2share.conx2share.ui.web_view.WebViewActivity;
import com.conx2share.conx2share.ui.web_view.WebViewFragment;
import com.conx2share.conx2share.util.GooglePlayServicesUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.Statics;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.yqritc.scalablevideoview.ScalableType;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;

//import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginFragment extends BaseFragment {

    public static final String GCM_TOKEN = "768667733695";//"32936937907";
    public static final String POLICY_LINK = "https://wordpress.conx2share.com/privacy-policy/";
    public static final String TERMS_LINK = "https://wordpress.conx2share.com/terms-of-service/";

    public static final String FROM_LOGIN_KEY = "fromLogin";

    public static String TAG = LoginFragment.class.getSimpleName();

    @Inject
    PreferencesUtil preferencesUtil;

    @Inject
    NetworkClient networkClient;

    @Inject
    GooglePlayServicesUtil mGooglePlayServicesUtil;

    @InjectView(R.id.login_email)
    InputFormView mLoginEmail;

    @InjectView(R.id.login_password)
    InputFormView mLoginPassword;

    @InjectView(R.id.signup_email)
    public InputFormView emailFormView;

    @InjectView(R.id.signup_first_name)
    public InputFormView firstNameFormView;

    @InjectView(R.id.signup_last_name)
    public InputFormView lastNameFormView;

    @InjectView(R.id.signup_username)
    public InputFormView usernameFormView;

    @InjectView(R.id.signup_password)
    public InputFormView passwordFormView;

    @InjectView(R.id.login_container)
    public LinearLayout loginContainer;

    @InjectView(R.id.signup_container)
    public LinearLayout signupContainer;

    @InjectView(R.id.buttons_container)
    public LinearLayout buttonContainer;

    @InjectView(R.id.video_view)
    ScalableVideoView videoView;

    @InjectView(R.id.singup_terms)
    TextView termsTv;

    private GoogleCloudMessaging mGoogleCloudMessaging;

    private String mRegistrationId;

    private ProgressDialog mProgressDialog;

    private boolean mNeedToUpdateUserToPromoUser;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    // TODO: Duplicated in SignUpActivity.  refactor into utility class
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            mProgressDialog = new ProgressDialog(getActivity());
        }

        // Check to see if we have a saved user
        if (preferencesUtil.getAuthToken() != null && preferencesUtil.getEmail() != null && preferencesUtil.getPassword() != null) {
            startActivity(new Intent(getActivity(), FeedActivity.class).putExtra(FROM_LOGIN_KEY, true));
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else {
            mLoginEmail.getEditText().setText(preferencesUtil.getLastEmail());
        }

        setupBackHandler();
        makeTermsAndConditionalLinks();
        playVideo();
        checkPlayServices();
    }

    private void makeTermsAndConditionalLinks() {
        termsTv.setMovementMethod(LinkMovementMethod.getInstance());
        Spannable span = Spannable.Factory.getInstance().newSpannable(getString(R.string.text_terms));
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                clickOnPolicy();
            }
        }, 45, 60, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                clickOnTerms();

            }
        }, 63, getString(R.string.text_terms).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsTv.setText(span);
    }

    private void clickOnPolicy() {
        startActivity(new Intent(getActivity(), WebViewActivity.class)
                .putExtra(WebViewFragment.EXTRA_WEB_URI, POLICY_LINK)
                .putExtra(WebViewActivity.EXTRA_SCREEN_TITLE, getString(R.string.title_policy)));
    }

    private void clickOnTerms() {
        startActivity(new Intent(getActivity(), WebViewActivity.class)
                .putExtra(WebViewFragment.EXTRA_WEB_URI, TERMS_LINK)
                .putExtra(WebViewActivity.EXTRA_SCREEN_TITLE, getString(R.string.title_term)));
    }

    private void playVideo() {
        try {
            videoView.setRawData(R.raw.video);
        } catch (IOException ioe) {
            //handle error
        }
        videoView.setScalableType(ScalableType.CENTER_CROP);
        videoView.requestFocus();
        videoView.prepareAsync(mp -> videoView.start());
        videoView.setOnCompletionListener(mp -> videoView.start());
    }

    private void setupBackHandler() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && buttonContainer.getVisibility() == View.GONE) {
                signupContainer.setVisibility(View.GONE);
                loginContainer.setVisibility(View.GONE);
                buttonContainer.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });
    }

    @OnClick(R.id.login_button)
    public void showLogin() {
        loginContainer.setVisibility(View.VISIBLE);
        buttonContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.login_login_button)
    public void onLoginClicked() {
        String email = mLoginEmail.getEditText().getText().toString();
        String password = mLoginPassword.getEditText().getText().toString();

        if (!validateCredentials(email, password)) {
            SnackbarManager.show(Snackbar.with(getActivity())
                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                            .type(SnackbarType.MULTI_LINE)
                            .text(getInvalidLoginFieldTextResource(email, password))
                    , getActivity());
            return;
        }

        SnackbarManager.dismiss();

        preferencesUtil.setEmail(email);
        preferencesUtil.setPassword(password);

        ApiUser apiUser = new ApiUser(email, password, null, Locale.getDefault().getLanguage());
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getActivity().getString(R.string.logging_in));
        mProgressDialog.show();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mLoginPassword.getWindowToken(), 0);
        loginAsync(new Creds(apiUser));
    }

    @OnClick(R.id.sign_up_button)
    public void showSignup() {
        signupContainer.setVisibility(View.VISIBLE);
        buttonContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.signup_signup_button)
    public void signUp() {

        String email = emailFormView.getEditText().getText().toString();
        String username = usernameFormView.getEditText().getText().toString();
        String password = passwordFormView.getEditText().getText().toString();
        String passwordConfirm = passwordFormView.getEditText().getText().toString();
        String firstName = firstNameFormView.getEditText().getText().toString();
        String lastName = lastNameFormView.getEditText().getText().toString();
//        String birthday = mSignUpBirthday.getText().toString();
//        mPromoCode = mSignUpPromoCode.getText().toString();
        ApiUser mApiUser = new ApiUser(email, password, firstName, lastName, passwordConfirm, username, Locale.getDefault().getLanguage());
//        mApiUser.setBirthday(birthday);
        Activity activity = getActivity();

        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setMessage(getString(R.string.signup_dialog));
        mProgressDialog.show();

//        if (!mCheckBox.isChecked()) {
//            Snackbar.with(getActivity()).text(R.string.please_accept_the_terms_of_service).show(getActivity());
//            mProgressDialog.cancel();
//        } else
        if (anyFieldIsBlank(email, username, password, passwordConfirm, firstName, lastName)) {
            Snackbar.with(activity).text(R.string.please_fill_out_all_fields_text).show(activity);
            mProgressDialog.cancel();
        } else if (!email.trim().matches("\\b[a-zA-Z0-9._%+-]+@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,4}\\b")) {
            Snackbar.with(activity).text(getString(R.string.please_enter_a_valid_email_address)).type(SnackbarType.MULTI_LINE).show(activity);
            mProgressDialog.cancel();
        } else if (username.trim().replaceAll("[a-zA-Z0-9]", "").length() > 0) {
            Snackbar.with(activity).text(getString(R.string.please_use_only_letters_and_numbers_for_your_username)).type(SnackbarType.MULTI_LINE).show(activity);
            mProgressDialog.cancel();
        } else if (username.trim().length() < 2 || username.trim().length() > 20) {
            Snackbar.with(activity).text(R.string.please_enter_in_a_username_that_is_between_two_and_twenty_characters).type(SnackbarType.MULTI_LINE).show(activity);
            mProgressDialog.cancel();
        } else if (password.length() < 8) {
            Snackbar.with(activity).text(R.string.password_length).show(activity);
            mProgressDialog.cancel();
        } else if (!password.trim().equals(passwordConfirm.trim())) {
            Snackbar.with(activity).text(R.string.passwords_dont_match).show(activity);
            mProgressDialog.cancel();
//        } else if (TextUtils.isEmpty(birthday)){
//            Snackbar.with(getActivity()).text(getString(R.string.please_enter_in_a_birthday)).show(getActivity());
//            mProgressDialog.cancel();
        } else {
//            if (mSignUpPromoCode.getText().toString().trim().equals("")) {
//                Log.d(TAG, "No promo code entered, signing up like normal");
            signUpAsync(new Creds(mApiUser));
//            new SignUpAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Creds(mApiUser));
//            } else {
//                Log.d(TAG, "Promo code entered, need to check if it's valid");
//                checkPromoCode(mPromoCode);
//            }
        }
    }

    private boolean anyFieldIsBlank(String email, String username, String password, String passwordConfirm, String firstName, String lastName) {
        return email.trim().equals("") || username.trim().equals("") || password.trim().equals("") || passwordConfirm.trim().equals("") || firstName.trim().equals("") || lastName.trim().equals("");
    }

    private int getInvalidLoginFieldTextResource(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            return R.string.email_cant_be_blank;
        } else if (TextUtils.isEmpty(password) || password.length() < 8) {
            return R.string.password_length;
        } else {
            return R.string.generic_error_message;
        }
    }

    private boolean validateCredentials(String email, String password) {
        return !(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || password.length() < 8);
    }

    @OnClick(R.id.forgot_password_button)
    public void onForgotPasswordClicked() {
        String url = Statics.BASE_URL + "/api/users/password/new";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void buildForgotPasswordDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle(getActivity().getString(R.string.reset_password));
        dialog.setContentView(R.layout.dialog_password_reset);
        final EditText input = (EditText) dialog.findViewById(R.id.password_reset_dialog_input);
        Button button = (Button) dialog.findViewById(R.id.password_reset_dialog_button);
        button.setOnClickListener(v -> {
            resetPasswordAsync(input.getText().toString());
            dialog.cancel();
        });
        dialog.show();
    }

    private void buildFailedPasswordResetDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getActivity().getString(R.string.cant_reset_password));
        alert.setMessage(getActivity().getString(R.string.please_enter_your_email_address));
        alert.setNegativeButton(getString(R.string.ok), (dialog, whichButton) -> {
        });
        alert.show();
    }

    private void buildSuccessPasswordResetDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getActivity().getString(R.string.reset_password_email_sent));
        alert.setNegativeButton(getString(R.string.ok), (dialog, whichButton) -> {
        });
        alert.show();
    }

    private void checkPlayServices() {
        Activity activity = getActivity();
        if (activity != null) {
            if (mGooglePlayServicesUtil.checkPlayServices(activity, activity)) {
                Integer appVersion = preferencesUtil.getGCMTokenAppVersion();
                String token = preferencesUtil.getDeviceToken();
                if ((token == null) || ((getAppVersion(getActivity()) != appVersion))) {

                    try {
                        mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(getActivity().getApplicationContext());
                        mRegistrationId = mGoogleCloudMessaging.register(GCM_TOKEN);
                        Log.d(TAG, "Id " + mRegistrationId + " registered.");
                        preferencesUtil.setGCMTokenAppVersion(getAppVersion(activity));
                        preferencesUtil.setDeviceToken(mRegistrationId);
                    } catch (IOException e) {
                        Log.e(TAG, "GCM registration error");
                    }
                } else {
                    Log.d(TAG, "regId already registered");
                }
            } else {
                Log.i(TAG, "No valid Google Play Services APK found.");
            }
        }
    }

    public interface LoginCallback {
        void onLoginClicked(String email, String password);
    }

    private void resetPasswordAsync(String email) {

        addSubscription(networkClient.resetPassword(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getGroupInviteActionResponse -> {
                    if (getActivity() != null) {
                        buildSuccessPasswordResetDialog();
                    }
                }, throwable -> {
                    if (getActivity() != null) {
                        buildFailedPasswordResetDialog();
                    }
                }));
    }

    public void loginAsync(Creds creds) {

        addSubscription(networkClient.signIn(creds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(signUpResponse -> {
                    preferencesUtil.setAuthToken(signUpResponse.getAuthUser().getAuthenticationToken());
                    preferencesUtil.setAuthUser(signUpResponse.getAuthUser());
                    preferencesUtil.setLastEmail(signUpResponse.getAuthUser().getEmail());
                    if (!BuildConfig.DEBUG) {
                        AuthUser authUser = signUpResponse.getAuthUser();
                        Crashlytics.setUserIdentifier(String.valueOf(authUser.getId()));
                        Crashlytics.setUserName(authUser.getFirstName() + " " + authUser.getLastName());
                        Crashlytics.setUserEmail(authUser.getEmail());
                    }
                    if (getActivity() != null) {
                        mProgressDialog.cancel();
                        startActivity(new Intent(getActivity(), FeedActivity.class).putExtra(FROM_LOGIN_KEY, true));
                        getActivity().finish();
                    }
                }, throwable -> {
                    if (getActivity() != null) {
                        mProgressDialog.cancel();
                        if (throwable.getMessage().contains("401")) {
                            mSnackbarUtil.displaySnackBar(getActivity(), R.string.invalid_user_name_or_password);
                        } else {
                            mSnackbarUtil.displaySnackBar(getActivity(), R.string.login_failed);
                        }
                    }
                    // Remove email & password for future login
                    preferencesUtil.clearPreferences();
                }));
    }


    private void signUpAsync(Creds creds) {

        Activity activity = getActivity();
        if (activity != null) {
            creds.getApiUser().setDevice_attributes(new DeviceAttributes("android", InstanceID.getInstance(activity).getId()));
        }

        addSubscription(networkClient.signUp(creds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> mProgressDialog.cancel())
                .subscribe(signUpResponse -> {
                    preferencesUtil.setEmail(emailFormView.getEditText().getText().toString().trim());
                    preferencesUtil.setPassword(passwordFormView.getEditText().getText().toString().trim());
                    preferencesUtil.setAuthToken(signUpResponse.getAuthUser().getAuthenticationToken());
                    preferencesUtil.setAuthUser(signUpResponse.getAuthUser());

                    if (mNeedToUpdateUserToPromoUser) {
                        Log.d(TAG, "Sign up successful, but now we need to upgrade user to a promo user");
//                        PromoCodeWrapper promoCodeWrapper = new PromoCodeWrapper(preferencesUtil.getAuthUser().getId(), new PromoCodeHolder(mPromoCode));
//                        updateUserToPromoUser(preferencesUtil.getAuthUser().getId(), promoCodeWrapper);
                    } else {
                        startFeedActivity();
                    }
                }, throwable -> {
                    if (throwable instanceof RetrofitError) {
                        RetrofitError retrofitError = (RetrofitError) throwable;
                        if (retrofitError.getResponse().getStatus() == 422) {
                            ErrorMessage error = (ErrorMessage) retrofitError.getBodyAs(ErrorMessage.class);
                            if (error != null && error.getMessage() != null && !error.getMessage().equals("")) {
                                if (error.getMessage().toLowerCase().equals("username has already been taken")) {
                                    Snackbar.with(getActivity()).text(getString(R.string.user_name_is_taken)).show(getActivity());
                                } else if (error.getMessage().toLowerCase().equals("email has already been taken")) {
                                    Snackbar.with(getActivity()).text(getString(R.string.email_has_already_been_taken)).show(getActivity());
                                } else {
                                    Log.w(TAG, "Unknown error message in response body: " + error.getMessage());
                                    Snackbar.with(getActivity()).text(getString(R.string.failed_to_sign_up)).show(getActivity());
                                }
                            } else {
                                Snackbar.with(getActivity()).text(getString(R.string.failed_to_sign_up)).show(getActivity());
                            }
                        } else {
                            Snackbar.with(getActivity()).text(getText(R.string.failed_to_sign_up)).show(getActivity());
                        }
                    } else {
                        Snackbar.with(getActivity()).text(getText(R.string.failed_to_sign_up)).show(getActivity());
                    }
                    mNeedToUpdateUserToPromoUser = false;
                }));
    }


    private void startFeedActivity() {

//        if (mBusinessCheckbox.isChecked()) {
//            if(preferencesUtil.getAuthUser().isOverEighteen()) {
//                Intent businessIntent = new Intent(getActivity(), BusinessSignUpWebActivity.class);
//                businessIntent.putExtra(BusinessSignUpWebActivity.BUSINESS_USER_ID, mPreferenceUtil.getAuthUser().getId());
//                getActivity().finish();
//                startActivity(businessIntent);
//            } else {
//                Toast.makeText(getActivity(), getString(R.string.you_must_be_over_eighteen_to_register_a_business), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getActivity(), DiscoverActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                getActivity().finish();
//                startActivity(intent);
//            }
//        } else {
        startActivity(new Intent(getActivity(), FeedActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        getActivity().finish();
    }

}
