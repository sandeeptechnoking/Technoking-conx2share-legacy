package com.conx2share.conx2share.ui.sayno.cell;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.MessageCell;

public class AnonymableMessageCell extends MessageCell implements CellAnonymity {

    private boolean isAnonymous;

    public AnonymableMessageCell(View view) {
        super(view);
    }

    @Override
    protected void setupAvatar(Context context, ImageView avatar, String avatarUrl) {
        if (isOutgoing() && isAnonymous) {
            Glide.with(context)
                    .load(R.drawable.ic_chat_anonymous)
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.drawable.friend_placeholder)
                    .into(avatar);
        } else {
            super.setupAvatar(context, avatar, avatarUrl);
        }
    }

    @Override
    public void changeAnonymity(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
}