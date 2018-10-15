package com.conx2share.conx2share.util;

import android.util.Patterns;
import android.widget.EditText;

import java.util.List;

public class ValidationUtil {

    public static boolean checkIfStringIsValid(String string) {
        return !(string == null || string.trim().isEmpty() || "null".equals(string) || string.equals(null));
    }

    public static boolean checkIfListIsValid(List list) {
        return !(list == null || list.isEmpty());
    }

    public static boolean checkIfEditTextIsValid(EditText editText) {
        return checkIfStringIsValid(editText.getText().toString());
    }

    public static boolean isEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
