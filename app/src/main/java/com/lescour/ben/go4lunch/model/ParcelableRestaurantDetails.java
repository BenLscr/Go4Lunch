package com.lescour.ben.go4lunch.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.lescour.ben.go4lunch.model.details.PlaceDetailsResponse;
import com.lescour.ben.go4lunch.model.nearby.Result;

import java.util.List;

/**
 * Created by benja on 28/03/2019.
 */
public class ParcelableRestaurantDetails implements Parcelable {

    private Double currentLat, currentLng;
    private List<Result> nearbyResults;
    private List<PlaceDetailsResponse> placeDetailsResponses;
    private List<Bitmap> mBitmapList;

    public ParcelableRestaurantDetails() {
    }

    protected ParcelableRestaurantDetails(Parcel in) {
        if (in.readByte() == 0) {
            currentLat = null;
        } else {
            currentLat = in.readDouble();
        }
        if (in.readByte() == 0) {
            currentLng = null;
        } else {
            currentLng = in.readDouble();
        }
        nearbyResults = in.createTypedArrayList(Result.CREATOR);
        placeDetailsResponses = in.createTypedArrayList(PlaceDetailsResponse.CREATOR);
        mBitmapList = in.createTypedArrayList(Bitmap.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (currentLat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(currentLat);
        }
        if (currentLng == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(currentLng);
        }
        dest.writeTypedList(nearbyResults);
        dest.writeTypedList(placeDetailsResponses);
        dest.writeTypedList(mBitmapList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParcelableRestaurantDetails> CREATOR = new Creator<ParcelableRestaurantDetails>() {
        @Override
        public ParcelableRestaurantDetails createFromParcel(Parcel in) {
            return new ParcelableRestaurantDetails(in);
        }

        @Override
        public ParcelableRestaurantDetails[] newArray(int size) {
            return new ParcelableRestaurantDetails[size];
        }
    };

    public Double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(Double currentLat) {
        this.currentLat = currentLat;
    }

    public Double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(Double currentLng) {
        this.currentLng = currentLng;
    }

    public List<Result> getNearbyResults() {
        return nearbyResults;
    }

    public void setNearbyResults(List<Result> nearbyResults) {
        this.nearbyResults = nearbyResults;
    }

    public List<PlaceDetailsResponse> getPlaceDetailsResponses() {
        return placeDetailsResponses;
    }

    public void setPlaceDetailsResponses(List<PlaceDetailsResponse> placeDetailsResponses) {
        this.placeDetailsResponses = placeDetailsResponses;
    }

    public List<Bitmap> getBitmapList() {
        return mBitmapList;
    }

    public void setBitmapList(List<Bitmap> bitmapList) {
        mBitmapList = bitmapList;
    }
}
