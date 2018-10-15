package com.conx2share.conx2share.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.conx2share.conx2share.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rx.functions.Func0;
import rx.functions.Func1;

public final class MediaHelper {

    private static final int TAKE_PHOTO_REQUEST = 1;
    private static final int TAKE_VIDEO_REQUEST = 2;
    private static final int PICK_FILE_REQUEST = 3;

    private static final long FILE_BIT_LIMIT = 20 * 1048L * 1048L;

    private static final String JPEG_SUFFIX = ".jpeg";

    private static final String[] VIDEO_EXTENSIONS = {"mp4", "avi", "mov"};
    private static final String[] IMAGE_EXTENSIONS = {"jpeg", "jpg", "png", "bmp"};

    public static final String[] IMAGE_MIME_TYPES = new String[]{
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/bmp"
    };

    public static final String[] VIDEO_MIME_TYPES = new String[]{
            "video/mp4",
            "video/avi",
            "video/mov"
    };

    public static final String[] IMAGE_AND_VIDEO_MIME_TYPES = CommonUtil.combine(IMAGE_MIME_TYPES, VIDEO_MIME_TYPES);

    private static final Func0<String> DEFAULT_MEDIA_FILE_FACTORY = () -> {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        return "JPEG_" + timeStamp + "_";
    };

    private static File photoFile;

    private MediaHelper() {
    }

    public static void showAttachmentDialog(Context context,
                                            AttachmentChooser chooser) {
        showAttachmentDialog(context, chooser, null);
    }

