package com.conx2share.conx2share.ui.profile_settings;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.conx2share.conx2share.BuildConfig;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.AuthUser;
import com.conx2share.conx2share.model.ErrorMessage;
import com.conx2share.conx2share.model.PromoCodeWrapper;
import com.conx2share.conx2share.model.UpdatePasswordWrapper;
import com.conx2share.conx2share.model.event.UpdateProfileImageEvent;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.network.models.response.GetUserResponse;
import com.conx2share.conx2share.strategies.PromoCodeStrategy;
import com.conx2share.conx2share.ui.base.BaseDatePickerFragment;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.sign_up.BusinessSignUpWebActivity;
import com.conx2share.conx2share.util.EventBusUtil;
import com.conx2share.conx2share.util.MediaHelper;
import com.conx2share.conx2share.util.PermissionUtil;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.conx2share.conx2share.util.ViewUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

//import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.mime.TypedFile;
import roboguice.inject.InjectView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


public class ProfileSettingsFragment extends BaseFragment {

    public static final String EXTRA_PROFILE_ID = "profileId";

    private static final int ANIMATION_DURATION = 300;

    private static final String TAG = ProfileSettingsFragment.class.getSimpleName();

    @Inject
    NetworkClient networkClient;

    @Inject
    PreferencesUtil mPreferencesUtil;

    @InjectView(R.id.profile_settings_header_background)
    ImageView mSettingsHeaderBackgroundImage;

    @InjectView(R.id.profile_settings_avatar)
    RoundedImageView mProfileUserAvatar;

    @InjectView(R.id.profile_settings_name)
    TextView mProfileName;

    @InjectView(R.id.email_settings)
    EditText mEmailSettings;

    @InjectView(R.id.username_settings)
    EditText mUsernameSettings;

    @InjectView(R.id.change_profile_photo_button)
    Button mChangeProfilePhotoButton;

    @InjectView(R.id.settings_version)
    TextView mSettingVersion;

    @InjectView(R.id.bio_settings)
    EditText mBioSettings;

    @InjectView(R.id.old_password_input)
    EditText mOldPasswordInput;

    @InjectView(R.id.new_password_input)
    EditText mNewPasswordInput;

    @InjectView(R.id.confirm_new_password_input)
    EditText mConfirmNewPasswordInput;

    @InjectView(R.id.change_password_submit_button)
    Button mChangePasswordSubmitButton;

    @InjectView(R.id.slide_up_about_panel)
    RelativeLayout mSlideUpPanel;

    @InjectView(R.id.close_about_screen)
    ImageButton mCloseAboutButton;

    @InjectView(R.id.activate_promo_code_button)
    Button mActivatePromoCodeButton;

    @InjectView(R.id.birthday)
    EditText mBirthday;

    @InjectView(R.id.profile_settings_progress_bar)
    ProgressBar mProfileSettingsProgressBar;

    @InjectView(R.id.progress_bar_avatar)
    ProgressBar mProgressBarAvatar;

    @InjectView(R.id.progress_bar_back)
    ProgressBar mProgressBarBack;

    @InjectView(R.id.main_view)
    RelativeLayout mMainView;

    @InjectView(R.id.toggle_message_notifications)
    Switch mToggleMessages;

    @InjectView(R.id.toggle_tag_notifications)
    Switch mToggleTag;

    @InjectView(R.id.toggle_post_notifications)
    Switch mTogglePosts;

    @InjectView(R.id.toggle_invite_notifications)
    Switch mToggleInvites;

    @InjectView(R.id.toggle_following_notifications)
    Switch mToggleFollowing;

    @InjectView(R.id.toggle_followers_notifications)
    Switch mToggleFollowers;

    @InjectView(R.id.helper_messages)
    TextView mHelperMessages;

    @InjectView(R.id.helper_tags)
    TextView mHelperTags;

    @InjectView(R.id.helper_posts)
    TextView mHelperPosts;

    @InjectView(R.id.helper_invites)
    TextView mHelperInvites;

    @InjectView(R.id.helper_following)
    TextView mHelperFollowing;

    @InjectView(R.id.helper_followers)
    TextView mHelperFollowers;

