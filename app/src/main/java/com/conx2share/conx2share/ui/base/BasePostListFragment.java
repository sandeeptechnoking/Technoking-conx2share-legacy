package com.conx2share.conx2share.ui.base;

import android.content.Intent;
import android.util.Log;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.PostsAdapter;
import com.conx2share.conx2share.async.LikePostAsync;
import com.conx2share.conx2share.async.LikeUnlikeHelper;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.model.UserIdWrapper;
import com.conx2share.conx2share.model.event.PostCreatedEvent;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.Like;
import com.conx2share.conx2share.strategies.BlockUserStrategy;
import com.conx2share.conx2share.strategies.DeletePostStrategy;
import com.conx2share.conx2share.strategies.EditPostStrategy;
import com.conx2share.conx2share.strategies.FlagPostStrategy;
import com.conx2share.conx2share.strategies.RemoveUserFromGroupStrategy;
import com.conx2share.conx2share.strategies.UnblockUserStrategy;
import com.conx2share.conx2share.ui.dialog.ShareDialogFragment;
import com.conx2share.conx2share.ui.messaging.MediaViewerActivity;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.nispok.snackbar.SnackbarManager;

import javax.inject.Inject;

import retrofit.RetrofitError;

public abstract class BasePostListFragment extends BaseFragment implements PostsAdapter.PostAdapterCallback {

    public static final String TAG = BasePostListFragment.class.getSimpleName();

    @Inject
    protected SnackbarUtil mSnackbarUtil;

    protected PostsAdapter mPostsAdapter;

    private LikePostAsync mLikePostAsync;

    private LikePostAsync mUnlikePostAsync;

    protected abstract void refreshPosts();

    protected abstract void scrollTo(int position);

    @Override
    public void onLikeClicked(Post post) {
        likePost(post);
    }

    @Override
    public void onUnlikeClicked(Post post) {
        unlikePost(post);
    }

    @Override
    public void onShareClicked(Post post) {
        ShareDialogFragment.sharePost(getActivity().getSupportFragmentManager(), post);
    }

    @Override
    public void onNearingEndOfList() {
        // TODO: refactor logic to pull more posts to here?
    }

    @Override
    public void onPostEditClicked(Post post) {
        EditPostStrategy editPostStrategy = new EditPostStrategy(getActivity(), post);
        editPostStrategy.checkIfUserAllowedToEdit();
    }

    @Override
    public void onPostDeleteClicked(Post post) {
        DeletePostStrategy deletePostStrategy = new DeletePostStrategy(getActivity(), post);
        deletePostStrategy.launchDeleteDialog();
    }

    @Override
    public void onRemoveFromGroupClicked(Post post, Group mGroup) {
        RemoveUserFromGroupStrategy removeUserFromGroupStrategy = new RemoveUserFromGroupStrategy(getActivity(), post.getUserId(), mGroup);
        removeUserFromGroupStrategy.launchRemoveUserFromGroupConfirmationDialog();
    }

    @Override
    public void onBlockUserClicked(Post post, Group mGroup) {
        BlockUserStrategy blockUserStrategy = new BlockUserStrategy(getActivity(), mGroup.getId());
        UserIdWrapper userIdWrapper = new UserIdWrapper(post.getUserId());
        blockUserStrategy.launchBlockUserConfirmationDialog(userIdWrapper);
    }

    @Override
    public void onUnblockUserClicked(Post post, Group mGroup) {
        UnblockUserStrategy unblockUserStrategy = new UnblockUserStrategy(getActivity(), mGroup.getId());
        unblockUserStrategy.launchUnblockUserConfirmationDialog(String.valueOf(post.getUserId()));
    }

    @Override
    public void onPostFlagClicked(Post post) {
        FlagPostStrategy flagPostStrategy = new FlagPostStrategy(getActivity(), post);
        flagPostStrategy.launchFlagDialog();
    }

    @Override
    public void onImageClicked(Post post) {
        if (post.hasVideo() || post.hasImage()) {
            String imageUrl = post.getFullImageUrl();
            Intent intent = new Intent(getActivity(), MediaViewerActivity.class);
            intent.putExtra(MediaViewerActivity.IMAGE_EXTRA_KEY, imageUrl);
            intent.putExtra(MediaViewerActivity.IMAGE_EXTRA_KEY_SMALL, post.getImageUrl());
            if (post.hasVideo()) {
                String videoUrl = post.getVideoUrl();
                intent.putExtra(MediaViewerActivity.VIDEO_EXTRA_KEY, videoUrl);
            }
            startActivity(intent);
        }
    }

    public void onEventMainThread(UnblockUserStrategy.LoadUnblockUserSuccessEvent event) {
        Log.d(TAG, "Received an unblock user success event");
        refreshPosts();
    }

    public void onEventMainThread(BlockUserStrategy.LoadBlockUserSuccessEvent event) {
        Log.d(TAG, "Received a block user success event");
        refreshPosts();
    }

    public void onEventMainThread(RemoveUserFromGroupStrategy.LoadRemoveUserFromGroupSuccessEvent event) {
        Log.d(TAG, "Received a remove user from group success event");
        refreshPosts();
    }

    public void onEventMainThread(DeletePostStrategy.LoadDeletePostSuccessEvent event) {
        Log.d(TAG, "Received a delete post success event");
        mPostsAdapter.removePost(event.getPost());
    }

    public void onEventMainThread(PostCreatedEvent event) {
        Log.d(TAG, "Post has been created");
        mPostsAdapter.insertPost(event.post, 0);
        scrollTo(0);
    }

    protected void likePost(Post post) {
        if (mLikePostAsync != null) {
            Log.w(TAG, "Like in progress, new like request ignored");
            // TODO: queue like requests?
            return;
        }

        mLikePostAsync = new LikePostAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<Like> result) {
                getHelper().post.setLikesCount(getHelper().post.getLikesCount() + 1);
                getHelper().post.setHasLiked(true);
                mPostsAdapter.notifyDataSetChanged();
                mLikePostAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not like post", error);
                mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_like_post_text, R.string.retry, snackbar -> {
                    likePost(getHelper().post);
                    SnackbarManager.dismiss();
                });
                mLikePostAsync = null;
            }
        }.executeInParallel(new LikeUnlikeHelper(true, new Like(post.getId()), post));
    }

    protected void unlikePost(Post post) {
        if (mUnlikePostAsync != null) {
            Log.w(TAG, "Unlike in progress, unlike request ignored");
            // TODO: queue unlike requests?
            return;
        }

        mUnlikePostAsync = new LikePostAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<Like> result) {
                if (getActivity() != null) {
                    getHelper().post.setLikesCount(getHelper().post.getLikesCount() - 1);
                    getHelper().post.setHasLiked(false);
                    mPostsAdapter.notifyDataSetChanged();
                    mUnlikePostAsync = null;
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not unlike post", error);
                if (getActivity() != null) {
                    mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_unlike_post_text, R.string.retry, snackbar -> {
                        unlikePost(getHelper().post);
                        SnackbarManager.dismiss();
                    });
                }
                mUnlikePostAsync = null;
            }
        }.executeInParallel(new LikeUnlikeHelper(false, null, post));
    }
}
