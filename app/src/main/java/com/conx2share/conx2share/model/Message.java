package com.conx2share.conx2share.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Message implements Parcelable {

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    protected int id;

    protected String body;

    protected VideoMessage video;

    protected ImageMessage image;

    protected AudioMessage audio;

    protected int userId;

    protected int toId;

    protected String created_at;

    protected String message_type;

    protected StreamingUrl streamingUrl;

    protected Integer chatId;

    protected String expirationTime;

    protected String timeToLive;

    protected String displayTime;

    protected String userFirstName;

    protected String userLastName;

    protected UserAvatar userAvatar;

    protected int audioLength;

    protected String userUsername;

    public Message() {
        // NO OP
    }

    public Message(String body) {
        this.body = body;
        userAvatar = new UserAvatar(new Picture(""));
    }

    public Message(int id, String body, VideoMessage video, ImageMessage image, int user_id, int to_id, String created_at, String message_type, StreamingUrl streamingUrl) {
        this.id = id;
        this.body = body;
        this.video = video;
        this.image = image;
        this.userId = user_id;
        this.toId = to_id;
        this.created_at = created_at;
        this.message_type = message_type;
        this.streamingUrl = streamingUrl;
        userAvatar = new UserAvatar(new Picture(""));
    }

    public Message(int id, String body, VideoMessage video, ImageMessage image, int user_id, int to_id, String created_at, String message_type, StreamingUrl streamingUrl, AudioMessage audio) {
        this.id = id;
        this.body = body;
        this.video = video;
        this.image = image;
        this.userId = user_id;
        this.toId = to_id;
        this.created_at = created_at;
        this.message_type = message_type;
        this.streamingUrl = streamingUrl;
        this.audio = audio;
        userAvatar = new UserAvatar(new Picture(""));
    }

    public Message(int id, String body, VideoMessage video, ImageMessage image, int user_id, int to_id, String created_at, String message_type, StreamingUrl streamingUrl, Integer chatId) {
        this.id = id;
        this.body = body;
        this.video = video;
        this.image = image;
        this.userId = user_id;
        this.toId = to_id;
        this.created_at = created_at;
        this.message_type = message_type;
        this.streamingUrl = streamingUrl;
        this.chatId = chatId;
        userAvatar = new UserAvatar(new Picture(""));
    }

    public Message(int id, String body, VideoMessage video, ImageMessage image, int user_id, int to_id, String created_at, String message_type, StreamingUrl streamingUrl, Integer chatId,
                   String userFirstName, String userLastName, UserAvatar userAvatar) {
        this.id = id;
        this.body = body;
        this.video = video;
        this.image = image;
        this.userId = user_id;
        this.toId = to_id;
        this.created_at = created_at;
        this.message_type = message_type;
        this.streamingUrl = streamingUrl;
        this.chatId = chatId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userAvatar = userAvatar;
    }

    public Message(int id, String body, VideoMessage video, ImageMessage image, int user_id, int to_id, String created_at, String message_type, StreamingUrl streamingUrl, Integer chatId,
                   String userFirstName, String userLastName, UserAvatar userAvatar, AudioMessage audio) {
        this.id = id;
        this.body = body;
        this.video = video;
        this.image = image;
        this.userId = user_id;
        this.toId = to_id;
        this.created_at = created_at;
        this.message_type = message_type;
        this.streamingUrl = streamingUrl;
        this.chatId = chatId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userAvatar = userAvatar;
        this.audio = audio;
    }

    private Message(Parcel in) {
        this.id = in.readInt();
        this.body = in.readString();
        this.video = in.readParcelable(VideoMessage.class.getClassLoader());
        this.image = in.readParcelable(ImageMessage.class.getClassLoader());
        this.userId = in.readInt();
        this.toId = in.readInt();
        this.created_at = in.readString();
        this.message_type = in.readString();
        this.streamingUrl = in.readParcelable(StreamingUrl.class.getClassLoader());
    }

    public String getVideoUrl() {
        if (video != null && video.getVideo() != null && !TextUtils.isEmpty(video.getVideo().getUrl())) {
            return video.getVideo().getUrl();
        }

        return null;
    }

    public boolean hasVideo() {
        return getVideoUrl() != null;
    }

    public String getAudioUrl() {
        if (audio != null && audio.getAudio() != null && !TextUtils.isEmpty(audio.getAudio().getUrl())) {
            return audio.getAudio().getUrl();
        }

        return null;
    }

    public String getImageUrl() {
        if (image != null && image.getImage() != null && !TextUtils.isEmpty(image.getImage().getFeedUrl())) {
            return image.getImage().getFeedUrl();
        }

        return null;
    }

    public String getFullImageUrl() {
        if (image != null && image.getImage() != null && !TextUtils.isEmpty(image.getImage().getUrl())) {
            return image.getImage().getUrl();
        }

        return null;
    }

    public boolean hasImage() {
        return getImageUrl() != null;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public UserAvatar getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(UserAvatar userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public StreamingUrl getStreamingUrl() {
        return streamingUrl;
    }

    public void setStreamingUrl(StreamingUrl streamingUrl) {
        this.streamingUrl = streamingUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public VideoMessage getVideo() {
        return video;
    }

    public void setVideo(VideoMessage video) {
        this.video = video;
    }

    public void setImage(ImageMessage image) {
        this.image = image;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int user_id) {
        this.userId = user_id;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int to_id) {
        this.toId = to_id;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public String getMessageType() {
        return message_type;
    }

    public void setMessageType(String message_type) {
        this.message_type = message_type;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        expirationTime = df.format(date);
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Date getExpirationTimeAsDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = null;
        try {
            date = simpleDateFormat.parse(expirationTime);
        } catch (ParseException e) {
            Log.e("Message", e.getMessage());
        }

        return date;
    }

    public String getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(String timeToLive) {
        this.timeToLive = timeToLive;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public AudioMessage getAudio() {
        return audio;
    }

    public void setAudio(AudioMessage audio) {
        this.audio = audio;
    }

    public int getAudioLength() {
        return audioLength;
    }

    public void setAudioLength(int audioLength) {
        this.audioLength = audioLength;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.body);
        dest.writeParcelable(this.video, flags);
        dest.writeParcelable(this.image, flags);
        dest.writeInt(this.userId);
        dest.writeInt(this.toId);
        dest.writeString(this.created_at);
        dest.writeString(this.message_type);
        dest.writeParcelable(this.streamingUrl, flags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Message message = (Message) o;

        if (id != message.id) {
            return false;
        }
        if (toId != message.toId) {
            return false;
        }
        if (userId != message.userId) {
            return false;
        }
        if (body != null ? !body.equals(message.body) : message.body != null) {
            return false;
        }
        if (created_at != null ? !created_at.equals(message.created_at) : message.created_at != null) {
            return false;
        }
        if (image != null ? !image.equals(message.image) : message.image != null) {
            return false;
        }
        if (message_type != null ? !message_type.equals(message.message_type) : message.message_type != null) {
            return false;
        }
        if (streamingUrl != null ? !streamingUrl.equals(message.streamingUrl) : message.streamingUrl != null) {
            return false;
        }
        if (video != null ? !video.equals(message.video) : message.video != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (video != null ? video.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + userId;
        result = 31 * result + toId;
        result = 31 * result + (created_at != null ? created_at.hashCode() : 0);
        result = 31 * result + (message_type != null ? message_type.hashCode() : 0);
        result = 31 * result + (streamingUrl != null ? streamingUrl.hashCode() : 0);
        return result;
    }

    public String getUserAvatarUrl() {
        if (getUserAvatar() != null && getUserAvatar().getAvatar() != null && !TextUtils.isEmpty(getUserAvatar().getAvatar().getUrl())) {
            return getUserAvatar().getAvatar().getUrl();
        }

        return null;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }
}
