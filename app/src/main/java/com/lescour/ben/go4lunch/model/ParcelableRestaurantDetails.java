package com.lescour.ben.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lescour.ben.go4lunch.model.nearby.Result;

import java.util.List;

/**
 * Created by benja on 28/03/2019.
 */
public class ParcelableRestaurantDetails implements Parcelable {

    private List<Result> nearbyResults;
    private List<PlaceDetailsResponse> placeDetailsResponses;

    public ParcelableRestaurantDetails() {
    }

    protected ParcelableRestaurantDetails(Parcel in) {
        nearbyResults = in.createTypedArrayList(Result.CREATOR);
        placeDetailsResponses = in.createTypedArrayList(PlaceDetailsResponse.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(nearbyResults);
        dest.writeTypedList(placeDetailsResponses);
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

}
