package com.conx2share.conx2share.util;

import android.util.Log;
import android.widget.PopupMenu;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PopUpMenuUtil {

    private static final String TAG = PopUpMenuUtil.class.getSimpleName();

    public PopupMenu showPopUpMenu(PopupMenu popupMenu, int menuResourceId) {

        popupMenu.getMenuInflater().inflate(menuResourceId, popupMenu.getMenu());

        // Sets icons to be shown
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper
                            .getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to set force icons: " + e.toString());
        }

        popupMenu.show();

        return popupMenu;
    }
}
