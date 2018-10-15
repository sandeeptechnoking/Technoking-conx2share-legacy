package com.conx2share.conx2share.ui.dialog;

import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.ui.base.BaseDialogFragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.Window;

public class ShareDialogFragment extends BaseDialogFragment {

    public static final String TAG = ShareDialogFragment.class.getSimpleName();

    public static final String SHARE_POST_EXTRA = "SHARE_POST_EXTRA";

    private static final String EXCEPTION_TEXT = "Post must not be null in order to be shared. Please add a post as an extra to this dialog fragment";

    private Post mPost;

    public static ShareDialogFragment newInstance(Post post) {
        ShareDialogFragment fragment = new ShareDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(SHARE_POST_EXTRA, post);
        fragment.setArguments(args);
        return fragment;
    }

    public static void sharePost(FragmentManager fm, Post post) {
        ShareDialogFragment dialogFragment = ShareDialogFragment.newInstance(post);
        dialogFragment.show(fm, ShareDialogFragment.TAG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPost = getArguments().getParcelable(SHARE_POST_EXTRA);
        if (mPost == null) {
            throw new RuntimeException(EXCEPTION_TEXT);
        }
        shareDialog();

    }

/*    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_share, container, false);
    }*/

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String truncateTextToShare(String postBody){
        String shareBody = postBody;
        try {
            String shareToString = mPost.getShareToTwitterString(getResources());

            // Subtract an extra 3 for ellipses and 1 for quote
            int maxLength = 140 - shareToString.length() - 4;

            if (postBody.length() >= maxLength) { // Adding 1 for quote
                postBody = postBody.substring(0, maxLength);
                shareBody = "\"" + postBody + "..." + shareToString;
            } else {
                shareBody = "\"" + postBody + shareToString;
            }
        } catch (StringIndexOutOfBoundsException e) {
            Log.e(TAG, "StringIndexOutOfBoundsException occurred trying to truncate text to share", e);
        }

        return shareBody;
    }

    private void shareDialog() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");

        String shareBody = truncateTextToShare(mPost.getBody());

        if (mPost.hasVideo()) {
            String videoUrl = mPost.getVideoUrl();
            Log.d(TAG, "videoUrl: " + videoUrl);

            String send = shareBody + " - " + videoUrl;
            Log.d(TAG,send);

            sendIntent.putExtra(Intent.EXTRA_TEXT, send);
        }else if (mPost.hasImage()) {
            String imageUrl = mPost.getImageUrl();
            Log.d(TAG, "imageUrl: " + imageUrl);

            String send = shareBody + " - " + imageUrl;
            Log.d(TAG,send);

            sendIntent.putExtra(Intent.EXTRA_TEXT, send);
        }else{
            Log.d(TAG, "no video or image");
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        }

        startActivity(Intent.createChooser(sendIntent,"Send to..."));
        dismiss();
    }
}

