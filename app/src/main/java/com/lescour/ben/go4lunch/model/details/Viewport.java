package com.lescour.ben.go4lunch.model.details;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by benja on 26/03/2019.
 */
public class Viewport implements Parcelable {

    @SerializedName("northeast")
    @Expose
    private Northeast northeast;
    @SerializedName("southwest")
    @Expose
    private Southwest southwest;

    protected Viewport(Parcel in) {
        northeast = in.readParcelable(Northeast.class.getClassLoader());
        southwest = in.readParcelable(Southwest.class.getClassLoader());
    }

    public static final Creator<Viewport> CREATOR = new Creator<Viewport>() {
        @Override
        public Viewport createFromParcel(Parcel in) {
            return new Viewport(in);
        }

        @Override
        public Viewport[] newArray(int size) {
            return new Viewport[size];
        }
    };

    public Northeast getNortheast() {
        return northeast;
    }

    public void setNortheast(Northeast northeast) {
        this.northeast = northeast;
    }

    public Southwest getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Southwest southwest) {
        this.southwest = southwest;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(northeast, flags);
        dest.writeParcelable(southwest, flags);
    }
}
