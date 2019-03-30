package com.lescour.ben.go4lunch.model.details;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by benja on 26/03/2019.
 */
public class Period implements Parcelable {

    @SerializedName("close")
    @Expose
    private Close close;
    @SerializedName("open")
    @Expose
    private Open open;

    protected Period(Parcel in) {
        close = in.readParcelable(Close.class.getClassLoader());
        open = in.readParcelable(Open.class.getClassLoader());
    }

    public static final Creator<Period> CREATOR = new Creator<Period>() {
        @Override
        public Period createFromParcel(Parcel in) {
            return new Period(in);
        }

        @Override
        public Period[] newArray(int size) {
            return new Period[size];
        }
    };

    public Close getClose() {
        return close;
    }

    public void setClose(Close close) {
        this.close = close;
    }

    public Open getOpen() {
        return open;
    }

    public void setOpen(Open open) {
        this.open = open;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(close, flags);
        dest.writeParcelable(open, flags);
    }
}
