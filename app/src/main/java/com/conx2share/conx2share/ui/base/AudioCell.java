package com.conx2share.conx2share.ui.base;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.conx2share.conx2share.util.AudioHelper;
import com.conx2share.conx2share.util.PreferencesUtil;
import com.conx2share.conx2share.util.SimpleAudioHelper;
import com.conx2share.conx2share.util.SnackbarUtil;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;
import io.techery.celladapter.Cell;
import io.techery.celladapter.Layout;

@Layout(R.layout.item_chat_audio_message)
public class AudioCell extends BaseRoboCell<Message, AudioCell.MessageCellListener> {
    @BindView(R.id.item_chat_avatar)
    AvatarImageView avatar;

    @BindView(R.id.item_chat_message)
    TextView message;

    @BindView(R.id.item_chat_play_bt)
    ImageView playButton;

    @BindView(R.id.item_chat_play_progress)
    ProgressBar progressBar;

    @BindView(R.id.item_chat_play_time)
    TextView playTime;

    @BindView(R.id.item_chat_message_container)
    ViewGroup messageContainer;

    @Inject
    PreferencesUtil preferencesUtil;

    @Inject
    SnackbarUtil snackbarUtil;

    AudioHelper audioHelper;
    boolean isPlaying;

    public AudioCell(View view) {
        super(view);
        ButterKnife.bind(this, view);
        audioHelper = new SimpleAudioHelper(this::resetPlayingViews, time -> {
            playTime.setText(time);
            progressBar.setProgress(progressBar.getProgress() + 1);
        });
    }

    private void resetPlayingViews() {
        playButton.setImageResource(R.drawable.v_ic_play_circle);
        setPlayBtColor();
        progressBar.setProgress(0);
        isPlaying = false;
        playTime.setText(playTime.getTag() != null ?
                (String) playTime.getTag() : itemView.getContext().getString(R.string.cant_play_audio_msg));
    }

    private void setPlayBtColor() {
        DrawableCompat.setTint(playButton.getDrawable(),
                ContextCompat.getColor(itemView.getContext(), isOutgoing() ? R.color.white : R.color.conx_primary));
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
        playTime.setTextColor(ContextCompat.getColor(context, isOutgoing ? R.color.white : R.color.text_color_dark));
        DrawableCompat.setTint(playButton.getDrawable(),
                ContextCompat.getColor(context, isOutgoing ? R.color.white : R.color.conx_primary));

        avatar.initView(msg.getUserAvatarUrl(), msg.getUserFirstName(), msg.getUserLastName());
        if (!TextUtils.isEmpty(msg.getBody())) {
            message.setText(msg.getBody());
            message.setVisibility(View.VISIBLE);
        } else {
            message.setText("");
            message.setVisibility(View.GONE);
        }
        progressBar.setProgress(0);
        progressBar.setProgressDrawable(ContextCompat.getDrawable(context,
                isOutgoing ? R.drawable.white_progress_bar : R.drawable.teal_progress_bar));
        if (hasAttachment(msg)) {
            playButton.setEnabled(true);
            playTime.setText(getFormattedTime(msg));
            playTime.setTag(getFormattedTime(msg));
            progressBar.setMax(msg.getAudioLength());
            playButton.setOnClickListener(v -> playMessage(msg));
        } else {
            playButton.setEnabled(false);
            playTime.setText(R.string.zero_play_length);
            progressBar.setEnabled(false);
        }
        if (!progressBar.isInLayout()){
            progressBar.requestLayout();
        }

    }

    private String getFormattedTime(Message msg) {
        return String.format("%02d:%02d",
                TimeUnit.SECONDS.toMinutes(msg.getAudioLength()),
                TimeUnit.SECONDS.toSeconds(msg.getAudioLength()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(msg.getAudioLength()))
        );
    }

    private void playMessage(Message msg) {
        if (isPlaying) {
            audioHelper.stopPlaying();
        } else {
            if (audioHelper.startPlaying(msg.getAudioUrl(), msg.getAudioLength())) {
                isPlaying = true;
                playButton.setImageResource(R.drawable.v_ic_pause_circle);
                setPlayBtColor();
            } else {
                isPlaying = false;
                snackbarUtil.displaySnackBar(itemView.getContext(), R.string.cant_play_audio_msg);
            }
        }
    }

    protected final boolean isOutgoing() {
        return getItem().getUserId() == preferencesUtil.getAuthUser().getId();
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
        return !TextUtils.isEmpty(message.getAudio().getAudio().getUrl());
    }

    public static abstract class MessageCellListener implements Cell.Listener<Message> {

        @Override
        public void onCellClicked(Message message) {
        }
    }
}