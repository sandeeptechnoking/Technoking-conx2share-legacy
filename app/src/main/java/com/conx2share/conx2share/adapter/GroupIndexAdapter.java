package com.conx2share.conx2share.adapter;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.makeramen.roundedimageview.RoundedImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupIndexAdapter extends ArrayAdapter<Group> {

    private static final String TAG = GroupIndexAdapter.class.getSimpleName();

    private GroupIndexAdapterCallbacks mCallbacks;

    private LayoutInflater mLayoutInflater;

    private Integer mAuthUserId;

    public GroupIndexAdapter(ArrayList<Group> groups, Context context, GroupIndexAdapterCallbacks callbacks, Integer authUserId) {
        super(context, R.layout.group_index_list_item, groups);
        mAuthUserId = authUserId;
        mCallbacks = callbacks;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Group group = getItem(position);

        if (position >= getCount() - 1) {
            mCallbacks.onNearingEndOfList();
        }

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.group_index_list_item, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = ((ViewHolder) convertView.getTag());
            vh.resetViews(convertView);
        }

        vh.groupAvatar.initView(group.getGroupavatar().getGroupAvatar().getUrl(), group.getName());

        int groupTypeStringId;
        if (group.getGroupType() != null) {
            switch (group.getGroupType()) {
                case Group.PRIVATE_KEY:
                    groupTypeStringId = R.string.private_group;
                    break;
                case Group.DISCUSSION_KEY:
                    groupTypeStringId = R.string.discussion_group;
                    break;
                case Group.BLOG_KEY:
                    groupTypeStringId = R.string.blog_group;
                    break;
                default:
                    groupTypeStringId = R.string.empty_string;
                    break;
            }
        } else {
            groupTypeStringId = R.string.empty_string;
        }

        if (mAuthUserId != null && mAuthUserId.equals(group.getCreatorId())) {
            vh.groupOwnerSign.setVisibility(View.VISIBLE);
            vh.groupStatus.setText(getContext().getString(R.string.group_owner) + " - " + getContext().getString(groupTypeStringId));
        } else {
            vh.groupOwnerSign.setVisibility(View.GONE);
            if (group.isMember()) {
                if (group.getGroupType() == Group.DISCUSSION_KEY) {
                    vh.groupStatus.setText(getContext().getString(R.string.follower) + " - " + getContext().getString(groupTypeStringId));
                } else {
                    vh.groupStatus.setText(getContext().getString(R.string.member) + " - " + getContext().getString(groupTypeStringId));
                }
            } else if (group.isFollowing()) {
                vh.groupStatus.setText(getContext().getString(R.string.follower) + " - " + getContext().getString(groupTypeStringId));
            } else {
                vh.groupStatus.setText(getContext().getString(groupTypeStringId));
            }
        }

        if (group.getBadgeCount() != null && group.getBadgeCount() != 0) {
            vh.groupName.setText(group.getName() + " (" + group.getBadgeCount() + ")");
        } else {
            vh.groupName.setText(group.getName());
        }

        return convertView;
    }

    public interface GroupIndexAdapterCallbacks {
        void onNearingEndOfList();
    }

    private class ViewHolder {

        TextView groupName;
        AvatarImageView groupAvatar;
        TextView groupStatus;
        RoundedImageView groupOwnerSign;

        public ViewHolder(View view) {
            setupViews(view);
        }

        private void setupViews(View view) {
            groupName = (TextView) view.findViewById(R.id.group_name);
            groupAvatar = (AvatarImageView) view.findViewById(R.id.group_avatar);
            groupStatus = (TextView) view.findViewById(R.id.group_status);
            groupOwnerSign = (RoundedImageView) view.findViewById(R.id.group_owner_sign);
        }

        public void resetViews(View view) {
            groupName = null;
            groupAvatar = null;
            groupStatus = null;
            groupOwnerSign = null;
            setupViews(view);
        }
    }
}
