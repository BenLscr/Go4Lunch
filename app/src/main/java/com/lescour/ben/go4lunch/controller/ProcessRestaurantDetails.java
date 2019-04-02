package com.lescour.ben.go4lunch.controller;

import android.graphics.Bitmap;
import android.view.View;

import com.google.android.libraries.places.api.model.DayOfWeek;
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
    private int hours;
    private String hoursString;
    private String minutesString;

    public ProcessRestaurantDetails(Result nearbyResult, PlaceDetailsResponse placeDetailsResponse) {
        this.mResult = nearbyResult;
        this.mPlaceDetailsResponse = placeDetailsResponse;
    }

    public String getRestaurantName() {
        if (mResult.getName().length() >= 30) {
            return mResult.getName().substring(0, 30) + "...";
        } else {
        return mResult.getName();
        }
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

                    SimpleDateFormat rawFormatHours = new SimpleDateFormat("hhmm", Locale.getDefault());
                    SimpleDateFormat newFormatHours = new SimpleDateFormat("h.mma", Locale.getDefault());

                    String day = null;
                    switch (currentDay) {
                        case 0:
                            day = "SUNDAY";
                            break;
                        case 1:
                            day = "MONDAY";
                            break;
                        case 2:
                            day = "TUESDAY";
                            break;
                        case 3:
                            day = "WEDNESDAY";
                            break;
                        case 4:
                            day = "THURSDAY";
                            break;
                        case 5:
                            day = "FRIDAY";
                            break;
                        case 6:
                            day = "SATURDAY";
                            break;
                    }

                    int i = 0;
                    boolean bDay = false;
                    DayOfWeek dayFound;
                    do {
                        dayFound = mPlaceDetailsResponse.getOpeningHours().getPeriods().get(i).getClose().getDay();
                        if (!day.equals(dayFound.toString())) {
                            bDay = true;
                            if (currentHours > hours) {
                                i++;
                            }
                            retrievesHoursAndMinutes(i);
                        } else {
                            i++;
                        }
                    } while (!bDay);

                    Date date = null;
                    try {
                        date = rawFormatHours.parse(hoursString + minutesString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String newHoursAndMinutes = newFormatHours.format(date);
                    return "Open until " + newHoursAndMinutes;
                }
            } else {
                return "Currently closed";
            }
        } else {
            return "No schedules defined";
        }
    }

    private void retrievesHoursAndMinutes(int i) {
        hours = mPlaceDetailsResponse.getOpeningHours().getPeriods().get(i).getClose().getTime().getHours();
        int minutes = mPlaceDetailsResponse.getOpeningHours().getPeriods().get(i).getClose().getTime().getMinutes();
        SimpleDateFormat formatHours = new SimpleDateFormat("HH", Locale.getDefault());
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

    public Bitmap getRestaurantImage() {
        return mPlaceDetailsResponse.getBitmap();
    }
}
