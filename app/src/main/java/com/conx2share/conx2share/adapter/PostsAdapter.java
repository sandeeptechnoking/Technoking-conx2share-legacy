package com.conx2share.conx2share.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.ui.feed.post_comments.PostCommentsActivity;
import com.conx2share.conx2share.ui.feed.post_comments.PostCommentsFragment;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.conx2share.conx2share.util.DateUtils;
import com.conx2share.conx2share.util.PopUpMenuUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
//import roboguice.inject.InjectView;


public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = PostsAdapter.class.getSimpleName();
    public static final int IMAGE_SIZE = 270;
    public static final int MAX_USERNAME_LENGTH = 20;
    private PostAdapterCallback mCallback;
    private LayoutInflater mLayoutInflater;
    private boolean mPostNeedsToBeExpanded;
    private int mUserId;
    private Group mGroup;
    private Business mBusiness;
    private PopUpMenuUtil mPopUpMenuUtil;
    private int screenWidth;
    private ArrayList<Post> mPosts;

    public PostsAdapter(Context context, ArrayList<Post> posts, PostAdapterCallback callback,
                        boolean postNeedsToBeExpanded, int userId, @Nullable Group group, @Nullable Business business) {

        if (callback == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }

        mCallback = callback;
        mUserId = userId;
        mBusiness = business;
        mGroup = group;
        mLayoutInflater = LayoutInflater.from(context);
        mPostNeedsToBeExpanded = postNeedsToBeExpanded;
        mPopUpMenuUtil = new PopUpMenuUtil();
        mPosts = posts;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {

        View convertView = vh.itemView;

        if (position >= getItemCount() - 1) {
            mCallback.onNearingEndOfList();
        }

        vh.likeCountTextView.setText("0");
        vh.commentCountTextView.setText("0");
        vh.ageTextView.setText("");
        vh.videoIcon.setVisibility(View.GONE);

        Post post = mPosts.get(position);

        View.OnClickListener postClickListener = v -> {
            Intent intent = new Intent(v.getContext(), PostCommentsActivity.class);
            intent.putExtra(PostCommentsFragment.EXTRA_POST_ID, String.valueOf(post.getId()));
            intent.putExtra(PostCommentsFragment.EXTRA_POST_POSITION, position);
            intent.putExtra(Group.EXTRA, NetworkClient.getGson().toJson(mGroup));
            intent.putExtra(Business.EXTRA, NetworkClient.getGson().toJson(mBusiness));

            v.getContext().startActivity(intent);
        };

        if (post.hasImage()) {
            vh.videoIcon.setVisibility(post.hasVideo() ? View.VISIBLE : View.GONE);

            vh.imageContainer.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) vh.postImage.getLayoutParams();
            float feedHeight = post.getPicture().getPicture().getHeight();
            float feedWidth = post.getPicture().getPicture().getWidth();
            vh.height = (int) (screenWidth * (feedHeight / feedWidth));
            lp.width = screenWidth;
            lp.height = vh.height;
            vh.postImage.setLayoutParams(lp);

            Glide.with(convertView.getContext())
                    .load(post.getImageUrl())
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(vh.postImage);

            vh.imageContainer.setOnClickListener(v -> mCallback.onImageClicked(post));

        } else {
            vh.imageContainer.setVisibility(View.GONE);
        }

        vh.bodyTextView.setText(post.getBodyTextWithSpans(convertView.getContext()));
        vh.bodyTextView.setOnClickListener(postClickListener);
        vh.ageTextView.setText(DateUtils.getTimeDifference(post.getCreatedAt()));
        vh.likeCountTextView.setText(String.valueOf(post.getLikesCount()));

        vh.likeCountTextView.setOnClickListener(v -> {
            mCallback.onLikeCountClicked(post.getId());
        });

        vh.userAvatar.initView(post.getAvatarUrl(), post.getUserFirstName(), post.getUserLastName());
        vh.userAvatar.setOnClickListener(v -> goToUserProfile(post, v.getContext()));
        vh.userNameTextView.setOnClickListener(v -> goToUserProfile(post, v.getContext()));
        vh.handleTextView.setOnClickListener(v -> goToUserProfile(post, v.getContext()));

        String userName = post.getUserDisplayName();
        if (userName.length() > MAX_USERNAME_LENGTH) {
            userName = userName.substring(0, MAX_USERNAME_LENGTH) + '\u2026';
        }
        vh.userNameTextView.setText(userName);

        if (!mPostNeedsToBeExpanded) {
            String handle = post.getUsername();
            if (handle != null) {
                vh.handleTextView.setText("@".concat(handle));
            } else {
                vh.handleTextView.setText("");
            }
        }

        vh.commentCountTextView.setText(String.valueOf(post.getCommentCount()));

        ImageButton postCommentButton = (ImageButton) convertView.findViewById(R.id.post_comment_button);
        postCommentButton.setOnClickListener(postClickListener);

        if (post.getHasLiked()) {
            vh.likeButton.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.like_orange));
        } else {
            vh.likeButton.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.like_grey));
        }

        vh.likeButton.setOnClickListener(v -> {
            if (post.getHasLiked()) {
                mCallback.onUnlikeClicked(post);
            } else {
                mCallback.onLikeClicked(post);
            }
        });

        vh.shareButton.setOnClickListener(v -> mCallback.onShareClicked(post));

        vh.optionCarrot.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(v.getContext(), vh.optionCarrot);

            if (post.getUserId().equals(mUserId)) {
                mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_options_edit_delete);
            } else {
                if (mGroup != null && mGroup.getCreatorId().equals(mUserId)) {
                    switch (post.getGroupStatus()) {
                        case Group.GROUP_STATUS_MEMBER:
                            if (mGroup.getGroupType() == Group.DISCUSSION_KEY) {
                                mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_options_delete_block);
                            } else {
                                mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_options_delete_remove);
                            }
                            break;
                        case Group.GROUP_STATUS_FOLLOWER:
                            mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_options_delete_block);
                            break;
                        case Group.GROUP_STATUS_BLOCKED:
                            mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_options_delete_unblock);
                            break;
                        default:
                            if (mGroup.getGroupType() == Group.PRIVATE_KEY) {
                                mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_options_delete);
                            } else {
                                mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_options_delete_block);
                            }
                            break;
                    }
                } else if (mBusiness != null && mBusiness.getIsOwner()) {
                    mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_options_delete);
                } else {
                    mPopUpMenuUtil.showPopUpMenu(popupMenu, R.menu.post_options_flag);
                }
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.post_option_delete:
                        mCallback.onPostDeleteClicked(post);
                        break;
                    case R.id.post_option_edit:
                        mCallback.onPostEditClicked(post);
                        break;
                    case R.id.post_option_flag:
                        mCallback.onPostFlagClicked(post);
                        break;
                    case R.id.post_option_remove:
                        mCallback.onRemoveFromGroupClicked(post, mGroup);
                        break;
                    case R.id.post_option_block:
                        mCallback.onBlockUserClicked(post, mGroup);
                        break;
                    case R.id.post_option_unblock:
                        mCallback.onUnblockUserClicked(post, mGroup);
                        break;
                }
                return false;
            });
        });
    }

    private void goToUserProfile(Post post, Context context) {
        context.startActivity(new Intent(context, ProfileActivity.class)
                .putExtra("profileId", String.valueOf(post.getUserId())));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.
                from(viewGroup.getContext()).inflate(R.layout.post_text_list_item, viewGroup, false));
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void removePost(Post post) {
        boolean itemWasRemoved = false;
        int postId = post.getId();
        if (mPosts == null || mPosts.size() < 1) {
            throw new IllegalStateException("List of posts are empty");
        }
        for (int i = 0; i < mPosts.size(); i++) {
            if (mPosts.get(i).getId() == postId) {
                mPosts.remove(i);
                notifyItemRemoved(i);
                itemWasRemoved = true;
                break;
            }
        }
        if (!itemWasRemoved) {
            throw new IllegalStateException(String.format("Post %s not present in list", post.toString()));
        }
    }

    public void insertPost(Post post, int i) {
        mPosts.add(i, post);
        notifyItemInserted(i);
    }

    public interface PostAdapterCallback {

        void onLikeClicked(Post post);

        void onUnlikeClicked(Post post);

        void onShareClicked(Post post);

        void onPostEditClicked(Post post);

        void onPostDeleteClicked(Post post);

        void onPostFlagClicked(Post post);

        void onRemoveFromGroupClicked(Post post, Group mGroup);

        void onBlockUserClicked(Post post, Group mGroup);

        void onUnblockUserClicked(Post post, Group mGroup);

        void onNearingEndOfList();

        void onLikeCountClicked(int postId);

        void onImageClicked(Post post);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_layout)
        RelativeLayout imageContainer;
        @BindView(R.id.post_text_view)
        TextView bodyTextView;
        @BindView(R.id.post_comment_count)
        TextView commentCountTextView;
        @BindView(R.id.post_like_count)
        TextView likeCountTextView;
        @BindView(R.id.post_date)
        TextView ageTextView;
        @BindView(R.id.post_comment_button)
        ImageButton postCommentButton;
        @BindView(R.id.share_button)
        ImageButton shareButton;
        @BindView(R.id.media_post_image)
        ImageView postImage;
        @BindView(R.id.video_icon)
        ImageView videoIcon;
        @BindView(R.id.post_user_name)
        TextView userNameTextView;
        @BindView(R.id.post_user_avatar)
        AvatarImageView userAvatar;
        @BindView(R.id.post_like_button)
        ImageButton likeButton;
        @BindView(R.id.post_options_carrot)
        ImageButton optionCarrot;
        @BindView(R.id.post_handle)
        TextView handleTextView;

        int height;
        int width;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
