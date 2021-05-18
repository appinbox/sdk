package com.appinbox.sdk.msg;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * A dummy item representing a piece of content.
 */
public class Message implements Parcelable {
    public final String id;
    public final String title;
    public final String body;
    public final Date sentAt;
    public final Boolean read;

    protected Message(Parcel in) {
        id = in.readString();
        title = in.readString();
        body = in.readString();
        sentAt = new Date(in.readLong());
        read = in.readLong() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeLong(sentAt.getTime());
        dest.writeLong(read?1:0);
    }

    @SuppressWarnings("unused")
    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
