package com.lescour.ben.go4lunch;

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
        mResult.setName("Le Prélude");
        mPlaceDetailsResponse = new PlaceDetailsResponse();
        mPlaceDetailsResponse.setAddress("225 Rue Edouard Cannevel, 76510 Saint-Nicolas-d'Aliermont, France");
        mProcessRestaurantDetails = new ProcessRestaurantDetails(mResult, mPlaceDetailsResponse);
    }

    @Test
    public void restaurantName_isCorrect() {
        assertEquals("Le Prélude", mProcessRestaurantDetails.getRestaurantName());
    }

    @Test
    public void restaurantAddress_isCorrect() {
        assertEquals("225 Rue Edouard Cannevel", mProcessRestaurantDetails.getRestaurantAddress());
    }
}