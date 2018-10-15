package com.conx2share.conx2share.adapter;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.conx2share.conx2share.util.PopUpMenuUtil;
import com.conx2share.conx2share.util.SnackbarUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

import javax.inject.Inject;

public class GroupMembersAdapter extends ArrayAdapter<User> {

    private static final String TAG = GroupMembersAdapter.class.getSimpleName();

    private LayoutInflater mLayoutInflater;

    private Group mGroup;

    private GroupMembersAdapterCallbacks mCallbacks;

    private int mAuthUserId;

    @Inject
    private SnackbarUtil mSnackbarUtil;

    private PopUpMenuUtil mPopUpMenuUtil = new PopUpMenuUtil();

    public GroupMembersAdapter(Context context, GroupMembersAdapterCallbacks callback, ArrayList<User> users, Group group, int authUserId) {
        super(context, R.layout.group_invite_list_item, users);
        mLayoutInflater = LayoutInflater.from(context);
        mGroup = group;
        mAuthUserId = authUserId;
        mCallbacks = callback;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.group_invite_list_item, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = ((ViewHolder) convertView.getTag());
            vh.resetViews(convertView);
        }

        User user = getItem(position);

        vh.name.setText(user.getFirstName() + " " + user.getLastName());
        vh.handleView.setText("@".concat(user.getUsername()));
        vh.avatar.initView(user);

        if (user.getId() == mGroup.getCreatorId()) {
            vh.ownerSign.setVisibility(View.VISIBLE);
        } else {
            vh.ownerSign.setVisibility(View.GONE);
        }

        if (mGroup.isOwner() != null && mGroup.isOwner()) {
            if (user.getId() != mAuthUserId) {
                vh.optionCarrot.setVisibility(View.VISIBLE);
            } else {
                vh.optionCarrot.setVisibility(View.GONE);
            }
        } else {
            vh.optionCarrot.setVisibility(View.GONE);
        }

        vh.optionCarrot.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(getContext(), vh.optionCarrot);

            if (mAuthUserId == mGroup.getCreatorId()) {
                mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.group_members_menu);
            } else {
                mSnackbarUtil.showSnackBarWithoutAction(getContext(), R.string.you_cant_do_anything_with_this_group_member);
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.group_member_remove_from_group:
                        mCallbacks.onRemoveMemberFromGroupClicked(user);
                        break;
                }
                return false;
            });
        });

        return convertView;
    }

    public interface GroupMembersAdapterCallbacks {
        void onRemoveMemberFromGroupClicked(User user);
    }

    private class ViewHolder {

        AvatarImageView avatar;
        TextView name;
        LinearLayout ownerSign;
        ImageView optionCarrot;
        TextView handleView;

        public ViewHolder(View view) {
            setupViews(view);
        }

        private void setupViews(View view) {
            avatar = (AvatarImageView) view.findViewById(R.id.group_invite_avatar);
            name = (TextView) view.findViewById(R.id.group_invite_name);
            ownerSign = (LinearLayout) view.findViewById(R.id.group_invite_owner_sign);
            optionCarrot = (ImageView) view.findViewById(R.id.group_invite_option_carrot);
            handleView = (TextView) view.findViewById(R.id.handle);
        }

        public void resetViews(View view) {
            avatar = null;
            name = null;
            ownerSign = null;
            optionCarrot = null;
            handleView = null;
            setupViews(view);
        }
    }
}
