package com.conx2share.conx2share.ui.sign_up;

import com.conx2share.conx2share.ui.base.BaseActivity;
import com.conx2share.conx2share.ui.base.BaseDatePickerFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class SignUpActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, SignUpFragment.newInstance());
            ft.commit();
        }
    }
}
