package com.conx2share.conx2share.adapter;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MessagingAdapter extends BaseAdapter {

    public static String TAG = MessagingAdapter.class.getSimpleName();

    LayoutInflater mLayoutInflater;

    private ArrayList<Message> mMessages;

    private Context mContext;

    private ImageView messagePhoto;

    private ImageView photoGradient;

    public MessagingAdapter(Context context, ArrayList<Message> messages) {
        mMessages = messages;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Message getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // TODO - clean up all of the potential exceptions in this adapter ~Sarah
        Message message = mMessages.get(position);
        if (mMessages.get(position).getMessageType().equals("unread")) {
            if (!mMessages.get(position).hasImage()) {
                convertView = mLayoutInflater.inflate(R.layout.unread_message_list_item, null);
            } else {
                if (mMessages.get(position).getAudio().getAudio().getUrl() == null || mMessages.get(position).getAudio().getAudio().getUrl().equals("")) {
                    convertView = mLayoutInflater.inflate(R.layout.unread_photo_message_list, null);
                } else {
                    convertView = mLayoutInflater.inflate(R.layout.unread_message_list_item, null);
                }
                setupPhotoMessage(convertView, position);
            }

        } else if (mMessages.get(position).getMessageType().equals("sent")) {
            if (!mMessages.get(position).hasImage()) {
                convertView = mLayoutInflater.inflate(R.layout.sent_message_list_item, null);
            } else {
                if (mMessages.get(position).getAudio().getAudio().getUrl() == null || mMessages.get(position).getAudio().getAudio().getUrl().equals("")) {
                    convertView = mLayoutInflater.inflate(R.layout.sent_photo_message_list, null);
                } else {
                    convertView = mLayoutInflater.inflate(R.layout.sent_message_list_item, null);
                }
                setupPhotoMessage(convertView, position);
            }

        }

        TextView timeTextView = (TextView) convertView.findViewById(R.id.message_text_timestamp);
        if (message.getExpirationTime() != null && message.getTimeToLive() != null) {
            timeTextView.setVisibility(View.VISIBLE);
            if (message.getDisplayTime() == null) {
                timeTextView.setText(message.getTimeToLive() + "s");
            } else {
                timeTextView.setText(message.getDisplayTime() + "s");
            }
        } else if (message.getTimeToLive() != null) {
            timeTextView.setText(message.getTimeToLive() + "s");
        } else {
            timeTextView.setVisibility(View.GONE);
        }

        TextView messageTextBody = (TextView) convertView.findViewById(R.id.message_text_body);
        ImageView videoPlayImage = (ImageView) convertView.findViewById(R.id.video_play_image);
        if (!mMessages.get(position).getVideo().getVideo().getUrl().equals("") || mMessages.get(position).hasImage() || !mMessages.get(position).getAudio().getAudio()
                .getUrl().equals("")) {
            if (!mMessages.get(position).getBody().equals("")) {
                if (photoGradient != null) {
                    photoGradient.setVisibility(View.VISIBLE);
                }
            }
            String imageUrl = mMessages.get(position).getImageUrl();
            if (imageUrl != null) {
                if (mMessages.get(position).getAudio().getAudio().getUrl() == null || mMessages.get(position).getAudio().getAudio().getUrl().equals("")) {
                    Glide.with(mContext).load(imageUrl).override(270, 270).dontAnimate().centerCrop().into(messagePhoto);
                }
            }

            if (!mMessages.get(position).getVideo().getVideo().getUrl().equals("")) {
                if (videoPlayImage != null) {
                    videoPlayImage.setVisibility(View.VISIBLE);
                }
            }

            if (!mMessages.get(position).getAudio().getAudio().getUrl().equals("")) {
                View audioMessageHolder = convertView.findViewById(R.id.audio_message_holder);
                ImageView audioMessagePlayButton = (ImageView) convertView.findViewById(R.id.audio_message_play_button);
                ImageView audioMessageWav = (ImageView) convertView.findViewById(R.id.audio_message_wav);
                TextView audioMessageDuration = (TextView) convertView.findViewById(R.id.audio_message_duration);

                if (audioMessageHolder != null) {
                    audioMessageHolder.setVisibility(View.VISIBLE);
                }

                if (audioMessagePlayButton != null) {
                    audioMessagePlayButton.setVisibility(View.VISIBLE);
                }

                if (audioMessageWav != null) {
                    audioMessageWav.setVisibility(View.VISIBLE);
                    if (mMessages != null && mMessages.size() > 0
                            && mMessages.get(position).hasImage()) {

                        String wavUrl = mMessages.get(position).getImageUrl();
                        Glide.with(mContext).load(wavUrl).dontAnimate().into(audioMessageWav);
                    }
                }

                if (audioMessageDuration != null) {
                    audioMessageDuration.setVisibility(View.VISIBLE);
                    int displayMinutes = message.getAudioLength() / 60;
                    int displaySeconds = message.getAudioLength() % 60;
                    audioMessageDuration.setText(String.format("%02d:%02d", displayMinutes, displaySeconds));
                }

                messageTextBody.setVisibility(View.INVISIBLE);
            }

        }
        messageTextBody.setText(mMessages.get(position).getBody());
        return convertView;
    }

    public void setupPhotoMessage(View convertView, int position) {
        messagePhoto = (ImageView) convertView.findViewById(R.id.message_photo);
        if (!mMessages.get(position).getBody().equals("")) {
            photoGradient = (ImageView) convertView.findViewById(R.id.photo_gradient);
        }
    }

    public void setMessages(ArrayList<Message> messages) {
        mMessages = messages;
    }
}
