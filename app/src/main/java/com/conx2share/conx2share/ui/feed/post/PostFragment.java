package com.conx2share.conx2share.ui.feed.post;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.BaseRetrofitAsyncTask;
import com.conx2share.conx2share.async.EditPostAsync;
import com.conx2share.conx2share.async.FollowSearchAsync;
import com.conx2share.conx2share.async.GetGroupMembersAsync;
import com.conx2share.conx2share.async.LoadBusinessesToBusAsyncTask;
import com.conx2share.conx2share.async.LoadGroupsToBusAsyncTask;
import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.model.TagHolder;
import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.model.event.PostCreatedEvent;
import com.conx2share.conx2share.model.event.UpdatePostEvent;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.network.models.UserTag;
import com.conx2share.conx2share.network.models.param.PostParams;
import com.conx2share.conx2share.network.models.response.GetPostsResponse;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.feed.UserSuggestionsListBuilder;
import com.conx2share.conx2share.util.EmergencyUtil;
import com.conx2share.conx2share.util.EventBusUtil;
import com.conx2share.conx2share.util.LogUtil;
import com.conx2share.conx2share.util.MediaUploadUtil;
import com.conx2share.conx2share.util.PermissionUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.PrivilegeChecker;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.conx2share.conx2share.util.TypedUri;
import com.conx2share.conx2share.util.ViewUtil;
import com.linkedin.android.spyglass.mentions.MentionSpan;
import com.linkedin.android.spyglass.suggestions.SuggestionsResult;
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsListBuilder;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.linkedin.android.spyglass.tokenization.impl.WordTokenizer;
import com.linkedin.android.spyglass.tokenization.impl.WordTokenizerConfig;
import com.linkedin.android.spyglass.tokenization.interfaces.QueryTokenReceiver;
import com.linkedin.android.spyglass.ui.RichEditorView;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

public class PostFragment extends BaseFragment implements QueryTokenReceiver {

    public static String TAG = PostFragment.class.getSimpleName();

    public static final String EXTRA_POST = "post";

    private static final int POST_CHAR_LIMIT = 16383;

    @Inject
    NetworkClient mNetworkClient;

    @BindView(R.id.attachment_button)
    ImageView mAttachmentButton;

    @BindView(R.id.post_edit_text)
    RichEditorView mPostEditText;

    @BindView(R.id.preview_layout)
    RelativeLayout mPreviewLayout;

    @BindView(R.id.attachment_preview)
    ImageView mAttachmentPreview;

    @BindView(R.id.remove_attachment_button)
    ImageView mRemoveAttachmentButton;

    @BindView(R.id.post_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.post_title)
    TextView mPostTitle;

    @BindView(R.id.post_button)
    TextView mPostButton;

    @BindView(R.id.post_receiver_spinner)
    Spinner mPostReceiverSpinner;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @Inject
    private MediaUploadUtil mMediaUploadUtil;

    @Inject
    private PreferencesUtil mPreferencesUtil;

    private TypedUri mAttachmentUri;

    private List<Group> mGroupList;

    private List<Business> mBusinessList;

    private PostReceiver mPostReceiverFromIntent;

    private PostParams mPostParams;

    private PostReceiver mPostReceiver;

    private ProgressDialog mProgressDialog;

    private TypedFile mVideo;

    private boolean mNeedToUpdatePost;

    private Post mPostExtra;

    private EditPostAsync mEditPostAsync;

