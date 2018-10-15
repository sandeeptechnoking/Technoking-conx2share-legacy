package com.conx2share.conx2share.ui.sign_up;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.CheckPromoCodeAsync;
import com.conx2share.conx2share.async.UpdateUserToPromoUserAsync;
import com.conx2share.conx2share.model.ApiUser;
import com.conx2share.conx2share.model.AuthUser;
import com.conx2share.conx2share.model.Creds;
import com.conx2share.conx2share.model.DeviceAttributes;
import com.conx2share.conx2share.model.ErrorMessage;
import com.conx2share.conx2share.model.PromoCodeHolder;
import com.conx2share.conx2share.model.PromoCodeWrapper;
import com.conx2share.conx2share.model.ResponseMessage;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetUserResponse;
import com.conx2share.conx2share.network.models.response.SignUpResponse;
import com.conx2share.conx2share.ui.base.BaseDatePickerFragment;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.discover.DiscoverActivity;
import com.conx2share.conx2share.util.GooglePlayServicesUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.metova.slim.annotation.Layout;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.inject.Inject;

//import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

@Layout(R.layout.fragment_sign_up)
public class SignUpFragment extends BaseFragment {

    public static final String GCM_TOKEN = "32936937907";

    public static String TAG = SignUpFragment.class.getSimpleName();

    @Inject
    NetworkClient mNetworkClient;

    @Inject
    PreferencesUtil mPreferenceUtil;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @Inject
    GooglePlayServicesUtil mGooglePlayServicesUtil;

    @InjectView(R.id.sign_up_email)
    EditText mSignUpEmail;

    @InjectView(R.id.sign_up_username)
    EditText mSignUpUsername;

    @InjectView(R.id.sign_up_password)
    EditText mSignUpPassword;

    @InjectView(R.id.sign_up_confirm_password)
    EditText mSignUpPasswordConfirm;

    @InjectView(R.id.sign_up_first_name)
    EditText mSignUpFirstName;

    @InjectView(R.id.sign_up_last_name)
    EditText mSignUpLastName;

    @InjectView(R.id.terms_and_conditions_checkbox)
    CheckBox mCheckBox;

    @InjectView(R.id.sign_up_ive_read_and_agree)
    TextView mTermsAndConditionsTextView;

    @InjectView(R.id.sign_up_promo_code)
    EditText mSignUpPromoCode;

    @InjectView(R.id.i_am_a_business_checkbox)
    CheckBox mBusinessCheckbox;

    @InjectView(R.id.i_am_a_business_text)
    TextView mBusinessText;

    @InjectView(R.id.sign_up_birthday)
    EditText mSignUpBirthday;

    private GoogleCloudMessaging mGoogleCloudMessaging;

    private String mRegistrationId;

    private ProgressDialog mProgressDialog;

    private String mPromoCode;

    private ApiUser mApiUser;

    private CheckPromoCodeAsync mCheckPromoCodeAsync;

    private UpdateUserToPromoUserAsync mUpdateUserToPromoUserAsync;

    private boolean mNeedToUpdateUserToPromoUser;

    private int mYear;

    private int mMonth;

    private int mDay;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            Pattern patternTermsOfService = Pattern.compile(getActivity().getString(R.string.terms_of_service));
            Pattern patternPrivacyPolicy = Pattern.compile(getActivity().getString(R.string.privacy_policy));

            TransformFilter termsOfServiceTransformFilter = (match, url) ->
                    match.group(0).replace(getString(R.string.terms_of_service), "terms_of_service") + "?locale=" + Locale.getDefault().getLanguage();

            TransformFilter privacyPolicyTransformFilter = (match, url) ->
                    match.group(0).replace(getString(R.string.privacy_policy), "privacy_policy") + "?locale=" + Locale.getDefault().getLanguage();

