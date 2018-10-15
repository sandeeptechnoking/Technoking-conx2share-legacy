package com.conx2share.conx2share.ui.contact;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.reusableviews.InputFormView;
import com.conx2share.conx2share.util.ValidationUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;
import butterknife.OnClick;


public class AddEmailDialog extends BottomSheetDialog {

    private EmailDialogSent listener;

    @BindView(R.id.email_send)
    FloatingActionButton emailSend;
    @BindView(R.id.email_friend_et)
    InputFormView emailFriendEt;

    public AddEmailDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public AddEmailDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
        initView();
    }

    protected AddEmailDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    private void initView() {
        setContentView(R.layout.add_email_dialog);
        ButterKnife.bind(this, this);
        emailFriendEt.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailFriendEt.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @OnClick(R.id.email_send)
    void emailOnClick() {
        String email = emailFriendEt.getEditText().getText().toString();
        if (!TextUtils.isEmpty(email) && ValidationUtil.isEmail(email)) {
            if (listener != null) {
                listener.onSendClick(emailFriendEt.getEditText().getText().toString());
            }
            dismiss();
        } else {
            emailFriendEt.setError(getContext().getString(R.string.please_enter_a_valid_email_address));
        }
    }

    void setEmailListener(EmailDialogSent listener) {
        this.listener = listener;
    }

    interface EmailDialogSent {
        void onSendClick(String email);
    }
}
