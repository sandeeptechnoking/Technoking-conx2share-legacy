package com.conx2share.conx2share.ui.feed.post_comments;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.BaseRetrofitAsyncTask;
import com.conx2share.conx2share.model.Comment;
import com.conx2share.conx2share.model.TagHolder;
import com.conx2share.conx2share.model.Users;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.network.models.UserTag;
import com.conx2share.conx2share.network.models.param.SearchParams;
import com.conx2share.conx2share.strategies.EditPostCommentStrategy;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.feed.UserSuggestionsListBuilder;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.linkedin.android.spyglass.mentions.MentionSpan;
import com.linkedin.android.spyglass.suggestions.SuggestionsResult;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.linkedin.android.spyglass.tokenization.impl.WordTokenizer;
import com.linkedin.android.spyglass.tokenization.impl.WordTokenizerConfig;
import com.linkedin.android.spyglass.tokenization.interfaces.QueryTokenReceiver;
import com.linkedin.android.spyglass.ui.RichEditorView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class EditPostCommentFragment extends BaseFragment implements QueryTokenReceiver {

    public static final String TAG = EditPostCommentFragment.class.getSimpleName();

    public static final String EXTRA_COMMENT = "commentExtra";

    private static final int COMMENT_CHAR_LIMIT = 300;

    @InjectView(R.id.edit_post_comment_edit_text)
    RichEditorView mCommentEditText;

    @InjectView(R.id.edit_post_comment_back_button)
    ImageButton mBackButton;

    @InjectView(R.id.edit_post_comment_update)
    TextView mEditPostCommentTextView;

    @Inject
    PreferencesUtil mPreferencesUtil;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @Inject
    NetworkClient networkClient;

    private Comment mCommentToEdit;

    private ProgressDialog mProgressDialog;


    public static EditPostCommentFragment newInstance(Bundle arguments) {
        EditPostCommentFragment fragment = new EditPostCommentFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_post_comment, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCommentEditText.getMentionsEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(COMMENT_CHAR_LIMIT)});
        mCommentEditText.displayTextCounter(false);
        mCommentEditText.setQueryTokenReceiver(this);
        mCommentEditText.setSuggestionsListBuilder(new UserSuggestionsListBuilder());
        mCommentEditText.setTokenizer(
                new WordTokenizer(new WordTokenizerConfig.Builder().setThreshold(32000).build()));   // threshold set high enough to never allow non-explicit suggestions

        mProgressDialog = new ProgressDialog(getActivity());

        if (getArguments() != null && getArguments().getString(EXTRA_COMMENT) != null) {
            try {
                mCommentToEdit = NetworkClient.getGson().fromJson(getArguments().getString(EXTRA_COMMENT), Comment.class);
                mCommentEditText.setText(mCommentToEdit.getBodyTextForEditing(getActivity()));
                Selection.setSelection(mCommentEditText.getText(), mCommentEditText.getText().length());
            } catch (Exception e) {
                Log.e(TAG, "Failed to populate comment. Exception: " + e.toString());
            }
        }

        mBackButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        mEditPostCommentTextView.setOnClickListener(v -> {
            mProgressDialog.setMessage(getString(R.string.updating_comment));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            EditPostCommentStrategy editPostCommentStrategy = new EditPostCommentStrategy(getActivity(), mCommentToEdit);
            Comment updatedComment = mCommentToEdit;
            updatedComment.setBody(mCommentEditText.getText().toString());
            ArrayList<UserTag> userTags = TagHolder.getUserTagsFromText(mCommentEditText.getMentionSpans(), mCommentEditText.getText(), mPreferencesUtil.getAuthUser().getId());
            updatedComment.setUserTagsToSendToServer(userTags);
            editPostCommentStrategy.editPostComment(mCommentToEdit);
        });
    }

    @Override
    public List<String> onQueryReceived(@NonNull QueryToken queryToken) {
        Log.d(TAG, "onQueryReceived");
        searchUsers(queryToken);
        return Collections.singletonList(UserSuggestionsListBuilder.USER_BUCKET);
    }

    private void searchUsers(final QueryToken token) {

        addSubscription(networkClient.searchUsers(token.getKeywords(), 1, null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(users -> {
                            filterAlreadyTaggedUsers(users.getUsers());
                            SuggestionsResult suggestionResult = new SuggestionsResult(token, users.getUsers());
                            mCommentEditText.onReceiveSuggestionsResult(suggestionResult, UserSuggestionsListBuilder.USER_BUCKET);
                        },
                        throwable -> {
                            Log.e(TAG, "Could not search for users", throwable);
                            mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.search_users_error);
                        }));
    }

    /**
     * Matching iOS functionality -- previously tagged users are not displayed in autocomplete results
     * @param users users returned from server
     */
    private void filterAlreadyTaggedUsers(ArrayList<User> users) {
        List<MentionSpan> mentionSpans = mCommentEditText.getMentionSpans();
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
    }

    public void onEventMainThread(EditPostCommentStrategy.LoadEditCommentSuccessEvent event) {
        Log.d(TAG, "Received an edit comment success event");
        mProgressDialog.cancel();
        Toast.makeText(getActivity(), getString(R.string.comment_updated), Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    public void onEventMainThread(EditPostCommentStrategy.LoadEditCommentFailureEvent event) {
        Log.d(TAG, "Received an edit comment failure event");
        mProgressDialog.cancel();
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mCommentEditText.getWindowToken(), 0);
    }
}