    @InjectView(R.id.avatar_change_tv)
    TextView mAvatarChangeTv;

    @InjectView(R.id.register_as_a_business_button)
    Button mRegisterAsABusinessButton;

    @InjectView(R.id.register_business_birthday_error)
    TextView mRegisterBusinessBirthdayError;

    @Inject
    SnackbarUtil mSnackbarUtil;

    private int mProfileUserId;

    private User mProfileUser;

    private ValueAnimator mAnimator;

    private String mNewPassword;

    private User mUser;

    private String mAuthToken;

    private ProgressDialog mProgressDialog;

    private String mPromoCode;

    private PromoCodeWrapper mPromoCodeWrapper;

    private File photoFile;

    private boolean willChangeAvatar;

    private boolean fromResult;

    public static ProfileSettingsFragment newInstance() {
        return new ProfileSettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressDialog = new ProgressDialog(getActivity());

        mRegisterAsABusinessButton.setOnClickListener(v -> {
            Intent businessIntent = new Intent(getActivity(), BusinessSignUpWebActivity.class);
            businessIntent.putExtra(BusinessSignUpWebActivity.BUSINESS_USER_ID, mPreferencesUtil.getAuthUser().getId());
            getActivity().finish();
            startActivity(businessIntent);
        });

        mSettingVersion.setText(String.format(getString(R.string.app_version), BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")"));

        mActivatePromoCodeButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                PromoCodeStrategy promoCodeStrategy = new PromoCodeStrategy(getActivity(), mProfileUserId);
                promoCodeStrategy.launchPromoDialog();
            }
        });

        mBirthday.setOnClickListener(view1 -> {
            if (getActivity() != null) {
                DialogFragment datePickerFragment = new BaseDatePickerFragment();
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        mChangeProfilePhotoButton.setOnClickListener(v -> {
            willChangeAvatar = false;
            startChangePicture();
        });

        mAvatarChangeTv.setOnClickListener(v -> {
            willChangeAvatar = true;
            startChangePicture();
        });

        mHelperMessages.setOnClickListener(v -> launchDialog(R.string.helper_messages_title, R.string.helper_messages_description));

        mHelperTags.setOnClickListener(v -> launchDialog(R.string.helper_tags_title, R.string.helper_tags_description));

        mHelperPosts.setOnClickListener(v -> launchDialog(R.string.helper_posts_title, R.string.helper_posts_description));

        mHelperInvites.setOnClickListener(v -> launchDialog(R.string.helper_invites_title, R.string.helper_invites_description));

        mHelperFollowing.setOnClickListener(v -> launchDialog(R.string.helper_following_title, R.string.helper_following_description));

        mHelperFollowers.setOnClickListener(v -> launchDialog(R.string.helper_followers_title, R.string.helper_followers_description));
    }

    private void startChangePicture() {
        if (hasCameraPermission()) {
             new AlertDialog.Builder(getActivity() )
                .setMessage(getActivity().getString(R.string.would_you_like_to_take_a_photo_or_upload_one_from_your_library))
                .setPositiveButton(getActivity().getString(R.string.take_a_photo), (dialog, which) ->{
                    try {

                        MediaHelper.startActivityForTakingPhoto(ProfileSettingsFragment.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })

                .setNegativeButton(getActivity().getString(R.string.pick_a_file), (dialog, which) ->{
                    MediaHelper.startActivityForPickingMediaFile(ProfileSettingsFragment.this, MediaHelper.IMAGE_MIME_TYPES);
                }
            ).show();
        } else {
            requestCameraPermission();
        }
    }

    public void onEventMainThread(BaseDatePickerFragment.OnDateSelectedEvent event) {
        Log.d(TAG, "Received a date selected event");
//      just comment it for now, it can be useful in the future
//        if(getActivity() != null) {
//            mBirthday.setText(String.format("%02d", event.getDay()) + "-" + String.format("%02d", event.getMonth()) + "-" + event.getYear());
//            updateUser();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mProfileUserId = getArguments().getInt(EXTRA_PROFILE_ID);
        if (mProfileUserId < 0) {
            Log.wtf(TAG, "user profile id is unexpected: " + mProfileUserId);
        }
        if (!fromResult) {
            new GetProfileUser().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mProfileUserId);
            fromResult = false;
        }
    }

    @OnClick(R.id.change_password_tv)
    public void onChangePasswordClicked() {
        slidePanelUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fromResult = true;
        mProgressDialog.setMessage(getString(R.string.updating_user_avatar));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        if (resultCode == Activity.RESULT_OK) {
            File photoFile = null;
            if (MediaHelper.canCatchPhotoResult(requestCode)) {
                //attach photo
                photoFile = MediaHelper.getPhotoFile();
            } else if (MediaHelper.canCatchPickMediaFileResult(requestCode)) {
                //attach media file
                photoFile = MediaHelper.getMediaFile(getActivity(), data);
                if (MediaHelper.isImage(photoFile.getPath())) {
                    photoFile = MediaHelper.getMediaFile(getActivity(), data);
                }
            }

            if (photoFile != null) {
                changePictureRequest(photoFile.getPath());
            } else {
                mSnackbarUtil.showSnackBarWithoutAction(getActivity(), R.string.something_went_wrong_while_uploading_your_image);
            }
        }
        mProgressDialog.cancel();
    }

    @Override
    public void onPause() {
        super.onPause();
        updateUser();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA_RESULT) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                startChangePicture();
            }
        }
    }

    private void updateUser() {
        if (mUsernameSettings.getText().toString().trim().replaceAll("[a-zA-Z0-9]", "").length() <= 0) {
            if (mUsernameSettings.getText().toString().length() >= 2 && mUsernameSettings.getText().toString().length() <= 20) {
                new UpdateProfileUserAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                Toast.makeText(getActivity(), getString(R.string.please_enter_in_a_username_that_is_between_two_and_twenty_characters), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.please_use_only_letters_and_numbers_for_your_username), Toast.LENGTH_SHORT).show();
        }
    }

    private void launchDialog(int titleResourceId, int descriptionResourceId) {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(titleResourceId));
            builder.setMessage(descriptionResourceId);
            builder.setNegativeButton(getString(R.string.ok), (dialog, whichButton) -> {
                dialog.cancel();
            });
            builder.show();
        }
    }

    private void setupNotificationToggles() {
        if (mProfileUser.getMessageNotifications() != null && mProfileUser.getMessageNotifications()) {
            mToggleMessages.setChecked(true);
        } else {
            mToggleMessages.setChecked(false);
        }

        if (mProfileUser.getTagNotifications() != null && mProfileUser.getTagNotifications()) {
            mToggleTag.setChecked(true);
        } else {
            mToggleTag.setChecked(false);
        }

        if (mProfileUser.getInviteNotifications() != null && mProfileUser.getInviteNotifications()) {
            mToggleInvites.setChecked(true);
        } else {
            mToggleInvites.setChecked(false);
        }

        if (mProfileUser.getPostNotifications() != null && mProfileUser.getPostNotifications()) {
            mTogglePosts.setChecked(true);
        } else {
            mTogglePosts.setChecked(false);
        }

        if (mProfileUser.getNewPostNotifications() != null && mProfileUser.getNewPostNotifications()) {
            mToggleFollowing.setChecked(true);
        } else {
            mToggleFollowing.setChecked(false);
        }

        if (mProfileUser.getFollowerNotifications() != null && mProfileUser.getFollowerNotifications()) {
            mToggleFollowers.setChecked(true);
        } else {
            mToggleFollowers.setChecked(false);
        }
    }

    @OnClick(R.id.change_password_submit_button)
    public void onChangePassordClick() {
        submitChangePassword();
        ViewUtil.hideKeyboard(getActivity());
    }

    @OnClick(R.id.close_about_screen)
    public void onCloseClick() {
        slidePanelDown();
        ViewUtil.hideKeyboard(getActivity());
    }

    public void submitChangePassword() {
        if (!mOldPasswordInput.getText().toString().equals(mPreferencesUtil.getPassword())) {
            mSnackbarUtil.displaySnackBar(getActivity(), R.string.old_password_error);
            return;
        }

        mNewPassword = mNewPasswordInput.getText().toString();
        String confirmNewPassword = mConfirmNewPasswordInput.getText().toString();

        mUser = new User(mNewPassword);
        if (mNewPassword.equals(confirmNewPassword)) {
            mAuthToken = mPreferencesUtil.getAuthToken();
            mPreferencesUtil.setAuthToken(null);
            Log.i(TAG, "AUTH TOKEN: " + mPreferencesUtil.getAuthToken());
            mProgressDialog.setMessage(getString(R.string.updating_password));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            updatePasswordAsync();
        } else {
            mSnackbarUtil.displaySnackBar(getActivity(), R.string.passwords_dont_match);
        }
    }

    //change password dialog
    private int getPanelHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    private void slidePanelUp() {

        if (mAnimator != null) {
            mAnimator.removeAllUpdateListeners();
            mAnimator.removeAllListeners();
            mAnimator.cancel();
        }

        mAnimator = ValueAnimator.ofFloat((float) getPanelHeight(), (float) 0);
        mAnimator.setDuration(ANIMATION_DURATION);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mSlideUpPanel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mAnimator.addUpdateListener(animation -> mSlideUpPanel.setY((float) animation.getAnimatedValue()));

        mAnimator.start();
    }

    private void slidePanelDown() {

        if (mAnimator != null) {

            mAnimator.removeAllUpdateListeners();
            mAnimator.removeAllListeners();
            mAnimator.cancel();
        }

        // Get the height of the layout to handle moving it
        mAnimator = ValueAnimator.ofFloat((float) 0, (float) getPanelHeight());
        mAnimator.setDuration(ANIMATION_DURATION);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mSlideUpPanel.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mAnimator.addUpdateListener(animation -> mSlideUpPanel.setY((float) animation.getAnimatedValue()));

        mAnimator.start();
    }

    public class GetProfileUser extends AsyncTask<Integer, Void, Result<GetUserResponse>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (getActivity() != null) {
                mProfileSettingsProgressBar.setVisibility(View.VISIBLE);
                mProgressBarBack.setVisibility(View.VISIBLE);
                mProgressBarAvatar.setVisibility(View.VISIBLE);
                mMainView.setVisibility(View.GONE);
            }
        }

        @Override
        protected Result<GetUserResponse> doInBackground(Integer... params) {
            return networkClient.getUser(String.valueOf(params[0]));
        }

        @Override
        protected void onPostExecute(Result<GetUserResponse> userResult) {
            super.onPostExecute(userResult);

            if (getActivity() != null) {
                mProfileSettingsProgressBar.setVisibility(View.GONE);
                mMainView.setVisibility(View.VISIBLE);

                if (userResult != null && userResult.getResource() != null && userResult.getError() == null) {
                    mProfileUser = userResult.getResource().getUser();
                    mEmailSettings.setText(mProfileUser.getEmail());
                    mUsernameSettings.setText(mProfileUser.getUsername());
                    if (mProfileUser.getAbout() != null) {
                        mBioSettings.setText(mProfileUser.getAbout());
                    }

//      just comment it for now, it can be useful in the future
//                    if(mProfileUser.isOverEighteen() != null && mProfileUser.isOverEighteen()) {
//                        mBirthday.setText(DateUtils.getDateAsDayMonthYear(mProfileUser.getBirthday()));
//                        mRegisterBusinessBirthdayError.setVisibility(View.GONE);
//                        mRegisterAsABusinessButton.setTextColor(getResources().getColor(R.color.conx_blue));
//                        mRegisterAsABusinessButton.setClickable(true);
//                        mRegisterAsABusinessButton.setEnabled(true);
//                    } else {
//                        mRegisterBusinessBirthdayError.setVisibility(View.VISIBLE);
//                        mRegisterAsABusinessButton.setTextColor(getResources().getColor(R.color.hint_gray_pressed));
//                        mRegisterAsABusinessButton.setClickable(false);
//                        mRegisterAsABusinessButton.setEnabled(false);
//                    }

                    showProfilePictures(mProfileUser);
                    mProfileName.setText(mProfileUser.getFirstName() + " " + mProfileUser.getLastName());
                    setupNotificationToggles();
                } else {
                    SnackbarManager.show(
                            Snackbar.with(getActivity().getApplicationContext())
                                    .type(SnackbarType.MULTI_LINE)
                                    .text(getString(R.string.unable_to_load_profile_text))
                                    .actionLabel(getString(R.string.retry))
                                    .actionListener(snackbar -> {
                                        new GetProfileUser().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mProfileUserId);
                                        SnackbarManager.dismiss();
                                    })
                            , getActivity());
                }
            }
        }
    }

    private void showProfilePictures(User profileUser) {
        if (!TextUtils.isEmpty(profileUser.getAvatar().getAvatar().getThumbUrl())) {
            mProgressBarAvatar.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(profileUser.getAvatar().getAvatar().getThumbUrl())
                    .centerCrop()
                    .dontAnimate()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mProgressBarAvatar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromCache, boolean isFirst) {
                            mProgressBarAvatar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(mProfileUserAvatar);
        }else{
            mProgressBarAvatar.setVisibility(View.GONE);
        }

        String backUrl = "";
        if (!TextUtils.isEmpty(profileUser.getCover().getCoverPhoto().getUrl())) {
            backUrl = profileUser.getCover().getCoverPhoto().getUrl();
        } else if (!TextUtils.isEmpty(profileUser.getAvatar().getAvatar().getUrl())) {
            backUrl = profileUser.getAvatar().getAvatar().getUrl();
        }
        if (!TextUtils.isEmpty(backUrl)) {
            mProgressBarBack.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(profileUser.getCover().getCoverPhoto().getUrl())
                    .centerCrop()
                    .dontAnimate()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mProgressBarBack.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mProgressBarBack.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(mSettingsHeaderBackgroundImage);
        }else{
            mProgressBarBack.setVisibility(View.GONE);
        }
    }

    private void changePictureRequest(String filePath) {
        String mime = MediaHelper.getMimeType(filePath);
        if (!TextUtils.isEmpty(filePath) && !TextUtils.isEmpty(mime)) {
            TypedFile typedFile = new TypedFile(mime, new File(filePath));
            Observable<GetUserResponse> observable;
            if (willChangeAvatar) {
                mProgressBarAvatar.setVisibility(View.VISIBLE);
                observable = networkClient.updateAvatar(typedFile, mProfileUserId);
            } else {
                mProgressBarBack.setVisibility(View.VISIBLE);
                observable = networkClient.updateCover(typedFile, mProfileUserId);
            }
            addSubscription(observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            getUserResponse -> {
                                if (getActivity() == null) return;
                                mProfileUser = getUserResponse.getUser();
                                showProfilePictures(mProfileUser);

                                EventBusUtil.getEventBus().post(new UpdateProfileImageEvent(mProfileUser.getAvatarUrl()));

                                AuthUser authUser = mPreferencesUtil.getAuthUser();
                                authUser.updateFromUser(mProfileUser);
                                mPreferencesUtil.setAuthUser(authUser);
                            },
                            throwable -> {
                                if (getActivity() == null) return;
                                mProgressBarAvatar.setVisibility(View.GONE);
                                mProgressBarBack.setVisibility(View.GONE);
                                mSnackbarUtil.displaySnackBar(getActivity(),
                                        willChangeAvatar ? R.string.unable_to_update_avatar_text : R.string.unable_to_update_cover_text);
                            }
                    ));
        } else {
            throw new IllegalArgumentException(
                    String.format("Can't change avatar with mime %s and file path %s", mime, filePath));
        }
    }

    public class UpdateProfileUserAsync extends AsyncTask<Void, Void, Result<GetUserResponse>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mPreferencesUtil.getAuthToken() == null) {
                // no need to attempt to update things after logging out
                cancel(true);
            }
        }

        @Override
        protected Result<GetUserResponse> doInBackground(Void... params) {
            User updateUser = new User(mEmailSettings.getText().toString(),
                    mUsernameSettings.getText().toString(),
                    mBioSettings.getText().toString(),
                    mToggleMessages.isChecked(),
                    mToggleTag.isChecked(),
                    mTogglePosts.isChecked(),
                    mToggleInvites.isChecked(),
                    mToggleFollowing.isChecked(),
                    mToggleFollowers.isChecked());
            updateUser.setBirthday(mBirthday.getText().toString());
            return networkClient.updateProfileUser(mProfileUserId, updateUser);
        }

        @Override
        protected void onPostExecute(Result<GetUserResponse> getUserResponseResult) {
            super.onPostExecute(getUserResponseResult);
            if (getActivity() != null) {
                if (getUserResponseResult != null && getUserResponseResult.getResource() != null && getUserResponseResult.getError() == null) {
                    Log.i(TAG, "User profile has been updated");
                    if (getUserResponseResult.getResource().getUser() != null && getUserResponseResult.getResource().getUser().isOverEighteen() != null && getUserResponseResult.getResource().getUser().isOverEighteen()) {
                        mRegisterAsABusinessButton.setClickable(true);
                        mRegisterAsABusinessButton.setEnabled(true);
                        //mRegisterBusinessBirthdayError.setVisibility(View.GONE);
                        mRegisterAsABusinessButton.setTextColor(getResources().getColor(R.color.conx_blue));
                    } else {
                        //mRegisterBusinessBirthdayError.setVisibility(View.VISIBLE);
                        mRegisterAsABusinessButton.setTextColor(getResources().getColor(R.color.hint_gray_pressed));
                        mRegisterAsABusinessButton.setClickable(false);
                        mRegisterAsABusinessButton.setEnabled(false);
                    }
                } else {
                    Log.e(TAG, "User profile has NOT been updated");
                    if (getUserResponseResult != null && getUserResponseResult.getError() != null && getUserResponseResult.getError().getResponse() != null) {
                        Log.d(TAG, "getUserResponseResult.getError().getResponse().getStatus(): " + getUserResponseResult.getError().getResponse().getStatus());
                        if (getUserResponseResult.getError().getResponse().getStatus() == 422) {
                            ErrorMessage error = (ErrorMessage) getUserResponseResult.getError().getBodyAs(ErrorMessage.class);
                            if (error != null && error.getMessage() != null && !error.getMessage().equals("")) {
                                switch (error.getMessage().toLowerCase()) {
                                    case "username has already been taken":
                                        Toast.makeText(getActivity(), getString(R.string.unable_to_update_users_profile_text) + " Reason: " + getString(R.string.user_name_is_taken),
                                                Toast.LENGTH_SHORT).show();
                                        break;
                                    case "email can't be blank":
                                        Toast.makeText(getActivity(), getString(R.string.unable_to_update_users_profile_text) + " Reason: " + getString(R.string.email_cant_be_blank),
                                                Toast.LENGTH_SHORT).show();
                                        break;
                                    case "email has already been taken":
                                        Toast.makeText(getActivity(), getString(R.string.unable_to_update_users_profile_text) + " Reason: " + getString(R.string.email_has_already_been_taken),
                                                Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Log.w(TAG, "Unknown error message in response body: " + error.getMessage());
                                        Toast.makeText(getActivity(), getString(R.string.unable_to_update_users_profile_text), Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.unable_to_update_users_profile_text), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.unable_to_update_users_profile_text), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.unable_to_update_users_profile_text), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void updatePasswordAsync() {
        UpdatePasswordWrapper updatePasswordWrapper = new UpdatePasswordWrapper(mAuthToken, mNewPassword, mUser.getId());
        addSubscription(networkClient.updatePassword(updatePasswordWrapper, String.valueOf(mPreferencesUtil.getAuthUser().getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getUserResponse -> {
                            if (getActivity() == null) return;
                            mSnackbarUtil.displaySnackBar(getActivity(), R.string.password_updated);
                            mProgressDialog.cancel();
                            mPreferencesUtil.setAuthToken(mAuthToken);
                            slidePanelDown();
                            mNewPasswordInput.setText("");
                            mOldPasswordInput.setText("");
                            mConfirmNewPasswordInput.setText("");
                        },
                        throwable -> {
                            if (getActivity() == null) return;
                            mSnackbarUtil.displaySnackBar(getActivity(), R.string.password_failed_to_update);
                            mPreferencesUtil.setAuthToken(mAuthToken);
                            mProgressDialog.cancel();
                        }));

    }
}