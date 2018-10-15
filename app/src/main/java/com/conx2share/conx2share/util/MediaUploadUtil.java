package com.conx2share.conx2share.util;

import com.conx2share.conx2share.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import retrofit.mime.TypedFile;

@Deprecated
/**
 * Use {@link MediaHelper} instead
 */
public class MediaUploadUtil {

    public static final int PHOTO_FROM_LIB_REQUEST = 1;

    public static final int TAKE_PHOTO_REQUEST = 2;

    public static final int VIDEO_FROM_LIB_REQUEST = 3;

    public static final int TAKE_VIDEO_REQUEST = 4;

    public static final long MEGABYTE_LIMIT = 20L;

    public static final long FILE_BIT_LIMIT = MEGABYTE_LIMIT * 1048L * 1048L;

    private static final String TAG = MediaUploadUtil.class.getSimpleName();

    public static String EXTRA_PHOTO_URI;

    private static Uri mPhotoUri;

    private Context mContext;

    private Activity mActivity;

    private Fragment mFragment;

    private TypedFile mTypedVideoFile;

    private TypedFile mTypedThumbnailFile;

    private File mFile;

    private Uri mVideoUri;

    @Inject
    public MediaUploadUtil(Activity activity, @Nullable Fragment fragment) {
        mContext = activity;
        mFragment = fragment;
        mActivity = activity;
    }

    public static Uri getPhotoUri() {
        Log.d(TAG, "mPhotoUri during get: " + mPhotoUri);
        return mPhotoUri;
    }

    public static void setPhotoUri(Uri photoUri) {
        if (mPhotoUri == null) {
            Log.w(TAG, "mPhotoUri was null before set, setting from saved instance is valid");
        }
        Log.d(TAG, "photoUri to set to: " + photoUri);
        mPhotoUri = photoUri;
    }

    public void launchUploadPhotoDialog(@Nullable DialogInterface.OnCancelListener dismissListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setMessage(mContext.getString(R.string.would_you_like_to_take_a_photo_or_upload_one_from_your_library));
        dialogBuilder.setPositiveButton(mContext.getString(R.string.take_a_photo), (dialog, which) -> {
            takePhoto();
        });
        dialogBuilder.setNegativeButton(mContext.getString(R.string.library), (dialog, which) -> {
            getMediaFromGallery("image/*", PHOTO_FROM_LIB_REQUEST);
        });
        if (dismissListener != null) dialogBuilder.setOnCancelListener(dismissListener);
        dialogBuilder.show();
    }

