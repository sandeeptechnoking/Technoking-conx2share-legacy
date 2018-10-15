package com.conx2share.conx2share.text;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.network.models.response.HashTag;
import com.conx2share.conx2share.ui.discover.DiscoverActivity;
import com.conx2share.conx2share.ui.discover.DiscoverFragment;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class HashTagLinkSpan extends ClickableSpan {

    public static final String TAG = HashTagLinkSpan.class.getSimpleName();

    private final int TEXT_COLOR;

    private final HashTag mHashTag;

    public HashTagLinkSpan(Context context, HashTag hashTag) {
        TEXT_COLOR = context.getResources().getColor(R.color.conx_teal);
        mHashTag = hashTag;
    }

    @Override
    public void onClick(View widget) {
        Intent intent = new Intent(widget.getContext(), DiscoverActivity.class);
        intent.putExtra(DiscoverFragment.HASHTAG, mHashTag.getTitle());
        widget.getContext().startActivity(intent);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setColor(TEXT_COLOR);
        tp.setUnderlineText(false);
    }
}
