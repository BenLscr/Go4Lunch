package com.lescour.ben.go4lunch.model.nearby;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by benja on 25/03/2019.
 */
public class AltId {

    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("scope")
    @Expose
    private String scope;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

}
