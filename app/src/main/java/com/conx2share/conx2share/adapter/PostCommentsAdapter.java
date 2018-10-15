package com.conx2share.conx2share.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.model.Comment;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.conx2share.conx2share.util.DateUtils;
import com.conx2share.conx2share.util.PopUpMenuUtil;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;

public class PostCommentsAdapter extends ArrayAdapter<Comment> {

    public static final String PROFILEID_KEY = "profileId";

    private LayoutInflater mLayoutInflater;

    private PostCommentsCallback mCallback;

    private int mAuthUserId;

    private int mPostCreatorId;

    private Group mGroup;

    private Business mBusiness;

    private PopUpMenuUtil mPopUpMenuUtil;

    public PostCommentsAdapter(ArrayList<Comment> comments, Context context, PostCommentsCallback callback, int authUserId, int postCreatorId, @Nullable Group group,
                               @Nullable Business business) {
        super(context, R.layout.post_comment_list_item, comments);
        mLayoutInflater = LayoutInflater.from(context);
        mAuthUserId = authUserId;
        mPostCreatorId = postCreatorId;
        mGroup = group;
        mBusiness = business;
        mCallback = callback;
        mPopUpMenuUtil = new PopUpMenuUtil();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Comment comment = getItem(position);

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.post_comment_list_item, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = ((ViewHolder) convertView.getTag());
        }

        vh.commentText.setText(comment.getBodyTextWithSpans(parent.getContext()));
        vh.commentText.setMovementMethod(LinkMovementMethod.getInstance());

        vh.commenterName.setText(comment.getCommenterFirstName() + " " + comment.getCommenterLastName());
        vh.commenterHandle.setText("@".concat(comment.getCommenterHandle()));
        vh.commentDate.setText(DateUtils.getTimeDifference(comment.getCreatedAt()));

        vh.commenterAvatar.initView(comment.getCommenterAvatar(), comment.getCommenterFirstName(), comment.getCommenterLastName());
        vh.commenterAvatar.setOnClickListener(v -> {
            Intent profileActivityIntent = new Intent(getContext(), ProfileActivity.class);
            profileActivityIntent.putExtra(PROFILEID_KEY, String.valueOf(comment.getCommenterId()));
            getContext().startActivity(profileActivityIntent);
        });

        vh.commentOptionsCarrot.setVisibility(comment.getCommenterId() == mAuthUserId ? View.VISIBLE : View.INVISIBLE);
        vh.commentOptionsCarrot.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(getContext(), vh.commentOptionsCarrot);

            if (comment.getCommenterId().equals(mAuthUserId)) {
                mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_comment_options_edit_delete);
            } else {
                if (mGroup != null && mGroup.getCreatorId().equals(mAuthUserId)) {
                    switch (comment.getGroupStatus()) {
                        case Group.GROUP_STATUS_MEMBER:
                            if (mGroup.getGroupType() == Group.DISCUSSION_KEY) {
                                mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_comment_options_delete_block);
                            } else {
                                mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_comment_options_delete_remove);
                            }
                            break;
                        case Group.GROUP_STATUS_FOLLOWER:
                            mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_comment_options_delete_block);
                            break;
                        case Group.GROUP_STATUS_BLOCKED:
                            mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_comment_options_delete_unblock);
                            break;
                        default:
                            if (mGroup.getGroupType() == Group.PRIVATE_KEY) {
                                mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_comment_options_delete);
                            } else {
                                mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_comment_options_delete_block);
                            }
                            break;
                    }
                } else if (mAuthUserId == mPostCreatorId) {
                    mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_comment_options_delete);
                } else if (mBusiness != null && mBusiness.getIsOwner()) {
                    mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_comment_options_delete);
                } else {
                    SnackbarManager.show(Snackbar.with(getContext()).duration(Snackbar.SnackbarDuration.LENGTH_SHORT).type(SnackbarType.MULTI_LINE).text(getContext().getString(R.string.cant_do_anything_with_comment_since_you_dont_own_it)));
                }
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.post_comment_option_delete:
                        mCallback.onPostCommentDeleteClicked(comment);
                        break;
                    case R.id.post_comment_option_edit:
                        mCallback.onPostCommentEditClicked(comment);
                        break;
                    case R.id.post_comment_option_block:
                        mCallback.onBlockUserClicked(comment, mGroup);
                        break;
                    case R.id.post_comment_option_unblock:
                        mCallback.onUnblockUserClicked(comment, mGroup);
                        break;
                    case R.id.post_comment_option_remove:
                        mCallback.onRemoveFromGroupClicked(comment, mGroup);
                        break;
                }
                return false;
            });
        });

        return convertView;
    }

    public interface PostCommentsCallback {
        void onPostCommentDeleteClicked(Comment comment);

        void onPostCommentEditClicked(Comment comment);

        void onRemoveFromGroupClicked(Comment comment, Group group);

        void onBlockUserClicked(Comment comment, Group group);

        void onUnblockUserClicked(Comment comment, Group group);
    }

    static class ViewHolder {
        @BindView(R.id.post_user_avatar)
        AvatarImageView commenterAvatar;
        @BindView(R.id.post_user_name)
        TextView commenterName;
        @BindView(R.id.post_user_handle)
        TextView commenterHandle;
        @BindView(R.id.post_comment_date)
        TextView commentDate;
        @BindView(R.id.post_comment_option_carrot)
        ImageButton commentOptionsCarrot;
        @BindView(R.id.comment_text)
        TextView commentText;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