    private final TextWatcher mAutoCompleteTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                mPostButton.setEnabled(false);
            } else {
                mPostButton.setEnabled(true);
            }
        }
    };

    private BaseRetrofitAsyncTask mSearchUsersAsync;

    private SuggestionsListBuilder mSuggestionsListBuilder = new UserSuggestionsListBuilder();

    public static PostFragment newInstance(@Nullable PostReceiver receiver) {
        PostFragment fragment = new PostFragment();
        fragment.setIntendedPostReceiver(receiver);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMediaUploadUtil = new MediaUploadUtil(getActivity(), this);

        AppCompatActivity postActivity = ((AppCompatActivity) getActivity());
        postActivity.setSupportActionBar(mToolbar);
        postActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.back_button_dark_selector);
        postActivity.getSupportActionBar().setTitle("");
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(mPostEditText, InputMethodManager.SHOW_FORCED);

        mPostEditText.setQueryTokenReceiver(this);
        mPostEditText.displayTextCounter(false);
        mPostEditText.setSuggestionsListBuilder(mSuggestionsListBuilder);
        mPostEditText.setTokenizer(new WordTokenizer(new WordTokenizerConfig.Builder().setThreshold(32000).build()));   // threshold set high enough to never allow non-explicit suggestions

        mPostButton.setEnabled(false);

        checkForExtra();
        mPostEditText.addTextChangedListener(mAutoCompleteTextWatcher);
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressDialog = new ProgressDialog(getActivity());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA_RESULT) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                mMediaUploadUtil.launchWhatWouldYouLikeToUploadDialog();
            }
        }
    }

    @OnClick(R.id.post_button)
    public void onPostBtClick() {
        if (mNeedToUpdatePost) {
            Log.d(TAG, "mAttachmentUri: " + mAttachmentUri);
            Log.d(TAG, "mVideo: " + mVideo);

            String postBody = mPostEditText.getText().toString();
            mPostButton.setEnabled(false);

            mPostParams = new PostParams();
            mPostParams.setBody(postBody);

            if (mAttachmentUri != null) {
                mPostParams.setPicture(mAttachmentUri);
            }
            mPostParams.setPrivate(mPostReceiverSpinner.getSelectedItem().toString().equals(getString(R.string.post_followers)));

            mPostParams.setVideo(mVideo);
            mPostParams.setId(String.valueOf(mPostExtra.getId()));

            ArrayList<UserTag> userTags = TagHolder.getUserTagsFromText(mPostEditText.getMentionSpans(), mPostEditText.getText(), mPreferencesUtil.getAuthUser().getId());
            mPostParams.setUserTags(userTags);

            mProgressDialog.setMessage(getString(R.string.updating_post));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            editPost();
        } else {
            mPostButton.setEnabled(false);
            createPost();
        }
        ViewUtil.hideKeyboard(getActivity());
    }

    @OnClick(R.id.attachment_button)
    public void onAttachmentBtClick() {
        if (hasCameraPermission()) {
            mMediaUploadUtil.launchWhatWouldYouLikeToUploadDialog();
        } else {
            requestCameraPermission();
        }
    }

    @OnClick(R.id.remove_attachment_button)
    public void onRemoveAttachmentBtClick() {
        if (TextUtils.isEmpty(mPostEditText.getText().toString())) {
            mPostButton.setEnabled(false);
        }
        mPreviewLayout.setVisibility(View.GONE);
        mAttachmentPreview.setImageDrawable(null);
        mVideo = null;
        mAttachmentUri = null;
    }

    private void checkForExtra() {
        mNeedToUpdatePost = false;
        if (getActivity().getIntent().hasExtra(EXTRA_POST)) {
            Log.d(TAG, "Has post extra so we know we need to update instead of create");
            mNeedToUpdatePost = true;
            mPostTitle.setText(getString(R.string.edit_post));
            mPostButton.setText(getString(R.string.update_post));
            try {
                mPostExtra = getActivity().getIntent().getParcelableExtra(EXTRA_POST);
                Log.d(TAG, "mPostExtra.toString(): " + mPostExtra);

                mPostEditText.setText(mPostExtra.getBodyTextForEditing(getActivity()));

                Selection.setSelection(mPostEditText.getText(), mPostEditText.getText().length());
                if (mPostExtra.getImageUrl() != null) {
                    Log.d(TAG, "Has preview image");
                    mPreviewLayout.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(mPostExtra.getImageUrl())
                            .dontAnimate()
                            .into(mAttachmentPreview);
                }
                if (mPostExtra.IsPrivate()){
                    mPostReceiverSpinner.setAdapter(new PostReceiverSpinnerAdapter(getActivity()));
                }
                mPostReceiverSpinner.setEnabled(false);
            } catch (Exception e) {
                Log.e(TAG, "Failed to populate post fragment from extra. Exception: " + e.toString());
            }
        }
    }

    protected void editPost() {
        if (mEditPostAsync != null) {
            Log.w(TAG, "Edit post in progress, edit post request ignored");
            return;
        }

        mEditPostAsync = new EditPostAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<GetPostsResponse> result) {
                if (getActivity() != null) {
                    mProgressDialog.cancel();
                    mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.post_edited);
                    getActivity().finish();
                    if (result.getResource() != null && result.getResource().getPost() != null) {
                        EventBusUtil.getEventBus().post(new UpdatePostEvent(result.getResource().getPost()));
                    }
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                mProgressDialog.cancel();
                Log.e(TAG, "Could not edit post", error);

                if (getActivity() != null) {
                    mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.unable_to_edit_post, R.string.retry, snackbar -> {
                        editPost();
                        SnackbarManager.dismiss();
                    });
                    mPostButton.setEnabled(true);
                }
                mEditPostAsync = null;
            }
        }.executeInParallel(mPostParams);
    }

    // TODO - This is very similar to how MessagingActivity handles media, should consider consolidating
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        TypedUri typedUri = mMediaUploadUtil.onActivityResult(requestCode, resultCode, data);

        if (typedUri != null) {
            if (mMediaUploadUtil.getFile() != null && (mMediaUploadUtil.getFile().length() / (1024 * 1024) > MediaUploadUtil.MEGABYTE_LIMIT)) {
                mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.file_exceeds_limit);
            } else {
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "typedUri: " + typedUri);
                    Log.d(TAG, "requestCode: " + requestCode);

                    mPostButton.setEnabled(true);
                    mAttachmentUri = typedUri;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mAttachmentUri = typedUri;

                        if (DocumentsContract.isDocumentUri(getActivity(), mAttachmentUri.getUri())) {
                            String documentPath = mMediaUploadUtil.getStorageFrameworkPath(getActivity().getContentResolver(), mAttachmentUri.getUri());
                            if (documentPath != null) {
                                Uri uri = Uri.parse(documentPath);
                                mAttachmentUri.setUri(uri);
                                mAttachmentUri.setFilePath(documentPath);
                            } else {
                                // There was an error
                                Toast.makeText(getActivity(), getString(R.string.something_went_wrong_while_uploading_your_file), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    Log.d(TAG, "mAttachmentUri.getFilePath(): " + mAttachmentUri.getFilePath());

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                    BitmapFactory.decodeFile(mAttachmentUri.getFilePath(), options);
                    int width = options.outWidth;
                    int height = options.outHeight;

                    mPreviewLayout.setVisibility(View.VISIBLE);

                    if (requestCode == MediaUploadUtil.TAKE_VIDEO_REQUEST || requestCode == MediaUploadUtil.VIDEO_FROM_LIB_REQUEST) {
                        Glide.with(getActivity()).load("file:" + mAttachmentUri.getFilePath()).dontAnimate().override(width * 4, height * 4).into(mAttachmentPreview);
                    } else {
                        Glide.with(this)
                                .load("file:" + mAttachmentUri.getFilePath())
                                .into(mAttachmentPreview);
                    }

                    if (requestCode == MediaUploadUtil.TAKE_VIDEO_REQUEST || requestCode == MediaUploadUtil.VIDEO_FROM_LIB_REQUEST) {
                        mVideo = mMediaUploadUtil.getTypedVideoFile();
                    }
                } else {
                    Log.e(TAG, "resultCode was not OK. resultCode: " + resultCode);
                    mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.something_went_wrong_while_uploading_your_file);
                }
            }
        } else {
            Log.e(TAG, "typedUri is null");
        }
    }

    @OnClick(R.id.post_to_layout)
    public void spinnerDropdownClicked() {
        if (!mNeedToUpdatePost) {
            mPostReceiverSpinner.performClick();
        } else {
            mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.you_cannot_change_the_audience_when_editing_a_post);
        }
    }

    private void createPost() {

        if (mPreferencesUtil.getAuthUser() != null) {
            if (!TextUtils.isEmpty(mPostEditText.getText().toString())) {

                ArrayList<UserTag> userTagsAttributes = TagHolder.getUserTagsFromText(mPostEditText.getMentionSpans(), mPostEditText.getText(), mPreferencesUtil.getAuthUser().getId());

                String postBody = mPostEditText.getText().toString();
                mPostButton.setEnabled(false);

                mPostParams = new PostParams();
                mPostParams.setBody(postBody);
                mPostParams.setPrivate(mPostReceiverSpinner.getSelectedItemPosition() == 1);

                if (userTagsAttributes.size() > 0) {
                    mPostParams.setUserTags(userTagsAttributes);
                }

                if (mAttachmentUri != null) {
                    mPostParams.setPicture(mAttachmentUri);
                }

                mPostReceiver = (PostReceiver) mPostReceiverSpinner.getSelectedItem();
                mPostParams.setPostReceiver(mPostReceiver);

                mPostParams.setVideo(mVideo);

                new UnifiedPostAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPostParams);
            } else {
                Snackbar.with(getActivity()).text(getString(R.string.please_enter_text)).show(getActivity());
            }
        } else {
            EmergencyUtil.emergencyLogoutWithNotification(getActivity(), mPreferencesUtil);
        }
    }

    @Override
    public List<String> onQueryReceived(@NonNull QueryToken queryToken) {
        Log.d(TAG, "onQueryReceived");
        searchUsers(queryToken);
        return Collections.singletonList(UserSuggestionsListBuilder.USER_BUCKET);
    }

    private void searchUsers(final QueryToken token) {
        if (mSearchUsersAsync != null) {
            return;
        }
        //search among user followers
        if (((PostReceiver) mPostReceiverSpinner.getSelectedItem()).getType() != PostReceiver.PostReceiverType.GROUP) {

            mSearchUsersAsync = new FollowSearchAsync(getActivity()) {
                @Override
                protected void onSuccess(Result<Users> result) {
                    onSuccessUserSearch(result, token);
                }

                @Override
                protected void onFailure(RetrofitError error) {
                    Log.e(TAG, "Could not search for users", error);
                    mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.search_users_error);
                }

                @Override
                protected void onPostExecute(Result<Users> result) {
                    super.onPostExecute(result);
                    mSearchUsersAsync = null;
                }
            }.executeInParallel(token.getKeywords(), null, null);

        // search among group members
        } else {
            mSearchUsersAsync = new GetGroupMembersAsync(getActivity()) {
                @Override
                protected void onSuccess(Result<Users> result) {
                    for (int i = result.getResource().getUsers().size() - 1; i >= 0; i--) {
                        if (!result.getResource().getUsers().get(i).isUserMatchQuery(token.getKeywords())){
                            result.getResource().getUsers().remove(i);
                        }
                    }
                    onSuccessUserSearch(result, token);
                }

                @Override
                protected void onFailure(RetrofitError error) {
                    Log.e(TAG, "Could not search for users", error);
                    mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.search_users_error);
                }

                @Override
                protected void onPostExecute(Result<Users> result) {
                    super.onPostExecute(result);
                    mSearchUsersAsync = null;
                }
            }.executeInParallel(String.valueOf(((Group)mPostReceiverSpinner.getSelectedItem()).getId()));
        }
    }

    private void onSuccessUserSearch(Result<Users> result, QueryToken token) {
        ArrayList<User> users = result.getResource().getUsers();
        List<MentionSpan> mentionSpans = mPostEditText.getMentionSpans();
        for (MentionSpan span : mentionSpans) {
            if (span.getMention() instanceof User) {
                for (int i = users.size() - 1; i >= 0; i--) {
                    User user = users.get(i);
                    if (user.getId() == span.getMention().getId()) {
                        users.remove(i);
                        Log.d(TAG, "User already tagged, removing from results");
                    }
                }
            }
        }

        mPostEditText.onReceiveSuggestionsResult(new SuggestionsResult(token, users),
                UserSuggestionsListBuilder.USER_BUCKET);
    }


    public class UnifiedPostAsyncTask extends AsyncTask<PostParams, Void, Result<Post>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setMessage(getString(R.string.posting));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Result<Post> doInBackground(PostParams... params) {
            switch (params[0].getPostReceiver().getType()) {
                case GROUP:
                    return mNetworkClient.createGroupPost(params[0]);
                case EVERYONE:
                    return mNetworkClient.createPost(params[0]);
                case FOLLOWERS:
                    return mNetworkClient.createPost(params[0]);
                case BUSINESS:
                    return mNetworkClient.createBusinessPost(params[0]);
                default:
                    return new Result<>(RetrofitError.unexpectedError(null, new IllegalStateException("Invalid Post Receiver Type")));
            }
        }

        @Override
        protected void onPostExecute(Result<Post> postResult) {
            super.onPostExecute(postResult);

            if (getActivity() != null && !getActivity().isFinishing()) {
                if (postResult != null) {
                    mPostButton.setEnabled(true);
                    mProgressDialog.cancel();
                    handlePostCreation(postResult);
                } else {
                    mPostButton.setEnabled(true);
                }
            }
        }
    }

    private void handlePostCreation(Result<Post> result) {
        if (getActivity() != null && !getActivity().isFinishing() && result != null) {
            Log.i(TAG, "handling post creation");
            mPostButton.setEnabled(true);

            if (result.getError() == null) {
                Log.d(TAG, "Post successful");
                EventBusUtil.getEventBus().post(new PostCreatedEvent(result.getResource()));

                getActivity().finish();
            } else {
                Log.e(TAG, "Post not created", result.getError());
                mSnackbarUtil.showRetry(getActivity(), R.string.unable_to_post_text, snackbar -> {
                    new UnifiedPostAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPostParams);
                    SnackbarManager.dismiss();
                });
            }
        }
    }

    void setIntendedPostReceiver(PostReceiver receiver) {
        mPostReceiverFromIntent = receiver;
    }

    public void onEventMainThread(LoadGroupsToBusAsyncTask.LoadGroupSuccessEvent event) {
        Log.i(TAG, "fragment received event and is loading groups");
        if (mPostExtra != null && mPostExtra.IsPrivate()) return;
        if (mPostReceiverSpinner.getCount() > 0) return;
        if (mGroupList == null) {

            mGroupList = new ArrayList<>();

            List<Group> groupsFromEvent = event.getGroups();

            for (Group group : groupsFromEvent) {
                if (!group.isBlocked()) {
                    if (!PrivilegeChecker.isConx2ShareGroup(group.getId())) {
                        mGroupList.add(group);
                    }
                } else {
                    if (mPostExtra != null && (group.getId() == mPostExtra.getGroupId())) {
                        mGroupList.add(group);
                    }
                }
            }

            PostReceiverSpinnerAdapter adapter = new PostReceiverSpinnerAdapter(getActivity(), mGroupList, mBusinessList);
            mPostReceiverSpinner.setAdapter(adapter);

            if (mPostReceiverFromIntent != null) {
                Integer position = adapter.getPositionOfPostReceiver(mPostReceiverFromIntent);
                if (position != null) {
                    mPostReceiverSpinner.setSelection(position);
                } else {
                    if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "unable to find spinner position of receiver - " + mPostReceiverFromIntent.getName());
                    }
                }
            }

        }else{
            mPostReceiverSpinner.setAdapter(new PostReceiverSpinnerAdapter(getActivity(), null, null));
        }
    }

    public void onEventMainThread(LoadGroupsToBusAsyncTask.LoadGroupFailureEvent event) {
        if (mPostExtra != null && mPostExtra.IsPrivate()) return;
        if (mPostReceiverSpinner.getCount() > 0) return;
        mPostReceiverSpinner.setAdapter(new PostReceiverSpinnerAdapter(getActivity(), null, null));
    }

    public void onEventMainThread(LoadBusinessesToBusAsyncTask.LoadBusinessesSuccessEvent event) {
        if (mPostExtra != null && mPostExtra.IsPrivate()) return;
        if (mPostReceiverSpinner.getCount() > 0) return;
        Log.i(TAG, "fragment received event and is loading businesses");
        if (mNeedToUpdatePost) return;
        if (mBusinessList == null) {

            mBusinessList = event.getBusinesses();
            PostReceiverSpinnerAdapter adapter = new PostReceiverSpinnerAdapter(getActivity(), mGroupList, mBusinessList);
            mPostReceiverSpinner.setAdapter(adapter);

            if (mPostReceiverFromIntent != null) {
                Integer position = adapter.getPositionOfPostReceiver(mPostReceiverFromIntent);
                if (position != null) {
                    mPostReceiverSpinner.setSelection(position);
                } else {
                    if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "unable to find spinner position of receiver " + mPostReceiverFromIntent.getName());
                    }
                }
            }

        }else{
            mPostReceiverSpinner.setAdapter(new PostReceiverSpinnerAdapter(getActivity(), null, null));
        }
    }

    public void onEventMainThread(LoadBusinessesToBusAsyncTask.LoadBusinessFailureEvent event) {
        if (mPostExtra != null && mPostExtra.IsPrivate()) return;
        if (mPostReceiverSpinner.getCount() > 0) return;
        mPostReceiverSpinner.setAdapter(new PostReceiverSpinnerAdapter(getActivity(), null, null));
    }
}