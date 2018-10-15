package com.conx2share.conx2share.util;

import android.content.Context;
import android.support.annotation.StringRes;

import com.conx2share.conx2share.R;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

public class SnackbarUtil {

    public void displaySnackBar(Context context, String alertText) {
        if (ForegroundUtil.getAppInForeground()) {
            Snackbar snackbar = Snackbar.with(context)
                    .type(SnackbarType.MULTI_LINE)
                    .text(alertText)
                    .duration(Snackbar.SnackbarDuration.LENGTH_SHORT);
            SnackbarManager.show(snackbar);
        }
    }

    public void displaySnackBar(Context context, @StringRes int textResId) {
        if (ForegroundUtil.getAppInForeground()) {
            Snackbar snackbar = Snackbar.with(context)
                    .type(SnackbarType.MULTI_LINE)
                    .text(textResId)
                    .duration(Snackbar.SnackbarDuration.LENGTH_SHORT);
            SnackbarManager.show(snackbar);
        }
    }

    public void showSnackBarWithAction(Context context, int textStringRes, int actionLabelStringRes, ActionClickListener actionClickListener) {
        Snackbar snackbar = Snackbar.with(context)
                .type(SnackbarType.MULTI_LINE)
                .text(textStringRes)
                .actionLabel(actionLabelStringRes)
                .actionListener(actionClickListener);
        SnackbarManager.show(snackbar);
    }

    public void showSnackBarWithAction(Context context, String textStringRes, int actionLabelStringRes, ActionClickListener actionClickListener) {
        Snackbar snackbar = Snackbar.with(context)
                .type(SnackbarType.MULTI_LINE)
                .text(textStringRes)
                .actionLabel(actionLabelStringRes)
                .actionListener(actionClickListener);
        SnackbarManager.show(snackbar);
    }

    public void showSnackBarWithoutAction(Context context, int textStringRes) {
        Snackbar snackbar = Snackbar.with(context)
                .type(SnackbarType.MULTI_LINE)
                .text(textStringRes)
                .duration(Snackbar.SnackbarDuration.LENGTH_SHORT);
        SnackbarManager.show(snackbar);
    }

    public void showRetry(Context context, int textStringRes, ActionClickListener actionClickListener) {
        Snackbar snackbar = Snackbar.with(context)
                .type(SnackbarType.MULTI_LINE)
                .text(textStringRes)
                .actionLabel(R.string.retry)
                .actionListener(actionClickListener);
        SnackbarManager.show(snackbar);
    }

    public void showIndefiniteWithAction(Context context, int textStringRes, int actionLabelStringRes, ActionClickListener actionClickListener) {
        Snackbar snackbar = Snackbar.with(context)
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .type(SnackbarType.MULTI_LINE)
                .text(textStringRes)
                .actionLabel(actionLabelStringRes)
                .actionListener(actionClickListener);
        SnackbarManager.show(snackbar);
    }
}
