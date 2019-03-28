package com.lescour.ben.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by benja on 28/03/2019.
 */
public class ParcelableLocation implements Parcelable {

    private Double latitude;
    private Double longitude;

    public ParcelableLocation() {
    }

    protected ParcelableLocation(Parcel in) {
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
    }

    public static final Creator<ParcelableLocation> CREATOR = new Creator<ParcelableLocation>() {
        @Override
        public ParcelableLocation createFromParcel(Parcel in) {
            return new ParcelableLocation(in);
        }

        @Override
        public ParcelableLocation[] newArray(int size) {
            return new ParcelableLocation[size];
        }
    };

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
    }
}
