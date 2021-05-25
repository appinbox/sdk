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
    public final Date readAt;

    protected Message(Parcel in) {
        id = in.readString();
        title = in.readString();
        body = in.readString();
        sentAt = new Date(in.readLong());
        Long rat = in.readLong();
        if (rat > 0) {
            readAt = new Date(in.readLong());
        } else {
            readAt = null;
        }
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
        if (readAt != null) {
            dest.writeLong(readAt.getTime());
        } else {
            dest.writeLong(0);
        }
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
