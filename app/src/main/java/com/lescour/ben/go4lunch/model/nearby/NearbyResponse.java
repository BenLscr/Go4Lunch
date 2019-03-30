package com.lescour.ben.go4lunch.model.nearby;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by benja on 25/03/2019.
 */
public class NearbyResponse {

    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

}
