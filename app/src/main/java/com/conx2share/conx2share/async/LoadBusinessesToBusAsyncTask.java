package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.BusinessesResponse;
import com.conx2share.conx2share.util.EventBusUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import roboguice.RoboGuice;

public class LoadBusinessesToBusAsyncTask extends AsyncTask<Void, Void, Result<BusinessesResponse>> {

    public static String TAG = LoadBusinessesToBusAsyncTask.class.getSimpleName();

    @Inject
    private NetworkClient mNetworkClient;

    private Handler mHandler;

    public LoadBusinessesToBusAsyncTask(Context context) {
        RoboGuice.getInjector(context).injectMembers(this);
        mHandler = new Handler(context.getMainLooper());
    }

    @Override
    protected Result<BusinessesResponse> doInBackground(Void... params) {
        return mNetworkClient.getMyBusinesses();
    }

    @Override
    protected void onPostExecute(Result<BusinessesResponse> getBusinessResponse) {
        super.onPostExecute(getBusinessResponse);

        if (getBusinessResponse != null && getBusinessResponse.getResource() != null && getBusinessResponse.getError() == null) {
            Log.i(TAG, "loading businesses was successful");
            final List<Business> businesses = getBusinessResponse.getResource().getBusinesses();
            mHandler.post(() -> EventBusUtil.getEventBus().post(new LoadBusinessesSuccessEvent(businesses)));

        } else {
            Log.i(TAG, "loading businesses failed");
            mHandler.post(() -> EventBusUtil.getEventBus().post(new LoadBusinessFailureEvent()));
        }
    }

    public class LoadBusinessesSuccessEvent {

        private List<Business> businesses;

        public LoadBusinessesSuccessEvent(List<Business> businesses) {
            this.businesses = businesses;
        }

        public List<Business> getBusinesses() {
            return businesses;
        }
    }

    public class LoadBusinessFailureEvent {
        // TODO - Why is this empty?
    }
}
