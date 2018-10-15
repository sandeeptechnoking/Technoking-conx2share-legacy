package com.conx2share.conx2share.text;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.network.models.UserTag;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.ui.profile.ProfileFragment;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class UserTagLinkSpan extends ClickableSpan {

    public static final String TAG = UserTagLinkSpan.class.getSimpleName();

    private final int TEXT_COLOR;

    private final UserTag mUserTag;

    public UserTagLinkSpan(Context context, UserTag userTag) {
        mUserTag = userTag;
        TEXT_COLOR = context.getResources().getColor(R.color.conx_teal);
    }

    @Override
    public void onClick(View widget) {
        Intent intent = new Intent(widget.getContext(), ProfileActivity.class);
        intent.putExtra(ProfileFragment.PROFILEID_KEY, String.valueOf(mUserTag.getUserId()));
        widget.getContext().startActivity(intent);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setColor(TEXT_COLOR);
        tp.setUnderlineText(false);
    }
}
