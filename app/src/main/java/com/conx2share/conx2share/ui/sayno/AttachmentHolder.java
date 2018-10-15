package com.conx2share.conx2share.ui.sayno;

import android.os.Parcel;
import android.os.Parcelable;

public class AttachmentHolder implements Parcelable {

    public static final Creator<AttachmentHolder> CREATOR = new Creator<AttachmentHolder>() {
        @Override
        public AttachmentHolder createFromParcel(Parcel in) {
            return new AttachmentHolder(in);
        }

        @Override
        public AttachmentHolder[] newArray(int size) {
            return new AttachmentHolder[size];
        }
    };

    final String path;

    final Type type;

    public AttachmentHolder(String path, Type type) {
        this.path = path;
        this.type = type;
    }

    protected AttachmentHolder(Parcel in) {
        path = in.readString();
        type = Type.valueOf(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(type.name());
    }

    public enum Type {
        IMAGE, VIDEO
    }
}