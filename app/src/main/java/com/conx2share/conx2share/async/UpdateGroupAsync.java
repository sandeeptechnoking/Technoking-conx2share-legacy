package com.conx2share.conx2share.async;

import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.model.GroupResponse;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.util.TypedUri;

import android.content.Context;

public abstract class UpdateGroupAsync extends BaseRetrofitAsyncTask<Void, Void, GroupResponse> {

    private String mGroupName;

    private String mGroupAbout;

    private TypedUri mTypedUri;

    private Group mGroup;

    public UpdateGroupAsync(Context context, Group group, String groupName, String groupAbout, TypedUri typedUri) {
        super(context);
        mGroup = group;
        mGroupName = groupName;
        mGroupAbout = groupAbout;
        mTypedUri = typedUri;
    }

    @Override
    protected Result<GroupResponse> doInBackground(Void... params) {
        return getNetworkClient().updateGroup(mGroup, mGroupName, mGroupAbout, mTypedUri);
    }
}
