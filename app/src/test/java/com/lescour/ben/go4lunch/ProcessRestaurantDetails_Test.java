package com.lescour.ben.go4lunch;

import android.view.View;

import com.lescour.ben.go4lunch.controller.ProcessRestaurantDetails;
import com.lescour.ben.go4lunch.controller.fragment.RestaurantListFragment;
import com.lescour.ben.go4lunch.model.PlaceDetailsResponse;
import com.lescour.ben.go4lunch.model.nearby.Result;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ProcessRestaurantDetails_Test {

    private Result mResult;
    private PlaceDetailsResponse mPlaceDetailsResponse;
    private ProcessRestaurantDetails mProcessRestaurantDetails;

    @Before
    public void setUp() {
        mResult = new Result();
        mResult.setRating(3.7);
        mPlaceDetailsResponse = new PlaceDetailsResponse();
        mPlaceDetailsResponse.setAddress("225 Rue Edouard Cannevel, 76510 Saint-Nicolas-d'Aliermont, France");
        mProcessRestaurantDetails = new ProcessRestaurantDetails(mResult, mPlaceDetailsResponse);
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