package com.conx2share.conx2share.ui.feed.post_comments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.PostCommentsAdapter;
import com.conx2share.conx2share.async.BaseRetrofitAsyncTask;
import com.conx2share.conx2share.async.FollowSearchAsync;
import com.conx2share.conx2share.async.GetFollowersAsync;
import com.conx2share.conx2share.async.GetGroupAsync;
import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.model.Comment;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.model.GroupResponse;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.model.TagHolder;
import com.conx2share.conx2share.model.UserIdWrapper;
import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.model.event.UpdatePostEvent;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.Like;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.network.models.UserTag;
import com.conx2share.conx2share.network.models.param.CommentWrapper;
import com.conx2share.conx2share.network.models.response.GetPostCommentsResponse;
import com.conx2share.conx2share.network.models.response.GetPostsResponse;
import com.conx2share.conx2share.strategies.BlockUserStrategy;
import com.conx2share.conx2share.strategies.DeletePostCommentStrategy;
import com.conx2share.conx2share.strategies.DeletePostStrategy;
import com.conx2share.conx2share.strategies.EditPostCommentStrategy;
import com.conx2share.conx2share.strategies.EditPostStrategy;
import com.conx2share.conx2share.strategies.FlagPostStrategy;
import com.conx2share.conx2share.strategies.RemoveUserFromGroupStrategy;
import com.conx2share.conx2share.strategies.UnblockUserStrategy;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.dialog.ShareDialogFragment;
import com.conx2share.conx2share.ui.feed.UserSuggestionsListBuilder;
import com.conx2share.conx2share.ui.likers.LikersActivity;
import com.conx2share.conx2share.ui.messaging.MediaViewerActivity;
import com.conx2share.conx2share.util.EventBusUtil;
import com.conx2share.conx2share.util.ForegroundUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.conx2share.conx2share.util.ViewUtil;
import com.linkedin.android.spyglass.mentions.MentionSpan;
import com.linkedin.android.spyglass.suggestions.SuggestionsResult;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.linkedin.android.spyglass.tokenization.impl.WordTokenizer;
import com.linkedin.android.spyglass.tokenization.impl.WordTokenizerConfig;
import com.linkedin.android.spyglass.tokenization.interfaces.QueryTokenReceiver;
import com.linkedin.android.spyglass.ui.RichEditorView;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.OnClick;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class PostCommentsFragment extends BaseFragment implements PostCommentsAdapter.PostCommentsCallback,
        QueryTokenReceiver {

    private static final String TAG = PostCommentsFragment.class.getSimpleName();
    public static final String EXTRA_POST_ID = "postId";
    public static final String EXTRA_POST_POSITION = "postPosition";
    public static final int IMAGE_SIZE = 450;
    private static final int COMMENT_CHAR_LIMIT = 300;

    @Inject
    NetworkClient networkClient;
    @Inject
    PreferencesUtil mPreferencesUtil;
    @Inject
    SnackbarUtil mSnackbarUtil;

    @InjectView(R.id.post_comments_toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.post_comments_listview)
    ListView mPostCommentsListView;
    @InjectView(R.id.comment_edit_text)
    RichEditorView mCommentEditText;
    @InjectView(R.id.submit_comment)
    TextView mSubmitComment;
    @InjectView(R.id.comments_toolbar_title)
    TextView mToolbarTitle;
    @InjectView(R.id.leave_a_comment_bar)
    LinearLayout mLeaveACommentBar;
    @InjectView(R.id.post_button_layout)
    LinearLayout mButtonLayout;

    private String mPostId;
    private Post mPost;
    private int mPostPosition;
    private PostCommentsAdapter mPostCommentsAdapter;
    private ArrayList<Comment> mComments = new ArrayList<>();
    private View mHeader;
    private TextView mPostCommentCount;
    private TextView mLikeCount;
    private ImageButton mCommentButton;
    private ImageButton mLikeButton;
    private ImageButton mFlagButton;
    private ImageButton mSocialButton;
    private Comment mPostComment;
    private Like mLike;
    private Boolean mPostingComment = false;
    private boolean mLiking;
    private boolean mUnliking;
    private Group mGroup;
    private Business mBusiness;
    private FlagPostStrategy mFlagPostStrategy;
    private GetGroupAsync mGetGroupAsync;
    private BaseRetrofitAsyncTask mSearchUsersAsync;
    private BaseRetrofitAsyncTask mSearchFollowersAsync;
    private ArrayList<User> mSuggestionUsers = new ArrayList<>();
    private boolean firstSuggestion, secondSuggestion;

    public static PostCommentsFragment newInstance(@Nullable Group group, @Nullable Business business) {
        PostCommentsFragment fragment = new PostCommentsFragment();
        fragment.setGroup(group);
        fragment.setBusiness(business);
        return fragment;
    }

    void setGroup(Group group) {
        mGroup = group;
    }

    void setBusiness(Business business) {
        mBusiness = business;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPostId = getArguments().getString(EXTRA_POST_ID);
        mPostPosition = getArguments().getInt(EXTRA_POST_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_comments, container, false);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHeader = getLayoutInflater(savedInstanceState).inflate(R.layout.post_comments_listview_header, null);
        mPostCommentsListView.addHeaderView(mHeader);

        AppCompatActivity postCommentsActivity = ((AppCompatActivity) getActivity());

        mToolbar.setOnClickListener(v -> getActivity().finish());

        mSubmitComment.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(mCommentEditText.getText())) {
                if (!mPostingComment) {
                    Log.i(TAG, "Post Comment enabled");
                    postComment();
                } else {
                    Log.d(TAG, "Post Comment disabled");
                }
            }

        });

        postCommentsActivity.setSupportActionBar(mToolbar);
        postCommentsActivity.getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.back_button_dark_selector);
        postCommentsActivity.getSupportActionBar().setTitle("");
        mCommentEditText.setEditTextShouldWrapContent(true);
        mCommentEditText.getMentionsEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter
                (COMMENT_CHAR_LIMIT)});
        mCommentEditText.displayTextCounter(false);
        mCommentEditText.setQueryTokenReceiver(this);
        mCommentEditText.setSuggestionsListBuilder(new UserSuggestionsListBuilder());
        mCommentEditText.setTokenizer(new WordTokenizer(new WordTokenizerConfig.Builder().setThreshold(32000).build()
        ));   // threshold set high enough to never allow non-explicit suggestions
    }

    @Override
    public void onResume() {
        super.onResume();
        getPost();
    }

    public void setupPostHeaderView() {

        ImageView postImage = (ImageView) mHeader.findViewById(R.id.media_post_image);
        TextView postText = (TextView) mHeader.findViewById(R.id.post_text_view);
        mPostCommentCount = (TextView) mHeader.findViewById(R.id.post_comment_count);
        mLikeCount = (TextView) mHeader.findViewById(R.id.post_like_count);
        mCommentButton = (ImageButton) mHeader.findViewById(R.id.post_comment_button);
        mLikeButton = (ImageButton) mHeader.findViewById(R.id.post_like_button);
        mFlagButton = (ImageButton) mHeader.findViewById(R.id.flag_button);
        mSocialButton = (ImageButton) mHeader.findViewById(R.id.share_button);

        if (mPost.getHasLiked()) {
            mLikeButton.setImageDrawable(getResources().getDrawable(R.drawable.like_orange));
        } else {
            mLikeButton.setImageDrawable(getResources().getDrawable(R.drawable.like_grey));
        }

        mLikeButton.setOnClickListener(v -> {
            if (mPost.getHasLiked()) {
                if (!mUnliking) {
                    new UnLikePostAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPost.getId());
                }
            } else {
                if (!mLiking) {
                    mLike = new Like(mPost.getId());
                    new LikePostAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mLike);
                }
            }
        });

        mLikeCount.setOnClickListener(v -> {
            Intent likersIntent = new Intent(getActivity(), LikersActivity.class);
            likersIntent.putExtra(LikersActivity.EXTRA_POST_ID, mPost.getId());
            startActivity(likersIntent);
        });

        mCommentButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, 0);
            }
        });

        mFlagButton.setOnClickListener(v -> {
            mFlagPostStrategy = new FlagPostStrategy(getActivity(), mPost);
            mFlagPostStrategy.launchFlagDialog();
        });

        mSocialButton.setOnClickListener(v -> {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            ShareDialogFragment dialogFragment = ShareDialogFragment.newInstance(mPost);
            dialogFragment.show(fm, ShareDialogFragment.TAG);
        });

        postImage.setOnClickListener(v -> {
            if (mPost.hasVideo() || mPost.hasImage()) {
                String imageUrl = mPost.getFullImageUrl();
                Intent intent = new Intent(getActivity(), MediaViewerActivity.class);
                intent.putExtra(MediaViewerActivity.IMAGE_EXTRA_KEY, imageUrl);
                if (mPost.hasVideo()) {
                    String videoUrl = mPost.getVideoUrl();
                    intent.putExtra(MediaViewerActivity.VIDEO_EXTRA_KEY, videoUrl);
                }
                startActivity(intent);
            }
        });

        if (!mPost.hasImage()) {
            Log.i(TAG, "No Post Picture");
        } else {
            Glide.with(getActivity()).load(mPost.getImageUrl())
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.broken_image)
                    .override(IMAGE_SIZE, IMAGE_SIZE)
                    .dontAnimate()
                    .fitCenter()
                    .into(postImage);
        }

        if (mPost.hasVideo()) {
            getActivity().findViewById(R.id.play_button).setVisibility(View.VISIBLE);
        } else {
            getActivity().findViewById(R.id.play_button).setVisibility(View.GONE);
        }

        mToolbarTitle.setText(mPost.getUserDisplayName());
        mLikeCount.setText(String.valueOf(mPost.getLikesCount()));
        mPostCommentCount.setText(String.valueOf(mPost.getCommentCount()));
        if (mPreferencesUtil.getAuthUser() != null
                && mPost.getUserId().equals(mPreferencesUtil.getAuthUser().getId())) {
            mButtonLayout.setVisibility(View.VISIBLE);
            mToolbarTitle.setPadding(0, 0, ViewUtil.dpToPx(80), 0);
        } else {
            mButtonLayout.setVisibility(View.GONE);
            mToolbarTitle.setPadding(0, 0, 0, 0);
        }
        postText.setText(mPost.getBodyTextWithSpans(getActivity()));
        postText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @OnClick(R.id.post_content_edit)
    public void onPostEditClick() {
        if (mPost == null) return;
        EditPostStrategy editPostStrategy = new EditPostStrategy(getActivity(), mPost);
        editPostStrategy.checkIfUserAllowedToEdit();
    }

    public void onEventMainThread(DeletePostStrategy.LoadDeletePostSuccessEvent event) {
        Log.d(TAG, "Received a delete post success event");
        if (ForegroundUtil.getAppInForeground() && getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @OnClick(R.id.post_content_delete)
    public void onPostDeleteClick() {
        if (mPost == null) return;
        DeletePostStrategy deletePostStrategy = new DeletePostStrategy(getActivity(), mPost);
        deletePostStrategy.launchDeleteDialog();
    }

    @Override
    public List<String> onQueryReceived(@NonNull
                                        final QueryToken queryToken) {
        Log.d(TAG, "onQueryReceived " + queryToken.getKeywords());
        firstSuggestion = false;
        secondSuggestion = false;
        mSuggestionUsers.clear();

        searchUsersForPostOwner(queryToken);
        searchUsers(queryToken);
        return Collections.singletonList(UserSuggestionsListBuilder.USER_BUCKET);
    }

    private void searchUsers(final QueryToken token) {
        if (mSearchUsersAsync != null) {
            return;
        }
        Log.d(TAG, "searchUsers: ");

        mSearchUsersAsync = new FollowSearchAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<Users> result) {
                filterAlreadyTaggedUsers(result.getResource().getUsers(), token);
                firstSuggestion = true;
                showSuggestion(token);
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not search for users", error);
                mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.search_users_error);
                firstSuggestion = true;
                showSuggestion(token);
            }

            @Override
            protected void onPostExecute(Result<Users> result) {
                super.onPostExecute(result);
                mSearchUsersAsync = null;
            }
        }.executeInParallel(token.getKeywords(), null, null);
    }

    private void searchUsersForPostOwner(final QueryToken token) {
        if (mSearchFollowersAsync != null) {
            return;
        }
        Log.d(TAG, "searchUsersForPostOwner: ");
        mSearchFollowersAsync = new GetFollowersAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<Users> result) {
                filteringUserByQuery(result.getResource().getUsers(), token.getKeywords());
                filterAlreadyTaggedUsers(result.getResource().getUsers(), token);
                secondSuggestion = true;
                showSuggestion(token);
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not search for users", error);
                mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.search_users_error);
                secondSuggestion = true;
                showSuggestion(token);
            }

            @Override
            protected void onPostExecute(Result<Users> result) {
                super.onPostExecute(result);
                mSearchFollowersAsync = null;
            }
        }.executeInParallel(mPost.getUserId(), null);
    }

    /**
     * Matching iOS functionality -- previously tagged users are not displayed in autocomplete results
     *
     * @param users users returned from server
     * @param token
     */
    private void filterAlreadyTaggedUsers(ArrayList<User> users, QueryToken token) {
        Log.d(TAG, "filterAlreadyTaggedUsers: " + users.size());
        List<MentionSpan> mentionSpans = mCommentEditText.getMentionSpans();
        for (MentionSpan span : mentionSpans) {
            if (span.getMention() instanceof User) {
                for (int i = users.size() - 1; i >= 0; i--) {
                    User user = users.get(i);
                    if (user.getId() != span.getMention().getId()) {
                        users.remove(i);
                        Log.d(TAG, "User already tagged, removing from results");
                    }
                }
            }
        }
        for (User user : users) {
            if (!mSuggestionUsers.contains(user)) {
                mSuggestionUsers.add(user);
            }
        }
    }

    private void showSuggestion(QueryToken token) {
        if (firstSuggestion && secondSuggestion) {
            SuggestionsResult suggestionResult = new SuggestionsResult(token, mSuggestionUsers);
            mCommentEditText.onReceiveSuggestionsResult(suggestionResult, UserSuggestionsListBuilder.USER_BUCKET);
            firstSuggestion = false;
            secondSuggestion = false;
        }
    }

    private void filteringUserByQuery(ArrayList<User> users, String keyword) {
        Log.d(TAG, "filteringUserByQuery: " + keyword);
        for (int i = users.size() - 1; i >= 0; i--) {
            User user = users.get(i);
            if (!user.isUserMatchQuery(keyword)) users.remove(i);
        }
    }

    private void postComment() {
        mPostComment = new Comment(mCommentEditText.getText().toString(), Integer.parseInt(mPostId), "Post");
        ArrayList<UserTag> userTags = TagHolder.getUserTagsFromText(mCommentEditText.getMentionSpans(),
                mCommentEditText.getText(), mPreferencesUtil.getAuthUser().getId());
        mPostComment.setUserTagsToSendToServer(userTags);
        new PostCommentAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPostComment);
    }

    private void setupPostCommentsAdapter() {
        if (mPostCommentsAdapter == null) {
            mPostCommentsAdapter = new PostCommentsAdapter(mComments, getActivity(), this,
                    mPreferencesUtil.getAuthUser().getId(), mPost.getUserId(), mGroup, mBusiness);
            mPostCommentsListView.setAdapter(mPostCommentsAdapter);
        } else {
            mPostCommentsAdapter.notifyDataSetChanged();
        }
    }

    private void adjustCommentsBarVisibility() {
        if (mGroup != null) {
            if (mGroup.isBlocked()) {
                mLeaveACommentBar.setVisibility(View.GONE);
                mCommentEditText.setVisibility(View.GONE);
                mSubmitComment.setVisibility(View.GONE);
            } else {
                if (mGroup.isMember() || mGroup.isOwner() || mGroup.isFollowing()) {
                    mLeaveACommentBar.setVisibility(View.VISIBLE);
                    mCommentEditText.setVisibility(View.VISIBLE);
                    mSubmitComment.setVisibility(View.VISIBLE);
                } else {
                    mLeaveACommentBar.setVisibility(View.GONE);
                    mCommentEditText.setVisibility(View.GONE);
                    mSubmitComment.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onPostCommentDeleteClicked(Comment comment) {
        DeletePostCommentStrategy deletePostCommentStrategy = new DeletePostCommentStrategy(getActivity(), comment);
        deletePostCommentStrategy.launchDeleteDialog();
    }

    @Override
    public void onPostCommentEditClicked(Comment comment) {
        EditPostCommentStrategy editPostCommentStrategy = new EditPostCommentStrategy(getActivity(), comment);
        editPostCommentStrategy.checkIfUserAllowedToEdit();
    }

    @Override
    public void onRemoveFromGroupClicked(Comment comment, Group group) {
        RemoveUserFromGroupStrategy removeUserFromGroupStrategy = new RemoveUserFromGroupStrategy(getActivity(),
                comment.getCommenterId(), mGroup);
        removeUserFromGroupStrategy.launchRemoveUserFromGroupConfirmationDialog();
    }

    @Override
    public void onBlockUserClicked(Comment comment, Group group) {
        BlockUserStrategy blockUserStrategy = new BlockUserStrategy(getActivity(), mGroup.getId());
        UserIdWrapper userIdWrapper = new UserIdWrapper(comment.getCommenterId());
        blockUserStrategy.launchBlockUserConfirmationDialog(userIdWrapper);
    }

    @Override
    public void onUnblockUserClicked(Comment comment, Group group) {
        UnblockUserStrategy unblockUserStrategy = new UnblockUserStrategy(getActivity(), group.getId());
        unblockUserStrategy.launchUnblockUserConfirmationDialog(String.valueOf(comment.getCommenterId()));
    }

    protected void getGroup(int groupId) {
        if (mGetGroupAsync != null) {
            Log.w(TAG, "Canceling get group request and starting a new one");
            mGetGroupAsync.cancel(true);
        }

        mGetGroupAsync = new GetGroupAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<GroupResponse> result) {
                mGroup = result.getResource().getGroup();
                new GetPostCommentsAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPostId);
                mGetGroupAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.w(TAG, "Error trying to query for group", error);
                new GetPostCommentsAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPostId);
                mGetGroupAsync = null;
            }
        }.executeInParallel(groupId);
    }

    private void getPost() {
        new GetPostAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPostId);
    }

    public void onEventMainThread(UnblockUserStrategy.LoadUnblockUserSuccessEvent event) {
        Log.d(TAG, "Received an unblock user success event");
        getPost();
        ViewUtil.hideKeyboard(getActivity());
    }

    public void onEventMainThread(BlockUserStrategy.LoadBlockUserSuccessEvent event) {
        Log.d(TAG, "Received a block user success event");
        getPost();
        ViewUtil.hideKeyboard(getActivity());
    }

    public void onEventMainThread(RemoveUserFromGroupStrategy.LoadRemoveUserFromGroupSuccessEvent event) {
        Log.d(TAG, "Received a remove user from group success event");
        getPost();
        ViewUtil.hideKeyboard(getActivity());
    }

    public void onEventMainThread(DeletePostCommentStrategy.LoadDeleteCommentSuccessEvent event) {
        Log.d(TAG, "Received a delete comment success event");
        getPost();
        ViewUtil.hideKeyboard(getActivity());
    }

    public void onEventMainThread(EditPostCommentStrategy.LoadEditCommentSuccessEvent event) {
        Log.d(TAG, "Received an edit comment success event");
        getPost();
        ViewUtil.hideKeyboard(getActivity());
    }

    public class GetPostAsync extends AsyncTask<String, Void, Result<GetPostsResponse>> {

        @Override
        protected Result<GetPostsResponse> doInBackground(String... params) {
            return networkClient.getPost(params[0]);
        }

        @Override
        protected void onPostExecute(Result<GetPostsResponse> getPostsResponseResult) {
            super.onPostExecute(getPostsResponseResult);

            if (getActivity() != null) {
                if (getPostsResponseResult != null && getPostsResponseResult.getResource() != null &&
                        getPostsResponseResult.getError() == null) {
                    mPost = getPostsResponseResult.getResource().getPost();
                    setupPostHeaderView();
                    if (mPost.getGroupId() != null) {
                        Log.d(TAG, "Post has a group id...need to query for group before loading comments");
                        getGroup(mPost.getGroupId());
                    } else {
                        new GetPostCommentsAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPostId);
                    }
                } else {
                    SnackbarManager.show(Snackbar.with(getActivity().getApplicationContext()).type(SnackbarType
                            .MULTI_LINE).text(getString(R.string.unable_to_get_post_text)).actionLabel(getString(R
                            .string.retry)).actionListener(snackbar -> {
                        new GetPostAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPostId);
                        SnackbarManager.dismiss();
                    }), getActivity());
                }
            }
        }
    }

    public class GetPostCommentsAsync extends AsyncTask<String, Void, Result<GetPostCommentsResponse>> {

        @Override
        protected Result<GetPostCommentsResponse> doInBackground(String... params) {
            return networkClient.getPostComments(params[0], "1");
        }

        @Override
        protected void onPostExecute(Result<GetPostCommentsResponse> getPostCommentsResponseResult) {
            super.onPostExecute(getPostCommentsResponseResult);

            if (getActivity() != null) {
                if (getPostCommentsResponseResult != null && getPostCommentsResponseResult.getResource() != null &&
                        getPostCommentsResponseResult.getError() == null) {
                    mComments.clear();
                    mComments.addAll(getPostCommentsResponseResult.getResource().getComments());

                    setupPostCommentsAdapter();
                    adjustCommentsBarVisibility();
                } else {
                    SnackbarManager.show(Snackbar.with(getActivity()).type(SnackbarType.MULTI_LINE).text(getString(R
                            .string.unable_to_get_post_comments_text)).actionLabel(getString(R.string.retry))
                            .actionListener(snackbar -> {
                                new GetPostCommentsAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPostId);
                                SnackbarManager.dismiss();
                            }), getActivity());
                }
            }
        }
    }

    // TODO - Refactor this
    public class PostCommentAsync extends AsyncTask<Comment, Void, Result<GetPostCommentsResponse>> {

        @Override
        protected Result<GetPostCommentsResponse> doInBackground(Comment... params) {
            CommentWrapper wrapper = new CommentWrapper(params[0]);
            return networkClient.postComment(wrapper);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPostingComment = true;
        }

        @Override
        protected void onPostExecute(Result<GetPostCommentsResponse> getPostCommentsResponseResult) {
            super.onPostExecute(getPostCommentsResponseResult);

            mPostingComment = false;
            ViewUtil.hideKeyboard(getActivity());
            if (getActivity() != null) {
                TextView submitComment = (TextView) getActivity().findViewById(R.id.submit_comment);
                submitComment.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.conx_teal));
                if (getPostCommentsResponseResult != null && getPostCommentsResponseResult.getResource() != null &&
                        getPostCommentsResponseResult.getError() == null) {
                    mCommentEditText.setText("");

                    mComments.add(getPostCommentsResponseResult.getResource().getComment());
                    setupPostCommentsAdapter();
                    if (mPostCommentCount != null) {
                        mPostCommentCount.setText(String.valueOf(Integer.valueOf(mPostCommentCount.getText().toString
                                ()) + 1));
                    }
                    ViewUtil.hideKeyboard(getActivity());
                    if (!mComments.isEmpty()) {
                        mPostCommentsListView.smoothScrollToPosition(mComments.size());
                    }
                    EventBusUtil.getEventBus().post(new UpdatePostEvent(mPostPosition, UpdatePostEvent.PostEventType
                            .COMMENT_COUNT));
                } else {
                    if (getPostCommentsResponseResult != null && getPostCommentsResponseResult.getError() != null &&
                            getPostCommentsResponseResult.getError().getResponse() != null &&
                            getPostCommentsResponseResult.getError().getResponse().getStatus() == 403) {
                        mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.unable_to_post_comment_blocked);
                    } else {
                        mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_post_comment_text, R
                                .string.retry, snackbar -> {
                            new PostCommentAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPostComment);
                            SnackbarManager.dismiss();
                        });
                    }
                }
            }
        }
    }

    // TODO - Extract these Async tasks and clean up this frag
    public class LikePostAsync extends AsyncTask<Like, Void, Result<Like>> {

        @Override
        protected Result<Like> doInBackground(Like... params) {

            return networkClient.likePost(params[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLiking = true;
        }

        @Override
        protected void onPostExecute(Result<Like> likeResult) {

            super.onPostExecute(likeResult);
            mLiking = false;
            if (getActivity() != null) {
                if (likeResult.getError() == null && likeResult.getResource() != null) {
                    Integer currentLikeCount = Integer.parseInt(mLikeCount.getText().toString());
                    mLikeCount.setText(String.valueOf(currentLikeCount + 1));
                    mLikeButton.setImageDrawable(getResources().getDrawable(R.drawable.like_orange));
                    mPost.setHasLiked(true);
                    EventBusUtil.getEventBus().post(new UpdatePostEvent(mPostPosition, UpdatePostEvent.PostEventType
                            .LIKE_COUNT_INCREASE));
                } else {
                    mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_like_post_text, R.string
                            .retry, snackbar -> {
                        new LikePostAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mLike);
                        SnackbarManager.dismiss();
                    });
                }
            }
        }
    }

    public class UnLikePostAsync extends AsyncTask<Integer, Void, Result<Like>> {

        @Override
        protected Result<Like> doInBackground(Integer... params) {
            return networkClient.unlikePost(params[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mUnliking = true;
        }

        @Override
        protected void onPostExecute(Result<Like> likeResult) {
            super.onPostExecute(likeResult);
            mUnliking = false;
            if (getActivity() != null) {
                if (likeResult.getError() == null && likeResult.getResource() != null) {
                    Integer currentLikeCount = Integer.parseInt(mLikeCount.getText().toString());
                    mLikeCount.setText(String.valueOf(currentLikeCount - 1));
                    mLikeButton.setImageDrawable(getResources().getDrawable(R.drawable.like_grey));
                    mPost.setHasLiked(false);
                    EventBusUtil.getEventBus().post(new UpdatePostEvent(mPostPosition, UpdatePostEvent.PostEventType
                            .LIKE_COUNT_DECREASE));
                } else {
                    mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_like_post_text, R.string
                            .retry, snackbar -> {
                        new UnLikePostAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPost.getId());
                        SnackbarManager.dismiss();
                    });
                }
            }
        }
    }
}
