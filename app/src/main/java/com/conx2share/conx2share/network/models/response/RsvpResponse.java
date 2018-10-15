package com.conx2share.conx2share.network.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.conx2share.conx2share.network.models.Rsvp;

public class RsvpResponse implements Parcelable {

    private Rsvp rsvp;
    public final static Parcelable.Creator<RsvpResponse> CREATOR = new Creator<RsvpResponse>() {

        public RsvpResponse createFromParcel(Parcel in) {
            RsvpResponse instance = new RsvpResponse();
            instance.rsvp = ((Rsvp) in.readValue((Rsvp.class.getClassLoader())));
            return instance;
        }

        public RsvpResponse[] newArray(int size) {
            return (new RsvpResponse[size]);
        }

    };

    public Rsvp getRsvp() {
        return rsvp;
    }

    public void setRsvp(Rsvp rsvp) {
        this.rsvp = rsvp;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(rsvp);
    }

    public int describeContents() {
        return 0;
    }

}
