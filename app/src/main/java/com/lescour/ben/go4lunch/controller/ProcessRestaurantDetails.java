package com.lescour.ben.go4lunch.controller;

import android.view.View;

import com.lescour.ben.go4lunch.BuildConfig;
import com.lescour.ben.go4lunch.model.details.AddressComponent;
import com.lescour.ben.go4lunch.model.details.DetailsResponse;
import com.lescour.ben.go4lunch.model.nearby.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by benja on 26/03/2019.
 */
public class ProcessRestaurantDetails {

    private Result mResult;
    private DetailsResponse mDetailsResponse;
    private String newHours;

    public ProcessRestaurantDetails(Result nearbyResult, DetailsResponse detailsResponse) {
        this.mResult = nearbyResult;
        this.mDetailsResponse = detailsResponse;
    }

    public String getRestaurantName() {
        return mDetailsResponse.getResult().getName();
    }

    public String getRestaurantAddress() {
        boolean bStreetNumber = false;
        int i = 0;
        String streetNumber = null;
        do {
            AddressComponent tempAddressComponent = mDetailsResponse.getResult().getAddressComponents().get(i);
            if (tempAddressComponent.getTypes().get(0).equals("street_number")) {
                streetNumber = mDetailsResponse.getResult().getAddressComponents().get(i).getShortName();
                bStreetNumber = true;
            }
            i++;
        } while (!bStreetNumber);

        boolean bRoute = false;
        int j = 0;
        String route = null;
        do {
            AddressComponent tempAddressComponent = mDetailsResponse.getResult().getAddressComponents().get(j);
            if (tempAddressComponent.getTypes().get(0).equals("route")) {
                route = mDetailsResponse.getResult().getAddressComponents().get(j).getShortName();
                bRoute = true;
            }
            j++;
        } while (!bRoute);

        return streetNumber + ", " + route;
    }

    public String getRestaurantOpenHours() {
        if (mDetailsResponse.getResult().getOpeningHours() != null) {
            if (mDetailsResponse.getResult().getOpeningHours().getOpenNow()) {
                if (mDetailsResponse.getResult().getOpeningHours().getPeriods().size() == 1) {
                    return "Open 24/7";
                } else {
                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    String rawHours = null;
                    boolean getCloseFound = false;
                    int i = 0;
                    do {
                        int dayFound = mDetailsResponse.getResult().getOpeningHours().getPeriods().get(i).getClose().getDay();
                        if (dayFound == day - 1) {
                            rawHours = mDetailsResponse.getResult().getOpeningHours().getPeriods().get(i).getClose().getTime();
                            getCloseFound = true;
                        } else {
                            i++;
                        }
                    } while (!getCloseFound);
                    SimpleDateFormat rawFormatHours = new SimpleDateFormat("hhmm", Locale.getDefault());
                    SimpleDateFormat newFormatHours = new SimpleDateFormat("h.mma", Locale.getDefault());
                    try {
                        Date date = rawFormatHours.parse(rawHours);
                        newHours = newFormatHours.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return "Open until " + newHours;
                }
            } else {
                return "Currently closed";
            }
        } else {
            return "No schedules defined";
        }
    }

    public int getRestaurantRate1() {
        if (mResult.getRating() >= 2) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    public int getRestaurantRate2() {
        if (mResult.getRating() >= 3) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    public int getRestaurantRate3() {
        if (mResult.getRating() >= 4) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    public String getRestaurantImage() {
        String baseUrl = "https://maps.googleapis.com/maps/api/place/photo?";
        String maxHeight = "maxheight=76";
        String photoReference = "&photoreference=" + mResult.getPhotos().get(0).getPhotoReference();
        String apiKey = "&key=" + BuildConfig.PLACES_API_KEY;
        return baseUrl + maxHeight + photoReference + apiKey;
    }
}
