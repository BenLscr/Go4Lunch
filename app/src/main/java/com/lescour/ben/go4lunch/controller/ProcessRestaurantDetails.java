package com.lescour.ben.go4lunch.controller;

import android.view.View;

import com.lescour.ben.go4lunch.BuildConfig;
import com.lescour.ben.go4lunch.model.PlaceDetailsResponse;
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
    private PlaceDetailsResponse mPlaceDetailsResponse;
    private String newHoursAndMinutes;
    private int hours;
    private int minutes;
    private String hoursString;
    private String minutesString;

    public ProcessRestaurantDetails(Result nearbyResult, PlaceDetailsResponse placeDetailsResponse) {
        this.mResult = nearbyResult;
        this.mPlaceDetailsResponse = placeDetailsResponse;
    }

    public String getRestaurantName() {
        return mResult.getName();
    }

    public String getRestaurantAddress() {
        String address = mPlaceDetailsResponse.getAddress();
        return address.substring(0, address.indexOf(","));
    }

    public String getRestaurantOpenHours() {
        if (mPlaceDetailsResponse.getOpeningHours() != null) {
            if (mResult.getOpeningHours().getOpenNow()) {
                if (mPlaceDetailsResponse.getOpeningHours().getPeriods().size() == 1) {
                    return "Open 24/7";
                } else {
                    Calendar calendar = Calendar.getInstance();
                    int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
                    int currentHours = calendar.get(Calendar.HOUR_OF_DAY);
                    int currentMinutes = calendar.get(Calendar.MINUTE);

                    SimpleDateFormat rawFormatHours = new SimpleDateFormat("hhmm", Locale.getDefault());
                    SimpleDateFormat newFormatHours = new SimpleDateFormat("h.mma", Locale.getDefault());

                    retrievesHoursAndMinutes(currentDay);

                    try {
                        Date date;
                        if (currentHours > hours && currentMinutes > minutes) {
                            retrievesHoursAndMinutes(currentDay +1);
                            date = rawFormatHours.parse(hoursString + minutesString);
                        } else {
                            date = rawFormatHours.parse(hoursString + minutesString);
                        }
                        newHoursAndMinutes = newFormatHours.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return "Open until " + newHoursAndMinutes;
                }
            } else {
                return "Currently closed";
            }
        } else {
            return "No schedules defined";
        }
    }

    private void retrievesHoursAndMinutes(int currentDay) {
        hours = mPlaceDetailsResponse.getOpeningHours().getPeriods().get(currentDay).getClose().getTime().getHours();
        minutes = mPlaceDetailsResponse.getOpeningHours().getPeriods().get(currentDay).getClose().getTime().getMinutes();
        SimpleDateFormat formatHours = new SimpleDateFormat("hh", Locale.getDefault());
        SimpleDateFormat formatMinutes = new SimpleDateFormat("mm", Locale.getDefault());
        try {
            Date dateHour = formatHours.parse(String.valueOf(hours));
            Date dateMinute = formatMinutes.parse(String.valueOf(minutes));
            hoursString = formatHours.format(dateHour);
            minutesString = formatMinutes.format(dateMinute);
        } catch (ParseException e) {
            e.printStackTrace();
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
