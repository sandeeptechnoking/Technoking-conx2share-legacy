package com.conx2share.conx2share.ui.groups;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.async.CreateGroupAsync;
import com.conx2share.conx2share.async.UpdateGroupAsync;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.model.GroupResponse;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.param.GroupParam;
import com.conx2share.conx2share.strategies.DeleteGroupStrategy;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.util.LogUtil;
import com.conx2share.conx2share.util.MediaUploadUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.conx2share.conx2share.util.TypedUri;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nispok.snackbar.SnackbarManager;

import javax.inject.Inject;

//import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class AddGroupFragment extends BaseFragment {

    public static final String TAG = AddGroupFragment.class.getSimpleName();

    @Inject
    NetworkClient mNetworkClient;

    @InjectView(R.id.header_background_imageView)
    ImageView mHeaderBackgroundImage;

    @InjectView(R.id.avatar)
    RoundedImageView mAvatar;

    @InjectView(R.id.group_about_editText)
    EditText mAboutEditText;

    @InjectView(R.id.name_textView)
    TextView mName;

    @InjectView(R.id.delete_group_tv)
    TextView mDeleteGroup;

    @InjectView(R.id.group_name_editText)
    EditText mNameEditText;

    @InjectView(R.id.change_photo_button)
    Button mChangePhotoButton;

    @InjectView(R.id.edit_group_toolbar)
    Toolbar mToolbar;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @InjectView(R.id.group_restriction)
    RadioGroup mGroupRestriction;

    @InjectView(R.id.group_public)
    RadioButton mGroupPublic;

    @InjectView(R.id.group_private)
    RadioButton mGroupPrivate;

    @InjectView(R.id.group_type)
    RadioGroup mGroupType;

    @InjectView(R.id.group_blog)
    RadioButton mGroupBlog;

    @InjectView(R.id.group_discussion)
    RadioButton mGroupDiscussion;

    @Inject
    private MediaUploadUtil mMediaUploadUtil;

    private Group mGroup;

    private TypedUri mAttachmentUri;

    private String mGroupName;

    private String mGroupAbout;

    private AsyncTask mGroupAsync;

    private ProgressDialog mProgressDialog;

    public static AddGroupFragment newInstance() {
        return new AddGroupFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_group, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressDialog = new ProgressDialog(getActivity());

        if (getActivity() != null) {
            if (getActivity().getIntent().hasExtra(Group.EXTRA)) {
                Log.d(TAG, "Has group extra");
                try {
                    mGroup = getActivity().getIntent().getParcelableExtra(Group.EXTRA);
                } catch (Exception e) {
                    Log.d(TAG, "Exception getting group from extra: " + e.toString());
                }
            } else {
                Log.d(TAG, "Does not have group extra");
            }
        }

        mMediaUploadUtil = new MediaUploadUtil(getActivity(), this);

        if (mGroup != null) {
            setupForExistingGroup();
        } else {
            setupForNewGroup();
        }

        mChangePhotoButton.setOnClickListener(v -> {
            if (hasCameraPermission()) {
                mMediaUploadUtil.launchUploadPhotoDialog(null);
            } else {
                requestCameraPermission();
            }
        });

        mToolbar.inflateMenu(R.menu.edit_group_menu);
        mToolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.update_group_action_button:
                    onUpdateGroupOptionClicked();
                    return true;
                default:
                    return false;
            }
        });
    }

    @OnClick(R.id.toolbar_up)
    public void onNavigateUp() {
        getActivity().finish();
    }

    @OnClick(R.id.delete_group_tv)
    public void onDeleteGroupClick(){
        new DeleteGroupStrategy(getActivity()).launchDeleteGroupConfirmation(mGroup);
    }

    public void onEventMainThread(DeleteGroupStrategy.DeleteGroupSuccessEvent event) {
        Log.d(TAG, "Received a delete group success event");
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    // TODO - This is very similar to how ProfileSettingsFragment handles media, should consider consolidating
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        TypedUri photoUri = mMediaUploadUtil.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Photo Uri: " + photoUri);

        if (resultCode == Activity.RESULT_OK) {

            if (photoUri != null) {
                mAttachmentUri = photoUri;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

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

                Glide.with(getActivity()).load("file:" + mAttachmentUri.getFilePath()).dontAnimate().override(250, 250).centerCrop().into(mHeaderBackgroundImage);
                Glide.with(getActivity()).load("file:" + mAttachmentUri.getFilePath()).dontAnimate().override(250, 250).centerCrop().into(mAvatar);
            } else {
                mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.something_went_wrong_while_uploading_your_image);
            }
        } else {
            Log.e(TAG, "resultCode was not OK. resultCode: " + resultCode);
        }
    }

    private void setupForNewGroup() {
        if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "setupForNewGroup");
        }

        mGroupRestriction.clearCheck();
        mGroupPublic.setChecked(true);

        mGroupType.clearCheck();
        mGroupDiscussion.setChecked(true);
        mDeleteGroup.setVisibility(View.GONE);

        mGroupRestriction.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isPrivate = checkedId == R.id.group_private;
            Log.e(TAG, "private selected: " + isPrivate);

            if (isPrivate) {
                mGroupType.setVisibility(View.GONE);
            } else {
                mGroupType.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupForExistingGroup() {

        if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "setupForExistingGroup");
        }

        mGroupRestriction.setVisibility(View.GONE);
        mGroupType.setVisibility(View.GONE);

        mName.setText(mGroup.getName());
        mAboutEditText.setText(mGroup.getAbout());

        mNameEditText.setText(mGroup.getName());
        mDeleteGroup.setVisibility(View.VISIBLE);
        loadImagesFromGroupUrl();
    }

    private void onUpdateGroupOptionClicked() {
        if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "update group option selected");
        }

        if (mGroupAsync == null) {
            if (mGroup == null) {
                onCreateGroupClicked();
            } else {
                onUpdateGroupClicked();
            }
        } else {
            Log.i(TAG, "Group request in progress, ignoring new request");
        }
    }

    public void onCreateGroupClicked() {
        mGroupName = mNameEditText.getText().toString();
        mGroupAbout = mAboutEditText.getText().toString();

        hideKeyboard();

        if (mGroupName.isEmpty()) {
            mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.invalid_group_name);
            return;
        }

        if (mAttachmentUri == null) {
            mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.invalid_group_image);
            return;
        }

        mProgressDialog.setMessage(getString(R.string.creating_group));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        createGroup(mGroupName, mGroupAbout, mAttachmentUri, getGroupType());
    }

    private void onUpdateGroupClicked() {
        if (LogUtil.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "update group selected");
        }

        mGroupName = mNameEditText.getText().toString();
        mGroupAbout = mAboutEditText.getText().toString();

        hideKeyboard();

        if (mGroupName.isEmpty()) {
            mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.invalid_group_name);
            return;
        }

        mProgressDialog.setMessage(getString(R.string.updating_group));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        updateGroup();
    }

    private void loadImagesFromGroupUrl() {
        try {
            Glide.with(getActivity()).load(mGroup.getGroupavatar().getGroupAvatar().getUrl()).dontAnimate().centerCrop().into(mHeaderBackgroundImage);
            Glide.with(getActivity()).load(mGroup.getGroupavatar().getGroupAvatar().getUrl()).dontAnimate().centerCrop().into(mAvatar);
        } catch (Exception e) {
            Log.e(TAG, "Exception loading images. Exception: " + e.toString());
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mNameEditText.getWindowToken(), 0);
    }

    public GroupType getGroupType() {
        if (mGroupRestriction.getCheckedRadioButtonId() == R.id.group_public) {
            if (mGroupType.getCheckedRadioButtonId() == R.id.group_blog) {
                return GroupType.BLOG;
            } else {
                return GroupType.DISCUSSION;
            }
        } else {
            return GroupType.PRIVATE;
        }
    }

    private void createGroup(final String groupName, final String groupAbout, final TypedUri attachmentUri, final GroupType groupType) {
        mGroupAsync = new CreateGroupAsync(getActivity()) {
            @Override
            protected void onSuccess(Result<GroupResponse> result) {
                if (getActivity() != null) {
                    mProgressDialog.cancel();
                    mGroup = result.getResource().getGroup();
                    if (mGroup != null) {
                        Toast.makeText(getActivity(), String.format(getString(R.string.group_creation_success), mGroup.getName()), Toast.LENGTH_SHORT).show();
                        loadImagesFromGroupUrl();
                        getActivity().finish();
                    } else {
                        hideKeyboard();
                        mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.group_creation_fail, R.string.retry, snackbar -> {
                            createGroup(mGroupName, mGroupAbout, mAttachmentUri, getGroupType());
                            SnackbarManager.dismiss();
                        });
                    }
                }
                mGroupAsync = null;
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.w(TAG, "Error creating group", error);
                mProgressDialog.cancel();
                if (getActivity() != null) {
                    if (error.getResponse() != null && error.getResponse().getStatus() == 422) {
                        mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.group_name_taken);
                    } else {
                        mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.group_creation_fail, R.string.retry, snackbar -> {
                            createGroup(mGroupName, mGroupAbout, mAttachmentUri, getGroupType());
                            SnackbarManager.dismiss();
                        });
                    }
                }
                mGroupAsync = null;
            }
        }.executeInParallel(new GroupParam(groupName, groupAbout, attachmentUri, groupType));
    }

    protected void updateGroup() {
        mGroupAsync = new UpdateGroupAsync(getActivity(), mGroup, mGroupName, mGroupAbout, mAttachmentUri) {
            @Override
            protected void onSuccess(Result<GroupResponse> result) {
                if (getActivity() != null) {
                    mProgressDialog.cancel();
                    hideKeyboard();
                    Toast.makeText(getActivity(), String.format(getString(R.string.group_update_success), mGroup.getName()), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    mGroupAsync = null;
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not update group", error);
                if (getActivity() != null) {
                    mProgressDialog.cancel();
                    hideKeyboard();
                    mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.group_update_fail, R.string.retry, snackbar -> {
                        updateGroup();
                        SnackbarManager.dismiss();
                    });
                }
                mGroupAsync = null;
            }
        }.executeInParallel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA_RESULT) {
            mMediaUploadUtil.launchUploadPhotoDialog(null);
        }
    }
}