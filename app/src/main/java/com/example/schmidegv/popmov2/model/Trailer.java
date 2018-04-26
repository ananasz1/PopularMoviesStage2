package com.example.schmidegv.popmov2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class Trailer implements Parcelable {

    private static final String VIDEO_LINK_BASE_URL = "https://www.youtube.com/watch?v=";
    private static final String VIDEO_THUMBNAIL_URL_PATTERN =
            "https://img.youtube.com/vi/%s/mqdefault.jpg";

    @SerializedName("name")
    private String mName;

    @SerializedName("source")
    private String mId;

    @SerializedName("type")
    private String mType;

    public String getName() {
        return mName;
    }

    public String getLink() {
        return VIDEO_LINK_BASE_URL + mId;
    }

    public String getType() {
        return mType;
    }

    public Trailer(Parcel in) {
        mName = in.readString();
        mId = in.readString();
        mType = in.readString();
    }

    public String getThumbnailLink() {
        return String.format(VIDEO_THUMBNAIL_URL_PATTERN, mId);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mId);
        dest.writeString(mType);
    }
}