    public void launchUploadVideoDialog(@Nullable DialogInterface.OnCancelListener dismissListener) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setMessage(mContext.getString(R.string.would_you_like_to_take_a_video_or_upload_one_from_your_library));
        dialogBuilder.setPositiveButton(mContext.getString(R.string.take_a_video), (dialog, which) -> {
            takeVideo();
        });
        dialogBuilder.setNegativeButton(mContext.getString(R.string.library), (dialog, which) -> {
            getMediaFromGallery("video/*", VIDEO_FROM_LIB_REQUEST);
        });
        if (dismissListener != null) dialogBuilder.setOnCancelListener(dismissListener);
        dialogBuilder.show();
    }

    public void launchWhatWouldYouLikeToUploadDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setMessage(mContext.getString(R.string.what_upload));
        dialogBuilder.setPositiveButton(mContext.getString(R.string.photo_option), (dialog, which) -> {
            launchUploadPhotoDialog(null);
        });
        dialogBuilder.setNegativeButton(mContext.getString(R.string.video_option), (dialog, which) -> {
            launchUploadVideoDialog(null);
        });
        dialogBuilder.show();
    }

    private void takePhoto() {
        File image;
        try {
            image = createImageFile();
            mPhotoUri = Uri.fromFile(image);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
            if (mFragment != null) {
                mFragment.startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST);
            } else {
                mActivity.startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to create image", e);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    private void takeVideo() {
        mVideoUri = getOutputVideoUri(); // TODO - I don't think this does anything, but I put it back in because it was in ImageAndVideoUploadUtil
        if (mFragment != null) {
            mFragment.startActivityForResult(MediaHelper.getTakeVideoIntent(), TAKE_VIDEO_REQUEST);
        } else {
            mActivity.startActivityForResult(MediaHelper.getTakeVideoIntent(), TAKE_VIDEO_REQUEST);
        }
    }

    private Uri getOutputVideoUri() {
        File videoFile = new File(Environment.getExternalStorageDirectory().toString());
        if (!videoFile.exists()) {
            if (!videoFile.mkdirs()) {
                return null;
            }
        }
        String timestamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        File file = new File(videoFile.getPath() + File.separator + "VIDEO_" + timestamp + ".mp4");
        return Uri.fromFile(file);
    }

    public void getMediaFromGallery(String type, int requestCode) {
        Intent getMediaIntent = new Intent();
        getMediaIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        getMediaIntent.setType(type);
        getMediaIntent.setAction(Intent.ACTION_GET_CONTENT);
        try {
            if (mFragment != null) {
                mFragment.startActivityForResult(Intent.createChooser(getMediaIntent, mFragment.getString(R.string.select_file)), requestCode);
            } else {
                mActivity.startActivityForResult(Intent.createChooser(getMediaIntent, mActivity.getString(R.string.select_file)), requestCode);
            }
        } catch (Exception e) {
            Toast.makeText(mContext, R.string.an_error_occurred_trying_to_start_your_gallery, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Exception occurred while trying to start gallery", e);
        }
    }

    public TypedUri onActivityResult(int requestCode, int resultCode, Intent data) {
        TypedUri typedUri = null;

        if (resultCode == Activity.RESULT_OK) {
            Uri uri;

            switch (requestCode) {
                case PHOTO_FROM_LIB_REQUEST:
                    uri = data.getData();
                    Log.d(TAG, "Photo from lib request. uri: " + uri);
                    typedUri = getPhotoTypedUri(uri);
                    break;
                case TAKE_PHOTO_REQUEST:
                    // TODO - Fix this so it uses getData() because sometimes android will destroy the activity
                    uri = mPhotoUri;
                    Log.d(TAG, "Take photo request. uri: " + uri);
                    typedUri = getPhotoTypedUri(uri);
                    break;
                case VIDEO_FROM_LIB_REQUEST:
                    uri = data.getData();
                    Log.d(TAG, "Video from lib request. uri: " + uri);
                    typedUri = getVideoTypedUri(uri, requestCode);
                    break;
                case TAKE_VIDEO_REQUEST:
                    uri = data.getData();
                    Log.d(TAG, "Take video request. uri: " + uri);
                    typedUri = getVideoTypedUri(uri, requestCode);
                    break;
                default:
                    Log.e(TAG, "Unknown request code: " + requestCode);
                    break;
            }
        } else {
            Log.w(TAG, "resultCode was not OK. resultCode: " + resultCode);
        }

        Log.d(TAG, "typedUri: " + typedUri);
        return typedUri;
    }

    private TypedUri getPhotoTypedUri(Uri photoUri) {
        String pathFromUri = (photoUri == null ? null : photoUri.getPath());
        String pathFromQuery = getRealPathFromURI(photoUri);
        String photoFilePath = null;
        TypedUri typedUri = null;

        if (pathFromQuery != null) {
            photoFilePath = pathFromQuery;
        } else if (pathFromUri != null) {
            photoFilePath = pathFromUri;
        } else {
            Log.e(TAG, "Unknown path for image");
        }

        Log.d(TAG, "photoFilePath: " + photoFilePath);

        if (photoFilePath != null) {
            typedUri = new TypedUri(mActivity, photoUri, "image/jpeg", photoFilePath);
        }

        return typedUri;
    }

    public String getRealPathFromURI(Uri contentUri) {
        return getPath(mContext, contentUri);
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private TypedUri getVideoTypedUri(Uri videoUri, int requestCode) {
        TypedUri typedUri = null;
        String videoPath;
        File videoFile;

        if (videoUri != null) {
            switch (requestCode) {
                case TAKE_VIDEO_REQUEST:
                    videoFile = getFileFromContentUri(videoUri, mContext);
                    if (videoFile != null) {
                        videoPath = videoFile.getPath();
                        Log.i(TAG, videoPath);
                        mTypedVideoFile = new TypedFile("video/", videoFile);

                        typedUri = createThumbnailAndReturnUri(videoPath);

                    } else {
                        typedUri = null;
                    }
                    break;
                case VIDEO_FROM_LIB_REQUEST:
                    videoPath = getRealPathFromURI(videoUri);
                    if (videoPath != null) {
                        videoFile = new File(videoPath);
                        Log.i(TAG, videoPath);
                        mFile = videoFile;
                        mTypedVideoFile = new TypedFile("video/", videoFile);

                        typedUri = createThumbnailAndReturnUri(videoPath);
                    } else {
                        typedUri = null;
                    }
                    break;
                default:
                    break;
            }
        } else {
            Log.w(TAG, "videoUri was null for take video request");
        }
        return typedUri;
    }

    private File getFileFromContentUri(Uri uri, Context context) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File file = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + "myvideo." + getFileExtensionFromURI(uri, context));
            outputStream = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "Could not write image file", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Could not close a stream", e);
            }
        }

        return file;
    }

    private TypedUri createThumbnailAndReturnUri(String videoPath) {
        TypedUri typedUri;
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);
        Uri thumbnailUri = getThumbnailUri(mContext, thumbnail);
        if (thumbnailUri != null) {
            String thumbnailPath = getRealPathFromURI(thumbnailUri);
            typedUri = new TypedUri(mContext, thumbnailUri, "", thumbnailPath);
            File thumbnailFile = new File(thumbnailPath);
            mTypedThumbnailFile = new TypedFile("image/jpeg", thumbnailFile);
        } else {
            typedUri = null;
        }

        return typedUri;
    }

    private Uri getThumbnailUri(Context context, Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "Title", null);
        return Uri.parse(path);
    }

    private String getFileExtensionFromURI(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public TypedFile getTypedVideoFile() {
        return mTypedVideoFile;
    }

    public TypedFile getTypedThumbnailFile() {
        return mTypedThumbnailFile;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public String getStorageFrameworkPath(ContentResolver contentResolver, Uri uri) {
        String path = null;
        Cursor cursor = null;

        try {
            String wholeId = DocumentsContract.getDocumentId(uri);
            String id = wholeId.split(":")[1];

            String[] proj = new String[]{MediaStore.Images.Media.DATA};
            String selection = MediaStore.Images.Media._ID + " = ?";

            cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, selection, new String[]{id}, null);
            int dataIndex = cursor.getColumnIndex(proj[0]);
            if (cursor.moveToFirst() && dataIndex != -1) {
                path = cursor.getString(dataIndex);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting storage framework path", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return path;
    }
}
