package com.conx2share.conx2share.util;

import com.conx2share.conx2share.R;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UriUtil {

    public static final String TAG = UriUtil.class.getSimpleName();

    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";

    private UriUtil() {
        // prevent instantiation
    }

    public static Uri getOutputPhotoUri(Context context) {
        String appName = context.getString(R.string.app_name);
        File pictureDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
        if (!pictureDir.exists()) {
            if (!pictureDir.mkdirs()) {
                return null;
            }
        }
        String timestamp = new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date());
        File file = new File(pictureDir.getPath() + File.separator + "IMG_" + timestamp + ".jpeg");
        return Uri.fromFile(file);
    }

    public static Uri getOutputVideoUri() {
        File videoFile = new File(Environment.getExternalStorageDirectory().toString());
        if (!videoFile.exists()) {
            if (!videoFile.mkdirs()) {
                return null;
            }
        }
        String timestamp = new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date());
        File file = new File(videoFile.getPath() + File.separator + "VIDEO_" + timestamp + ".mp4");
        return Uri.fromFile(file);
    }

    public static Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static File getFileFromContentUri(Context context, Uri uri) {
        InputStream is = null;
        OutputStream os = null;
        File file = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + "myvideo." + getFileExtensionFromURI(context, uri));
            os = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            os.close();
            is.close();
        } catch (Exception e) {
            Log.e(TAG, "Could not write image file: " + e.getMessage(), e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Could not close a stream: " + e.getMessage(), e);
            }
        }

        return file;
    }

    public static String getFileExtensionFromURI(Context context, Uri uri) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
