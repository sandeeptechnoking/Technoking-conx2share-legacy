package com.conx2share.conx2share.util;

import com.google.android.gms.common.ConnectionResult;
import com.google.inject.Singleton;

import com.android.vending.billing.IInAppBillingService;
import com.conx2share.conx2share.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

@Singleton
public class GooglePlayServicesUtil {

    public static final String TAG = GooglePlayServicesUtil.class.getSimpleName();

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public final static String PLUS_SUBSCRIPTION_SKU = "plus_subscription";

    public final static String PLUS_SUBSCRIPTION_2_SKU = "plus_subscription_2";

    public final static String PREMIUM_SUBSCRIPTION_SKU = "premium_subscription";

    public final static String PLATINUM_SUBSCRIPTION_SKU = "platinum_subscription";

    private final static int PLAY_SERVICES_API_VERSION = 3;

    private IInAppBillingService mService;

    private ServiceConnection mServiceConn;

    private Context mContext;

    private GooglePlayServiceUtilCallbacks mGooglePlayServiceUtilCallbacks;

    public boolean checkPlayServices(Activity activity, Context context) {
        int resultCode = com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (com.google.android.gms.common.GooglePlayServicesUtil.isUserRecoverableError(resultCode) && activity != null) {
                try {
                    com.google.android.gms.common.GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } catch (RuntimeException e) {
                    Log.e(TAG, "Exception trying to show GooglePlayServices error dialog, resultCode: " + resultCode, e);
                }
            } else {
                Log.i(TAG, "This device is lacks an updated google play service, resultCode: " + resultCode);
            }
            return false;
        }
        return true;
    }

    public void getCurrentSubscription(Activity activity, GooglePlayServiceUtilCallbacks googlePlayServiceUtilCallbacks) {
        if (activity != null) {
            new GetCurrentSubscriptionAsync(activity, googlePlayServiceUtilCallbacks).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Log.w(TAG, "mActivity was null");
        }
    }

    public void getSubscriptionPrices(Activity activity, GooglePlayServiceUtilCallbacks googlePlayServiceUtilCallbacks) {
        if (activity != null) {
            new GetSubscriptionPricesAsync(activity, googlePlayServiceUtilCallbacks).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Log.w(TAG, "mActivity was null");
        }
    }

    public interface GooglePlayServiceUtilCallbacks {

        void bindService(ServiceConnection serviceConnection);

        void getCurrentSubscription(String planFromGoogle, String purchaseToken);

        void getPlanPrice(String plan, String price);
    }

    private class GetCurrentSubscriptionAsync extends AsyncTask<Void, Void, Void> {

        public GetCurrentSubscriptionAsync(Context context, GooglePlayServiceUtilCallbacks googlePlayServiceUtilCallbacks) {
            mContext = context;
            mGooglePlayServiceUtilCallbacks = googlePlayServiceUtilCallbacks;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mServiceConn = new ServiceConnection() {
                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mService = null;
                }

                @Override
                public void onServiceConnected(ComponentName name,
                        IBinder service) {
                    mService = IInAppBillingService.Stub.asInterface(service);

                    String subscriptionFromGoogle = "";
                    String purchaseTokenFromGoogle = "";

                    if (mService != null) {

                        Bundle ownedSubscriptions = new Bundle();
                        try {
                            ownedSubscriptions = mService.getPurchases(PLAY_SERVICES_API_VERSION, mContext.getPackageName(), "subs", null);
                        } catch (RemoteException e) {
                            Log.e(TAG, "Exception querying for subscriptions: " + e.toString());
                        }

                        int response = ownedSubscriptions.getInt("RESPONSE_CODE");
                        if (response == 0) {
                            ArrayList<String> inappPurchaseItemList = ownedSubscriptions.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");

                            for (String purchaseItem : inappPurchaseItemList) {

                                ArrayList<String> inappPurchaseDataList = ownedSubscriptions.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

                                for (String dataListItem : inappPurchaseDataList) {

                                    JSONObject object = null;
                                    try {
                                        object = new JSONObject(dataListItem);
                                    } catch (JSONException e) {
                                        Log.e(TAG, "Exception getting data list item: " + e.toString());
                                    }

                                    int purchaseState = -1;
                                    try {
                                        purchaseState = object.getInt("purchaseState");
                                    } catch (JSONException e) {
                                        Log.e(TAG, "Exception getting purchase state: " + e.toString());
                                    }

                                    String productId = null;
                                    try {
                                        productId = object.getString("productId");
                                    } catch (JSONException e) {
                                        Log.e(TAG, "Exception getting product id: " + e.toString());
                                    }

                                    String purchaseToken = null;
                                    try {
                                        purchaseToken = object.getString("purchaseToken");
                                    } catch (JSONException e) {
                                        Log.e(TAG, "Exception getting purchaseToken: " + e.toString());
                                    }

                                    // From http://developer.android.com/google/play/billing/billing_reference.html
                                    // We make sure the status is purchase, not canceled (1) or refunded (2)
                                    subscriptionFromGoogle = "";
                                    if (purchaseState == 0) {
                                        switch (productId) {
                                            case PLATINUM_SUBSCRIPTION_SKU:
                                                subscriptionFromGoogle = productId;
                                                purchaseTokenFromGoogle = purchaseToken;
                                                break;
                                            case PREMIUM_SUBSCRIPTION_SKU:
                                                if (!subscriptionFromGoogle.equals(PLATINUM_SUBSCRIPTION_SKU)) {
                                                    subscriptionFromGoogle = productId;
                                                    purchaseTokenFromGoogle = purchaseToken;
                                                }
                                                break;
                                            case PLUS_SUBSCRIPTION_SKU:
                                            case PLUS_SUBSCRIPTION_2_SKU:
                                                if (!subscriptionFromGoogle.equals(PLATINUM_SUBSCRIPTION_SKU) && !subscriptionFromGoogle.equals(PREMIUM_SUBSCRIPTION_SKU)) {
                                                    subscriptionFromGoogle = productId;
                                                    purchaseTokenFromGoogle = purchaseToken;
                                                }
                                                break;
                                            default:
                                                Log.e(TAG, "Unknown subscription type from Google: " + productId);
                                                break;
                                        }
                                    }
                                }
                            }

                            if (subscriptionFromGoogle.equals("")) {
                                subscriptionFromGoogle = "free";
                            }
                        } else {
                            Log.w(TAG, "Did not get a good response code from Google. Response " + response);
                            subscriptionFromGoogle = "free";
                        }
                    } else {
                        Log.w(TAG, "mService was null");
                        subscriptionFromGoogle = "free";

                    }
                    mGooglePlayServiceUtilCallbacks.getCurrentSubscription(subscriptionFromGoogle, purchaseTokenFromGoogle);
                }
            };

            mGooglePlayServiceUtilCallbacks.bindService(mServiceConn);

            return null;
        }

    }

    private class GetSubscriptionPricesAsync extends AsyncTask<GooglePlayServiceUtilCallbacks, Void, Void> {

        public GetSubscriptionPricesAsync(Context context, GooglePlayServiceUtilCallbacks googlePlayServiceUtilCallbacks) {
            mContext = context;
            mGooglePlayServiceUtilCallbacks = googlePlayServiceUtilCallbacks;
        }

        @Override
        protected Void doInBackground(GooglePlayServiceUtilCallbacks... params) {

            mServiceConn = new ServiceConnection() {
                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mService = null;
                }

                @Override
                public void onServiceConnected(ComponentName name,
                        IBinder service) {
                    mService = IInAppBillingService.Stub.asInterface(service);

                    if (mService != null) {

                        ArrayList<String> skuList = new ArrayList<>();
                        skuList.add(PLUS_SUBSCRIPTION_SKU);
                        skuList.add(PLUS_SUBSCRIPTION_2_SKU);
                        skuList.add(PREMIUM_SUBSCRIPTION_SKU);
                        skuList.add(PLATINUM_SUBSCRIPTION_SKU);
                        Bundle querySkus = new Bundle();
                        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

                        Bundle skuDetails = new Bundle();
                        try {
                            skuDetails = mService.getSkuDetails(PLAY_SERVICES_API_VERSION, mContext.getPackageName(), "subs", querySkus);
                        } catch (RemoteException e) {
                            Log.e(TAG, "Exception querying for subscription details: " + e.toString());
                        }

                        int response = skuDetails.getInt("RESPONSE_CODE");
                        if (response == 0) {
                            ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

                            for (String thisResponse : responseList) {

                                JSONObject object = null;
                                try {
                                    object = new JSONObject(thisResponse);
                                } catch (JSONException e) {
                                    Log.e(TAG, "Exception turning JSON into object: " + e.toString());
                                }

                                if (object != null) {
                                    String sku = null;
                                    try {
                                        sku = object.getString("productId");
                                        Log.d(TAG, "sku: " + sku);
                                    } catch (JSONException e) {
                                        Log.e(TAG, "Exception getting product id/sku: " + e.toString());
                                    }

                                    String price = null;
                                    try {
                                        price = object.getString("price");
                                        Log.d(TAG, "price: " + price);
                                    } catch (JSONException e) {
                                        Log.e(TAG, "Exception getting price: " + e.toString());
                                    }

                                    switch (sku) {
                                        case (PLUS_SUBSCRIPTION_2_SKU):
                                            mGooglePlayServiceUtilCallbacks.getPlanPrice(mContext.getString(R.string.plus), price);
                                            break;
                                        case (PREMIUM_SUBSCRIPTION_SKU):
                                            mGooglePlayServiceUtilCallbacks.getPlanPrice(mContext.getString(R.string.premium_subscription_title), price);
                                            break;
                                        case (PLATINUM_SUBSCRIPTION_SKU):
                                            mGooglePlayServiceUtilCallbacks.getPlanPrice(mContext.getString(R.string.platinum_subscription_title), price);
                                            break;
                                    }
                                } else {
                                    Log.w(TAG, "object was null");
                                }
                            }
                        } else {
                            Log.w(TAG, "Did not get a good response code from Google. Response " + response);
                        }
                    } else {
                        Log.w(TAG, "mService was null");
                    }
                }
            };

            mGooglePlayServiceUtilCallbacks.bindService(mServiceConn);

            return null;
        }

    }

}