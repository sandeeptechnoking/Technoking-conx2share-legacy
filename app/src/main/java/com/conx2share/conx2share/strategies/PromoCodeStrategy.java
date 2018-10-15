package com.conx2share.conx2share.strategies;

import com.google.inject.Inject;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.CheckPromoCodeAsync;
import com.conx2share.conx2share.async.UpdateUserToPromoUserAsync;
import com.conx2share.conx2share.model.AuthUser;
import com.conx2share.conx2share.model.PromoCodeHolder;
import com.conx2share.conx2share.model.PromoCodeWrapper;
import com.conx2share.conx2share.model.ResponseMessage;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetUserResponse;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.SnackbarManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import retrofit.RetrofitError;
import roboguice.RoboGuice;


public class PromoCodeStrategy {

    public static final String TAG = PromoCodeStrategy.class.getSimpleName();

    @Inject
    SnackbarUtil mSnackbarUtil;

    @Inject
    PreferencesUtil mPreferencesUtil;

    private Activity mActivity;

    private String mPromoCode;

    private int mUserId;

    private CheckPromoCodeAsync mCheckPromoCodeAsync;

    private UpdateUserToPromoUserAsync mUpdateUserToPromoUserAsync;

    public PromoCodeStrategy(Activity activity, int userId) {
        mActivity = activity;
        mUserId = userId;
        RoboGuice.injectMembers(activity.getApplicationContext(), this);
    }

    public void launchPromoDialog() {
        if (mActivity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            LayoutInflater layoutInflater = mActivity.getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.dialog_promo_code, null);
            builder.setView(view);
            final EditText promoCodeEditText = (EditText) view.findViewById(R.id.promo_code_edit_text);
            builder.setPositiveButton(mActivity.getString(R.string.ok), (dialog, id) -> {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(promoCodeEditText.getWindowToken(), 0);
                mPromoCode = promoCodeEditText.getText().toString();
                Log.d(TAG, "Promo code: " + mPromoCode);
                checkPromoCode(mPromoCode);
                dialog.cancel();
            });
            builder.setNegativeButton(mActivity.getString(R.string.cancel), (dialog, whichButton) -> {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(promoCodeEditText.getWindowToken(), 0);
                dialog.cancel();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void launchValidPromoCodeDialog() {
        if (mActivity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(mActivity.getString(R.string.valid_promo_code));
            builder.setMessage(mActivity.getString(R.string.promo_upgrade_description));
            builder.setNegativeButton(mActivity.getString(R.string.ok),
                    (dialog, whichButton) -> {
                        dialog.cancel();
                    });
            builder.show();
        }
    }

    private void launchInvalidPromoCodeDialog() {
        if (mActivity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(mActivity.getString(R.string.invalid_promo_code));
            builder.setMessage(mActivity.getString(R.string.the_promo_code_you_entered_is_invalid));
            builder.setNegativeButton(mActivity.getString(R.string.ok),
                    (dialog, whichButton) -> {
                        dialog.cancel();
                    });
            builder.show();
        }
    }

    private void launchUnableToCheckIfPromoCodeIsValidSnackbar(final String promoCode) {
        if (mActivity != null) {
            mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_check_if_promo_code_is_valid, R.string.retry, snackbar -> {
                checkPromoCode(promoCode);
                SnackbarManager.dismiss();
            });
        }
    }

    protected void checkPromoCode(final String promoCode) {
        if (mCheckPromoCodeAsync != null) {
            Log.w(TAG, "Already checking promo code, new request will be ignored");
            return;
        }

        mCheckPromoCodeAsync = new CheckPromoCodeAsync(mActivity) {
            @Override
            protected void onSuccess(Result<ResponseMessage> result) {
                if (result != null && result.getResource() != null && result.getResource().getMessage() != null) {
                    switch (result.getResource().getMessage().toLowerCase()) {
                        case "valid":
                            PromoCodeWrapper promoCodeWrapper = new PromoCodeWrapper(mUserId, new PromoCodeHolder(promoCode));
                            updateUserToPromoUser(mUserId, promoCodeWrapper);
                            break;
                        case "invalid":
                            launchInvalidPromoCodeDialog();
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
                Log.e(TAG, "An error occurred while checking if promo code is valid", error);
                launchUnableToCheckIfPromoCodeIsValidSnackbar(promoCode);
                mCheckPromoCodeAsync = null;
            }
        }.executeInParallel(promoCode);
    }

    protected void updateUserToPromoUser(final int userId, final PromoCodeWrapper promoCodeWrapper) {
        if (mUpdateUserToPromoUserAsync != null) {
            Log.w(TAG, "Already updating user to promo user, new request will be ignored");
            return;
        }

        mUpdateUserToPromoUserAsync = new UpdateUserToPromoUserAsync(mActivity, userId) {
            @Override
            protected void onSuccess(Result<GetUserResponse> result) {
                Log.d(TAG, "User with id " + userId + " was updated to a promo user");
                launchValidPromoCodeDialog();
                // To prevent downgrading the user's plan in preference util
                if (mPreferencesUtil.getAuthUser().getPlan().equals("free")) {
                    AuthUser authUser = mPreferencesUtil.getAuthUser();
                    authUser.setPromoUser(true);
                    authUser.setPlan("plus");
                    mPreferencesUtil.setAuthUser(authUser);
                } else {
                    Log.w(TAG, "User already has a plus or higher subscription, we don't want to overwrite what is in preference util");
                }
                Log.d(TAG, "mPreferencesUtil.getAuthUser().isPromoUser(): " + mPreferencesUtil.getAuthUser().isPromoUser());

                mUpdateUserToPromoUserAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Error updating user to promo user", error);
                if (mActivity != null) {
                    mSnackbarUtil.showSnackBarWithAction(mActivity, R.string.unable_to_update_user_to_promo_user, R.string.retry, snackbar -> {
                        updateUserToPromoUser(userId, promoCodeWrapper);
                        SnackbarManager.dismiss();
                    });
                }
                mUpdateUserToPromoUserAsync = null;
            }
        }.executeInParallel(promoCodeWrapper);
    }
}