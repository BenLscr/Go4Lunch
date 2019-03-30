package com.lescour.ben.go4lunch.model.details;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by benja on 26/03/2019.
 */
public class DetailsResponse implements Parcelable {

    @SerializedName("result")
    @Expose
    private Result result;

    protected DetailsResponse(Parcel in) {
        result = in.readParcelable(Result.class.getClassLoader());
    }

    public static final Creator<DetailsResponse> CREATOR = new Creator<DetailsResponse>() {
        @Override
        public DetailsResponse createFromParcel(Parcel in) {
            return new DetailsResponse(in);
        }

        @Override
        public DetailsResponse[] newArray(int size) {
            return new DetailsResponse[size];
        }
    };

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(result, flags);
    }
}
