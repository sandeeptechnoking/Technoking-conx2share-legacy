package com.conx2share.conx2share.ui.subscription;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.SubscriptionsAdapter;
import com.conx2share.conx2share.async.PurchaseSubscriptionAsync;
import com.conx2share.conx2share.inappbilling.util.IabHelper;
import com.conx2share.conx2share.model.AuthUser;
import com.conx2share.conx2share.model.PurchaseSubscriptionWrapper;
import com.conx2share.conx2share.model.Receipt;
import com.conx2share.conx2share.model.Subscription;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetPurchaseResponse;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.util.Base64EncodedPublicKey;
import com.conx2share.conx2share.util.GooglePlayServicesUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.SnackbarManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

//import butterknife.InjectView;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class SubscriptionFragment extends BaseFragment implements SubscriptionsAdapter.SubscribeToPlanCallback, GooglePlayServicesUtil.GooglePlayServiceUtilCallbacks {

    private static final String TAG = SubscriptionFragment.class.getSimpleName();

    private static final int SUBSCRIPTION_REQUEST_CODE = 10001;

    private static final int USER_CANCELED_CODE = -1005;

    private final static String FREE_PLAN = "free";

    private final static String PLUS_PLAN = "plus";

    private final static String PREMIUM_PLAN = "premium";

    private final static String PLATINUM_PLAN = "platinum";

    @Inject
    PreferencesUtil mPreferencesUtil;

    @Inject
    Base64EncodedPublicKey mBase64EncodedPublicKey;

    @Inject
    GooglePlayServicesUtil mGooglePlayServicesUtil;

    @InjectView(R.id.subscriptions_listview)
    ListView mSubscriptionsListView;

    @Inject
    SnackbarUtil mSnackbarUtil;

    private ArrayList<Subscription> mSubscriptions;

    private SubscriptionsAdapter mSubscriptionsAdapter;

    private Integer mCurrentSubscriptionIndex;

    private IabHelper mHelper;

    private String mItemSkew;

    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;

    private String mPlan;

    private String mPlanFromGoogle;

    private int mPosition;

    private ProgressDialog mProgressDialog;

    private PurchaseSubscriptionWrapper mPurchaseSubscriptionWrapper;

    private PurchaseSubscriptionAsync mPurchaseSubscriptionAsync;

    private ConcurrentHashMap<ServiceConnection, Boolean> mServiceConnsHashMap;

    public static SubscriptionFragment newInstance() {
        return new SubscriptionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subscription, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSubscriptions = new ArrayList<>();

        // Setting the price to "" because I feel it's better to display no price if we can't get information from Google opposed to incorrect price
        Subscription freeSubscription = new Subscription(getString(R.string.free_subscription_title), getString(R.string.free_subscription_description), "");
        Subscription plusSubscription = new Subscription(getString(R.string.plus), getString(R.string.plus_subscription_description), "");
        Subscription premiumSubscription = new Subscription(getString(R.string.premium_subscription_title), getString(R.string.premium_subscription_description), "");
        Subscription platinumSubscription = new Subscription(getString(R.string.platinum_subscription_title), getString(R.string.platinum_subscription_description), "");

        mSubscriptions.add(freeSubscription);
        mSubscriptions.add(plusSubscription);
        mSubscriptions.add(premiumSubscription);
        mSubscriptions.add(platinumSubscription);

        updateUI();

        mProgressDialog = new ProgressDialog(getActivity());

        TextView subscriptionRestore = (TextView) getActivity().findViewById(R.id.subscription_restore);
        subscriptionRestore.setOnClickListener(v -> launchRestoreDialog());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGooglePlayServicesUtil != null) {
            mGooglePlayServicesUtil.getSubscriptionPrices(getActivity(), this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mServiceConnsHashMap != null) {
            Log.d(TAG, "Service hash map before: " + mServiceConnsHashMap.toString());

            for (ServiceConnection key : mServiceConnsHashMap.keySet()) {
                if (mServiceConnsHashMap.get(key)) {
                    Log.d(TAG, "Unbinding service: " + key);
                    getActivity().unbindService(key);
                    mServiceConnsHashMap.remove(key);
                }
            }

            Log.d(TAG, "Service hash map after: " + mServiceConnsHashMap.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.dispose();
        }
        mHelper = null;
    }

    public void onSubscriptionReturn(int requestCode, int resultCode, Intent data) {
        if (mHelper != null) {
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                // not handled, so handle it ourselves (here's where you'd
                // perform any handling of activity results not related to in-app
                // billing...
                super.onActivityResult(requestCode, resultCode, data);
            } else {
                Log.d(TAG, "onActivityResult handled by IABUtil.");
            }
        }
    }

    @Override
    public void getCurrentSubscription(String planFromGoogle, String purchaseToken) {
        mPlanFromGoogle = planFromGoogle;
        Log.d(TAG, "Current subscription from Google: " + mPlanFromGoogle);
        Log.d(TAG, "Purchase token: " + purchaseToken);

        if (!mPlanFromGoogle.equals("free")) {
            updatePlanWithDataFromGoogle(purchaseToken);
        }
    }

    private void launchRestoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.restore_subscription));
        builder.setMessage(getString(R.string.restore_subscription_description));
        builder
                .setPositiveButton(getActivity().getString(R.string.yes),
                        (dialog, id) -> {
                            getCurrentSubscriptionForRestore();
                            dialog.cancel();
                        })
                .setNegativeButton(getActivity().getString(R.string.no),
                        (dialog, id) -> {
                            dialog.cancel();
                        });
        builder.show();
    }

    private void getCurrentSubscriptionForRestore() {
        mGooglePlayServicesUtil.getCurrentSubscription(getActivity(), this);
    }

    private void updatePlanWithDataFromGoogle(String purchaseToken) {
        if (getActivity() != null) {
            Log.d(TAG, "User's current plan before update: " + mPreferencesUtil.getAuthUser().getPlan());
            Log.d(TAG, "Plan from Google before update: " + mPlanFromGoogle);

            switch (mPreferencesUtil.getAuthUser().getPlan()) {
                case PLUS_PLAN:
                    mCurrentSubscriptionIndex = 1;
                    break;
                case PREMIUM_PLAN:
                    mCurrentSubscriptionIndex = 2;
                    break;
                case PLATINUM_PLAN:
                    mCurrentSubscriptionIndex = 3;
                    break;
                default:
                    mCurrentSubscriptionIndex = 0;
                    break;
            }

            switch (mPlanFromGoogle) {
                case GooglePlayServicesUtil.PLUS_SUBSCRIPTION_SKU:
                case GooglePlayServicesUtil.PLUS_SUBSCRIPTION_2_SKU:
                    mPlan = PLUS_PLAN;
                    mPosition = 1;
                    break;
                case GooglePlayServicesUtil.PREMIUM_SUBSCRIPTION_SKU:
                    mPlan = PREMIUM_PLAN;
                    mPosition = 2;
                    break;
                case GooglePlayServicesUtil.PLATINUM_SUBSCRIPTION_SKU:
                    mPlan = PLATINUM_PLAN;
                    mPosition = 3;
                    break;
                default:
                    mPlan = FREE_PLAN;
                    mPosition = 0;
                    break;
            }

            Log.d(TAG, "Index of user's current subscription: " + mCurrentSubscriptionIndex);
            Log.d(TAG, "Index of subscription user is going to: " + mPosition);

            Receipt receipt = new Receipt(mPlanFromGoogle, getActivity().getPackageName(), purchaseToken);
            mPurchaseSubscriptionWrapper = new PurchaseSubscriptionWrapper(receipt, true);

            mProgressDialog.setMessage(getString(R.string.restoring_subscription));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            restoreSubscription(mPurchaseSubscriptionWrapper);
        }
    }

    private boolean checkIfAllowedToSubscribe(int position) {
        boolean allowedToSubscribe = false;

        if (getActivity() != null) {
            String planChosen = "";
            switch (position) {
                case 0:
                    planChosen = FREE_PLAN;
                    break;
                case 1:
                    planChosen = PLUS_PLAN;
                    break;
                case 2:
                    planChosen = PREMIUM_PLAN;
                    break;
                case 3:
                    planChosen = PLATINUM_PLAN;
                    break;
            }

            Log.d(TAG, "Current user plan: " + mPreferencesUtil.getAuthUser().getPlan());
            Log.d(TAG, "Plan the user is trying to subscribe to: " + planChosen);

            switch (mPreferencesUtil.getAuthUser().getPlan()) {
                case PLATINUM_PLAN:
                    allowedToSubscribe = false;
                    break;
                case PREMIUM_PLAN:
                    if (planChosen.equals(PLATINUM_PLAN)) {
                        allowedToSubscribe = true;
                    }
                    break;
                case PLUS_PLAN:
                    if (planChosen.equals(PREMIUM_PLAN) || planChosen.equals(PLATINUM_PLAN)) {
                        allowedToSubscribe = true;
                    }
                    break;
                case FREE_PLAN:
                    if (planChosen.equals(PLUS_PLAN) || planChosen.equals(PREMIUM_PLAN) || planChosen.equals(PLATINUM_PLAN)) {
                        allowedToSubscribe = true;
                    }
                    break;
            }
        }

        Log.d(TAG, "Can the user subscribe?: " + allowedToSubscribe);

        return allowedToSubscribe;
    }

    @Override
    public void bindService(ServiceConnection serviceConnection) {
        if (getActivity() != null && serviceConnection != null) {
            if (mServiceConnsHashMap == null) {
                mServiceConnsHashMap = new ConcurrentHashMap<>();
            }

            mServiceConnsHashMap.put(serviceConnection, true);

            Log.d(TAG, "Binding service: " + serviceConnection.toString());
            Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
            serviceIntent.setPackage("com.android.vending");
            getActivity().bindService(serviceIntent, serviceConnection, getActivity().BIND_AUTO_CREATE);
        }
    }

    @Override
    public void getPlanPrice(String plan, String price) {
        for (Subscription subscription : mSubscriptions) {
            if (plan.equals(subscription.getTitle())) {
                subscription.setPrice(price);
            }
        }
        updateUI();
    }

    @Override
    public void onSubscribeToPlan(int position) {
        boolean allowedToSubscribe = checkIfAllowedToSubscribe(position);
        if (allowedToSubscribe) {
            if (mHelper != null) {
                mHelper.flagEndAsync();
            }

            mPurchaseFinishedListener = (result, info) -> {
                if (result.isSuccess()) {
                    Log.d(TAG, "info.getSku(): " + info.getSku());
                    Log.d(TAG, "info.getToken(): " + info.getToken());
                    Receipt receipt = new Receipt(info.getSku(), getActivity().getPackageName(), info.getToken());
                    mPurchaseSubscriptionWrapper = new PurchaseSubscriptionWrapper(receipt, true);
                    purchaseSubscription(mPurchaseSubscriptionWrapper);
                } else {
                    Log.w(TAG, "Failed to subscribe.  result.response: " + result.getResponse() + ", result.getMessage: " + result.getMessage());
                    if (result.getResponse() != USER_CANCELED_CODE) {
                        if (getActivity() != null) {
                            mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.failed_to_subscribe_text);
                        }
                    }
                }
            };

            mPosition = position;
            mPlan = "";
            switch (position) {
                case 0:
                    mPlan = FREE_PLAN;
                    mItemSkew = "";
                    break;
                case 1:
                    mPlan = PLUS_PLAN;
                    mItemSkew = GooglePlayServicesUtil.PLUS_SUBSCRIPTION_2_SKU;
                    break;
                case 2:
                    mPlan = PREMIUM_PLAN;
                    mItemSkew = GooglePlayServicesUtil.PREMIUM_SUBSCRIPTION_SKU;
                    break;
                case 3:
                    mPlan = PLATINUM_PLAN;
                    mItemSkew = GooglePlayServicesUtil.PLATINUM_SUBSCRIPTION_SKU;
                    break;
            }

            if (!TextUtils.isEmpty(mPlan)) {
                if (!mPlan.equals(FREE_PLAN)) {
                    launchPurchaseDialog();
                }
            }
        } else {
            if (getActivity() != null) {
                mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.you_already_have_a_higher_subscription_level);
            }
        }
    }

    private void launchPurchaseDialog() {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.dialog_subscription_cancel, null);
            builder.setView(view);
            builder
                    .setPositiveButton(getString(R.string.continue_to_purchase), (dialog, id) -> {
                        dialog.dismiss();
                        startInAppPurchaseHelper();
                    })
                    .setNegativeButton(getString(R.string.go_back), (dialog, id) -> {
                        dialog.dismiss();
                    });
            builder.show();
        }
    }

    private void startInAppPurchaseHelper() {
        if (getActivity() != null) {
            String base64EncodedPublicKey = mBase64EncodedPublicKey.getBase64EncodedPublicKey();
            mHelper = new IabHelper(getActivity(), base64EncodedPublicKey);
            mHelper.startSetup(result -> {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " + result);
                    if (getActivity() != null) {
                        mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.not_connected_to_the_google_play_store);
                    }
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                    if (mHelper != null) {
                        mHelper.launchSubscriptionPurchaseFlow(getActivity(), mItemSkew, SUBSCRIPTION_REQUEST_CODE, mPurchaseFinishedListener, "mysubscriptiontoken");
                    } else {
                        Log.e(TAG, "mHelper was somehow null :'( ");
                    }
                }
            });
        }
    }

    private void updateUI() {
        if (getActivity() != null) {
            mCurrentSubscriptionIndex = 0;
            switch (mPreferencesUtil.getAuthUser().getPlan()) {
                case FREE_PLAN:
                    mCurrentSubscriptionIndex = 0;
                    break;
                case PLUS_PLAN:
                    mCurrentSubscriptionIndex = 1;
                    break;
                case PREMIUM_PLAN:
                    mCurrentSubscriptionIndex = 2;
                    break;
                case PLATINUM_PLAN:
                    mCurrentSubscriptionIndex = 3;
                    break;
            }

            mSubscriptions.get(mCurrentSubscriptionIndex).setSubscribed(true);

            updateSubscriptionAdapter();
        }
    }

    public void updateSubscriptionAdapter() {
        if (getActivity() != null) {
            if (mSubscriptionsListView.getAdapter() == null) {
                mSubscriptionsAdapter = new SubscriptionsAdapter(mSubscriptions, getActivity(), this);
                mSubscriptionsListView.setAdapter(mSubscriptionsAdapter);
            } else {
                mSubscriptionsAdapter.notifyDataSetChanged();
            }
        }
    }

    public void updateAuthUserPlan() {
        if (getActivity() != null) {
            mSubscriptions.get(mCurrentSubscriptionIndex).setSubscribed(false);
            mSubscriptions.get(mPosition).setSubscribed(true);

            // TODO - Figure out why I can't just set the plan on the auth user and have to do it this way
            AuthUser authUser = mPreferencesUtil.getAuthUser();
            authUser.setPlan(mPlan);
            mPreferencesUtil.setAuthUser(authUser);

            Log.i(TAG, "authUser's plan was changed to: " + mPreferencesUtil.getAuthUser().getPlan());

            updateUI();
            mCurrentSubscriptionIndex = mPosition;
        }
    }

    protected void purchaseSubscription(final PurchaseSubscriptionWrapper purchaseSubscriptionWrapper) {
        if (mPurchaseSubscriptionAsync != null) {
            Log.w(TAG, "Subscription purchase in progress, subscription purchase request ignored");
            return;
        }

        mPurchaseSubscriptionAsync = new PurchaseSubscriptionAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<GetPurchaseResponse> result) {
                updateAuthUserPlan();
                mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.subscribed_successfully_text);
                mPurchaseSubscriptionAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not purchase subscription", error);
                if (getActivity() != null) {
                    mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_purchase_subscription_text, R.string.retry, snackbar -> {
                        purchaseSubscription(purchaseSubscriptionWrapper);
                        SnackbarManager.dismiss();
                    });
                }
                mPurchaseSubscriptionAsync = null;
            }
        }.executeInParallel(purchaseSubscriptionWrapper);
    }

    protected void restoreSubscription(final PurchaseSubscriptionWrapper purchaseSubscriptionWrapper) {
        if (mPurchaseSubscriptionAsync != null) {
            Log.w(TAG, "Restore subscription request already in progress, new request will be ignored");
            return;
        }

        mPurchaseSubscriptionAsync = new PurchaseSubscriptionAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<GetPurchaseResponse> result) {
                updateAuthUserPlan();
                mProgressDialog.cancel();
                mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.subscription_restored_successfully);
                mPurchaseSubscriptionAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not restore subscription", error);
                mProgressDialog.cancel();
                if (getActivity() != null) {
                    if (error.getResponse() != null && error.getResponse().getStatus() == 406) {
                        mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.you_cannot_restore_your_account_using_someone_elses_google_credentials);
                    } else {
                        mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_restore_subscription, R.string.retry, snackbar -> {
                            mProgressDialog.setMessage(getString(R.string.restoring_subscription));
                            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            mProgressDialog.setCancelable(false);
                            mProgressDialog.show();
                            restoreSubscription(purchaseSubscriptionWrapper);
                            SnackbarManager.dismiss();
                        });
                    }
                }
                mPurchaseSubscriptionAsync = null;
            }
        }.executeInParallel(purchaseSubscriptionWrapper);
    }
}