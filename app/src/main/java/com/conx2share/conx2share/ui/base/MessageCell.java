package com.conx2share.conx2share.ui.base;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.conx2share.conx2share.util.PreferencesUtil;

import javax.inject.Inject;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import io.techery.celladapter.Cell;
import io.techery.celladapter.Layout;
import roboguice.inject.InjectView;

@Layout(R.layout.item_chat_message)
public class MessageCell extends BaseRoboCell<Message, MessageCell.MessageCellListener> {
    @InjectView(R.id.item_chat_avatar)
    AvatarImageView avatar;

    @InjectView(R.id.item_chat_message)
    TextView message;

    @InjectView(R.id.item_chat_attachment)
    ImageView attachment;

    @InjectView(R.id.item_chat_message_container)
    ViewGroup messageContainer;

    @Inject
    PreferencesUtil preferencesUtil;

    public MessageCell(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    protected void bindView() {
        Message msg = getItem();
        Context context = itemView.getContext();

        boolean isOutgoing = isOutgoing();
        applyItemAlign(isOutgoing);

        messageContainer.setBackgroundResource(isOutgoing ? R.drawable.bubble_blue
                : R.drawable.bubble_white);
        message.setTextColor(ContextCompat.getColor(context, isOutgoing ? R.color.white : R.color.text_color_dark));
        message.setLinkTextColor(ContextCompat.getColor(context, isOutgoing ? R.color.white : R.color.text_color_dark));
        message.setHighlightColor(ContextCompat.getColor(context, isOutgoing ? R.color.text_color_dark : R.color.white));

        avatar.initView(msg.getUserAvatarUrl(), msg.getUserFirstName(), msg.getUserLastName());
        if (!TextUtils.isEmpty(msg.getBody())) {
            message.setText(msg.getBody());
            message.setVisibility(View.VISIBLE);
        }else{
            message.setText("");
            message.setVisibility(View.GONE);
        }

        if (hasAttachment(msg)) {
            attachment.setVisibility(View.VISIBLE);

            if (msg.hasImage()) {
                Glide.with(context)
                        .load(msg.getImageUrl())
                        .centerCrop()
                        .dontAnimate()
                        .into(attachment);

                attachment.setOnClickListener(v -> getListener().onImageOpen(msg.getImageUrl()));
            }

            if (msg.hasVideo()) {
                attachment.setImageResource(R.drawable.video_orange);
                if (isOutgoing()) {
                    attachment.setColorFilter(ContextCompat.getColor(context, R.color.white));
                } else {
                    attachment.setColorFilter(ContextCompat.getColor(context, R.color.conx_teal));
                }
                attachment.setOnClickListener(v -> getListener().onVideoOpen(msg.getVideoUrl()));
            }
        } else {
            attachment.setVisibility(View.GONE);
        }
        if (!message.isInLayout()){
            message.requestLayout();
        }
    }

    protected final boolean isOutgoing() {
        return getItem().getUserId() == preferencesUtil.getAuthUser().getId();
    }

    protected void setupAvatar(Context context,
                               ImageView avatar,
                               String avatarUrl) {
        Glide.with(context)
                .load(avatarUrl)
                .centerCrop()
                .dontAnimate()
                .placeholder(R.drawable.friend_placeholder)
                .into(avatar);
    }

    private void applyItemAlign(boolean isOutgoing) {
        RelativeLayout.LayoutParams avatarLayoutParams = (RelativeLayout.LayoutParams) avatar.getLayoutParams();
        avatarLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
        avatarLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        avatarLayoutParams.addRule(isOutgoing ? RelativeLayout.ALIGN_PARENT_RIGHT
                : RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        RelativeLayout.LayoutParams msgLayoutParams = (RelativeLayout.LayoutParams) messageContainer.getLayoutParams();
        msgLayoutParams.removeRule(RelativeLayout.LEFT_OF);
        msgLayoutParams.removeRule(RelativeLayout.RIGHT_OF);
        msgLayoutParams.addRule(isOutgoing ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, avatar.getId());
    }

    private boolean hasAttachment(Message message) {
        return message.getVideoUrl() != null
                || message.getImageUrl() != null;
    }

    public static abstract class MessageCellListener implements Cell.Listener<Message> {
        public abstract void onImageOpen(String imageUrl);

        public abstract void onVideoOpen(String videoUrl);

        @Override
        public void onCellClicked(Message message) {
        }
    }
}