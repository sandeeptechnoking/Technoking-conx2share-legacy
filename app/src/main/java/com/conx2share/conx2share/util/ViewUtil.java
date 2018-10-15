package com.conx2share.conx2share.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import static android.content.res.Resources.getSystem;

public class ViewUtil {

    private ViewUtil() {
    }

    public static int dpToPx(int dp) {
        float density = getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity == null) return;
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static void hideKeyboard(Activity activity, Window window) {
        if (activity == null) return;
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(window.getDecorView().getWindowToken(), 0);
    }

    public static void hideKeyboard(Context context, EditText editText) {
        if (context == null || editText == null) return;
        if (((Activity) context).getCurrentFocus() != null
                && ((Activity) context).getCurrentFocus() instanceof EditText) {
            InputMethodManager imm =
                    (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public static boolean pictureWasSetted(Context context, String pictureUrl, ImageView imageView) {
        if (!TextUtils.isEmpty(pictureUrl)) {
            Glide.with(context).load(pictureUrl).centerCrop().dontAnimate().into(imageView);
            return true;
        } else return false;
    }
}