    public static void showAttachmentDialog(Context context,
                                            AttachmentChooser chooser,
                                            @Nullable DialogInterface.OnCancelListener cancelListener) {
        new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.would_you_like_to_take_a_media_or_upload_one_from_your_library))
                .setOnCancelListener(cancelListener)
                .setPositiveButton(context.getString(R.string.take_a_photo), (dialog, which) -> {
                    chooser.onTakePhoto();
                })
                .setNegativeButton(context.getString(R.string.take_a_video), (dialog, which) -> {
                    chooser.onTakeVideo();
                })
                .setNeutralButton(context.getString(R.string.pick_a_file), (dialog, which) -> {
                    chooser.onPickFile();
                }).show();
    }

    public static void showPictureAttachmentDialog(Context context,
                                                   PhotoChooser chooser) {
        showPictureAttachmentDialog(context, chooser, null);
    }

    public static void showPictureAttachmentDialog(Context context,
                                                   PhotoChooser chooser,
                                                   @Nullable DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.would_you_like_to_take_a_photo_or_upload_one_from_your_library))
                .setOnCancelListener(cancelListener)
                .setPositiveButton(context.getString(R.string.take_a_photo), (dialog, which) -> {
                    chooser.onTakePhoto();
                })
                .setNegativeButton(context.getString(R.string.pick_a_file), (dialog, which) -> {
                    chooser.onPickFile();
                });

        dialogBuilder.show();
    }

    public static void showVideoAttachmentDialog(Context context,
                                                 VideoChooser chooser) {
        showVideoAttachmentDialog(context, chooser, null);
    }

    public static void showVideoAttachmentDialog(Context context,
                                                 VideoChooser chooser,
                                                 @Nullable DialogInterface.OnCancelListener dismissListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.would_you_like_to_take_a_video_or_upload_one_from_your_library))
                .setOnCancelListener(dismissListener)
                .setPositiveButton(context.getString(R.string.take_a_video), (dialog, which) -> {
                    chooser.onTakeVideo();
                })
                .setNegativeButton(context.getString(R.string.library), (dialog, which) -> {
                    chooser.onPickFile();
                });

        dialogBuilder.show();
    }

    public static void startActivityForTakingPhoto(Activity activity) throws IOException {
        startActivityForTakingPhoto(activity, DEFAULT_MEDIA_FILE_FACTORY.call());
    }

    public static void startActivityForTakingPhoto(Fragment fragment) throws IOException {
        startActivityForTakingPhoto(fragment, DEFAULT_MEDIA_FILE_FACTORY.call());
    }

    public static void startActivityForTakingPhoto(Activity activity, String fileName) throws IOException {
        photoFile = createMediaFile(activity, () -> fileName);
        activity.startActivityForResult(getTakePictureIntent(photoFile), TAKE_PHOTO_REQUEST);
    }

    public static void startActivityForTakingPhoto(Fragment fragment, String fileName) throws IOException {
        photoFile = createMediaFile(fragment.getContext(), () -> fileName);
        fragment.startActivityForResult(getTakePictureIntent(photoFile), TAKE_PHOTO_REQUEST);
    }

    public static void startActivityForTakingVideo(Activity activity) {
        activity.startActivityForResult(getTakeVideoIntent(), TAKE_VIDEO_REQUEST);
    }

    public static void startActivityForTakingVideo(Fragment fragment) {
        fragment.startActivityForResult(getTakeVideoIntent(), TAKE_VIDEO_REQUEST);
    }

    public static void startActivityForPickingMediaFile(Activity activity, String[] mimeTypes) {
        activity.startActivityForResult(getPickMediaFileIntent(mimeTypes), PICK_FILE_REQUEST);
    }

    public static void startActivityForPickingMediaFile(Fragment fragment, String[] mimeTypes) {
        fragment.startActivityForResult(getPickMediaFileIntent(mimeTypes), PICK_FILE_REQUEST);
    }

    public static void startActivityForPickImageOrVideo(Activity activity, String[] mimeTypes) {
        activity.startActivityForResult(getPickImageOrVideoIntent(mimeTypes), PICK_FILE_REQUEST);
    }

    public static boolean canCatchPhotoResult(int requestCode) {
        return requestCode == TAKE_PHOTO_REQUEST;
    }

    public static boolean canCatchVideoResult(int requestCode) {
        return requestCode == TAKE_VIDEO_REQUEST;
    }

    public static boolean canCatchPickMediaFileResult(int requestCode) {
        return requestCode == PICK_FILE_REQUEST;
    }

    @Nullable
    public static File getPhotoFile() {
        return photoFile;
    }

    @NonNull
    public static File getVideoFile(Context context, Intent data) {
        return new File(getPath(context, data));
    }

    @NonNull
    public static File getMediaFile(Context context, Intent data) {
        return new File(getPath(context, data));
    }

    public static File createMediaFile(Context context) throws IOException {
        return createMediaFile(context, DEFAULT_MEDIA_FILE_FACTORY);
    }

    private static File createMediaFile(Context context, Func0<String> fileNameFactory) throws IOException {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                fileNameFactory.call(),
                JPEG_SUFFIX,
                storageDir
        );
    }

    public static File getVideoThumbnailFile(Context context,
                                             @NonNull String videoPath) {
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);
        Uri thumbnailUri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), thumbnail, null, null));
        return new File(getRealPathFromUri(context, thumbnailUri));
    }

    @Nullable
    public static String getRealPathFromUri(@NonNull Context context, @NonNull Uri uri) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            assert cursor != null;

            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getMimeType(@NonNull String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (!TextUtils.isEmpty(extension)) {
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
            if (!TextUtils.isEmpty(mimeType)) {
                return mimeType;
            }
        }
        return "image/jpg";
    }

    public static boolean isVideo(String path) {
        return execPredicate(VIDEO_EXTENSIONS, extension -> path.toLowerCase().endsWith(extension));
    }

    public static boolean isImage(String path) {
        return execPredicate(IMAGE_EXTENSIONS, extension -> path.toLowerCase().endsWith(extension));
    }

    private static Intent getTakePictureIntent(File pictureFile) {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureFile));
    }

    public static Intent getTakeVideoIntent() {
        return new Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                .putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//                .putExtra(MediaStore.EXTRA_SIZE_LIMIT, FILE_BIT_LIMIT);
    }

    private static Intent getPickMediaFileIntent(String[] mimeTypes) {
        return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                .setType("*/*")
                .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
    }

    private static Intent getPickImageOrVideoIntent(String[] mimeTypes) {
        return new Intent(Intent.ACTION_OPEN_DOCUMENT)
        .addCategory(Intent.CATEGORY_OPENABLE)
        .putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        .setType("image/*")
        .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
    }

    @NonNull
    private static String getPath(Context context, Intent data) {
        String path = getRealPathFromUri(context, data.getData());
        if (path == null) {
            throw new IllegalStateException("Cannot get the right path");
        }

        return path;
    }

    private static boolean execPredicate(String[] source, Func1<String, Boolean> predicate) {
        for (String item : source) {
            if (predicate.call(item)) {
                return true;
            }
        }

        return false;
    }

    public interface AttachmentChooser extends PhotoChooser, VideoChooser {
    }

    public interface PhotoChooser {
        void onPickFile();

        void onTakePhoto();
    }

    public interface VideoChooser {
        void onPickFile();

        void onTakeVideo();
    }
}