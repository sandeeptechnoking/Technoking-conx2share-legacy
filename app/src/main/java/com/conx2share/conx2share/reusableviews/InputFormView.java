package com.conx2share.conx2share.reusableviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import android.widget.EditText;
import android.widget.LinearLayout;

import com.conx2share.conx2share.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by heathersnepenger on 1/18/17.
 */

public class InputFormView extends LinearLayout {

    private TextInputLayout textInputLayout;
    private EditText editText;

    private InputChangedListener inputChangedListener;

    public InputFormView(Context context) {
        super(context);
        init(null, 0);
    }

    public InputFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public InputFormView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        LayoutInflater.from(getContext()).inflate(R.layout.input_form_view, this);

        textInputLayout = (TextInputLayout) findViewById(R.id.input_form_text_input_layout);
        editText = (EditText) findViewById(R.id.input_form_edittext);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.InputFormView, defStyle, 0);

        String hint = a.getString(R.styleable.InputFormView_hint);
        int inputType = a.getInt(R.styleable.InputFormView_input_type, 0);
        boolean singleLine = a.getBoolean(R.styleable.InputFormView_single_line, true);
        int imeOption = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "imeOptions", -1);

        if (!isInEditMode()) {
            if (imeOption > -1){
                editText.setImeOptions(imeOption);
            }

            textInputLayout.setHint(hint);
            if (singleLine) {
                editText.setMaxLines(1);
                editText.setSingleLine(true);
            }

            switch (inputType) {
                case 0:
                    // Standard text input
                    break;
                case 1:
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    break;
                case 2:
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    break;
                case 3:
                    editText.setInputType(InputType.TYPE_CLASS_PHONE);
                    break;
                case 4:
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                    break;
                case 5:
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    break;
            }

            setupUI();
        }

        a.recycle();

    }

    private void setupUI() {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (inputChangedListener != null) {
                    inputChangedListener.inputChanged(InputFormView.this, s.toString());
                }
            }
        });
    }

    public TextInputLayout getTextInputLayout() {
        return textInputLayout;
    }

    public void setError(String error) {
        textInputLayout.setError(error);
    }

    public boolean checkValid(String errorMessage) {
        if (editText.getText().length() > 0) {
            return true;
        } else {
            setError(errorMessage);
            return false;
        }
    }

    public boolean checkValidEmail() {
        if (editText.getText().length() > 0 && isEmailValid(editText.getText().toString())) {
            return true;
        } else {
            setError("Please Enter a Valid Email");
            return false;
        }
    }

    private static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.+-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setInputChangedListener(InputChangedListener inputChangedListener) {
        this.inputChangedListener = inputChangedListener;
    }

    public interface InputChangedListener {
        void inputChanged(InputFormView view, String s);
    }
}