            String scheme = "https://conx2share.com/static/";
            Linkify.addLinks(mTermsAndConditionsTextView, patternTermsOfService, scheme, null, termsOfServiceTransformFilter);
            Linkify.addLinks(mTermsAndConditionsTextView, patternPrivacyPolicy, scheme, null, privacyPolicyTransformFilter);
            mTermsAndConditionsTextView.setLinkTextColor(Color.parseColor("#1A9BFC"));

            mBusinessText.setOnClickListener(v -> mBusinessCheckbox.setChecked(!mBusinessCheckbox.isChecked()));

            mSignUpBirthday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getActivity() != null) {
                        DialogFragment datePickerFragment = new BaseDatePickerFragment();
                        datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                    }
                }
            });
        }
    }

    @OnClick(R.id.sign_up_submit_button)
    public void signUpSubmit() {
        signUp();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSignUpLastName.getWindowToken(), 0);
    }

    private void signUp() {

        String email = mSignUpEmail.getText().toString();
        String username = mSignUpUsername.getText().toString();
        String password = mSignUpPassword.getText().toString();
        String passwordConfirm = mSignUpPasswordConfirm.getText().toString();
        String firstName = mSignUpFirstName.getText().toString();
        String lastName = mSignUpLastName.getText().toString();
        String birthday = mSignUpBirthday.getText().toString();
        mPromoCode = mSignUpPromoCode.getText().toString();
        mApiUser = new ApiUser(email, password, firstName, lastName, passwordConfirm, username, Locale.getDefault().getLanguage());
        mApiUser.setBirthday(birthday);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.signup_dialog));
        mProgressDialog.show();

        if (!mCheckBox.isChecked()) {
            Snackbar.with(getActivity()).text(R.string.please_accept_the_terms_of_service).show(getActivity());
            mProgressDialog.cancel();
        } else if (anyFieldIsBlank(email, username, password, passwordConfirm, firstName, lastName)) {
            Snackbar.with(getActivity()).text(R.string.please_fill_out_all_fields_text).show(getActivity());
            mProgressDialog.cancel();
        } else if (!email.trim().matches("\\b[a-zA-Z0-9._%+-]+@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,4}\\b")) {
            Snackbar.with(getActivity()).text(getString(R.string.please_enter_a_valid_email_address)).type(SnackbarType.MULTI_LINE).show(getActivity());
            mProgressDialog.cancel();
        } else if (username.trim().replaceAll("[a-zA-Z0-9]", "").length() > 0) {
            Snackbar.with(getActivity()).text(getString(R.string.please_use_only_letters_and_numbers_for_your_username)).type(SnackbarType.MULTI_LINE).show(getActivity());
            mProgressDialog.cancel();
        } else if (username.trim().length() < 2 || username.trim().length() > 20) {
            Snackbar.with(getActivity()).text(R.string.please_enter_in_a_username_that_is_between_two_and_twenty_characters).type(SnackbarType.MULTI_LINE).show(getActivity());
            mProgressDialog.cancel();
        } else if (password.length() < 8) {
            Snackbar.with(getActivity()).text(R.string.password_length).show(getActivity());
            mProgressDialog.cancel();
        } else if (!password.trim().equals(passwordConfirm.trim())) {
            Snackbar.with(getActivity()).text(R.string.passwords_dont_match).show(getActivity());
            mProgressDialog.cancel();
        } else if (TextUtils.isEmpty(birthday)){
            Snackbar.with(getActivity()).text(getString(R.string.please_enter_in_a_birthday)).show(getActivity());
            mProgressDialog.cancel();
        } else {
            if (mSignUpPromoCode.getText().toString().trim().equals("")) {
                Log.d(TAG, "No promo code entered, signing up like normal");
                new SignUpAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Creds(mApiUser));
            } else {
                Log.d(TAG, "Promo code entered, need to check if it's valid");
                checkPromoCode(mPromoCode);
            }
        }
    }

    private boolean anyFieldIsBlank(String email, String username, String password, String passwordConfirm, String firstName, String lastName) {
        return email.trim().equals("") || username.trim().equals("") || password.trim().equals("") || passwordConfirm.trim().equals("") || firstName.trim().equals("") || lastName.trim().equals("");
    }

    // TODO - Think of a way to consolidate this in with the version in PromoCodeStrategy...since for SignUp we were using Snackbars, had the progress dialog, had a different invalid message, and needed to kick off the sign up task after a valid promo code I haven't done that yet ~ Sarah
    protected void checkPromoCode(final String promoCode) {
        if (mCheckPromoCodeAsync != null) {
            Log.w(TAG, "Already checking if promo code is valid, new request will be ignored");
            return;
        }

        mCheckPromoCodeAsync = new CheckPromoCodeAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<ResponseMessage> result) {
                mProgressDialog.cancel();
                if (result != null && result.getResource() != null && result.getResource().getMessage() != null) {
                    switch (result.getResource().getMessage().toLowerCase()) {
                        case "valid":
                            mNeedToUpdateUserToPromoUser = true;
                            new SignUpAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Creds(mApiUser));
                            break;
                        case "invalid":
                            if (getActivity() != null) {
                                mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.the_promo_code_you_entered_is_invalid_sign_up);
                            }
                            break;
                        default:
                            Log.wtf(TAG, "Unknown message response from the server: " + result.getResource().getMessage());
                            launchUnableToCheckIfPromoCodeIsValidSnackbar(promoCode);
                            break;
                    }
                } else {
                    Log.wtf(TAG, "Unable to check promo code, result from server was null");
                    launchUnableToCheckIfPromoCodeIsValidSnackbar(promoCode);
                }
                mCheckPromoCodeAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                mProgressDialog.cancel();
                Log.d(TAG, "Error checking if promo code is valid", error);
                launchUnableToCheckIfPromoCodeIsValidSnackbar(promoCode);
                mCheckPromoCodeAsync = null;
            }
        }.executeInParallel(promoCode);
    }

    private void launchUnableToCheckIfPromoCodeIsValidSnackbar(final String promoCode) {
        if (getActivity() != null) {
            mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_check_if_promo_code_is_valid, R.string.retry, snackbar -> {
                checkPromoCode(promoCode);
                SnackbarManager.dismiss();
            });
        }
    }

    public void onEventMainThread(BaseDatePickerFragment.OnDateSelectedEvent event) {
        Log.d(TAG, "Received a date selected event");
        if(getActivity() != null) {
            mYear = event.getYear();
            mMonth = event.getMonth();
            mDay = event.getDay();
            mSignUpBirthday.setText(String.format("%02d", event.getDay()) + "-" + String.format("%02d", event.getMonth()) + "-" + event.getYear());
        }
    }

    // TODO - Another fail...need to consolidate this with the version in PromoCodeStrategy...this one is slightly different because of a silent fail and progress dialogs as design for SignUp
    protected void updateUserToPromoUser(final int userId, final PromoCodeWrapper promoCodeWrapper) {
        if (mUpdateUserToPromoUserAsync != null) {
            Log.w(TAG, "Already updating user to promo user, new request will be ignored");
            return;
        }

        mUpdateUserToPromoUserAsync = new UpdateUserToPromoUserAsync(getActivity(), userId) {
            @Override
            protected void onSuccess(Result<GetUserResponse> result) {
                mProgressDialog.cancel();
                Log.d(TAG, "User with id " + userId + " was updated to a promo user");
                AuthUser authUser = mPreferenceUtil.getAuthUser();
                authUser.setPromoUser(true);
                authUser.setPlan("plus");
                mPreferenceUtil.setAuthUser(authUser);
                Log.d(TAG, "mPreferenceUtil.getAuthUser().isPromoUser(): " + mPreferenceUtil.getAuthUser().isPromoUser());
                startDiscoverActivity();
                mUpdateUserToPromoUserAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Unable to update user to promo user :'( ", error);
                mProgressDialog.cancel();
                startDiscoverActivity();
                mUpdateUserToPromoUserAsync = null;
            }
        }.executeInParallel(promoCodeWrapper);
    }

    private void startDiscoverActivity() {
        Log.d(TAG, "Starting discover activity...");

        if (mBusinessCheckbox.isChecked()) {
            if(mPreferenceUtil.getAuthUser().isOverEighteen()) {
                Intent businessIntent = new Intent(getActivity(), BusinessSignUpWebActivity.class);
                businessIntent.putExtra(BusinessSignUpWebActivity.BUSINESS_USER_ID, mPreferenceUtil.getAuthUser().getId());
                getActivity().finish();
                startActivity(businessIntent);
            } else {
                Toast.makeText(getActivity(), getString(R.string.you_must_be_over_eighteen_to_register_a_business), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DiscoverActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().finish();
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getActivity(), DiscoverActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().finish();
            startActivity(intent);
        }
    }

    public class SignUpAsync extends AsyncTask<Creds, Void, Result<SignUpResponse>> {

        @Override
        protected Result<SignUpResponse> doInBackground(Creds... params) {
            if (getActivity() != null) {
                if (mGooglePlayServicesUtil.checkPlayServices(getActivity(), getActivity())) {
                    Integer appVersion = mPreferenceUtil.getGCMTokenAppVersion();
                    String token = mPreferenceUtil.getDeviceToken();
                    if ((token == null) || ((getAppVersion(getActivity()) != appVersion))) {

                        try {
                            mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(getActivity().getApplicationContext());
                            mRegistrationId = mGoogleCloudMessaging.register(GCM_TOKEN);
                            Log.d(TAG, "Id " + mRegistrationId + " registered.");
                            mPreferenceUtil.setGCMTokenAppVersion(getAppVersion(getActivity()));
                            mPreferenceUtil.setDeviceToken(mRegistrationId);
                        } catch (IOException e) {
                            Log.e(TAG, "GCM registration error");
                        }
                    } else {
                        Log.d(TAG, "regId already registered");
                    }
                } else {
                    Log.i(TAG, "No valid Google Play Services APK found.");
                }

                DeviceAttributes deviceAttributes = new DeviceAttributes("android", mPreferenceUtil.getDeviceToken());
                params[0].getApiUser().setDevice_attributes(deviceAttributes);
            } else {
                Log.w(TAG, "Activity was null");
            }

//            return mNetworkClient.signUp(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Result<SignUpResponse> signUpResponseResult) {
            super.onPostExecute(signUpResponseResult);

            if (getActivity() != null) {
                if (signUpResponseResult != null && signUpResponseResult.getResource() != null && signUpResponseResult.getError() == null) {
                    mPreferenceUtil.setEmail(mSignUpEmail.getText().toString().trim());
                    mPreferenceUtil.setPassword(mSignUpPassword.getText().toString().trim());
                    mPreferenceUtil.setAuthToken(signUpResponseResult.getResource().getAuthUser().getAuthenticationToken());
                    mPreferenceUtil.setAuthUser(signUpResponseResult.getResource().getAuthUser());

                    if (mNeedToUpdateUserToPromoUser) {
                        Log.d(TAG, "Sign up successful, but now we need to upgrade user to a promo user");
                        PromoCodeWrapper promoCodeWrapper = new PromoCodeWrapper(mPreferenceUtil.getAuthUser().getId(), new PromoCodeHolder(mPromoCode));
                        updateUserToPromoUser(mPreferenceUtil.getAuthUser().getId(), promoCodeWrapper);
                    } else {
                        startDiscoverActivity();
                    }
                } else {

                    if (signUpResponseResult != null && signUpResponseResult.getError() != null && signUpResponseResult.getError().getResponse() != null) {
                        Log.d(TAG, "signUpResponseResult.getError().getResponse().getStatus(): " + signUpResponseResult.getError().getResponse().getStatus());
                        if (signUpResponseResult.getError().getResponse().getStatus() == 422) {
                            ErrorMessage error = (ErrorMessage) signUpResponseResult.getError().getBodyAs(ErrorMessage.class);
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
                }
                mProgressDialog.cancel();
                mNeedToUpdateUserToPromoUser = false;
            }
        }
    }
}
