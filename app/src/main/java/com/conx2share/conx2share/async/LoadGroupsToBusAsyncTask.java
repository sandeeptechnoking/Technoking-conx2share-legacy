package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.GetGroupListResponse;
import com.conx2share.conx2share.util.EventBusUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import roboguice.RoboGuice;

public class LoadGroupsToBusAsyncTask extends AsyncTask<Void, Void, Result<GetGroupListResponse>> {

    public static String TAG = LoadGroupsToBusAsyncTask.class.getSimpleName();

    @Inject
    private NetworkClient mNetworkClient;

    private Handler mHandler;

    public LoadGroupsToBusAsyncTask(Context context) {
        RoboGuice.getInjector(context).injectMembers(this);
        mHandler = new Handler(context.getMainLooper());
    }

    @Override
    protected Result<GetGroupListResponse> doInBackground(Void... params) {
        return mNetworkClient.getGroups(true);
    }

    @Override
    protected void onPostExecute(Result<GetGroupListResponse> getGroupListResponseResult) {
        super.onPostExecute(getGroupListResponseResult);

        if (getGroupListResponseResult != null && getGroupListResponseResult.getResource() != null && getGroupListResponseResult.getError() == null) {
            Log.i(TAG, "loading groups was successful");
            final List<Group> groupList = getGroupListResponseResult.getResource().getGroups();
            mHandler.post(() -> EventBusUtil.getEventBus().post(new LoadGroupSuccessEvent(groupList)));

        } else {
            Log.i(TAG, "loading groups failed");
            mHandler.post(() -> EventBusUtil.getEventBus().post(new LoadGroupFailureEvent()));
        }
    }

    public class LoadGroupSuccessEvent {

        private List<Group> groups;

        public LoadGroupSuccessEvent(List<Group> groups) {
            this.groups = groups;
        }

        public List<Group> getGroups() {
            return groups;
        }
    }

    public class LoadGroupFailureEvent {
        // TODO - Why is this empty?
    }
}
