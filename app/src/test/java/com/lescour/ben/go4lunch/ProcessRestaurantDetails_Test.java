package com.lescour.ben.go4lunch;

import android.content.Context;
import android.view.View;

import com.lescour.ben.go4lunch.controller.ProcessRestaurantDetails;
import com.lescour.ben.go4lunch.model.ParcelableRestaurantDetails;
import com.lescour.ben.go4lunch.model.details.PlaceDetailsResponse;
import com.lescour.ben.go4lunch.model.nearby.Geometry;
import com.lescour.ben.go4lunch.model.nearby.Location;
import com.lescour.ben.go4lunch.model.nearby.Result;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


public class ProcessRestaurantDetails_Test {

    private Double currentLat;
    private Double currentLng;
    private Result mResult;
    private PlaceDetailsResponse mPlaceDetailsResponse;
    private ProcessRestaurantDetails mProcessRestaurantDetails;

    @Before
    public void setUp() {
        currentLat = 49.8778;
        currentLng = 1.22824;
        mResult = new Result();
        mResult.setRating(3.7);
        mPlaceDetailsResponse = new PlaceDetailsResponse();
        mPlaceDetailsResponse.setAddress("225 Rue Edouard Cannevel, 76510 Saint-Nicolas-d'Aliermont, France");
        mProcessRestaurantDetails = new ProcessRestaurantDetails(mResult, mPlaceDetailsResponse, mock(Context.class));
    }

    @Test
    public void restaurantName_isCorrect() {
        mResult.setName("Le Prélude");
        assertEquals("Le Prélude", mProcessRestaurantDetails.getRestaurantName());
    }

    @Test
    public void restaurantName_isCorrect_whenIsLong() {
        mResult.setName("Le Trèfle - Restaurant et Bar Lounge du Casino de Dieppe");
        assertEquals("Le Trèfle - Restaurant et Bar ...", mProcessRestaurantDetails.getRestaurantName());
    }

    @Test
    public void restaurantAddress_isCorrect() {
        assertEquals("225 Rue Edouard Cannevel", mProcessRestaurantDetails.getRestaurantAddress());
    }

    /**@Test
    public void restaurantDistance_isCorrect() {
        Location location = new Location(49.8791513, 1.2210993);
        Geometry geometry = new Geometry(location);
        mResult.setGeometry(geometry);
        assertEquals("536" , mProcessRestaurantDetails.howFarIsThisRestaurant(parcelableRestaurantDetails.getCurrentLat(), parcelableRestaurantDetails.getCurrentLng()));
    }*/

    @Test
    public void restaurantRating1_isCorrect() {
        assertEquals(View.VISIBLE, mProcessRestaurantDetails.getRestaurantRate1());
    }
    @Test
    public void restaurantRating2_isCorrect() {
        assertEquals(View.VISIBLE, mProcessRestaurantDetails.getRestaurantRate2());
    }
    @Test
    public void restaurantRating3_isCorrect() {
        assertEquals(View.GONE, mProcessRestaurantDetails.getRestaurantRate3());
    }
}