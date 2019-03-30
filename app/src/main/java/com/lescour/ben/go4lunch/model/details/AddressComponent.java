package com.lescour.ben.go4lunch.model.details;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by benja on 26/03/2019.
 */
public class AddressComponent implements Parcelable {

    @SerializedName("long_name")
    @Expose
    private String longName;
    @SerializedName("short_name")
    @Expose
    private String shortName;
    @SerializedName("types")
    @Expose
    private List<String> types = null;

    protected AddressComponent(Parcel in) {
        longName = in.readString();
        shortName = in.readString();
        types = in.createStringArrayList();
    }

    public static final Creator<AddressComponent> CREATOR = new Creator<AddressComponent>() {
        @Override
        public AddressComponent createFromParcel(Parcel in) {
            return new AddressComponent(in);
        }

        @Override
        public AddressComponent[] newArray(int size) {
            return new AddressComponent[size];
        }
    };

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(longName);
        dest.writeString(shortName);
        dest.writeStringList(types);
    }
}
